import java.sql.*;
import java.util.*;

public class StudentDAO {
    public void addStudent(String fn, String ln, int age, double grade) {
        try (Connection conn = Database.connect()) {
            String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fn);
            stmt.setString(2, ln);
            stmt.setInt(3, age);
            stmt.setDouble(4, grade);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur ajout étudiant : " + e.getMessage());
        }
    }

    public void updateStudent(int id, String fn, String ln, int age, double grade) {
        try (Connection conn = Database.connect()) {
            String sql = "UPDATE student SET first_name=?, last_name=?, age=?, grade=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fn);
            stmt.setString(2, ln);
            stmt.setInt(3, age);
            stmt.setDouble(4, grade);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur modification : " + e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM student WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression : " + e.getMessage());
        }
    }

    public Student getStudent(int id) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT * FROM student WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getDouble("grade")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur recherche : " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents(String orderBy) {
        List<Student> list = new ArrayList<>();
        try (Connection conn = Database.connect()) {
            String sql = "SELECT * FROM student ORDER BY " + orderBy;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getDouble("grade")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur affichage : " + e.getMessage());
        }
        return list;
    }

    public void printStatistics() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT AVG(grade) as avg_grade, COUNT(*) as total FROM student";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("Moyenne de la classe : " + rs.getDouble("avg_grade"));
                System.out.println("Nombre total d'étudiants : " + rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur stats : " + e.getMessage());
        }
    }
}
