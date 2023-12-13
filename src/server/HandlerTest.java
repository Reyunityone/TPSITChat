package server;
public class HandlerTest {
  public static void main(String[] args) {
    DBHandler handler = new DBHandler();
    try {
      handler.writeXml();
    } catch (Exception e) {
      //TODO: handle exception
      System.err.println(e);
    }
  }
}
