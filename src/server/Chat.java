package server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Chat
 */
public class Chat implements Serializable{

    private int id;
    private ArrayList<String> users;
    private ArrayList<Message> messages;
    private boolean group;

    public Chat(int id, ArrayList<String> users, ArrayList<Message> messages, boolean group){
        this.id = id;
        this.users = users;
        this.messages = messages;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public boolean isGroup() {
        return group;
    }

    @Override
    public String toString(){
        return "" + getId() + " " + getMessages() + getUsers(); 
    }
}