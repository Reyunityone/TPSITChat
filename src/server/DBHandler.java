package server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DBHandler {
  private final String XML_FILE_NAME = "src/server/database.xml";
  
  public synchronized String readXml() throws Exception{
    String result = "";
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    return result;
  }

  public synchronized boolean writeXml() throws Exception{
    boolean success = false;
    String message = "Test message";
    GregorianCalendar messageTime = new GregorianCalendar();
    String group = "no";
    String[] users = {"Pippo","Merrino"};

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringElementContentWhitespace(true); 
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    Element chat = document.createElement("Chat");
    Element messages = document.createElement("Messages");
    Element m = document.createElement("Message");
    Element content = document.createElement("Content");
    content.setTextContent(message);

    Element g = document.createElement("Group");
    g.setTextContent(group);
    
    Element userList = document.createElement("Users");
    for(String u : users){
      Element user = document.createElement("User");
      user.setTextContent(u);
      userList.appendChild(user);
    }
    
    Element time = document.createElement("Message-Time");
    time.setTextContent(messageTime.getTime().toString());
    
    m.appendChild(content);
    m.appendChild(time);
    messages.appendChild(m);
    chat.appendChild(userList);
    chat.appendChild(g);
    chat.appendChild(messages);
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
