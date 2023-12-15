package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Client.ClientHandlerThread;

public class ServerChat {
    private static final int PORT_NUMBER = 5000;
    private List<ClientHandlerThread> clients = new ArrayList<>();
    private boolean running = true;
    private DBHandler handler = new DBHandler();
    private Map<String, ObjectOutputStream> userSocketMap = new HashMap<>();
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server listening on port " + PORT_NUMBER);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject("Trying to connect...");
                    ChatRequest request = (ChatRequest) in.readObject();
                    if(request.getCode() == ChatRequest.AUTH){
                        if(!userSocketMap.containsKey(request.getUser().getUsername())){
                            userSocketMap.put(request.getUser().getUsername(), out);
                            out.writeObject("Authentication succesful\n" + userSocketMap);
                            ClientHandlerThread clientThread = new ClientHandlerThread(clientSocket, this, request.getUser().getUsername());
                            clients.add(clientThread);
                            clientThread.start();
                        }
                        else{
                            out.writeObject("Authentication error");
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("Server chat: " + e);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer(){
        this.running = false;
    }

    public ArrayList<Chat> readChats(ChatRequest request) throws Exception{
        return handler.readChats();
    }
    public ArrayList<Message> readMessages(ChatRequest request) throws Exception{
      ArrayList<Chat> chats = handler.readChats();
      int id = request.getChatId();
      for(Chat c : chats){
        if(c.getId() == id){
            return c.getMessages();
        }
      }

      return new ArrayList<Message>();
    }

    public  void sendMessage(ChatRequest request) throws Exception{
        ArrayList<Chat> chats = handler.readChats();
        for(Chat c: chats){
            if(c.getId() == request.getChatId()){
                for(String user : c.getUsers()){
                    if(!user.equals(request.getUser().getUsername())){
                        ObjectOutputStream client = userSocketMap.get(user);
                        System.out.println(client + ":" + user);
                        if(client != null){
                            try {
                                client.writeObject(request.getMessage().getContent() + ">" +request.getMessage().getSender());
                                client.flush();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                c.getMessages().add(request.getMessage());
                System.out.println(handler.replaceChat(request.getChatId(), c));
            }
        }

    }

    public void closeConnection(String user) {
        userSocketMap.remove(user);
    }

}
