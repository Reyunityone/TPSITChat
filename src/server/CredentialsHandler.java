import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CredentialsHandler {
    
    private static final String XML_FILE_NAME = "credentials.xml";
    
    public synchronized ArrayList<User> readCredentials() throws Exception{
        ArrayList<User> userList = new ArrayList<User>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(XML_FILE_NAME));

        NodeList users = document.getElementsByTagName("user");

        for (int i = 0; i < users.getLength(); i++) {
            NodeList userChildren = users.item(i).getChildNodes();
            String username = userChildren.item(0).getTextContent();
            String password = userChildren.item(1).getTextContent();
            User user = new User(username, password);
            userList.add(user);
        }

        return userList;
    }

    public synchronized boolean writeCredentials(User user){

        ArrayList<User> userList = new ArrayList<User>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(XML_FILE_NAME));

        NodeList users = document.getElementsByTagName("user");

        for (int i = 0; i < users.getLength(); i++) {
            
        }
    }
}
