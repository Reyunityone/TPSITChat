package Client;

import server.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

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
    private JPanel contenitoreMessaggi;
    private JScrollPane gianpiero;

    private String username;
    private JFrame frame;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Socket client;

    private int currentChat = -1;
    public ChatFrame(String username) {
        this.username = username;
        ArrayList<Chat> chats = new ArrayList<Chat>();
        try{
            client = new Socket("localhost", 5000);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            //Auth request
            System.out.println(in.readObject());
            out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(username, null)));
            out.flush();
            //Response
            String response = (String)in.readObject();
            if(response.equalsIgnoreCase("authentication error")) JOptionPane.showMessageDialog(null, response);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
            out.flush();
            chats = (ArrayList<Chat>) in.readObject();
            System.out.println(chats);
        }
        catch(Exception e){
            System.err.println(e + "mortubio");
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

        contenitoreMessaggi.setLayout(new BoxLayout(contenitoreMessaggi, BoxLayout.Y_AXIS));

        for(Chat c : chats){
            contenitoreContatti.add(createPanel(c));
        }
        //###########################
        frame.setVisible(true);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login marco = new login();
                frame.setVisible(false);
                try{
                    client.close();
                }
                catch (Exception error){
                    System.err.println(error);
                }
                frame.dispose();
            }
        });

        newChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<User> prova = new ArrayList<User>();
                    String addedUser = "";
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

                        ArrayList<String> users = new ArrayList<>();
                        users.add(username);
                        users.add(addedUser);
                        out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                        out.flush();
                        ArrayList<Chat> currentChats = (ArrayList<Chat>) in.readObject();
                        boolean isPresent = isPresent(currentChats, addedUser);
                        if(!isPresent){
                            Chat chat = new Chat(currentChats.size() + 1, users, new ArrayList<Message>(), false);
                            ChatRequest request = new ChatRequest(ChatRequest.WRITE_CHATS, chat);
                            out.writeObject(request);
                            out.flush();
                            JPanel panel = createPanel(chat);
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
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Chat già esistente");
                        }

                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Non puoi chattare con te stesso.");
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            private boolean isPresent(ArrayList<Chat> currentChats, String addedUser) {
                boolean isPresent = false;
                for (Chat c: currentChats){
                    ArrayList<String> addingUsers = new ArrayList<>();
                    addingUsers.add(username);
                    addingUsers.add(addedUser);
                    addingUsers.sort(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    ArrayList<String> chatUsers = c.getUsers();
                    chatUsers.sort(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    System.out.println("is present >>>>" + chatUsers);
                    System.out.println("is present >>>>" + addingUsers);
                    isPresent = addingUsers.equals(chatUsers);
                    if(isPresent) break;
                }
                return isPresent;
            }
        });

        a️Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = messageArea.getText();
                messageArea.setText("");
                Message message = new Message(messageContent, username);
                User u = new User(username, null);
                ChatRequest request = new ChatRequest(ChatRequest.WRITE_MESSAGE, u, message, currentChat);
                try {
                    out.writeObject(request);
                    System.out.println(messageContent);
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

    private ChatPanel createPanel(Chat c) {
        ChatPanel panel = new ChatPanel(c.getId());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Utilizza BoxLayout con orientamento verticale

        Font customFont = new Font("Inter Semi Bold", Font.BOLD, 22);
        ArrayList<String> users = c.getUsers();
        String usersString = "";
        int i = 0;
        for(String user: users){
            if(!user.equals(username)){
                if(i == 0){
                    usersString += user;
                }
                else {
                    usersString += ", " + user;
                }
                i++;
            }
        }
        JLabel label = new JLabel(usersString);
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

        // Aggiungi un MouseListener al pannello
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Elimina tutti i componenti dal pannello
                contenitoreMessaggi.removeAll();

// Aggiorna il layout del pannello
                contenitoreMessaggi.revalidate();

// Ridisegna il pannello
                contenitoreMessaggi.repaint();
                if (c != null) {
                    ChatPanel thispanel = (ChatPanel) e.getSource();
                    int id = thispanel.getChatId();
                    currentChat = id;
                    System.out.println("Hai cliccato sul pannello con ID " + id);

                    ChatRequest request = new ChatRequest(ChatRequest.LOAD_MESSAGES, id);
                    ArrayList<Message> messaggi = null;
                    try{
                        out.writeObject(request);
                        messaggi = (ArrayList<Message>) in.readObject();
                        System.out.println(messaggi);
                    }
                    catch (Exception ex) {
                        System.err.println(ex);
                    }


                    for (Message prova : messaggi) {
                        System.out.println(prova.getContent());
                    }
                    for (Message m : messaggi) {
                        // Rinomina la variabile locale del pannello
                        JPanel messagePanel = createMessagePanel(m);
                        System.out.println(messagePanel);
                        contenitoreMessaggi.add(messagePanel);

                        int spazioTraPannelli = 5;
                        contenitoreMessaggi.setBorder(BorderFactory.createEmptyBorder(spazioTraPannelli, spazioTraPannelli, spazioTraPannelli, spazioTraPannelli));
                        // Aggiungi il pannello dei messaggi al contenitore
                        contenitoreMessaggi.add(messagePanel);

                        // Riorganizza il layout del contenitoreMessaggi
                        contenitoreMessaggi.revalidate();
                        contenitoreMessaggi.repaint();
                        // Ottieni la posizione corrente
                        Point currentViewPosition = gianpiero.getViewport().getViewPosition();

                        // Sposta di un pixel orizzontalmente e verticalmente
                        Point newViewPosition = new Point(currentViewPosition.x + 0, currentViewPosition.y + 1);

                        // Imposta la nuova posizione della vista
                        gianpiero.getViewport().setViewPosition(newViewPosition);

                        // Ottieni la posizione corrente
                        Point currentViewPosition1 = gianpiero.getViewport().getViewPosition();

                        // Sposta di un pixel orizzontalmente e verticalmente
                        Point newViewPosition1 = new Point(currentViewPosition1.x + 0, currentViewPosition1.y - 1);

                        // Imposta la nuova posizione della vista
                        gianpiero.getViewport().setViewPosition(newViewPosition1);
                    }
                }
            }
        });
        return panel;
    }

    private JPanel createMessagePanel(Message messaggio) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        Color marcello = new Color(205, 146, 255);
        messagePanel.setPreferredSize(new Dimension(400, 65));
        messagePanel.setMaximumSize(new Dimension(400, 65));


        int spazioTraPannelli = 5;

// Aggiungi uno spazio tra i pannelli dei messaggi impostando il colore di sfondo del margine
        messagePanel.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createMatteBorder(0, 0, spazioTraPannelli, 0, Color.WHITE),
                BorderFactory.createLineBorder(marcello, 1)
        ));

//        int margine = 5;
//        messagePanel.setBorder(BorderFactory.createCompoundBorder(
//
//                new EmptyBorder(margine, margine, margine, margine),
//                BorderFactory.createLineBorder(marcello, 1)
//        ));



        // Aggiungi un componente di testo al pannello dei messaggi
        JTextArea messageText = new JTextArea(messaggio.getContent());
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setEditable(false);

        // Allinea a sinistra se il mittente è diverso dall'utente corrente, altrimenti allinea a destra
        if (messaggio.getSender().equals(username)) {
            messageText.setAlignmentX(Component.LEFT_ALIGNMENT);
        } else {
            messageText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        // Aggiungi il componente di testo al pannello dei messaggi
        messagePanel.add(messageText, BorderLayout.CENTER);

        // Allinea a sinistra se il mittente è diverso dall'utente corrente, altrimenti allinea a destra
        if (messaggio.getSender().equals(username)) {
            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        } else {
            messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        // Aggiungi il componente di testo al pannello dei messaggi
        messagePanel.add(messageText, BorderLayout.CENTER);


        return messagePanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}
