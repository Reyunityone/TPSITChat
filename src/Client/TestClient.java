package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import server.Chat;
import server.ChatRequest;
import server.Message;
import server.User;

public class TestClient {
   private Socket client;
   private ObjectOutputStream out;
   private ObjectInputStream in;

   public TestClient(String host, int port, String username) throws IOException {
       this.client = new Socket(host, port);
       this.out = new ObjectOutputStream(client.getOutputStream());
       this.in = new ObjectInputStream(client.getInputStream());
       this.out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(username, null)));
       try {
        System.out.println(in.readObject());
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
   }

   public void sendMessage(String message) throws IOException {
       this.out.writeObject(new ChatRequest(ChatRequest.WRITE_MESSAGE, new User("drappeggiojoe", null), new Message(message, "test"), 0));
   }

   public void listen() throws IOException, ClassNotFoundException {
       while(true) {
           System.out.println(in.readObject());
       }
   }

   public void close() throws IOException {
       this.client.close();
   }

   public static void main(String[] args) {
    try {
        TestClient client1 = new TestClient("localhost", 5000, args[0]);
        TestClient client2 = new TestClient("localhost", 5000, args[1]);
 
        new Thread(() -> {
            try {
                client1.sendMessage("Hello from client 1");
                client1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
 
        new Thread(() -> {
            try {
                client2.listen();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
 
    } catch (IOException e) {
        e.printStackTrace();
    }
 }
}