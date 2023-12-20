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
import java.util.*;
import java.util.concurrent.Semaphore;

public class ChatFrame extends JFrame {
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
    private JScrollPane messageScroll;

    private final Semaphore inSemaforo = new Semaphore(1);

    private final String username;
    private final JFrame frame;
    private ObjectOutputStream out;
    private ObjectInputStream in ;
    Socket client;
    private ArrayList < Message > messaggi = new ArrayList < > ();
    private ArrayList < Message > currentMessages = new ArrayList < Message > ();
    private ChatPanel currentChat = null;
    public ChatFrame(String username) {
        gianfranco.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gianpiero.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.username = username;
        ArrayList < Chat > chats = new ArrayList < Chat > ();
        try {
            client = new Socket("localhost", 5000);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            //Auth request
            System.out.println(in.readObject());
            inSemaforo.acquire();
            out.writeObject(new ChatRequest(ChatRequest.AUTH, new User(username, null)));
            out.flush();
            //Response

            String response = (String) in.readObject();
            if (response.equalsIgnoreCase("authentication error")) JOptionPane.showMessageDialog(null, response);
            inSemaforo.release();
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            inSemaforo.acquire();
            out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
            out.flush();
            chats = (ArrayList < Chat > ) in.readObject();
            inSemaforo.release();
            System.out.println(chats);
        } catch (Exception e) {
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

        for (Chat c: chats) {
            contenitoreContatti.add(createPanel(c));
        }
        messageArea.setLineWrap(true); // Per andare a capo alla fine della riga
        messageArea.setWrapStyleWord(true); // Per andare a capo solo tra le parole

        // Creazione di uno JScrollPane e aggiunta della JTextArea

        messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Impostazione della dimensione massima desiderata per evitare l'allungamento
        messageArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Aggiunta del JScrollPane al contenuto del frame

        //###########################
        frame.setVisible(true);
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login marco = new login();
                frame.setVisible(false);
                try {
                    client.close();
                } catch (Exception error) {
                    System.err.println(error);
                }
                frame.dispose();
            }
        });

        newChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList < User > prova = new ArrayList < User > ();
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
                    } else if (!addedUser.equals(username)) {

                        ArrayList < String > users = new ArrayList < > ();
                        users.add(username);
                        users.add(addedUser);
                        inSemaforo.acquire();
                        out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                        out.flush();
                        ArrayList < Chat > currentChats = (ArrayList < Chat > ) in.readObject();
                        inSemaforo.release();
                        boolean isPresent = isPresent(currentChats, users);
                        if (!isPresent) {
                            inSemaforo.acquire();
                            out.writeObject(new ChatRequest(ChatRequest.GET_SIZE, (User) null));
                            out.flush();
                            inSemaforo.release();
                            int newId = (int) in.readObject();
                            Chat chat = new Chat(newId, users, new ArrayList < Message > (), false);
                            chat.getMessages().add(new Message(username + " ha creato la chat.", username));
                            for (String s: chat.getUsers()) {
                                if (!s.equalsIgnoreCase(username)) chat.getMessages().add(new Message(s + " ora partecipa alla chat.", s));
                            }
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
                        } else {
                            JOptionPane.showMessageDialog(null, "Chat già esistente");
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Non puoi chattare con te stesso.");
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            private boolean isPresent(ArrayList < Chat > currentChats, ArrayList < String > addingUsers) {
                boolean isPresent = false;
                for (Chat c: currentChats) {

                    addingUsers.sort(new Comparator < String > () {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    ArrayList < String > chatUsers = c.getUsers();
                    chatUsers.sort(new Comparator < String > () {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    System.out.println("is present >>>>" + chatUsers);
                    System.out.println("is present >>>>" + addingUsers);
                    isPresent = addingUsers.equals(chatUsers);
                    if (isPresent) break;
                }
                return isPresent;
            }
        });

        inviaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = messageArea.getText();
                if (!messageContent.isBlank()) {
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

                } else {
                    JOptionPane.showMessageDialog(null, "Non puoi inviare un messaggio vuoto");
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception marco) {}
                Point currentViewPosition = gianpiero.getViewport().getViewPosition();
                Point newViewPosition = new Point(currentViewPosition.x, currentViewPosition.y + 100000);
                gianpiero.getViewport().setViewPosition(newViewPosition);
            }

        });
        Thread reload = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    if (currentChat != null) {

                        ChatRequest request = new ChatRequest(ChatRequest.LOAD_MESSAGES, currentChat.getChatId());
                        messaggi = new ArrayList < > ();
                        try {
                            inSemaforo.acquire();
                            out.writeObject(request);
                            messaggi = (ArrayList < Message > ) in.readObject();
                            inSemaforo.release();
                        } catch (Exception ex) {
                            System.err.println(ex);
                        }

                        for (Message prova: messaggi) {
                            System.out.println(prova.getContent());
                        }
                        if (currentMessages.size() != messaggi.size()) {

                            contenitoreMessaggi.removeAll();
                            for (Message m: messaggi) {
                                // Rinomina la variabile locale del pannello
                                JPanel messagePanel = createMessagePanel(m, messaggi.size());
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
                            Component[] components = contenitoreMessaggi.getComponents();
                            int altezza = 0;
                            for (int i = 0; i < components.length; i++) {
                                JPanel panel = (JPanel) components[i];
                                altezza = altezza + panel.getPreferredSize().height;
                            }
                            if (altezza < 570) {
                                contenitoreMessaggi.setPreferredSize(new Dimension(-1, 570));
                            } else {
                                contenitoreMessaggi.setPreferredSize(new Dimension(-1, altezza + 5));
                            }
                        }
                        currentMessages = messaggi;

                    }
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });
        reload.start();
        cercatopo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList < User > prova = new ArrayList < User > ();
                    String addedUser = "";
                    addedUser = textGroup.getText();
                    textGroup.setText("");
                    StringTokenizer tokenizer = new StringTokenizer(addedUser, ",");
                    ArrayList < String > confronto = new ArrayList < String > ();
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (token.equalsIgnoreCase(username)) {
                            JOptionPane.showMessageDialog(null, "Non includere te stesso");
                            return;
                        }
                        confronto.add(token);
                    }

                    CredentialsHandler gestioneCred = new CredentialsHandler();
                    prova = gestioneCred.readCredentials();

                    for (String s: confronto) {
                        boolean found = false;
                        for (User u: prova) {
                            if (u.getUsername().equalsIgnoreCase(s)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            JOptionPane.showMessageDialog(null, "Utente non trovato: " + s);
                            return;
                        }
                    }
                    inSemaforo.acquire();
                    out.writeObject(new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null)));
                    out.flush();
                    ArrayList < Chat > currentChats = (ArrayList < Chat > ) in.readObject();
                    inSemaforo.release();
                    confronto.add(username);
                    boolean presente = this.isPresent(currentChats, confronto);
                    if (!presente) {
                        inSemaforo.acquire();
                        out.writeObject(new ChatRequest(ChatRequest.GET_SIZE, (User) null));
                        out.flush();
                        inSemaforo.release();
                        int newId = (int) in.readObject();
                        Chat chat = new Chat(newId, confronto, new ArrayList < Message > (), false);
                        chat.getMessages().add(new Message(username + " ha creato la chat.", username));
                        for (String s: chat.getUsers()) {
                            if (!s.equalsIgnoreCase(username)) chat.getMessages().add(new Message(s + " ora partecipa alla chat.", s));
                        }
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
                    } else {
                        JOptionPane.showMessageDialog(null, "Chat già esistente");
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
            private boolean isPresent(ArrayList < Chat > currentChats, ArrayList < String > addingUsers) {
                boolean isPresent = false;
                for (Chat c: currentChats) {

                    addingUsers.sort(new Comparator < String > () {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    ArrayList < String > chatUsers = c.getUsers();
                    chatUsers.sort(new Comparator < String > () {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    System.out.println("is present >>>>" + chatUsers);
                    System.out.println("is present >>>>" + addingUsers);
                    isPresent = addingUsers.equals(chatUsers);
                    if (isPresent) break;
                }
                return isPresent;
            }
        });

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
        new ChatFrame(null);
    }

    private ChatPanel createPanel(Chat c) {

        ChatPanel panel = new ChatPanel(c.getId());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Utilizza BoxLayout con orientamento verticale
        int num = 0;
        try {
            inSemaforo.acquire();
            ChatRequest request = new ChatRequest(ChatRequest.LOAD_CHATS, new User(username, null));
            out.writeObject(request);
            out.flush();
            ArrayList < Chat > a = (ArrayList < Chat > ) in.readObject();
            num = a.size();
            inSemaforo.release();
        } catch (Exception e) {}

        int totale = 0;
        int finale = (num * 65);
        if (finale < 625) {
            totale = 625;
        } else {
            totale = finale;
        }

        contenitoreContatti.setPreferredSize(new Dimension(-1, totale));
        contenitoreContatti.setMinimumSize(new Dimension(-1, totale));
        contenitoreContatti.setMaximumSize(new Dimension(-1, totale));

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
                    for (Component component: currentSelectedPanel.getComponents()) {
                        if (component instanceof JLabel) {
                            ((JLabel) component).setForeground(Color.black);
                            break; // Trovata la JLabel, esci dal loop
                        }
                    }
                }

                // Imposta il colore del pannello corrente
                Color sender = new Color(164, 54, 242);
                panel.setBackground(sender);

                // Cerca la JLabel all'interno del pannello e imposta il colore del testo
                for (Component component: panel.getComponents()) {
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
                    messaggi = new ArrayList < > ();
                    try {
                        inSemaforo.acquire();
                        out.writeObject(request);
                        messaggi = (ArrayList < Message > ) in.readObject();
                        inSemaforo.release();
                        System.out.println(messaggi);
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }

                    for (Message prova: messaggi) {
                        System.out.println(prova.getContent());
                    }
                    for (Message m: messaggi) {
                        // Rinomina la variabile locale del pannello
                        JPanel messagePanel = createMessagePanel(m, messaggi.size());
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
                Component[] components = contenitoreMessaggi.getComponents();
                int altezza = 0;
                for (int i = 0; i < components.length; i++) {
                    JPanel panel = (JPanel) components[i];
                    altezza = altezza + panel.getPreferredSize().height;
                }
                if (altezza < 570) {
                    contenitoreMessaggi.setPreferredSize(new Dimension(-1, 570));
                } else {
                    contenitoreMessaggi.setPreferredSize(new Dimension(-1, altezza + 5));
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception marco) {}
                Point currentViewPosition = gianpiero.getViewport().getViewPosition();
                Point newViewPosition = new Point(currentViewPosition.x, currentViewPosition.y + 100000);
                gianpiero.getViewport().setViewPosition(newViewPosition);
            }
        });
        return panel;
    }

    private JLabel getjLabel(Chat c) {
        Font customFont = new Font("Inter Semi Bold", Font.BOLD, 22);
        ArrayList < String > users = c.getUsers();
        String usersString = "";
        int i = 0;
        for (String user: users) {
            if (!user.equals(username)) {
                if (i == 0) {
                    usersString += user;
                } else {
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

    private JPanel createMessagePanel(Message messaggio, int num) {

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        //        int totale = 0;
        //        int finale = (num * 81);
        //        if (finale < 570){
        //            totale = 570;
        //        }else{
        //            totale = finale;
        //        }
        //        contenitoreMessaggi.setPreferredSize(new Dimension(-1,totale));
        //        contenitoreMessaggi.setMinimumSize(new Dimension(-1,totale));
        //        contenitoreMessaggi.setMaximumSize(new Dimension(-1,totale));
        Color marcello = new Color(205, 146, 255);
        messagePanel.setPreferredSize(new Dimension(400, 80));
        messagePanel.setMaximumSize(new Dimension(400, 80));
        messagePanel.setMinimumSize(new Dimension(400, 80));

        Color sender = new Color(164, 54, 242);
        Color senderBG = new Color(225, 225, 234);

        int spazioTraPannelli = 5;

        StringTokenizer st = new StringTokenizer(messaggio.getContent());
        int counter = 0;
        int righe = 1;
        int numParole = 0;
        int numTokens = st.countTokens();
        String messaggioFinale = "";
        while (st.hasMoreTokens()) {
            String lunghezza = st.nextToken();
            counter = counter + lunghezza.length();
            if (counter >= 36 && numTokens == 1) {
                messaggioFinale = messaggioFinale + lunghezza + "\n";
                righe = lunghezza.length() / 36;
            } else if (counter >= 36) {
                messaggioFinale = messaggioFinale + "\n" + lunghezza;
                righe++;
                counter = 0;
            } else if (!(numParole == 0)) {
                messaggioFinale = messaggioFinale + " " + lunghezza;
                counter++;
            } else {
                messaggioFinale = messaggioFinale + lunghezza;
            }
            if (lunghezza.length() >= 36 && numParole != 0) {
                righe += lunghezza.length() / 36;
            }
            numParole++;
        }

        int height = righe * 21;

        // Aggiungi un componente di testo per il contenuto del messaggio
        JTextArea messageText = new JTextArea(messaggioFinale);
        Font customFont = new Font("Inter Semi Bold", Font.BOLD, 16);
        messageText.setFont(customFont);
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setEditable(false);

        int width = 400;

        messageText.setMinimumSize(new Dimension(width, height));
        messageText.setPreferredSize(new Dimension(width, height));
        messageText.setMaximumSize(new Dimension(width, height));

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

            timestampPanel.add(Box.createHorizontalGlue());

            String stringa = "";
            GregorianCalendar calendario = messaggio.getTime();
            int ora = calendario.get(GregorianCalendar.HOUR_OF_DAY);
            int minuti = calendario.get(GregorianCalendar.MINUTE);
            stringa = (ora < 10) ? "0" + ora + ":" : ora + ":";
            stringa = (minuti < 10) ? stringa + "0" + minuti + " " : stringa + minuti + " ";
            JLabel ciaoLabel = new JLabel(stringa);
            ciaoLabel.setFont(new Font("Inter Semi Bold", Font.BOLD, 13));
            timestampPanel.add(ciaoLabel);

            messagePanel.add(timestampPanel, BorderLayout.PAGE_START);

        } else {
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, spazioTraPannelli, 0, Color.white),
                    BorderFactory.createLineBorder(Color.black, 3)
            ));

            messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            messageText.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JPanel timestampPanel = new JPanel();
            timestampPanel.setLayout(new BoxLayout(timestampPanel, BoxLayout.X_AXIS));
            timestampPanel.add(Box.createHorizontalStrut(5)); // Spazio a sinistra
            JLabel timestampLabel = new JLabel(messaggio.getSender() + ":");
            timestampLabel.setFont(new Font("Inter Semi Bold", Font.BOLD, 13));
            timestampPanel.setBackground(senderBG);
            timestampLabel.setForeground(Color.black);
            timestampPanel.add(timestampLabel);

            timestampPanel.add(Box.createHorizontalGlue());

            String stringa = "";
            GregorianCalendar calendario = messaggio.getTime();
            int ora = calendario.get(GregorianCalendar.HOUR_OF_DAY);
            int minuti = calendario.get(GregorianCalendar.MINUTE);
            stringa = (ora < 10) ? "0" + ora + ":" : ora + ":";
            stringa = (minuti < 10) ? stringa + "0" + minuti + " " : stringa + minuti + " ";
            JLabel ciaoLabel = new JLabel(stringa);
            ciaoLabel.setFont(new Font("Inter Semi Bold", Font.BOLD, 13));
            timestampPanel.add(ciaoLabel);

            messagePanel.add(timestampPanel, BorderLayout.PAGE_START);

        }

        // Aggiungi il componente di testo al pannello dei messaggi nella regione centrale
        messagePanel.add(messageText, BorderLayout.CENTER);

        messagePanel.setPreferredSize(new Dimension(400, height + 50));
        messagePanel.setMaximumSize(new Dimension(400, height + 50));
        messagePanel.setMinimumSize(new Dimension(400, height + 50));

        return messagePanel;
    }

}