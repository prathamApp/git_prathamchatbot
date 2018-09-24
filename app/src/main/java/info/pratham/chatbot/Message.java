package info.pratham.chatbot;

public class Message {

    protected String message;
    protected String senderName;

    public Message(String message, String senderName) {
        this.message = message;
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
