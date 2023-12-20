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
    private CredentialsHandler credentials = new CredentialsHandler();
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
                        if(!userSocketMap.containsKey(request.getUser().getUsername())) {
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
                    else if(request.getCode() == ChatRequest.MANAGE_USERS){
                        out.writeObject("OK!");
                        ClientHandlerThread clientThread = new ClientHandlerThread(clientSocket, this, null);
                        clients.add(clientThread);
                        clientThread.start();
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

    public synchronized void writeChat(ChatRequest c) throws Exception{
        handler.writeChat(c.getChat());
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
                System.out.println(c);
                System.out.println(request.getChatId());
                c.getMessages().add(request.getMessage());
                System.out.println(handler.replaceChat(request.getChatId(), c));
            }
        }

    }

    public void closeConnection(String user) {
        userSocketMap.remove(user);
    }

    public synchronized int getChatsLength() throws Exception {
        return handler.readChats().size();
    }

    public synchronized boolean checkUser(User u) throws Exception{
        ArrayList<User> userList = credentials.readCredentials();
        for(User user : userList){
            if(user.getUsername().equals(u.getUsername()) && user.getPassword().equals(u.getPassword())) return true;
        }

        return false;
    }

    public synchronized boolean writeUser(User u) throws Exception{
        return credentials.writeCredentials(u);
    }

    public synchronized boolean checkUserNoPassword(User u) throws Exception{
        ArrayList<User> userList = credentials.readCredentials();
        for(User user : userList){
            if(user.getUsername().equals(u.getUsername())) return true;
        }

        return false;
    }

}
