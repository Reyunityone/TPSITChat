package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TestServer {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            ServerChat server = new ServerChat();
            Thread thread = new Thread(() -> server.startServer());
            thread.start();
            Socket client = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User("drappeggiojoe","test")));
            ArrayList<Chat> chats = (ArrayList<Chat>) in.readObject();
            System.out.println(chats);
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User("filippo","test")));
            chats = (ArrayList<Chat>) in.readObject();
            System.out.println(chats);
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
