package pl.codewise.internships;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MessageQueueLogicTest {

    @Test
    public void snapshot() throws InterruptedException {
        MessageQueue messageQueue = new MessageQueueImpl();
        Message m1 = new Message("user1", 200);
        Message m2 = new Message("user2", 400);
        Message m3 = new Message("user3", 500);
        messageQueue.add(m1);
        messageQueue.add(m2);
        messageQueue.add(m3);

        Snapshot snapshot = messageQueue.snapshot();
        List<Message> messageList = Arrays.asList(snapshot.getMessages());
        assertTrue(messageList.contains(m1));
        assertTrue(messageList.contains(m2));
        assertTrue(messageList.contains(m3));
    }

    @Test
    public void numberOfErrorMessages() throws InterruptedException {
        MessageQueue messageQueue = new MessageQueueImpl();
        messageQueue.add(new Message("user1", 200));
        messageQueue.add(new Message("user2", 400));
        messageQueue.add(new Message("user3", 500));

        long errorMessagesAmount = messageQueue.numberOfErrorMessages();
        assertEquals(2, errorMessagesAmount);
    }
}