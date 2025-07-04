// ==================== STRUCTURE DE LA BASE DE DONNÉES ====================
/*
Script SQL pour créer la base de données PostgreSQL :

CREATE DATABASE student_management;

CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL CHECK (age > 0),
    grade DECIMAL(4,2) NOT NULL CHECK (grade >= 0 AND grade <= 20),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Insérer un utilisateur par défaut (mot de passe: admin123)
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN');
*/

// ==================== IMPORTS ====================
import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ==================== CLASSE STUDENT ====================
class Student {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private double grade;
    private String email;
    private String phone;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructeurs
    public Student() {}
    
    public Student(String firstName, String lastName, int age, double grade, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %s %s | Âge: %d | Note: %.2f | Email: %s | Téléphone: %s",
                id, firstName, lastName, age, grade, email != null ? email : "N/A", phone != null ? phone : "N/A");
    }
}

// ==================== CLASSE DE CONNEXION À LA BASE DE DONNÉES ====================
class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/student_management";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password"; // Changez selon votre configuration
    
    private static Connection connection;
    
    /**
     * Établit la connexion à la base de données
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✓ Connexion à la base de données établie");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL non trouvé", e);
            }
        }
        return connection;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            return false;
        }
    }
}

// ==================== GESTION DES ÉTUDIANTS ====================
class StudentDAO {
    
    /**
     * Ajoute un nouvel étudiant à la base de données
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhone());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✓ Étudiant ajouté avec succès");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Modifie un étudiant existant
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ?, email = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.setDouble(4, student.getGrade());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhone());
            pstmt.setInt(7, student.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Étudiant modifié avec succès");
                return true;
            } else {
                System.out.println("❌ Aucun étudiant trouvé avec cet ID");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprime un étudiant par son ID
     */
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Étudiant supprimé avec succès");
                return true;
            } else {
                System.out.println("❌ Aucun étudiant trouvé avec cet ID");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Recherche un étudiant par son ID
     */
    public Student findStudentById(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère tous les étudiants avec pagination
     */
    public List<Student> getAllStudents(int page, int pageSize, String sortBy, String sortDirection) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY " + sortBy + " " + sortDirection + " LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, (page - 1) * pageSize);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(createStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Recherche avancée d'étudiants
     */
    public List<Student> searchStudents(String firstName, String lastName, Integer minAge, Integer maxAge, 
                                       Double minGrade, Double maxGrade) {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM student WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND first_name ILIKE ?");
            params.add("%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND last_name ILIKE ?");
            params.add("%" + lastName + "%");
        }
        if (minAge != null) {
            sql.append(" AND age >= ?");
            params.add(minAge);
        }
        if (maxAge != null) {
            sql.append(" AND age <= ?");
            params.add(maxAge);
        }
        if (minGrade != null) {
            sql.append(" AND grade >= ?");
            params.add(minGrade);
        }
        if (maxGrade != null) {
            sql.append(" AND grade <= ?");
            params.add(maxGrade);
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(createStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche avancée: " + e.getMessage());
        }
        return students;
    }
    
    /**
     * Compte le nombre total d'étudiants
     */
    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM student";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Calcule la moyenne des notes de tous les étudiants
     */
    public double getAverageGrade() {
        String sql = "SELECT AVG(grade) FROM student";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la moyenne: " + e.getMessage());
        }
        return 0.0;
    }
    
    /**
     * Obtient la distribution des étudiants par tranche d'âge
     */
    public Map<String, Integer> getAgeDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        String sql = "SELECT CASE " +
                    "WHEN age < 18 THEN 'Moins de 18' " +
                    "WHEN age BETWEEN 18 AND 22 THEN '18-22' " +
                    "WHEN age BETWEEN 23 AND 27 THEN '23-27' " +
                    "ELSE '28 et plus' END as age_range, COUNT(*) as count " +
                    "FROM student GROUP BY age_range ORDER BY age_range";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                distribution.put(rs.getString("age_range"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la distribution: " + e.getMessage());
        }
        return distribution;
    }
    
    /**
     * Crée un objet Student à partir d'un ResultSet
     */
    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setAge(rs.getInt("age"));
        student.setGrade(rs.getDouble("grade"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        return student;
    }
}

// ==================== GESTION DE L'AUTHENTIFICATION ====================
class AuthenticationService {
    
    /**
     * Authentifie un utilisateur
     */
    public boolean authenticate(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password); // En production, utilisez un hash
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
        }
        return false;
    }
}

// ==================== GESTION DES IMPORTS/EXPORTS ====================
class DataImportExport {
    
    /**
     * Exporte les étudiants vers un fichier CSV
     */
    public void exportToCSV(List<Student> students, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Prénom,Nom,Âge,Note,Email,Téléphone");
            for (Student student : students) {
                writer.printf("%d,%s,%s,%d,%.2f,%s,%s%n",
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getAge(),
                    student.getGrade(),
                    student.getEmail() != null ? student.getEmail() : "",
                    student.getPhone() != null ? student.getPhone() : "");
            }
            System.out.println("✓ Export CSV réussi: " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV: " + e.getMessage());
        }
    }
    
    /**
     * Importe les étudiants depuis un fichier CSV
     */
    public List<Student> importFromCSV(String filename) {
        List<Student> students = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    Student student = new Student();
                    student.setFirstName(fields[1]);
                    student.setLastName(fields[2]);
                    student.setAge(Integer.parseInt(fields[3]));
                    student.setGrade(Double.parseDouble(fields[4]));
                    if (fields.length > 5 && !fields[5].isEmpty()) {
                        student.setEmail(fields[5]);
                    }
                    if (fields.length > 6 && !fields[6].isEmpty()) {
                        student.setPhone(fields[6]);
                    }
                    students.add(student);
                }
            }
            System.out.println("✓ Import CSV réussi: " + students.size() + " étudiants importés");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors de l'import CSV: " + e.getMessage());
        }
        return students;
    }
}

// ==================== SAUVEGARDE AUTOMATIQUE ====================
class AutoBackupService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final StudentDAO studentDAO;
    private final DataImportExport importExport;
    
    public AutoBackupService(StudentDAO studentDAO, DataImportExport importExport) {
        this.studentDAO = studentDAO;
        this.importExport = importExport;
    }
    
    /**
     * Démarre la sauvegarde automatique
     */
    public void startAutoBackup(long intervalMinutes) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Student> allStudents = studentDAO.getAllStudents(1, Integer.MAX_VALUE, "id", "ASC");
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                String filename = "backup_" + timestamp + ".csv";
                importExport.exportToCSV(allStudents, filename);
                System.out.println("✓ Sauvegarde automatique effectuée: " + filename);
            } catch (Exception e) {
                System.err.println("Erreur lors de la sauvegarde automatique: " + e.getMessage());
            }
        }, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
    }
    
    /**
     * Arrête la sauvegarde automatique
     */
    public void stopAutoBackup() {
        scheduler.shutdown();
    }
}

// ==================== CLASSE PRINCIPALE ====================
public class StudentManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final DataImportExport importExport = new DataImportExport();
    private static final AuthenticationService authService = new AuthenticationService();
    private static AutoBackupService backupService;
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("   SYSTÈME DE GESTION D'ÉTUDIANTS");
        System.out.println("====================================");
        
        // Test de connexion
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Impossible de se connecter à la base de données");
            return;
        }
        
        // Authentification
        if (!authenticateUser()) {
            System.err.println("❌ Authentification échouée");
            return;
        }
        
        // Démarrage de la sauvegarde automatique (toutes les 30 minutes)
        backupService = new AutoBackupService(studentDAO, importExport);
        backupService.startAutoBackup(30);
        
        // Menu principal
        boolean running = true;
        while (running) {
            try {
                showMainMenu();
                int choice = getIntInput("Votre choix: ");
                
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> updateStudent();
                    case 3 -> deleteStudent();
                    case 4 -> displayAllStudents();
                    case 5 -> searchStudentById();
                    case 6 -> searchStudentsAdvanced();
                    case 7 -> showStatistics();
                    case 8 -> importExportMenu();
                    case 9 -> {
                        running = false;
                        System.out.println("Au revoir!");
                    }
                    default -> System.out.println("❌ Choix invalide, veuillez réessayer.");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur inattendue: " + e.getMessage());
                System.out.println("Appuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
        }
        
        // Nettoyage
        if (backupService != null) {
            backupService.stopAutoBackup();
        }
        DatabaseConnection.closeConnection();
    }
    
    /**
     * Authentifie l'utilisateur
     */
    private static boolean authenticateUser() {
        System.out.println("\n=== AUTHENTIFICATION ===");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();
        
        boolean authenticated = authService.authenticate(username, password);
        if (authenticated) {
            System.out.println("✓ Authentification réussie!");
        }
        return authenticated;
    }
    
    /**
     * Affiche le menu principal
     */
    private static void showMainMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Ajouter un étudiant");
        System.out.println("2. Modifier un étudiant");
        System.out.println("3. Supprimer un étudiant");
        System.out.println("4. Afficher tous les étudiants");
        System.out.println("5. Rechercher un étudiant par ID");
        System.out.println("6. Recherche avancée");
        System.out.println("7. Afficher les statistiques");
        System.out.println("8. Import/Export de données");
        System.out.println("9. Quitter");
        System.out.println("========================");
    }
    
    /**
     * Ajoute un nouvel étudiant
     */
    private static void addStudent() {
        System.out.println("\n=== AJOUTER UN ÉTUDIANT ===");
        
        try {
            System.out.print("Prénom: ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) {
                System.out.println("❌ Le prénom ne peut pas être vide");
                return;
            }
            
            System.out.print("Nom: ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) {
                System.out.println("❌ Le nom ne peut pas être vide");
                return;
            }
            
            int age = getIntInput("Âge: ");
            if (age <= 0) {
                System.out.println("❌ L'âge doit être positif");
                return;
            }
            
            double grade = getDoubleInput("Note (0-20): ");
            if (grade < 0 || grade > 20) {
                System.out.println("❌ La note doit être entre 0 et 20");
                return;
            }
            
            System.out.print("Email (optionnel): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = null;
            
            System.out.print("Téléphone (optionnel): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) phone = null;
            
            Student student = new Student(firstName, lastName, age, grade, email, phone);
            studentDAO.addStudent(student);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    /**
     * Modifie un étudiant existant
     */
    private static void updateStudent() {
        System.out.println("\n=== MODIFIER UN ÉTUDIANT ===");
        
        try {
            int id = getIntInput("ID de l'étudiant à modifier: ");
            Student student = studentDAO.findStudentById(id);
            
            if (student == null) {
                System.out.println("❌ Aucun étudiant trouvé avec cet ID");
                return;
            }
            
            System.out.println("Étudiant actuel: " + student);
            System.out.println("Laissez vide pour conserver la valeur actuelle");
            
            System.out.print("Nouveau prénom [" + student.getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();
            if (!firstName.isEmpty()) {
                student.setFirstName(firstName);
            }
            
            System.out.print("Nouveau nom [" + student.getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();
            if (!lastName.isEmpty()) {
                student.setLastName(lastName);
            }
            
            System.out.print("Nouvel âge [" + student.getAge() + "]: ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) {
                int age = Integer.parseInt(ageStr);
                if (age > 0) {
                    student.setAge(age);
                }
            }
            
            System.out.print("Nouvelle note [" + student.getGrade() + "]: ");
            String gradeStr = scanner.nextLine().trim();
            if (!gradeStr.isEmpty()) {
                double grade = Double.parseDouble(gradeStr);
                if (grade >= 0 && grade <= 20) {
                    student.setGrade(grade);
                }
            }
            
            System.out.print("Nouvel email [" + (student.getEmail() != null ? student.getEmail() : "N/A") + "]: ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                student.setEmail(email);
            }
            
            System.out.print("Nouveau téléphone [" + (student.getPhone() != null ? student.getPhone() : "N/A") + "]: ");
            String phone = scanner.nextLine().trim();
            if (!phone.isEmpty()) {
                student.setPhone(phone);
            }
            
            studentDAO.updateStudent(student);
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Format de nombre invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un étudiant
     */
    private static void deleteStudent() {
        System.out.println("\n=== SUPPRIMER UN ÉTUDIANT ===");
        
        try {
            int id = getIntInput("ID de l'étudiant à supprimer: ");
            Student student = studentDAO.findStudentById(id);
            
            if (student == null) {
                System.out.println("❌ Aucun étudiant trouvé avec cet ID");
                return;
            }
            
            System.out.println("Étudiant à supprimer: " + student);
            System.out.print("Êtes-vous sûr de vouloir supprimer cet étudiant? (oui/non): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("oui") || confirmation.equals("o")) {
                studentDAO.deleteStudent(id);
            } else {
                System.out.println("Suppression annulée");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
        }
    }
    
    /**
     * Affiche tous les étudiants avec pagination
     */
    private static void displayAllStudents() {
        System.out.println("\n=== TOUS LES ÉTUDIANTS ===");
        
        try {
            int totalStudents = studentDAO.getTotalStudentCount();
            if (totalStudents == 0) {
                System.out.println("Aucun étudiant trouvé");
                return;
            }
            
            System.out.println("Total d'étudiants: " + totalStudents);
            
            // Options de tri
            System.out.println("\nOptions de tri:");
            System.out.println("1. Par nom");
            System.out.println("2. Par prénom");
            System.out.println("3. Par âge");
            System.out.println("4. Par note");
            System.out.println("5. Par ID");
            
            int sortChoice = getIntInput("Choix de tri (1-5): ");
            String sortBy = switch (sortChoice) {
                case 1 -> "last_name";
                case 2 -> "first_name";
                case 3 -> "age";
                case 4 -> "grade";
                default -> "id";
            };
            
            System.out.print("Ordre croissant (c) ou décroissant (d): ");
            String orderChoice = scanner.nextLine().trim().toLowerCase();
            String sortDirection = orderChoice.equals("d") ? "DESC" : "ASC";
            
            int pageSize = getIntInput("Nombre d'étudiants par page (par défaut 10): ");
            if (pageSize <= 0) pageSize = 10;
            
            int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
            int currentPage = 1;
            
            while (true) {
                List<Student> students = studentDAO.getAllStudents(currentPage, pageSize, sortBy, sortDirection);
                
                System.out.println("\n--- Page " + currentPage + "/" + totalPages + " ---");
                for (Student student : students) {
                    System.out.println(student);
                }
                
                if (totalPages > 1) {
                    System.out.println("\nNavigation:");
                    System.out.println("n - Page suivante");
                    System.out.println("p - Page précédente");
                    System.out.println("g - Aller à une page");
                    System.out.println("q - Quitter");
                    
                    System.out.print("Votre choix: ");
                    String choice = scanner.nextLine().trim().toLowerCase();
                    
                    switch (choice) {
                        case "n" -> {
                            if (currentPage < totalPages) {
                                currentPage++;
                            } else {
                                System.out.println("Dernière page atteinte");
                            }
                        }
                        case "p" -> {
                            if (currentPage > 1) {
                                currentPage--;
                            } else {
                                System.out.println("Première page atteinte");
                            }
                        }
                        case "g" -> {
                            int page = getIntInput("Numéro de page (1-" + totalPages + "): ");
                            if (page >= 1 && page <= totalPages) {
                                currentPage = page;
                            } else {
                                System.out.println("Page invalide");
                            }
                        }
                        case "q" -> {
                            return;
                        }
                    }
                } else {
                    break;
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'affichage: " + e.getMessage());
        }
    }
    
    /**
     * Recherche un étudiant par ID
     */
    private static void searchStudentById() {
        System.out.println("\n=== RECHERCHE PAR ID ===");
        
        try {
            int id = getIntInput("ID de l'étudiant: ");
            Student student = studentDAO.findStudentById(id);
            
            if (student != null) {
                System.out.println("\nÉtudiant trouvé:");
                System.out.println(student);
            } else {
                System.out.println("❌ Aucun étudiant trouvé avec cet ID");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
        }
    }
    
    /**
     * Recherche avancée d'étudiants
     */
    private static void searchStudentsAdvanced() {
        System.out.println("\n=== RECHERCHE AVANCÉE ===");
        System.out.println("Laissez vide pour ignorer un critère");
        
        try {
            System.out.print("Prénom (recherche partielle): ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isEmpty()) firstName = null;
            
            System.out.print("Nom (recherche partielle): ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isEmpty()) lastName = null;
            
            System.out.print("Âge minimum: ");
            String minAgeStr = scanner.nextLine().trim();
            Integer minAge = minAgeStr.isEmpty() ? null : Integer.parseInt(minAgeStr);
            
            System.out.print("Âge maximum: ");
            String maxAgeStr = scanner.nextLine().trim();
            Integer maxAge = maxAgeStr.isEmpty() ? null : Integer.parseInt(maxAgeStr);
            
            System.out.print("Note minimum: ");
            String minGradeStr = scanner.nextLine().trim();
            Double minGrade = minGradeStr.isEmpty() ? null : Double.parseDouble(minGradeStr);
            
            System.out.print("Note maximum: ");
            String maxGradeStr = scanner.nextLine().trim();
            Double maxGrade = maxGradeStr.isEmpty() ? null : Double.parseDouble(maxGradeStr);
            
            List<Student> results = studentDAO.searchStudents(firstName, lastName, minAge, maxAge, minGrade, maxGrade);
            
            if (results.isEmpty()) {
                System.out.println("❌ Aucun étudiant trouvé avec ces critères");
            } else {
                System.out.println("\n✓ " + results.size() + " étudiant(s) trouvé(s):");
                for (Student student : results) {
                    System.out.println(student);
                }
                
                // Option d'export des résultats
                System.out.print("\nExporter les résultats en CSV? (oui/non): ");
                String exportChoice = scanner.nextLine().trim().toLowerCase();
                if (exportChoice.equals("oui") || exportChoice.equals("o")) {
                    String filename = "recherche_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".csv";
                    importExport.exportToCSV(results, filename);
                }
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Format de nombre invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la recherche: " + e.getMessage());
        }
    }
    
    /**
     * Affiche les statistiques
     */
    private static void showStatistics() {
        System.out.println("\n=== STATISTIQUES ===");
        
        try {
            int totalStudents = studentDAO.getTotalStudentCount();
            double averageGrade = studentDAO.getAverageGrade();
            Map<String, Integer> ageDistribution = studentDAO.getAgeDistribution();
            
            System.out.println("Nombre total d'étudiants: " + totalStudents);
            System.out.printf("Moyenne générale des notes: %.2f/20%n", averageGrade);
            
            System.out.println("\nDistribution par âge:");
            for (Map.Entry<String, Integer> entry : ageDistribution.entrySet()) {
                System.out.printf("  %s: %d étudiants%n", entry.getKey(), entry.getValue());
            }
            
            // Statistiques additionnelles
            System.out.println("\nStatistiques détaillées:");
            showDetailedStatistics();
            
            // Option d'export des statistiques
            System.out.print("\nExporter les statistiques? (oui/non): ");
            String exportChoice = scanner.nextLine().trim().toLowerCase();
            if (exportChoice.equals("oui") || exportChoice.equals("o")) {
                exportStatistics(totalStudents, averageGrade, ageDistribution);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du calcul des statistiques: " + e.getMessage());
        }
    }
    
    /**
     * Affiche des statistiques détaillées
     */
    private static void showDetailedStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Meilleure et pire note
            String sql = "SELECT MIN(grade) as min_grade, MAX(grade) as max_grade FROM student";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("  Note la plus basse: %.2f/20%n", rs.getDouble("min_grade"));
                    System.out.printf("  Note la plus haute: %.2f/20%n", rs.getDouble("max_grade"));
                }
            }
            
            // Répartition des notes
            sql = "SELECT " +
                  "COUNT(CASE WHEN grade >= 16 THEN 1 END) as excellent, " +
                  "COUNT(CASE WHEN grade >= 14 AND grade < 16 THEN 1 END) as bien, " +
                  "COUNT(CASE WHEN grade >= 12 AND grade < 14 THEN 1 END) as assez_bien, " +
                  "COUNT(CASE WHEN grade >= 10 AND grade < 12 THEN 1 END) as passable, " +
                  "COUNT(CASE WHEN grade < 10 THEN 1 END) as echec " +
                  "FROM student";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  Répartition des notes:");
                    System.out.println("    Excellent (16-20): " + rs.getInt("excellent"));
                    System.out.println("    Bien (14-16): " + rs.getInt("bien"));
                    System.out.println("    Assez bien (12-14): " + rs.getInt("assez_bien"));
                    System.out.println("    Passable (10-12): " + rs.getInt("passable"));
                    System.out.println("    Échec (<10): " + rs.getInt("echec"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des statistiques détaillées: " + e.getMessage());
        }
    }
    
    /**
     * Exporte les statistiques vers un fichier HTML
     */
    private static void exportStatistics(int totalStudents, double averageGrade, Map<String, Integer> ageDistribution) {
        try {
            String filename = "statistiques_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".html";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("<!DOCTYPE html>");
                writer.println("<html><head><title>Statistiques des Étudiants</title></head><body>");
                writer.println("<h1>Statistiques des Étudiants</h1>");
                writer.println("<p>Date: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "</p>");
                writer.println("<h2>Résumé</h2>");
                writer.println("<p>Nombre total d'étudiants: " + totalStudents + "</p>");
                writer.printf("<p>Moyenne générale: %.2f/20</p>%n", averageGrade);
                writer.println("<h2>Distribution par âge</h2>");
                writer.println("<ul>");
                for (Map.Entry<String, Integer> entry : ageDistribution.entrySet()) {
                    writer.printf("<li>%s: %d étudiants</li>%n", entry.getKey(), entry.getValue());
                }
                writer.println("</ul>");
                writer.println("</body></html>");
            }
            System.out.println("✓ Statistiques exportées: " + filename);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export des statistiques: " + e.getMessage());
        }
    }
    
    /**
     * Menu d'import/export
     */
    private static void importExportMenu() {
        System.out.println("\n=== IMPORT/EXPORT ===");
        System.out.println("1. Exporter tous les étudiants (CSV)");
        System.out.println("2. Importer des étudiants (CSV)");
        System.out.println("3. Retour au menu principal");
        
        int choice = getIntInput("Votre choix: ");
        
        switch (choice) {
            case 1 -> exportAllStudents();
            case 2 -> importStudents();
            case 3 -> { /* Retour au menu */ }
            default -> System.out.println("❌ Choix invalide");
        }
    }
    
    /**
     * Exporte tous les étudiants
     */
    private static void exportAllStudents() {
        try {
            List<Student> allStudents = studentDAO.getAllStudents(1, Integer.MAX_VALUE, "id", "ASC");
            if (allStudents.isEmpty()) {
                System.out.println("❌ Aucun étudiant à exporter");
                return;
            }
            
            String filename = "etudiants_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".csv";
            importExport.exportToCSV(allStudents, filename);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'export: " + e.getMessage());
        }
    }
    
    /**
     * Importe des étudiants depuis un fichier CSV
     */
    private static void importStudents() {
        try {
            System.out.print("Nom du fichier CSV à importer: ");
            String filename = scanner.nextLine().trim();
            
            if (!new File(filename).exists()) {
                System.out.println("❌ Fichier non trouvé: " + filename);
                return;
            }
            
            List<Student> students = importExport.importFromCSV(filename);
            if (students.isEmpty()) {
                System.out.println("❌ Aucun étudiant à importer");
                return;
            }
            
            System.out.println("Étudiants à importer:");
            for (int i = 0; i < Math.min(students.size(), 5); i++) {
                System.out.println("  " + students.get(i));
            }
            if (students.size() > 5) {
                System.out.println("  ... et " + (students.size() - 5) + " autres");
            }
            
            System.out.print("Confirmer l'import? (oui/non): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("oui") || confirmation.equals("o")) {
                int imported = 0;
                for (Student student : students) {
                    if (studentDAO.addStudent(student)) {
                        imported++;
                    }
                }
                System.out.println("✓ " + imported + " étudiants importés avec succès");
            } else {
                System.out.println("Import annulé");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'import: " + e.getMessage());
        }
    }
    
    /**
     * Lit un entier depuis l'entrée utilisateur avec gestion d'erreurs
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return 0; // Valeur par défaut
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide");
            }
        }
    }
    
    /**
     * Lit un double depuis l'entrée utilisateur avec gestion d'erreurs
     */
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return 0.0; // Valeur par défaut
                }
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide");
            }
        }
    }
}