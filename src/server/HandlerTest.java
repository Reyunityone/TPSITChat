package server;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class HandlerTest {

  public static void main(String[] args) {
    DBHandler handler = new DBHandler();
    try {
      // ArrayList<String> users = new ArrayList<>();
      // users.add("drappeggiojoe");
      // users.add("tomamaroia");
      // ArrayList<Message> messages = new ArrayList<>();
      // messages.add(new Message("Sesso oggi?", "drappeggiojoe"));
      // messages.add(new Message("Si dai?", "tomamaroia"));
      // Chat c1 = new Chat(1, users, messages, false);
      // handler.writeChat(c1);
      ArrayList<Chat> chats = handler.readChats();
      for(Chat c : chats){
        System.out.println(c.toString()+ " " + c.isGroup());
        for(Message m : c.getMessages()){
          System.out.println("\t" + m.getContent() +" " + m.getSender() + m.getTime().get(GregorianCalendar.HOUR) + ":" + m.getTime().get(GregorianCalendar.MINUTE));
        }
        for(String s : c.getUsers()){
          System.out.println("\t\t" + s +" ");
        }
      }
    } catch (Exception e) {
      //TODO: handle exception
      System.err.println(e);
    }
  }
}
