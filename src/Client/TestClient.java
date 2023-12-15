package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.ChatRequest;
import server.Message;
import server.User;

/**
 * TestClient
 */
public class TestClient {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            //Auth request
            System.out.println(in.readObject());
            out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(args[0], null)));
            out.flush();
            //Response
            System.out.println(in.readObject());

            //Send message
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            out.writeObject(new ChatRequest(ChatRequest.WRITE_MESSAGE, new User("tomamaroia", null), new Message("test222", "tomamaroia"), 0));
            out.flush();
            try{
                while(true){
                    System.out.println(in.readObject());
                }
            }
            finally{
                client.close();
                out.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}