package Client;

import server.ChatRequest;
import server.CredentialsHandler;
import server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SignUp extends JFrame{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JPanel panel1;
    private JButton REGISTRATIButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton RITORNAALLOGINButton;
    private JLabel errori;


    private JFrame frame;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        new SignUp();
    }
public SignUp() {
    try{
        socket = new Socket("localhost", 5000);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println(in.readObject());
        out.writeObject(new ChatRequest(ChatRequest.MANAGE_USERS, (User)null));
        System.out.println(in.readObject());
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

    }
    catch(Exception ex){
        System.err.println(ex);
    }
    frame = new JFrame("Sign Up");
    frame.setMinimumSize(new Dimension(660,460));
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.add(panel1);
    frame.setVisible(true);
    errori.setVisible(false);
    REGISTRATIButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                String username = "";
                String password = "";
                username = textField1.getText();
                password = passwordField1.getText();

                if (username.isBlank() && password.isBlank()){
                    errori.setForeground(Color.red);
                    errori.setVisible(true);
                    errori.setText("Inserisci il nome utente ed una password!!!");
                }else if(username.isBlank()){
                    errori.setForeground(Color.red);
                    errori.setVisible(true);
                    errori.setText("Inserisci il nome utente!!!");
                }
                else if(password.isBlank()){
                    errori.setForeground(Color.red);
                    errori.setVisible(true);
                    errori.setText("Inserisci una password!!!");
                } else if (username.length() < 5) {
                    errori.setForeground(Color.red);
                    errori.setVisible(true);
                    errori.setText("Inserisci un username di almeno 5 caratteri!!!");
                } else{
                    User user = new User(username, password);
                    ChatRequest request = new ChatRequest(ChatRequest.WRITE_USER, user);
                    out.writeObject(request);
                    out.flush();
                    boolean success = (boolean)in.readObject();
                    if(!success){
                        errori.setForeground(Color.red);
                        errori.setVisible(true);
                        errori.setText("Utente già registrato!!!");
                    }else{
                        errori.setVisible(true);
                        errori.setForeground(Color.green);
                        errori.setText("Registrazione avvenuta con successo!!!");
                    }
                }
            }catch (Exception error){}
        }
    });
    RITORNAALLOGINButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            login marco = new login();
            frame.setVisible(false);
        }
    });
}
}
