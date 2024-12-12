import java.sql.*;

public class DatabaseHandler {
    public static final String DB_URL = "jdbc:sqlite:identifier.sqlite";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";

    private static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);

            connection.setAutoCommit(true);

            if (connection != null) {
                System.out.println("Connected to the database.");
            }
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            return null;
        }
    }
    public static void createRunningLogTable(Connection conn) {
        String sql = """
    CREATE TABLE IF NOT EXISTS running_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER,
        hours INTEGER,
        minutes INTEGER,
        seconds INTEGER,
        distance REAL,
        date TEXT,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("Running log table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating running log table: " + e.getMessage());
        }
    }

    public static void createUserTable(Connection conn) {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            age INTEGER,
            gender TEXT,
            height INTEGER,
            weight REAL
        );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("User table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void saveUser(User user) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = """
    INSERT INTO users (username, password, age, gender, height, weight)
    VALUES (?, ?, ?, ?, ?, ?);
    """;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {

            checkStmt.setString(1, user.getUsername());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Username " + user.getUsername() + " already exists.");
                return;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, user.getPassword());
                insertStmt.setInt(3, user.getAge());
                insertStmt.setString(4, user.getGender());
                insertStmt.setInt(5, user.getHeight());
                insertStmt.setDouble(6, user.getWeight());

                insertStmt.executeUpdate();
                System.out.println("User saved successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    public static void printAllUsers(Connection conn) {
        String sql = "SELECT id, username, password, age, gender, height, weight FROM users";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                int height = rs.getInt("height");
                double weight = rs.getDouble("weight");

                // Udskriv brugerens informationer
                System.out.println("ID: " + id + ", Username: " + username + ", Password: " + password
                        + ", Age: " + age + ", Gender: " + gender + ", Height: " + height + ", Weight: " + weight);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    public static void saveRun(User user, Run run) {
        String sql = """
    INSERT INTO running_log (user_id, hours, minutes, seconds, distance, date)
    VALUES (?, ?, ?, ?, ?, ?);
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, run.getHours());
            stmt.setInt(3, run.getMinutes());
            stmt.setInt(4, run.getSeconds());
            stmt.setDouble(5, run.getDistance());
            stmt.setString(6, run.getDate());

            stmt.executeUpdate();
            System.out.println("Run saved successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving run: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
