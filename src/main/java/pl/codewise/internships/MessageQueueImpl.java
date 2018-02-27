package pl.codewise.internships;

import java.util.concurrent.Semaphore;

public class MessageQueueImpl implements MessageQueue {
    private static final long MAX_MESSAGE_AGE_MILLI = 1000 * 60 * 5;
    private Message[] messageArray = new Message[100];
    private Semaphore messageArraySemaphore = new Semaphore(1);
    private int insertInto = 0;
    private int takeFrom = 0;

    public void add(Message message) throws InterruptedException {
        messageArraySemaphore.acquire();

        messageArray[insertInto] = message;
        insertInto = (insertInto + 1) % messageArray.length;
        if (insertInto == takeFrom)
            takeFrom = (takeFrom + 1) % messageArray.length;

        messageArraySemaphore.release();
    }

    private void removeTooOldMessages() {
        while (messageArray[takeFrom] != null && messageArray[takeFrom].isOlderThan(MAX_MESSAGE_AGE_MILLI)) {
            messageArray[takeFrom] = null;
            takeFrom = (takeFrom + 1) % messageArray.length;
        }
    }

    public Snapshot snapshot() throws InterruptedException {
        messageArraySemaphore.acquire();

        removeTooOldMessages();
        int snapshotSize = takeFrom > insertInto ? messageArray.length - takeFrom + insertInto : insertInto - takeFrom;
        Message[] snapshotArray = new Message[snapshotSize];

        int i = 0, readFrom = takeFrom;
        while (readFrom != insertInto) {
            snapshotArray[i] = messageArray[readFrom];
            readFrom = (readFrom + 1) % messageArray.length;
            ++i;
        }

        messageArraySemaphore.release();
        return new Snapshot(snapshotArray);
    }

    public long numberOfErrorMessages() throws InterruptedException {
        messageArraySemaphore.acquire();

        removeTooOldMessages();
        long errorMessagesAmount = 0;

        int readFrom = takeFrom;
        while (readFrom != insertInto) {
            int errorCode = messageArray[readFrom].getErrorCode();
            if (errorCode >= 400 && errorCode < 600)
                errorMessagesAmount++;
            readFrom = (readFrom + 1) % messageArray.length;
        }

        messageArraySemaphore.release();
        return errorMessagesAmount;
    }
}
