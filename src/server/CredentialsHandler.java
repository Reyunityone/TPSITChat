package server;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;;

public class CredentialsHandler {
    
    private static final String XML_FILE_NAME = "src/server/credentials.xml";
    
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

    public synchronized boolean writeCredentials(User user) throws Exception{

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(XML_FILE_NAME));

        NodeList users = document.getElementsByTagName("user");

        for (int i = 0; i < users.getLength(); i++) {
            NodeList userChildren = users.item(i).getChildNodes();
            if(userChildren.item(0).getTextContent().equalsIgnoreCase(user.getUsername())) return false;
        }

        Element newUser = document.createElement("user");
        Element username = document.createElement("username");
        username.setTextContent(user.getUsername());
        Element password = document.createElement("password");
        password.setTextContent(user.getPassword());
        newUser.appendChild(username);
        newUser.appendChild(password);
        document.getElementsByTagName("credentials").item(0).appendChild(newUser);

        TransformerFactory factory2 = TransformerFactory.newInstance();
        Transformer t = factory2.newTransformer();
        FileOutputStream output = new FileOutputStream(XML_FILE_NAME);
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(output);
        t.transform(source, result);
        return true;
    }
}
