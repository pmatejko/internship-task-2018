package pl.codewise.internships;


public class Snapshot {
    private final Message[] messages;

    public Snapshot(Message[] messages) {
        this.messages = messages;
    }

    public Message[] getMessages() {
        return messages;
    }
}
