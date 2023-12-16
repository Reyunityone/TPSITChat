package Client;

import server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatFrame extends JFrame{
    private JPanel panel1;
    private JTextArea messageArea;
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
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public ChatFrame(String username) {
        ArrayList<Chat> chats = new ArrayList<Chat>();
        try{
            Socket client = new Socket("localhost", 5000);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            //Auth request
            System.out.println(in.readObject());
            out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(username, null)));
            out.flush();
            //Response
            System.out.println(in.readObject());
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
            out.flush();
            chats = (ArrayList<Chat>) in.readObject();
            System.out.println(chats);
        }
        catch(Exception e){
            System.err.println(e);
        }


        ArrayList<String> contatti = new ArrayList<String>();
        ArrayList<String> real = new ArrayList<String>();
        for (int i = 0; i < chats.size(); i++) {
            Chat prova = chats.get(i);
            contatti = prova.getUsers();
            real.addAll(contatti);
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
                    String addedUser = "";
                    int controllore = 0;
                    addedUser = newChatText.getText();
                    CredentialsHandler gestioneCred = new CredentialsHandler();
                    prova = gestioneCred.readCredentials();
                    boolean neg = false;
                    for (int i = 0; i < prova.size(); i++) {
                        if (prova.get(i).getUsername().equals(addedUser)) {
                            neg = true;
                        }
                    }
                    if (!neg) {
                        JOptionPane.showMessageDialog(null, "Utente non trovato");
                    }else if(!addedUser.equals(username)){
                        for (int i = 0; i < real.size(); i++) {
                                if(!real.get(i).equalsIgnoreCase(addedUser)){
                                    controllore++;
                                }
                        }

                        if(controllore == real.size()){
                            real.add(addedUser);
                            ArrayList<String> users = new ArrayList<>();
                            users.add(username);
                            users.add(addedUser);
                            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                            out.flush();
                            ArrayList<Chat> currentChats = (ArrayList<Chat>) in.readObject();
                            Chat chat = new Chat(currentChats.size() + 1, users, new ArrayList<Message>(), false);
                            ChatRequest request = new ChatRequest(ChatRequest.WRITE_CHATS, chat);
                            out.writeObject(request);
                            out.flush();
                            JPanel panel = createPanel(real.getLast());
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
                    else{
                        JOptionPane.showMessageDialog(null, "Non puoi chattare con te stesso.");
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });

        a️Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentChat = 0;
                String messageContent = messageArea.getText();
                Message message = new Message(messageContent, username);
                User u = new User(username, null);
                ChatRequest request = new ChatRequest(ChatRequest.WRITE_MESSAGE, u, message, currentChat);
                try {
                    out.writeObject(request);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
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
