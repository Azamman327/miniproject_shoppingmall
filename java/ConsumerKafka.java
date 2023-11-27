import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

//미완성
public class ConsumerKafka {

    public static void main(String[] args) {

        Properties configs = new Properties();
        configs.put("bootstrap.servers", "localhost:9092");
        configs.put("session.timeout.ms", "10000");
        configs.put("group.id", "shoppingmall-kafka");
        configs.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList("shoppingmall-kafka"));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("offset: " + record.offset() + ", " + "value: " + record.value());
                    LocalDateTime now = LocalDateTime.now();
                    String fileName = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH"));

                    try {
                        File file = new File("C:\\shoppingmall-log\\", fileName + "H.txt");
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                        bw.write(record.value());
                        bw.newLine();

                        bw.flush();
                        bw.close();
                    }catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            consumer.close();
        }
    }
}