package Client;

import javax.swing.*;

public class ChatPanel extends JPanel {
    private int chatId;
    public ChatPanel(int chatId){
        super();
        this.chatId = chatId;
    }

    public int getChatId() {
        return chatId;
    }
}
