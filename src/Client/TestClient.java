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
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            
            Socket client = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User("drappeggiojoe","test")));
            ArrayList<Chat> chats = (ArrayList<Chat>) in.readObject();
            for(Chat c: chats){
                System.out.println(c.isGroup() + "\n");
                for(String s: c.getUsers()){
                    System.out.println(s);
                }
                for(Message m : c.getMessages()){
                    System.out.println(m.getContent() +":"+m.getSender()+" "+m.getTime().get(GregorianCalendar.HOUR)+"/"+m.getTime().get(GregorianCalendar.MINUTE));
                }
            }
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
