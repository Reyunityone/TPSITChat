package server;

import java.io.Serializable;

public class ChatRequest implements Serializable{
    public static final int LOAD_CHATS = 1;
    public static final int WRITE_MESSAGE = 2;
    public static final int LOAD_MESSAGES = 3;
    private User user;
    private String chatId;
    private Message message;
    private int code;

    public ChatRequest(int code,User u){
        this.user = u;
        this.code = code;
    }

    public ChatRequest(int code,User u, Message m, String chatId){
        this.user =u;
        this.message = m;
        this.chatId = chatId;
        this.code = code;
    }
    
    public String getChatId() {
        return chatId;
    }
    public Message getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
    
    public int getCode() {
        return code;
    }
    
}
