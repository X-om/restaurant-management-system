package com.rms.rms.database;

import com.rms.rms.models.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://ep-orange-block-a4eerdid-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Z1LJ8FWypeKl";
    private static final HikariDataSource dataSource;
    private static final ConcurrentHashMap<String, PreparedStatement> statementCache = new ConcurrentHashMap<>();

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(3);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(10000);
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("reWriteBatchedInserts", "true");
        config.addDataSourceProperty("tcpKeepAlive", "true");
        config.setConnectionTestQuery("SELECT 1");
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return statementCache.computeIfAbsent(sql, k -> {
            try {
                return dataSource.getConnection().prepareStatement(k);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to prepare statement", e);
            }
        });
    }

    // Menu Table Operations
    public static void createMenuTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS menu (" +
                "id SERIAL PRIMARY KEY," +
                "foodName VARCHAR(255) NOT NULL UNIQUE," +
                "foodSection VARCHAR(50) NOT NULL," +
                "foodPrice NUMERIC(10,2) NOT NULL)";

        String createIndexSQL = "CREATE INDEX IF NOT EXISTS idx_food_section ON menu(foodSection)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createTableSQL);
            stmt.executeUpdate(createIndexSQL);
            System.out.println("Menu table and index created/verified");

        } catch (SQLException e) {
            System.err.println("Error creating menu table: " + e.getMessage());
            throw new RuntimeException("Table creation failed", e);
        }
    }

    public static void createOrderTable(){
        String createOrderTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id SERIAL PRIMARY KEY," +
                "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "total_price DECIMAL(10,2) NOT NULL," +
                "table_number INT REFERENCES tables(table_number) )";

        try(Connection conn = getConnection();
            Statement stmt = conn.createStatement()){
            stmt.executeUpdate(createOrderTableSQL);
            System.out.println("Order Table created");

        } catch (SQLException e){
            System.err.println("Error creating menu table " + e.getMessage());
            throw  new RuntimeException("Order creation failed");
        }
    }

    public static void createOrderItemsTable(){
        String createOrderItemSQL = "CREATE TABLE IF NOT EXISTS order_items (" +
                "order_item_id SERIAL PRIMARY KEY," +
                "order_id INT REFERENCES orders(order_id)," +
                "menu_item_id INT REFERENCES menu(id)," +
                "quantity INT NOT NULL," +
                "item_price DECIMAL(10,2) NOT NULL)";

        try(Connection conn = getConnection(); Statement stmt = conn.createStatement()){
            stmt.executeUpdate(createOrderItemSQL);
            System.out.println("Order items table created");

        } catch (SQLException e){
            System.err.println("Error creating menu table " + e.getMessage());
            throw new RuntimeException("Order items creation failed");
        }
    }


    public static int saveOrder(int tableNumber, double totalPrice, List<OrderItems> items) throws SQLException{
        Connection conn = null;
        int orderId = -1;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // 1. Insert order header
            String orderSql = "INSERT INTO orders (table_number, total_price) VALUES (?, ?)";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql,
                    Statement.RETURN_GENERATED_KEYS)) {

                orderStmt.setInt(1, tableNumber);
                orderStmt.setDouble(2, totalPrice);
                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

            // 2. Insert order items
            String itemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, item_price) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                for (OrderItems item : items) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getMenuItemId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            String updateTableSql = "UPDATE tables SET status = 'occupied' WHERE table_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateTableSql)) {
                stmt.setInt(1, tableNumber);
                stmt.executeUpdate();
            }
            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public static List<OrderHistory> getOrderHistory() throws SQLException{
        List<OrderHistory> history = new ArrayList<>();
        String sql = "SELECT order_id, table_number, total_price, order_date " +
                "FROM orders ORDER BY order_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                history.add(new OrderHistory(
                        rs.getInt("order_id"),
                        rs.getInt("table_number"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date").toLocalDateTime()
                ));
            }
        }
        return history;
    }

    public static List<OrderDetail> getOrderDetails(int orderId) throws SQLException {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT m.foodName, m.foodSection, oi.quantity, oi.item_price " +
                "FROM order_items oi " +
                "JOIN menu m ON oi.menu_item_id = m.id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(new OrderDetail(
                            rs.getString("foodName"),
                            rs.getString("foodSection"),
                            rs.getInt("quantity"),
                            rs.getDouble("item_price")
                    ));
                }
            }
        }
        return details;
    }

    public static int insertMenuItem(String name, String section, double price) {
        String sql = "INSERT INTO menu (foodName, foodSection, foodPrice) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, section);
            stmt.setDouble(3, price);

            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error inserting menu item: " + e.getMessage());
            return 0;
        }
    }


    public static Vector<MenuItemModel> getMenuItems(String foodSection) {
        Vector<MenuItemModel> menuItems = new Vector<>();
        String sql = "SELECT id, foodName, foodPrice FROM menu WHERE foodSection = ? ORDER BY foodName";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, foodSection);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                menuItems.add(new MenuItemModel(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getDouble("foodPrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }


    public static int insertTable() {
        String sql = "INSERT INTO tables (table_number, status) VALUES (?, 'available') RETURNING table_number";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int nextTableNumber = getNextTableNumber();  // Generate next table number
            stmt.setInt(1, nextTableNumber);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Return the inserted table number
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if insert fails
    }

    private static int getNextTableNumber() {
        String sql = "SELECT COALESCE(MAX(table_number), 0) + 1 FROM tables";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static List<RestaurantTable> fetchTables() {
        List<RestaurantTable> tableList = new ArrayList<>();
        String sql = "SELECT table_number, status FROM tables ORDER BY table_number";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableList.add(new RestaurantTable(
                        rs.getInt("table_number"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public static void createTablesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS tables (" +
                "table_id SERIAL PRIMARY KEY," +
                "table_number INT UNIQUE NOT NULL," +
                "status VARCHAR(255) NOT NULL DEFAULT 'available')";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Tables table created (if not exists)");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Table creation failed");
        }
    }

}