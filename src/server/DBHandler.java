package server;
import java.io.File;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DBHandler {
  private final String XML_FILE_NAME = "database.xml";
  
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
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(XML_FILE_NAME));
    return success;
  }
}
