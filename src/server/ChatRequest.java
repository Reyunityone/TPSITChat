package server;

import java.io.Serializable;

public class ChatRequest implements Serializable{
    public static final int LOAD_CHATS = 1;
    public static final int WRITE_MESSAGE = 2;
    public static final int LOAD_MESSAGES = 3;
    public static final int AUTH = 4;
    private User user;
    private int chatId;
    private Message message;
    private int code;

    public ChatRequest(int code,User u){
        this.user = u;
        this.code = code;
    }

    public ChatRequest(int code,User u, Message m, int chatId){
        this.user =u;
        this.message = m;
        this.chatId = chatId;
        this.code = code;
    }

    public ChatRequest(int code, int chatId){
        this.code = code;
        this.chatId = chatId;
    }
    
    public int getChatId() {
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