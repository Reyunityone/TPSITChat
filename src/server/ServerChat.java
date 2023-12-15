package server;

import java.io.IOException;
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
    private Map<String, Socket> userSocketMap = new HashMap<>();
    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server listening on port " + PORT_NUMBER);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandlerThread clientThread = new ClientHandlerThread(clientSocket, this);
                clients.add(clientThread);
                clientThread.start();
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
    public void serverAuth(String username, Socket client){
        userSocketMap.put(username, client);
    }
    public  void sendMessage(ChatRequest request) throws Exception{
        ArrayList<Chat> chats = handler.readChats();
        for(Chat c: chats){
            if(c.getId() == request.getChatId()){
                for(String user : c.getUsers()){
                    if(!user.equals(request.getUser().getUsername())){
                        Socket client = userSocketMap.get(user);
                        System.out.println(client + ":" + user);
                        try {
                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                            out.writeObject(request.getMessage());
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                }
            }
        }

    }

}
