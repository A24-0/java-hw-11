import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Домашнее задание:
        // Создать пользователя и базу данных
        // create user teacher with password 'queen';
        // create database top_academy with owner teacher;

        Connection connection = getFromPGSQL();
        if (connection != null) {
            try {
                insertProductTypesFromFile(connection, "productTypes.txt");
                insertProductsFromFile(connection, "products.txt");
                viewTable(connection, "productType", new String[]{"id", "name", "bestBefore"});
                viewTable(connection, "product", new String[]{"id", "name", "type_id", "produce", "price"});
                try {
                    connection.close();
                } catch (SQLException sqle) {
                    System.out.println("Cannot disconnect from PostgreSQL");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            createTable(connection);
//            insertIntoTable(connection);
//            selectFromTable(connection);
        }
    }
    public static Connection getFromPGSQL() {
        String jdbcUrl  = "jdbc:postgresql://localhost:5432/";
        String dbname   = "top_academy";
        String username = "teacher";
        String password = getPassword(dbname, username);
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl + dbname, username, password);
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }
        return connection;
    }
    private static void insertProductTypesFromFile(Connection connection, String filename) throws SQLException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String typeName = parts[0];
                String bestBefore = parts[1];
                insertIntoProductType(connection, typeName, bestBefore);
            }
        }
    }

    private static void insertProductsFromFile(Connection connection, String filename) throws SQLException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String productName = parts[0];
                String typeName = parts[1];
                String produce = parts[2];
                BigDecimal price = new BigDecimal(parts[3]);
                insertIntoProduct(connection, productName, typeName, Timestamp.valueOf(produce), price);
            }
        }
    }
    private static void insertIntoProductType(Connection connection, String name, String bestBefore) {
        String sql = "INSERT INTO productType (name, bestBefore) VALUES (?, ?::interval)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, bestBefore);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected != 1) {
                System.out.println("Ошибка: Не удалось вставить запись в таблицу productType.");
            } else {
                System.out.println("Данные успешно вставлены в таблицу productType.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertIntoProduct(Connection connection, String name, String typeName, Timestamp produce, BigDecimal price) {
        String sql = "INSERT INTO product (name, type_id, produce, price) VALUES (?, (SELECT id FROM productType WHERE name = ?), ?, ?::money)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, typeName);
            statement.setTimestamp(3, produce);
            statement.setBigDecimal(4, price);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected != 1) {
                System.out.println("Ошибка: Не удалось вставить запись в таблицу product.");
            } else {
                System.out.println("Данные успешно вставлены в таблицу product.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void viewTable(Connection connection, String tableName, String[] fields) throws SQLException {
        String sql = "SELECT " + String.join(", ", fields) + " FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Table: " + tableName);
            printHeader(fields);
            while (resultSet.next()) {
                printRow(resultSet, fields);
            }
            System.out.println();
        }
    }

    private static void printHeader(String[] fields) {
        for (String field : fields) {
            System.out.print(field + "\t");
        }
        System.out.println();
    }

    private static void printRow(ResultSet resultSet, String[] fields) throws SQLException {
        for (String field : fields) {
            System.out.print(resultSet.getString(field) + "\t");
        }
        System.out.println();
    }

    private static String getPassword(String dbname, String username) {
        String pss;
        try {
            pss = readPgpass(dbname, username);
        } catch (IOException ioe) {
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Cannot read pgpass file. Please provide password for db academy_top user teacher:");
            pss = keyboard.nextLine();
            keyboard.close();
        }
        return pss;
    }
    private static String readPgpass(String dbname, String username) throws IOException {
        // Join standard path for pgpass in our system
        Path filePath = Paths.get(
                System.getenv("APPDATA"),
                "postgresql",
                "pgpass.conf");
        // Open file
        String pss = "";
        String line = "";
        String[] parts;
        System.out.println(filePath.toString());
        File file = new File(filePath.toString());
        Scanner inputFile = new Scanner(file);
        // Read lines from the file until no more are left.
        while (inputFile.hasNext() && pss == "") {
            line = inputFile.nextLine();
            // hostname:port:dbname:username:password
            // 0        1    2      3        4
            System.out.println(line);
            parts = line.split(":");
            if (parts.length >= 5 && parts[2].equals(dbname) && parts[3].equals(username)) {
                pss = parts[4].trim();  // deleting end of line
                System.out.println(pss);
            }
        }
        // Close the file.
        inputFile.close();
        return pss;
    }
}