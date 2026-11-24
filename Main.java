import java.util.Scanner;

/** Menu-driven console interface for the Placement Portal. */
public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        PlacementPortal portal = new PlacementPortal();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    String name = ask("Student Name: ");
                    portal.addStudent(name, readCgpa("CGPA: "));
                    break;
                case "2":
                    portal.displayStudents();
                    break;
                case "3":
                    portal.searchStudent(ask("Student ID: "));
                    break;
                case "4":
                    String jid = ask("Job ID: ");
                    String company = ask("Company: ");
                    portal.addJob(jid, company, readCgpa("Minimum CGPA: "));
                    break;
                case "5":
                    portal.searchJob(ask("Job ID: "));
                    break;
                case "6":
                    String jobId = ask("Job ID: ");
                    String studentId = ask("Student ID: ");
                    portal.applyForJob(jobId, studentId);
                    break;
                case "7":
                    portal.showShortlist(ask("Job ID: "));
                    break;
                case "8":
                    portal.saveData();
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-8.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n===== PLACEMENT PORTAL =====");
        System.out.println("1. Add Student");
        System.out.println("2. Display Students");
        System.out.println("3. Search Student");
        System.out.println("4. Add Job");
        System.out.println("5. Search Job");
        System.out.println("6. Apply for Job");
        System.out.println("7. Show Shortlist");
        System.out.println("8. Exit");
        System.out.print("Enter choice: ");
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    /** Reads a CGPA; returns NaN when the text is not a number (validated later). */
    private static double readCgpa(String prompt) {
        try {
            return Double.parseDouble(ask(prompt).trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
