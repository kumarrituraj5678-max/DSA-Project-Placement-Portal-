/**
 * Represents one student profile.
 * Attributes are private; access is only through getters (encapsulation).
 */
public class Student {
    private final String id;
    private final String name;
    private final double cgpa;

    public Student(String id, String name, double cgpa) {
        this.id = id;
        this.name = name;
        this.cgpa = cgpa;
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public double getCgpa()  { return cgpa; }

    @Override
    public String toString() {
        return id + " | " + name + " | CGPA " + cgpa;
    }
}
