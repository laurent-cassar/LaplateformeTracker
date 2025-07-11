import java.sql.*;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/student_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

