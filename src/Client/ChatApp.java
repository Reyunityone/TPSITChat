package Client;
import javax.swing.*;
import java.awt.*;

public class ChatApp extends JFrame {

    private JPanel chatPanel;
    private JScrollPane scrollPane;

    public ChatApp() {
        // Impostazioni per la finestra principale
        setTitle("Chat App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // Creazione del pannello della chat
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        // Aggiunta di messaggi di esempio
        addMessage("Utente 1", "Ciao!");
        addMessage("Utente 2", "Salve!");
        addMessage("Utente 1", "Ciao!");
        addMessage("Utente 2", "Salve!");
        addMessage("Utente 1", "Ciao!");
        addMessage("Utente 2", "Salve!");
        addMessage("Utente 1", "Ciao!");
        addMessage("Utente 2", "Salve!");


        // Creazione dello JScrollPane con il pannello della chat
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Aggiunta dello JScrollPane alla finestra principale
        add(scrollPane);

        // Impostazioni finali per la finestra
        setVisible(true);
    }

    // Metodo per aggiungere un messaggio al pannello della chat
    private void addMessage(String sender, String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());

        JLabel senderLabel = new JLabel(sender + ": ");
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);

        messagePanel.add(senderLabel, BorderLayout.WEST);
        messagePanel.add(messageArea, BorderLayout.CENTER);

        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatApp());
    }
}