package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import server.Chat;
import server.ChatRequest;
import server.ServerChat;
import server.User;

public class ClientHandlerThread extends Thread{
    private final Socket clientSocket;
    private final ServerChat server;
    private final String user;

    public ClientHandlerThread(Socket clientSocket ,ServerChat server, String user){
        this.clientSocket = clientSocket;
        this.server = server;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            while(true){
                ChatRequest request = (ChatRequest) input.readObject();
                switch(request.getCode()){
                    case ChatRequest.LOAD_CHATS:
                        loadChats(request, output);
                        break;
                    case ChatRequest.LOAD_MESSAGES:
                        loadMessages(request, output);
                        break;
                    case ChatRequest.WRITE_MESSAGE:
                        writeMessage(request);
                        break;
                    case ChatRequest.WRITE_CHATS:
                        writeChat(request);
                        break;
                    case ChatRequest.GET_SIZE:
                        getChatsLength(output);
                        break;
                    case ChatRequest.CHECK_USER:
                        checkUser(request, output);
                        break;
                    case ChatRequest.WRITE_USER:
                        writeUser(request,output);
                        break;
                    case ChatRequest.CHECK_USERS_NOPASSWORD:
                        checkUserNoPassword(request, output);
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally{
            try {
                server.closeConnection(user);
                clientSocket.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void checkUserNoPassword(ChatRequest request, ObjectOutputStream out) {
        try{
            User user = request.getUser();
            boolean found = server.checkUserNoPassword(user);
            out.writeObject(found);
        }
        catch(Exception ex){
            System.err.println(ex);
        }
    }

    private void writeMessage(ChatRequest request) {
        try {
            server.sendMessage(request);
        } catch (Exception e) {
            System.err.println(this + ":" + e);
        }
    }

    private void writeChat(ChatRequest request){
        try{
            server.writeChat(request);
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    private void loadMessages(ChatRequest request, ObjectOutputStream output) {
        try {
            output.writeObject(server.readMessages(request));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void loadChats(ChatRequest request, ObjectOutputStream output) {
        try {
            User user = request.getUser();
            ArrayList<Chat> allChats = server.readChats(request);
            ArrayList<Chat> chats = new ArrayList<>();
            for(Chat c: allChats){
                if(c.getUsers().contains(user.getUsername())) chats.add(c);
            }
            output.writeObject(chats);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void getChatsLength(ObjectOutputStream out){
        try{
            out.writeObject(server.getChatsLength());
        }
        catch(Exception ex){
            System.err.println(ex);
        }
    }

    private void checkUser(ChatRequest request, ObjectOutputStream out){
        try{
            User user = request.getUser();
            boolean found = server.checkUser(user);
            out.writeObject(found);
        }
        catch(Exception ex){
            System.err.println(ex);
        }
    }

    private void writeUser(ChatRequest request, ObjectOutputStream output){
        try {
            output.writeObject(server.writeUser(request.getUser()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
