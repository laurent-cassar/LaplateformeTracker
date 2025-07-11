import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentDAO dao = new StudentDAO();

        while (true) {
            System.out.println("\n1. Ajouter | 2. Modifier | 3. Supprimer | 4. Afficher tous | 5. Rechercher | 6. Statistiques | 0. Quitter");
            int choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    System.out.print("Nom: ");
                    String ln = sc.nextLine();
                    System.out.print("Prénom: ");
                    String fn = sc.nextLine();
                    System.out.print("Âge: ");
                    int age = sc.nextInt();
                    System.out.print("Note: ");
                    double grade = sc.nextDouble();
                    dao.addStudent(fn, ln, age, grade);
                    break;
                case 2:
                    System.out.print("ID à modifier: ");
                    int idMod = sc.nextInt(); sc.nextLine();
                    System.out.print("Nom: ");
                    ln = sc.nextLine();
                    System.out.print("Prénom: ");
                    fn = sc.nextLine();
                    System.out.print("Âge: ");
                    age = sc.nextInt();
                    System.out.print("Note: ");
                    grade = sc.nextDouble();
                    dao.updateStudent(idMod, fn, ln, age, grade);
                    break;
                case 3:
                    System.out.print("ID à supprimer: ");
                    int idDel = sc.nextInt();
                    dao.deleteStudent(idDel);
                    break;
                case 4:
                    System.out.print("Trier par (first_name, last_name, age, grade): ");
                    String col = sc.nextLine();
                    dao.getAllStudents(col).forEach(System.out::println);
                    break;
                case 5:
                    System.out.print("ID à rechercher: ");
                    int id = sc.nextInt();
                    Student s = dao.getStudent(id);
                    if (s != null) System.out.println(s);
                    else System.out.println("Aucun étudiant trouvé.");
                    break;
                case 6:
                    dao.printStatistics();
                    break;
                case 0:
                    System.exit(0);
            }
        }
    }
}


