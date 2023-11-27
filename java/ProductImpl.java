import io.grpc.stub.StreamObserver;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import shoppingmall.*;

public class ProductImpl extends ProductInfoGrpc.ProductInfoImplBase {

    @Override
    public void getCategories(Empty request, StreamObserver<CategoryList> responseObserver) {
        List list = new ArrayList();
        CategoryList categoryList = CategoryList.newBuilder().build();
        try {
            Properties properties = new Properties();
            properties.put("user", "root");
            properties.put("password", "root");
            properties.put("characterEncoding", "UTF-8");
            properties.put("useUnicode", true);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall_db", properties);

            String sql = "select * from Category";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet resultSet = pstmt.executeQuery();
            for(int i = 0; resultSet.next(); i++) {
                int categoryId = resultSet.getInt(1);
                String categoryName = resultSet.getString(2);
                Category category = Category.newBuilder()
                        .setCategoryId(categoryId)
                        .setName(categoryName)
                        .build();
                list.add(category);
            }

            categoryList = CategoryList.newBuilder().addAllCategory(list).build();
//            System.out.println("categorylist : " + categoryList);
        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        responseObserver.onNext(categoryList);
        responseObserver.onCompleted();
    }

    @Override
    public void findByCategory(CategoryId request, StreamObserver<ProductList> responseObserver) {
        List list = new ArrayList();
        ProductList productList = ProductList.newBuilder().build();
        try {
            Properties properties = new Properties();
            properties.put("user", "root");
            properties.put("password", "root");
            properties.put("characterEncoding", "UTF-8");
            properties.put("useUnicode", true);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall_db", properties);

            String sql = "select * from Product where categoryId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, request.getValue());

            ResultSet resultSet = pstmt.executeQuery();
            for(int i = 0; resultSet.next(); i++) {
                int productId = resultSet.getInt(1);
                int categoryId = resultSet.getInt(2);
                String productName = resultSet.getString(3);
                float price = resultSet.getFloat(4);
                int count = resultSet.getInt(5);

                if (categoryId == request.getValue()) {
                    Product product = Product.newBuilder()
                            .setProductId(productId)
                            .setCategoryId(categoryId)
                            .setProductName(productName)
                            .setPrice(price)
                            .setCount(count)
                            .build();

                    list.add(product);
                }
            }
            productList = ProductList.newBuilder().addAllProduct(list).build();
//            System.out.println("productlist : " + productList);
        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        responseObserver.onNext(productList);
        responseObserver.onCompleted();
    }

    public void getProduct(ProductId request, StreamObserver<Product> responseObserver) {
        Product product = Product.newBuilder().build();
        try {
            Properties properties = new Properties();
            properties.put("user", "root");
            properties.put("password", "root");
            properties.put("characterEncoding", "UTF-8");
            properties.put("useUnicode", true);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall_db", properties);

            String sql = "select * from Product where productId = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, request.getProductId());

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt(1);
                int categoryId = resultSet.getInt(2);
                String productName = resultSet.getString(3);
                float price = resultSet.getFloat(4);
                int count = resultSet.getInt(5);

                product = Product.newBuilder()
                        .setProductId(productId)
                        .setCategoryId(categoryId)
                        .setProductName(productName)
                        .setPrice(price)
                        .setCount(count)
                        .build();
            }

        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        responseObserver.onNext(product);
        responseObserver.onCompleted();
    }

    @Override
    public void buyProduct(Buy request, StreamObserver<Empty> responseObserver) {
        try {
            Properties properties = new Properties();
            properties.put("user", "root");
            properties.put("password", "root");
            properties.put("characterEncoding", "UTF-8");
            properties.put("useUnicode", true);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall_db", properties);

            String sql1 = "update Product set count = count - ? where productId = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, request.getCount());
            pstmt1.setInt(2, request.getProduct().getProductId());
            pstmt1.executeUpdate();

            String sql2 = "insert into purchase (purchaseId, productId, productName, count, totalPrice)" +
                    "value (NULL, ?, ?, ?, ?)";
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, request.getProduct().getProductId());
            pstmt2.setString(2, request.getProduct().getProductName());
            pstmt2.setInt(3, request.getCount());
            pstmt2.setFloat(4, request.getProduct().getPrice() * request.getCount());
            pstmt2.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPurchaseList(Empty request, StreamObserver<PurchaseList> responseObserver) {
        List list = new ArrayList();
        PurchaseList purchaseList = PurchaseList.newBuilder().build();
        try {
            Properties properties = new Properties();
            properties.put("user", "root");
            properties.put("password", "root");
            properties.put("characterEncoding", "UTF-8");
            properties.put("useUnicode", true);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoppingmall_db", properties);

            String sql = "select * from Purchase";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet resultSet = pstmt.executeQuery();
            for(int i = 0; resultSet.next(); i++) {
                int purchaseId = resultSet.getInt(1);
                int productId = resultSet.getInt(2);
                String productName = resultSet.getString(3);
                int count = resultSet.getInt(4);
                float totalPrice = resultSet.getFloat(5);

                Purchase purchase = Purchase.newBuilder()
                        .setPurchaseId(purchaseId)
                        .setProductId(productId)
                        .setProductName(productName)
                        .setCount(count)
                        .setTotalPrice(totalPrice)
                        .build();
                list.add(purchase);
            }

            purchaseList = PurchaseList.newBuilder().addAllPurchase(list).build();
//            System.out.println("purchaselist : " + purchaseList);
        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        }
        responseObserver.onNext(purchaseList);
        responseObserver.onCompleted();
    }
}
