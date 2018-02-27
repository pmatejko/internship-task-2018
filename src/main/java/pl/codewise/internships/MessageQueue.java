package pl.codewise.internships;

public interface MessageQueue {

    void add(Message message) throws InterruptedException;

    Snapshot snapshot() throws InterruptedException;

    long numberOfErrorMessages() throws InterruptedException;
}
