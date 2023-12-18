package Client;

import server.*;

import javax.swing.*;
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
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class ChatFrame extends JFrame{
    private ChatPanel currentSelectedPanel;
    private JPanel panel1;
    private JTextArea messageArea;
    private JTextField textGroup;
    private JLabel bentornato;
    private JButton inviaButton;
    private JButton logout;
    private JButton cercatopo;
    private JButton newChat;
    private JTextField newChatText;
    private JPanel contenitoreContatti;
    private JScrollPane gianfranco;
    private JPanel contenitoreMessaggi;
    private JScrollPane gianpiero;

    private final Semaphore inSemaforo = new Semaphore(1);

    private final String username;
    private final JFrame frame;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Socket client;
    private ArrayList<Message> messaggi = new ArrayList<>();

    private ChatPanel currentChat = null;
    public ChatFrame(String username) {
        this.username = username;
        ArrayList<Chat> chats = new ArrayList<Chat>();
        try{
            client = new Socket("localhost", 5000);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            //Auth request
            System.out.println(in.readObject());
            inSemaforo.acquire();
            out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(username, null)));
            out.flush();
            //Response
            
            String response = (String)in.readObject();
            if(response.equalsIgnoreCase("authentication error")) JOptionPane.showMessageDialog(null, response);
            inSemaforo.release();
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            inSemaforo.acquire();
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
            out.flush();
            chats = (ArrayList<Chat>) in.readObject();
            inSemaforo.release();
            System.out.println(chats);
        }
        catch(Exception e){
            System.err.println(e);
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
                    newChatText.setText("");
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
                        inSemaforo.acquire();
                        out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                        out.flush();
                        ArrayList<Chat> currentChats = (ArrayList<Chat>) in.readObject();
                        inSemaforo.release();
                        boolean isPresent = isPresent(currentChats, users);
                        if(!isPresent){
                            Chat chat = new Chat(currentChats.size(), users, new ArrayList<Message>(), false);
                            ChatRequest request = new ChatRequest(ChatRequest.WRITE_CHATS, chat);
                            out.writeObject(request);
                            out.flush();
                            JPanel panel = createPanel(chat);
                            contenitoreContatti.add(panel);

                            // Ottieni la posizione corrente
                            Point currentViewPosition = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition = new Point(currentViewPosition.x, currentViewPosition.y + 1);

                            // Imposta la nuova posizione della vista
                            gianfranco.getViewport().setViewPosition(newViewPosition);

                            // Ottieni la posizione corrente
                            Point currentViewPosition1 = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition1 = new Point(currentViewPosition1.x, currentViewPosition1.y - 1);

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

            private boolean isPresent(ArrayList<Chat> currentChats, ArrayList<String> addingUsers) {
                boolean isPresent = false;
                for (Chat c: currentChats){

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

        inviaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = messageArea.getText();
                messageArea.setText("");
                Message message = new Message(messageContent, username);
                User u = new User(username, null);
                ChatRequest request = new ChatRequest(ChatRequest.WRITE_MESSAGE, u, message, currentChat.getChatId());
                try {
                    out.writeObject(request);
                    System.out.println(messageContent);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        Thread reload = new Thread(() ->{
            try{
                while(true){
                    Thread.sleep(2000);
                    if(currentChat != null){
                        contenitoreMessaggi.removeAll();

                        ChatRequest request = new ChatRequest(ChatRequest.LOAD_MESSAGES, currentChat.getChatId());
                        messaggi = new ArrayList<>();
                        try{
                            inSemaforo.acquire();
                            out.writeObject(request);
                            messaggi = (ArrayList<Message>) in.readObject();
                            inSemaforo.release();
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
            }
            catch (Exception ex){
                System.err.println(ex);
            }
        });
        reload.start();
        cercatopo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<User> prova = new ArrayList<User>();
                    String addedUser = "";
                    addedUser = textGroup.getText();
                    textGroup.setText("");
                    StringTokenizer tokenizer = new StringTokenizer(addedUser, ",");
                    ArrayList<String> confronto = new ArrayList<String>();
                    while(tokenizer.hasMoreTokens()){
                        String token = tokenizer.nextToken();
                        if(token.equalsIgnoreCase(username)){
                            JOptionPane.showMessageDialog(null, "Non includere te stesso");
                            return;
                        }
                        confronto.add(token);
                    }

                    CredentialsHandler gestioneCred = new CredentialsHandler();
                    prova = gestioneCred.readCredentials();


                    for (String s : confronto){
                        boolean found = false;
                        for (User u : prova){
                            if(u.getUsername().equalsIgnoreCase(s)){
                                found = true;
                            }
                        }
                        if(!found){
                            JOptionPane.showMessageDialog(null, "Utente non trovato: " + s);
                            return;
                        }
                    }
                        inSemaforo.acquire();
                        out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                        out.flush();
                        ArrayList<Chat> currentChats = (ArrayList<Chat>) in.readObject();
                        inSemaforo.release();
                        confronto.add(username);
                        boolean presente = this.isPresent(currentChats, confronto);
                        if(!presente){
                            Chat chat = new Chat(currentChats.size(), confronto, new ArrayList<Message>(), false);
                            ChatRequest request = new ChatRequest(ChatRequest.WRITE_CHATS, chat);
                            out.writeObject(request);
                            out.flush();
                            JPanel panel = createPanel(chat);
                            contenitoreContatti.add(panel);

                            // Ottieni la posizione corrente
                            Point currentViewPosition = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition = new Point(currentViewPosition.x, currentViewPosition.y + 1);

                            // Imposta la nuova posizione della vista
                            gianfranco.getViewport().setViewPosition(newViewPosition);

                            // Ottieni la posizione corrente
                            Point currentViewPosition1 = gianfranco.getViewport().getViewPosition();

                            // Sposta di un pixel orizzontalmente e verticalmente
                            Point newViewPosition1 = new Point(currentViewPosition1.x, currentViewPosition1.y - 1);

                            // Imposta la nuova posizione della vista
                            gianfranco.getViewport().setViewPosition(newViewPosition1);
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Chat già esistente");
                        }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
            private boolean isPresent(ArrayList<Chat> currentChats, ArrayList<String> addingUsers) {
                boolean isPresent = false;
                for (Chat c: currentChats){

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

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        new ChatFrame(null);
    }

    private ChatPanel createPanel(Chat c) {
        ChatPanel panel = new ChatPanel(c.getId());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Utilizza BoxLayout con orientamento verticale

        JLabel label = getjLabel(c);
        panel.add(Box.createVerticalGlue()); // Aggiunge uno spaziatore verticale per centrare verticalmente
        panel.add(label);
        panel.add(Box.createVerticalGlue()); // Aggiunge un altro spaziatore verticale per centrare verticalmente

        Color marcello = new Color(205, 146, 255);
        Color bianco = new Color(255, 255, 255);
        panel.setBackground(bianco);
        panel.setPreferredSize(new Dimension(298, 65));
        panel.setBorder(BorderFactory.createLineBorder(marcello, 1));

        // Aggiungi un MouseListener al pannello
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ripristina il colore del pannello precedente (se ce n'è uno)
                if (currentSelectedPanel != null) {
                    currentSelectedPanel.setBackground(bianco);

                    // Cerca la JLabel all'interno del pannello e imposta il colore del testo
                    for (Component component : currentSelectedPanel.getComponents()) {
                        if (component instanceof JLabel) {
                            ((JLabel) component).setForeground(Color.black);
                            break; // Trovata la JLabel, esci dal loop
                        }
                    }
                }

                // Imposta il colore del pannello corrente
                Color sender = new Color(164,54,242);
                panel.setBackground(sender);

                // Cerca la JLabel all'interno del pannello e imposta il colore del testo
                for (Component component : panel.getComponents()) {
                    if (component instanceof JLabel) {
                        ((JLabel) component).setForeground(Color.white);
                        break; // Trovata la JLabel, esci dal loop
                    }
                }

                // Aggiorna il pannello corrente selezionato
                currentSelectedPanel = panel;

                contenitoreMessaggi.removeAll();

                contenitoreMessaggi.revalidate();

                contenitoreMessaggi.repaint();
                if (c != null) {
                    ChatPanel thispanel = (ChatPanel) e.getSource();
                    int id = thispanel.getChatId();
                    currentChat = thispanel;
                    System.out.println("Hai cliccato sul pannello con ID " + id);

                    ChatRequest request = new ChatRequest(ChatRequest.LOAD_MESSAGES, id);
                    messaggi = new ArrayList<>();
                    try{
                        inSemaforo.acquire();
                        out.writeObject(request);
                        messaggi = (ArrayList<Message>) in.readObject();
                        inSemaforo.release();
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

    private JLabel getjLabel(Chat c) {
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
        return label;
    }

    private JPanel createMessagePanel(Message messaggio) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        Color marcello = new Color(205, 146, 255);
        messagePanel.setPreferredSize(new Dimension(400, 65));
        messagePanel.setMaximumSize(new Dimension(400, 65));
        messagePanel.setMinimumSize(new Dimension(400, 65));

        Color sender = new Color(164, 54, 242);
        Color senderBG = new Color(225, 225, 234);

        int spazioTraPannelli = 5;

        // Aggiungi un componente di testo per il contenuto del messaggio
        JTextArea messageText = new JTextArea(messaggio.getContent());
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setEditable(false);

        // Aggiungi uno spazio tra i pannelli dei messaggi impostando il colore di sfondo del margine
        if (messaggio.getSender().equals(username)) {
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, spazioTraPannelli, 0, Color.white),
                    BorderFactory.createLineBorder(sender, 3)
            ));
            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            messageText.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel timestampPanel = new JPanel();
            timestampPanel.setLayout(new BoxLayout(timestampPanel, BoxLayout.X_AXIS));
            timestampPanel.add(Box.createHorizontalStrut(5)); // Spazio a sinistra
            JLabel timestampLabel = new JLabel(messaggio.getSender() + ":");
            timestampLabel.setFont(new Font("Inter Semi Bold", Font.BOLD, 13));
            timestampPanel.setBackground(senderBG);
            timestampLabel.setForeground(sender);
            timestampPanel.add(timestampLabel);
            messagePanel.add(timestampPanel, BorderLayout.PAGE_START);
        } else {
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, spazioTraPannelli, 0, Color.white),
                    BorderFactory.createLineBorder(Color.black, 3)
            ));
            JPanel timestampPanel = new JPanel();
            timestampPanel.setLayout(new BoxLayout(timestampPanel, BoxLayout.X_AXIS));
            timestampPanel.add(Box.createHorizontalStrut(5)); // Spazio a sinistra
            JLabel timestampLabel = new JLabel(messaggio.getSender() + ":");
            timestampLabel.setFont(new Font("Inter Semi Bold", Font.BOLD, 13));
            timestampPanel.setBackground(senderBG);
            timestampLabel.setForeground(Color.black);
            timestampPanel.add(timestampLabel);
            messagePanel.add(timestampPanel, BorderLayout.PAGE_START);
            messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            messageText.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        // Aggiungi il componente di testo al pannello dei messaggi nella regione centrale
        messagePanel.add(messageText, BorderLayout.CENTER);

        // nome contatto se gruppo

        Font customFont = new Font("Inter Semi Bold", Font.BOLD, 16);
        messageText.setFont(customFont);

        return messagePanel;
    }


}
