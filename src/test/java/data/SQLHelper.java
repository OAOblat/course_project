package data;

import com.codeborne.selenide.Selenide;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass"
        );
    }

    @SneakyThrows
    public static String getStatusFromDebitPayment() {
        var codeSQL = "SELECT * FROM payment_entity WHERE created >= (SELECT MAX(created) FROM payment_entity);";
        var conn = getConnection();
        Selenide.sleep(1000);
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLPayment.class));
        return result.getStatus();
    }

    @SneakyThrows
    public static String getTransactionIDFromDebitPayment() {
        var codeSQL = "SELECT * FROM payment_entity WHERE created >= (SELECT MAX(created) FROM payment_entity);";
        var conn = getConnection();
        Selenide.sleep(1000);
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLPayment.class));
        return result.getTransaction_id();
    }

    @SneakyThrows
    public static String getBankIDFromCreditPayment() {
        var codeSQL = "SELECT * FROM credit_request_entity WHERE created >= (SELECT MAX(created) FROM credit_request_entity);";
        var conn = getConnection();
        Selenide.sleep(1000);
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLCreditRequest.class));
        return result.getBank_id();
    }

    @SneakyThrows
    public static String getStatusFromCreditPayment() {
        var codeSQL = "SELECT * FROM credit_request_entity WHERE created >= (SELECT MAX(created) FROM credit_request_entity);";
        var conn = getConnection();
        Selenide.sleep(1000);
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLCreditRequest.class));
        return result.getStatus();
    }

    @SneakyThrows
    public static String getIDFromOrder() {
        var codeSQL = "SELECT * FROM order_entity WHERE created >= (SELECT MAX(created) FROM order_entity);";
        var conn = getConnection();
        Selenide.sleep(1000);
        var result = runner.query(conn, codeSQL, new BeanHandler<>(SQLOrder.class));
        return result.getPayment_id();
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
