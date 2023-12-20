package Client;

import server.ChatRequest;
import server.CredentialsHandler;
import server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class login     extends JFrame{
    private JPanel panel1;
    private JButton LOGINButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton REGISTRATIButton;
    private JLabel errori;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame frame;
    private Socket socket;
    public login(){
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println(in.readObject());
            out.writeObject(new ChatRequest(ChatRequest.MANAGE_USERS, (User)null));
            System.out.println(in.readObject());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        frame = new JFrame("Login");
        frame.setMinimumSize(new Dimension(660,460));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(panel1);
        frame.setVisible(true);
        errori.setVisible(false);
        REGISTRATIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignUp marco = new SignUp();
                frame.setVisible(false);
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                frame.dispose();
            }
        });
        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String username = "";
                    String password = "";
                    username = textField1.getText();
                    password = passwordField1.getText();
                    if (username.isBlank() && password.isBlank()){
                        errori.setVisible(true);
                        errori.setText("Inserisci il nome utente ed una password!!!");
                    }else if(username.isBlank()){
                        errori.setVisible(true);
                        errori.setText("Inserisci il nome utente!!!");
                    }
                    else if(password.isBlank()){
                        errori.setVisible(true);
                        errori.setText("Inserisci una password!!!");
                    }else{

                        ChatRequest request = new ChatRequest(ChatRequest.CHECK_USER, new User(username,password));
                        out.writeObject(request);
                        boolean neg = (boolean) in.readObject();
                        if(!neg){
                            errori.setVisible(true);
                            errori.setText("Username o password errate!!!");
                        }else{
                            frame.setVisible(false);

                            ChatFrame marco = new ChatFrame(username);
                            socket.close();
                            frame.dispose();
                        }
                    }
                }catch (Exception error){}
            }

        });
    }

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
        new login();
    }
}


