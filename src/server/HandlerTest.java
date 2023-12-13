package server;

import java.util.ArrayList;

public class HandlerTest {
  private static final String ArrayList = null;

  public static void main(String[] args) {
    DBHandler handler = new DBHandler();
    try {
      ArrayList<String> users = new ArrayList<>();
      users.add("drappeggiojoe");
      users.add("tomamaroia");
      ArrayList<Message> messages = new ArrayList<>();
      messages.add(new Message("Sesso oggi?", "drappeggiojoe"));
      messages.add(new Message("Si dai?", "tomamaroia"));
      Chat c = new Chat(1, users, messages, true);
      handler.writeChat(c);
    } catch (Exception e) {
      //TODO: handle exception
      System.err.println(e);
    }
  }
}
