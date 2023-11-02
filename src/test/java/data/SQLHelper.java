package data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;


public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConnection() {
        var url = System.getProperty("spring.datasource.url");
        var username = System.getProperty("spring.datasource.username");
        var password = System.getProperty("spring.datasource.password");
        return DriverManager.getConnection(
                url, username, password
        );
    }

    @SneakyThrows
    public static SQLPayment getInfoFromDebitPayment() {
        var codeSQL = "SELECT * FROM payment_entity WHERE created >= (SELECT MAX(created) FROM payment_entity);";
        var conn = getConnection();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLPayment.class));
        return result;
    }

    @SneakyThrows
    public static SQLCreditRequest getInfoFromCreditPayment() {
        var codeSQL =  "SELECT * FROM credit_request_entity WHERE created >= (SELECT MAX(created) FROM credit_request_entity);";
        var conn = getConnection();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLCreditRequest.class));
        return result;
    }

    @SneakyThrows
    public static SQLOrder getInfoFromOrder() {
        var codeSQL = "SELECT * FROM order_entity WHERE created >= (SELECT MAX(created) FROM order_entity);";
        var conn = getConnection();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLOrder.class));
        return result;
    }

    @Data
    @NoArgsConstructor
    public static class SQLPayment {
        private String id;
        private String amount;
        private String created;
        private String status;
        private String transaction_id;
    }

    @Data
    @NoArgsConstructor
    public static class SQLOrder {
        private String id;
        private String created;
        private String credit_id;
        private String payment_id;
    }

    @Data
    @NoArgsConstructor
    public static class SQLCreditRequest {
        private String id;
        private String bank_id;
        private String created;
        private String status;
    }
}
