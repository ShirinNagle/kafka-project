import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Application {

    private static final String TOPIC = "suspicious-transactions";//change topic to correct name
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) {
        //*****************
        // YOUR CODE HERE
        //*****************
        Transaction transaction = new Transaction();
        Application kafkaConsumerApp = new Application();

        String consumerGroup = "user-notification";
        if (args.length == 1) {//if user passes in cmd line parameter use this otherwise use default above
            consumerGroup = args[0];
        }

        System.out.println("Consumer is part of consumer group " + consumerGroup);

        Consumer<String, Transaction> kafkaConsumer = kafkaConsumerApp.createKafkaConsumer(BOOTSTRAP_SERVERS, consumerGroup);

        kafkaConsumerApp.consumeMessages(TOPIC, kafkaConsumer);

        sendUserNotification(transaction);
    }

    public static void consumeMessages(String topic, Consumer<String, Transaction> kafkaConsumer) {
        //*****************
        // YOUR CODE HERE
        //*****************

        // COMPLETE THIS METHOD
        kafkaConsumer.subscribe(Collections.singletonList(topic));

        while (true) {
            ConsumerRecords<String, Transaction> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            if (consumerRecords.isEmpty()) {

            }
            for (ConsumerRecord<String, Transaction> record : consumerRecords) {
                sendUserNotification(record.value());
                //System.out.println(String.format("Received record (key: %s, value: %s, partition: %d, offset: %d, From Topic: %s",
                //record.key(),record.value(), record.partition(), record.offset(), record.topic()));
                System.out.println(String.format(" partition: %d, offset: %d, From Topic: %s", record.partition(), record.offset(), record.topic()));
            }
            kafkaConsumer.commitAsync();
        }


    }

    public static Consumer<String, Transaction> createKafkaConsumer(String bootstrapServers, String consumerGroup) {
        //*****************
        // YOUR CODE HERE
        //*****************
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);//how the consumer can access the cluster
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Transaction.TransactionDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//turning auto commit off.

        return new KafkaConsumer<>(properties);
    }

    private static void sendUserNotification(Transaction transaction) {
        // Print transaction information to the console
        //*****************
        // YOUR CODE HERE
        //decided not to use the overwritten toString() in Transaction as I want to print more information to the console.
        System.out.print("Sending user: " + transaction.getUser() + " notification about a suspicious transaction of: "
                + transaction.getAmount() + " Transaction originates in: " + transaction.getTransactionLocation() + " the reason for " +
                "flagging the transaction as suspicious is transaction origin does not match customer location on file");
    }

}
