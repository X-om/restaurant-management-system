package com.rms.rms.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://ep-orange-block-a4eerdid-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Z1LJ8FWypeKl";

    private static final HikariDataSource dataSource;
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://ep-orange-block-a4eerdid-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require");
        config.setUsername("neondb_owner");
        config.setPassword("npg_Z1LJ8FWypeKl");
        config.setMaximumPoolSize(10); // Control max connections
        dataSource = new HikariDataSource(config);
    }


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
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

    public static List<Integer> fetchTables() {
        List<Integer> tableList = new ArrayList<>();
        String sql = "SELECT table_number FROM tables ORDER BY table_number";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableList.add(rs.getInt("table_number"));
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

