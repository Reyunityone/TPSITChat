package server;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Message
 */
public class Message implements Serializable{

    private String content;
    private String sender;
    private GregorianCalendar time;

    public Message(String content, String sender){
        this.content = content;
        this.sender = sender;
        this.time = new GregorianCalendar();
    }

    public Message(String content, String sender, GregorianCalendar messageTime){
        this.content = content;
        this.sender = sender;
        this.time = messageTime;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public GregorianCalendar getTime() {
        return time;
    }

    public void setTime(GregorianCalendar time) {
        this.time = time;
    }
}