package server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class DBHandler {
  private final String XML_FILE_NAME = "src/server/database.xml";
  
  public synchronized ArrayList<Chat> readChats() throws Exception{
    ArrayList<Chat> result = new ArrayList<>();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    NodeList chats = document.getElementsByTagName("Chat");
    NodeList usersList = document.getElementsByTagName("Users");
    NodeList groups = document.getElementsByTagName("Group");
    NodeList messagesList = document.getElementsByTagName("Messages");
    for (int i = 0; i < chats.getLength(); i++) {
      ArrayList<String> users = new ArrayList<>();
      boolean group = groups.item(i).getTextContent().equals("yes");
      ArrayList<Message> messages = new ArrayList<>();
      NodeList singleUsers = usersList.item(i).getChildNodes();
      for (int j = 0; j < singleUsers.getLength(); j++) {
        users.add(singleUsers.item(j).getTextContent());
      }
      NodeList singleMessages = messagesList.item(i).getChildNodes();
      for (int j = 0; j < singleMessages.getLength(); j++) {
        Node message = singleMessages.item(j);
        String content = message.getChildNodes().item(0).getTextContent();
        String messageTimeString = message.getChildNodes().item(1).getTextContent();
        StringTokenizer st = new StringTokenizer(messageTimeString, "/");
        int day = Integer.parseInt(st.nextToken());
        int month = Integer.parseInt(st.nextToken());
        int year = Integer.parseInt(st.nextToken());
        int hour = Integer.parseInt(st.nextToken());
        int minute = Integer.parseInt(st.nextToken());
        GregorianCalendar messageTime = new GregorianCalendar(year, month , day, hour , minute);
        String sender = message.getChildNodes().item(2).getTextContent();
        Message msg  = new Message(content, sender ,messageTime);
        messages.add(msg);
      }
      Chat c = new Chat(i, users, messages, group);
      result.add(c);
      
    }
    return result;
  }

  public synchronized boolean writeChat(Chat c) throws Exception{
    boolean success = false;
    int chatId = c.getId();
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
       "/" + m.getTime().get(GregorianCalendar.YEAR) +
       "/" + m.getTime().get(GregorianCalendar.HOUR_OF_DAY) + 
       "/" + m.getTime().get(GregorianCalendar.MINUTE));
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
    Element id = document.createElement("chatId");
    id.setTextContent("" + chatId);
    chat.appendChild(id);
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

  public synchronized boolean replaceChat(int chatId, Chat c){
    boolean success = false;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new File(XML_FILE_NAME));
      NodeList ids = document.getElementsByTagName("chatId");
      for (int i = 0; i < ids.getLength(); i++) {
        System.out.println(ids.item(i).getTextContent());
        System.out.println(""+chatId);
        if(ids.item(i).getTextContent().equals("" + chatId)){
          Node chat = ids.item(i).getParentNode();
          System.out.println(chat);
          NodeList chatChildren = chat.getChildNodes();
          chatChildren.item(0).setTextContent("" + chatId);
          NodeList users = chatChildren.item(1).getChildNodes();
          int j = 0;
          for(String u : c.getUsers()){
            users.item(j).setTextContent(u);
            j++;
          }
          chatChildren.item(2).setTextContent(c.isGroup() ? "yes" : "no");
          NodeList messages = chatChildren.item(3).getChildNodes();
          j = 0;
          do{
            Message message = c.getMessages().get(j);
            System.out.println(messages.item(j));
            if(messages.item(j) != null){
              Node content = messages.item(j).getChildNodes().item(0);
              Node time = messages.item(j).getChildNodes().item(1);
              Node sender = messages.item(j).getChildNodes().item(2);
              content.setTextContent(message.getContent());
              time.setTextContent("" + message.getTime().get(GregorianCalendar.DATE)
              + "/" + message.getTime().get(GregorianCalendar.MONTH)
              + "/" + message.getTime().get(GregorianCalendar.YEAR)
              + "/" + message.getTime().get(GregorianCalendar.HOUR_OF_DAY)
              + "/" + message.getTime().get(GregorianCalendar.MINUTE));
              sender.setTextContent(message.getSender());
              System.out.println(message.getContent());
            }
            else{
              Node content = document.createElement("content");
              Node messageTime = document.createElement("messageTime");
              Node sender = document.createElement("sender");
              content.setTextContent(message.getContent());
              messageTime.setTextContent("" + message.getTime().get(GregorianCalendar.DATE)
              + "/" + message.getTime().get(GregorianCalendar.MONTH)
              + "/" + message.getTime().get(GregorianCalendar.YEAR)
              + "/" + message.getTime().get(GregorianCalendar.HOUR_OF_DAY)
              + "/" + message.getTime().get(GregorianCalendar.MINUTE));
              sender.setTextContent(message.getSender());
              Node newMessage = document.createElement("message");
              newMessage.appendChild(content);
              newMessage.appendChild(messageTime);
              newMessage.appendChild(sender);
              System.out.println(newMessage);
              Element messagesElement = (Element) messages;
              messagesElement.appendChild(newMessage);
            }
            j++;
          }while (j < c.getMessages().size());
            TransformerFactory factory2 = TransformerFactory.newInstance();
            Transformer t = factory2.newTransformer();
            FileOutputStream output = new FileOutputStream(XML_FILE_NAME);
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            t.transform(source, result);
            success = true;
        }
      }



    } catch (SAXException | IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return success;
  }
}
