package Client;

import server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.out;

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
    private JScrollPane gianfranco;

    private JFrame frame;

    public ChatFrame(String username) {

        User anna = new User("anna", "anna");
        User giulia = new User("giulia", "giulia");
        ArrayList<String> testUser = new ArrayList<String>();
        testUser.add("anna");
        testUser.add("giulia");
        Message prima = new Message("","");
        Message secondo = new Message("","");
        ArrayList<Message> testMessage = new ArrayList<Message>();
        testMessage.add(prima);
        testMessage.add(secondo);
        ArrayList<String> testUser1 = new ArrayList<String>();
        testUser1.add("porca");
        testUser1.add("fante");
        Chat porcodio = new Chat(1, testUser, testMessage, false);
        Chat porcamadonna = new Chat(2, testUser1, testMessage, false);

        ArrayList<Chat> chats = new ArrayList<Chat>();
        chats.add(porcodio);
        chats.add(porcamadonna);


        ArrayList<String> contatti = new ArrayList<String>();
        ArrayList<String> real = new ArrayList<String>();
        for (int i = 0; i < chats.size(); i++) {
            Chat prova = chats.get(i);
            contatti = prova.getUsers();
            for (int j = 0; j < contatti.size(); j++) {
                real.add(contatti.get(j));
            }
        }

        for (int i = 0; i < real.size(); i++) {
            System.out.println(real.get(i));
        }
        //#########
        frame = new JFrame("Chat");
        frame.setMinimumSize(new Dimension(1064, 760));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(panel1);
        bentornato.setText("Ciao, " + username);
        //###########################
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 0, 0);
        contenitoreContatti.setLayout(flowLayout);


        for (int i = 0; i < real.size(); i++) {
            JPanel panel = createPanel(real.get(i));
            contenitoreContatti.add(panel);
        }

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
                    int controllore = 0;
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
                        for (int i = 0; i < real.size(); i++) {
                                if(!real.get(i).equalsIgnoreCase(username)){
                                    controllore++;
                                }
                        }
                        if(controllore == real.size()){
                            real.add(username);
                            JPanel panel = createPanel(real.get(real.size() - 1));
                            contenitoreContatti.add(panel);

                            // Ottieni la posizione corrente
                            Point currentViewPosition = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition = new Point(currentViewPosition.x + 0, currentViewPosition.y + 1);

                            // Imposta la nuova posizione della vista
                            gianfranco.getViewport().setViewPosition(newViewPosition);

                            // Ottieni la posizione corrente
                            Point currentViewPosition1 = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition1 = new Point(currentViewPosition1.x + 0, currentViewPosition1.y - 1);

                            // Imposta la nuova posizione della vista
                            gianfranco.getViewport().setViewPosition(newViewPosition1);
                        }else{
                            JOptionPane.showMessageDialog(null, "Utente già presente");
                        }

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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Utilizza BoxLayout con orientamento verticale

        Font customFont = new Font("Inter Semi Bold", Font.BOLD, 22);
        JLabel label = new JLabel(labelText);
        label.setFont(customFont);

        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Imposta l'allineamento orizzontale della JLabel al centro
        panel.add(Box.createVerticalGlue()); // Aggiunge uno spaziatore verticale per centrare verticalmente
        panel.add(label);
        panel.add(Box.createVerticalGlue()); // Aggiunge un altro spaziatore verticale per centrare verticalmente

        Color marcello = new Color(205, 146, 255);
        Color down = new Color(255, 255, 255);
        panel.setBackground(down);
        panel.setPreferredSize(new Dimension(298, 65));
        panel.setBorder(BorderFactory.createLineBorder(marcello, 1));

        return panel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
