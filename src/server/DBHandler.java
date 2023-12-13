package server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class DBHandler {
  private final String XML_FILE_NAME = "src/server/database.xml";
  
  public synchronized ArrayList<Chat> readChats() throws Exception{
    ArrayList<Chat> result = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    NodeList chats = document.getElementsByTagName("Chat");
    NodeList usersList = document.getElementsByTagName("Users");
    NodeList groups = document.getElementsByTagName("group");
    for (int i = 0; i < chats.getLength(); i++) {
      ArrayList<String> users = new ArrayList<>();
      boolean group;
      ArrayList<Message> messages = new ArrayList<>();

      
    }
    return result;
  }

  public synchronized boolean writeChat(Chat c) throws Exception{
    boolean success = false;
    ArrayList<Message> messages = c.getMessages();
    String group = c.isGroup() ? "yes":"no";
    ArrayList<String> users = c.getUsers();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringElementContentWhitespace(true); 
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    Element chat = document.createElement("Chat");
    Element messagesElement = document.createElement("Messages");
    for(Message m : messages){
      Element content = document.createElement("content");
      content.setTextContent(m.getContent());
      Element time = document.createElement("messageTime");
      time.setTextContent(""+ m.getTime().get(GregorianCalendar.DATE) +
       "/" + m.getTime().get(GregorianCalendar.MONTH) +
       "/" + m.getTime().get(GregorianCalendar.YEAR));
      Element sender = document.createElement("sender");
      sender.setTextContent(m.getSender());
      Element message = document.createElement("message");
      message.appendChild(content);
      message.appendChild(time);
      message.appendChild(sender);
      messagesElement.appendChild(message);
    }

    Element g = document.createElement("Group");
    g.setTextContent(group);
    
    Element userList = document.createElement("Users");
    for(String u : users){
      Element user = document.createElement("User");
      user.setTextContent(u);
      userList.appendChild(user);
    }
    
    chat.appendChild(userList);
    chat.appendChild(g);
    chat.appendChild(messagesElement);
    document.getElementsByTagName("database").item(0).appendChild(chat);
    TransformerFactory factory2 = TransformerFactory.newInstance();
    Transformer t = factory2.newTransformer();
    FileOutputStream output = new FileOutputStream(XML_FILE_NAME);
    DOMSource source = new DOMSource(document);
    StreamResult result = new StreamResult(output);
    t.transform(source, result);
    return success;
  }
}
