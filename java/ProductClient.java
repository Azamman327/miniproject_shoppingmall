import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.apache.kafka.clients.producer.KafkaProducer;
import shoppingmall.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collection;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductClient {
    private static final Logger logger = Logger.getLogger(ProductClient.class.getName());

    private static ProductInfoGrpc.ProductInfoBlockingStub blockingStub;

    static String user = "somsom";

    //함수 안에 넣어보기
    private static void sendToKafkaConsumer(String message) {
        Properties configs = new Properties();
        configs.put("bootstrap.servers", "localhost:9092");
        configs.put("acks", "all");
        configs.put("block.on.buffer.full", "true");
        configs.put("compression.type", "gzip");
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);
        producer.send(new ProducerRecord<String, String>("shoppingmall-kafka", message));

        producer.flush();
        producer.close();
    }

     private static void getCategories() throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();

        try {
            blockingStub = ProductInfoGrpc.newBlockingStub(channel);
            CategoryList response;
            try {
                response = blockingStub.getCategories(Empty.newBuilder().build());
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "getCategories failed: {0}", e.getStatus());
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
            String message = "[ " + formatedNow + " ]: " + user + " get category list";
            sendToKafkaConsumer(message);

            for (Category category : response.getCategoryList())
                System.out.println("ID: " + category.getCategoryId() + ", name: " + category.getName());
            System.out.println("----------------------");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
    private static void findByCategory(int categoryId) throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();

        try {
            blockingStub = ProductInfoGrpc.newBlockingStub(channel);

            CategoryId request = CategoryId.newBuilder().setValue(categoryId).build();
            ProductList response;
            try {
                response = blockingStub.findByCategory(request);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "findByCategory failed: {0}", e.getStatus());
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
            String message = "[ " + formatedNow + " ]: " + user + " find (" + response.getProductCount() +
                    ") items in category (" + categoryId + ")";
            sendToKafkaConsumer(message);

            for (Product product : response.getProductList())
                System.out.println("ID: " + product.getProductId()
                        + ", categoryID: " + product.getCategoryId()
                        + ", productName: " + product.getProductName()
                        + ", price: " + product.getPrice()
                        + ", count: " + product.getCount()
                );
            System.out.println("----------------------");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void buyProduct(int productId, int count) throws InterruptedException {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();

        try {
            blockingStub = ProductInfoGrpc.newBlockingStub(channel);

            ProductId request1 = ProductId.newBuilder().setProductId(productId).build();
            Product response1;
            try {
                response1 = blockingStub.getProduct(request1);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "getProduct failed while execute buyProduct: {0}", e.getStatus());
                return;
            }

            Buy request2 = Buy.newBuilder()
                    .setProduct(response1)
                    .setCount(count)
                    .build();
            Empty response2;
            try {
                response2 = blockingStub.buyProduct(request2);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "buyProduct failed: {0}", e.getStatus());
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
            String message = "[ " + formatedNow + " ]: " + user + " buy " + response1.getProductName()
                    + " (" + count + ") paid (" + response1.getPrice() * count + ")";
            sendToKafkaConsumer(message);

            System.out.println("buy product successfully...");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void getPurchaseList() throws Exception {
        ManagedChannel channel = Grpc.newChannelBuilder("localhost:50051", InsecureChannelCredentials.create())
                .build();

        try {
            blockingStub = ProductInfoGrpc.newBlockingStub(channel);
            PurchaseList response;
            try {
                response = blockingStub.getPurchaseList(Empty.newBuilder().build());
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "getPurchaseList failed: {0}", e.getStatus());
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
            String message = "[ " + formatedNow + " ]: " + user + " get (" + response.getPurchaseCount() + ") purchase history";
            sendToKafkaConsumer(message);

            for (Purchase purchase : response.getPurchaseList())
                System.out.println("ID: " + purchase.getPurchaseId()
                        + ", productId: " + purchase.getProductId()
                        + ", productName: " + purchase.getProductName()
                        + ", purchase amount: " + purchase.getCount()
                        + ", total price: " + purchase.getTotalPrice());
            System.out.println("----------------------");
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
    public static int menu() throws Exception {
        Scanner sc = new Scanner(System.in);
        int selected;

        System.out.println("Select the menu...");
        System.out.println("1 : View category list");
        System.out.println("2 : Search by category");
        System.out.println("3 : Buy products");
        System.out.println("4 : View purchase history");
        System.out.println("0 : Exit program");
        selected = sc.nextInt();

        switch(selected) {
            case 1:
                getCategories();
                break;
            case 2:
                int categoryId;

                System.out.println("Enter the category id...: ");
                categoryId = sc.nextInt();
                findByCategory(categoryId);
                break;
            case 3:
                int productId;
                int count;

                System.out.println("Enter the product id...: ");
                productId = sc.nextInt();
                System.out.println("Enter the count...: ");
                count = sc.nextInt();
                buyProduct(productId, count);
                break;
            case 4:
                getPurchaseList();
                break;
            case 0:
                System.out.println("Exit System...");

                LocalDateTime now = LocalDateTime.now();
                String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
                String message = "[ " + formatedNow + " ]: " + user + " exit shoppingmall program";
                sendToKafkaConsumer(message);

                break;
        }
        return selected;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Start shoppingmall program...");

        LocalDateTime now = LocalDateTime.now();
        String formatedNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd, HH:mm:ss"));
        String message = "[ " + formatedNow + " ]: " + user + " access shoppingmall program";
        sendToKafkaConsumer(message);

        int selected = -1;
        while (selected != 0) {
            selected = menu();
        }
    }
}
