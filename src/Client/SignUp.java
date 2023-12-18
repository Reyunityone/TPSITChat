package Client;

import server.CredentialsHandler;
import server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUp extends JFrame{
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
                    CredentialsHandler gestioneCred = new CredentialsHandler();
                    if(!gestioneCred.writeCredentials(user)){
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
