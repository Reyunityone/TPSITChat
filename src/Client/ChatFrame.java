package Client;

import server.CredentialsHandler;
import server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChatFrame extends JFrame{
    private JPanel panel1;
    private JTextArea textArea1;
    private JTextField textField1;
    private JLabel bentornato;
    private JButton a️Button;
    private JButton logout;
    private JButton cerchito;
    private JButton cercatopo;
    private JButton newChat;
    private JTextField newChatText;
    private JPanel contenitoreContatti;

    private JFrame frame;

    public ChatFrame(String username) {
        frame = new JFrame("Chat");
        frame.setMinimumSize(new Dimension(1064, 760));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(panel1);
        bentornato.setText("Ciao, " + username);
        //############################
        contenitoreContatti.setLayout(new FlowLayout());

        // Aggiungi un pannello interno
        JPanel panel1 = createPanel("Pannello 1");
        contenitoreContatti.add(panel1);

        // Aggiungi un altro pannello interno
        JPanel panel2 = createPanel("Pannello 2");
        contenitoreContatti.add(panel2);

        //###########################
        frame.setVisible(true);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login marco = new login();
                frame.setVisible(false);
            }
        });

        newChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<User> prova = new ArrayList<User>();
                    String username = "";
                    username = newChatText.getText();
                    CredentialsHandler gestioneCred = new CredentialsHandler();
                    prova = gestioneCred.readCredentials();
                    boolean neg = false;
                    for (int i = 0; i < prova.size(); i++) {
                        if (prova.get(i).getUsername().equals(username)) {
                            neg = true;
                        }
                    }
                    if (!neg) {
                        JOptionPane.showMessageDialog(null, "Utente non trovato");
                    }else{
                        //da fare
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
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
        new ChatFrame(null);
    }

    private JPanel createPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel(labelText);
        JButton button = new JButton("Un pulsante");

        panel.add(label);
        panel.add(button);

        panel.setPreferredSize(new Dimension(300, 65));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return panel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
