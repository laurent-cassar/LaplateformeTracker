public class Student {
    int id;
    String firstName;
    String lastName;
    int age;
    double grade;

    public Student(int id, String firstName, String lastName, int age, double grade) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return id + " | " + firstName + " " + lastName + " | Age: " + age + " | Grade: " + grade;
    }
}
