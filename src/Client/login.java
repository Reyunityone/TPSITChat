package Client;

import server.CredentialsHandler;
import server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class login     extends JFrame{
    private JPanel panel1;
    private JButton LOGINButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton REGISTRATIButton;
    private JLabel errori;

    private JFrame frame;

    public login(){
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
            }
        });
        LOGINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    ArrayList<User> prova = new ArrayList<User>();
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
                        CredentialsHandler gestioneCred = new CredentialsHandler();
                        prova = gestioneCred.readCredentials();
                        boolean neg = false;
                        for (int i = 0; i < prova.size(); i++) {
                            if(prova.get(i).getUsername().equals(username)){
                                neg = true;
                            }
                        }
                        if(!neg){
                            errori.setVisible(true);
                            errori.setText("Username o password errate!!!");
                        }else{
                            frame.setVisible(false);
                            ChatFrame marco = new ChatFrame(username);
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


