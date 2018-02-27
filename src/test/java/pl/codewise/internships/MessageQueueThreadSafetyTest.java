package pl.codewise.internships;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class MessageQueueThreadSafetyTest {
    private static final Random RANDOM = new Random();
    private MessageQueue messageQueue;
    private AtomicLong totalErrorsAmount;
    private List<Thread> producers;
    private List<Thread> errorProducers;
    private List<Thread> consumers;

    private static class Producer extends Thread {
        public static final int MESSAGE_AMOUNT = 10;
        private MessageQueue messageQueue;
        private int errorCode;

        public Producer(MessageQueue messageQueue, int errorCode) {
            this.messageQueue = messageQueue;
            this.errorCode = errorCode;
        }

        @Override
        public void run() {
            for (int i = 0; i < MESSAGE_AMOUNT; i++) {
                try {
                    messageQueue.add(new Message(String.valueOf(RANDOM.nextInt()), errorCode));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class Consumer extends Thread {
        private MessageQueue messageQueue;
        private AtomicLong totalErrorsAmount;

        public Consumer(MessageQueue messageQueue, AtomicLong totalErrorsAmount) {
            this.messageQueue = messageQueue;
            this.totalErrorsAmount = totalErrorsAmount;
        }

        @Override
        public void run() {
            try {
                totalErrorsAmount.addAndGet(messageQueue.numberOfErrorMessages());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Before
    public void setup() {
        this.messageQueue = new MessageQueueImpl();
        this.totalErrorsAmount = new AtomicLong(0);

        producers = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            producers.add(new Producer(messageQueue, 200));
        }

        errorProducers = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            errorProducers.add(new Producer(messageQueue, 400));
        }

        consumers = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            consumers.add(new Consumer(messageQueue, totalErrorsAmount));
        }
    }

    @Test
    public void threadSafety() throws InterruptedException {
        for (Thread producer : producers) {
            producer.start();
        }
        for (Thread producer : producers) {
            producer.join();
        }

        for (Thread errorProducer : errorProducers) {
            errorProducer.start();
        }
        for (Thread errorProducer : errorProducers) {
            errorProducer.join();
        }

        for (Thread consumer : consumers) {
            consumer.start();
        }
        for (Thread consumer : consumers) {
            consumer.join();
        }

        assertEquals(errorProducers.size() * Producer.MESSAGE_AMOUNT * consumers.size(),
                totalErrorsAmount.get());
    }

}
