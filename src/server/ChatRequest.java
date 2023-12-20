package server;

import java.io.Serializable;

public class ChatRequest implements Serializable{
    public static final int LOAD_CHATS = 1;
    public static final int WRITE_MESSAGE = 2;
    public static final int LOAD_MESSAGES = 3;
    public static final int AUTH = 4;
    public static final int WRITE_CHATS = 5;

    public static final int GET_SIZE = 6;
    public static final int CHECK_USER = 7;
    public static final int MANAGE_USERS = 8;

    public static final int WRITE_USER = 9;
    private User user;
    private int chatId;
    private Message message;
    private int code;

    private Chat chat;

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

    public ChatRequest(int code, Chat c){
        this.chat = c;
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

    public Chat getChat() {
        return chat;
    }
}
