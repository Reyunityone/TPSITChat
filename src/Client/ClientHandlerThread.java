package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import server.Chat;
import server.ChatRequest;
import server.Message;
import server.ServerChat;
import server.User;

public class ClientHandlerThread extends Thread{
    private Socket clientSocket;
    private ServerChat server;
    private User user;
    private boolean authenticated = false;

    public ClientHandlerThread(Socket clientSocket ,ServerChat server){
        this.clientSocket = clientSocket;
        this.server = server;
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
                    case ChatRequest.AUTH:
                        authenticate(request,output);
                        break;
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally{
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void writeMessage(ChatRequest request) {
        try {
            server.sendMessage(request);
        } catch (Exception e) {
            System.err.println(this + ":" + e);
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
            if(authenticated){
                User user = request.getUser();
            ArrayList<Chat> allChats = server.readChats(request);
            ArrayList<Chat> chats = new ArrayList<>();
            for(Chat c: allChats){
                if(c.getUsers().contains(user.getUsername())) chats.add(c);
            }
            output.writeObject(chats);
            }
            else throw new Exception("Not authenticated");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void authenticate(ChatRequest request, ObjectOutputStream output) throws Exception{
        System.out.println("got here");
        try {
            this.user = request.getUser();
            authenticated = true;
            server.serverAuth(user.getUsername(), clientSocket);
            output.writeObject("dio cane autenticato");
        } catch (Exception e) {
            throw new Exception("Bad request");
        }
    }

    public String getUser()throws Exception{
        if(authenticated) return user.getUsername();
        else throw new Exception("User not authenticated");
    }
}
