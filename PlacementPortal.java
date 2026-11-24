import java.io.*;
import java.util.*;

/**
 * Core logic: students (BST), jobs (HashMap), shortlist (PriorityQueue)
 * and persistence (placements.txt).
 *
 * File format (fields separated by '|'):
 *   S|studentId|name|cgpa
 *   J|jobId|company|minCgpa
 *   A|jobId|studentId
 */
public class PlacementPortal {
    private final StudentBST students = new StudentBST();
    private final HashMap<String, Job> jobs = new HashMap<>();
    private int nextId = 101;                       // first ID is ST101
    private static final String FILE_NAME = "placements.txt";

    public PlacementPortal() {
        loadData();
    }

    // ---------------------------------------------------------------- students

    public void addStudent(String name, double cgpa) {
        name = (name == null) ? "" : name.trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be blank.");
            return;
        }
        if (name.contains("|")) {
            System.out.println("Name cannot contain the '|' character.");
            return;
        }
        if (!validCgpa(cgpa)) {
            System.out.println("Invalid CGPA. Enter a value between 0 and 10.");
            return;
        }
        String id = "ST" + nextId++;
        students.insert(new Student(id, name, cgpa));
        System.out.println("Student added: " + id + " | " + name + " | CGPA " + cgpa);
    }

    public void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }
        System.out.println("--- Students (sorted by ID) ---");
        students.inorder();
    }

    public void searchStudent(String id) {
        Student s = students.search(id.trim().toUpperCase());
        System.out.println(s != null ? s : "Student " + id.trim().toUpperCase() + " not found.");
    }

    // -------------------------------------------------------------------- jobs

    public void addJob(String id, String company, double minCgpa) {
        id = (id == null) ? "" : id.trim().toUpperCase();
        company = (company == null) ? "" : company.trim();
        if (id.isEmpty() || company.isEmpty()) {
            System.out.println("Job ID and company cannot be blank.");
            return;
        }
        if (id.contains("|") || company.contains("|")) {
            System.out.println("Job ID and company cannot contain the '|' character.");
            return;
        }
        if (!validCgpa(minCgpa)) {
            System.out.println("Invalid CGPA. Enter a value between 0 and 10.");
            return;
        }
        if (jobs.containsKey(id)) {
            System.out.println("Job ID " + id + " already exists.");
            return;
        }
        jobs.put(id, new Job(id, company, minCgpa));
        System.out.println("Job added: " + id + " | " + company + " | Minimum CGPA " + minCgpa);
    }

    public void searchJob(String id) {
        Job j = jobs.get(id.trim().toUpperCase());
        System.out.println(j != null ? j : "Job " + id.trim().toUpperCase() + " not found.");
    }

    // ------------------------------------------------------------ apply / rank

    public void applyForJob(String jobId, String studentId) {
        jobId = jobId.trim().toUpperCase();
        studentId = studentId.trim().toUpperCase();

        Job job = jobs.get(jobId);                       // HashMap lookup  O(1)
        if (job == null) {
            System.out.println("Job " + jobId + " not found.");
            return;
        }
        Student s = students.search(studentId);          // BST lookup  O(log N)
        if (s == null) {
            System.out.println("Student " + studentId + " not found.");
            return;
        }
        if (job.hasApplied(studentId)) {
            System.out.println("Duplicate application: already applied for this job.");
            return;
        }
        if (!job.apply(s)) {
            System.out.println("Not eligible: CGPA is below the minimum required.");
            return;
        }
        System.out.println("Application submitted: " + studentId + " | " + jobId);
    }

    public void showShortlist(String jobId) {
        jobId = jobId.trim().toUpperCase();
        Job job = jobs.get(jobId);
        if (job == null) {
            System.out.println("Job " + jobId + " not found.");
            return;
        }
        PriorityQueue<Student> pq = job.buildShortlist(students);
        if (pq.isEmpty()) {
            System.out.println("No applicants yet for " + jobId + ".");
            return;
        }
        System.out.println("Shortlist for " + jobId + " (" + job.getCompany() + "):");
        int rank = 1;
        while (!pq.isEmpty()) {                          // poll() -> highest CGPA first
            Student s = pq.poll();
            System.out.println(rank++ + ". " + s.getName() + " | " + s.getCgpa());
        }
    }

    // ----------------------------------------------------------- file handling

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students.getAll()) {
                bw.write("S|" + s.getId() + "|" + s.getName() + "|" + s.getCgpa());
                bw.newLine();
            }
            TreeSet<String> jobIds = new TreeSet<>(jobs.keySet());   // sorted output
            for (String id : jobIds) {
                Job j = jobs.get(id);
                bw.write("J|" + j.getJobId() + "|" + j.getCompany() + "|" + j.getMinCgpa());
                bw.newLine();
            }
            for (String id : jobIds) {
                for (String sid : new TreeSet<>(jobs.get(id).getApplicantIds())) {
                    bw.write("A|" + id + "|" + sid);
                    bw.newLine();
                }
            }
            System.out.println("Data saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private void loadData() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                try {
                    switch (p[0]) {
                        case "S":
                            students.insert(new Student(p[1], p[2], Double.parseDouble(p[3])));
                            if (p[1].startsWith("ST")) {      // keep ID counter ahead of loaded IDs
                                nextId = Math.max(nextId, Integer.parseInt(p[1].substring(2)) + 1);
                            }
                            break;
                        case "J":
                            jobs.put(p[1], new Job(p[1], p[2], Double.parseDouble(p[3])));
                            break;
                        case "A":
                            Job j = jobs.get(p[1]);
                            if (j != null) j.restoreApplicant(p[2]);
                            break;
                        default:
                            break;
                    }
                } catch (RuntimeException badLine) {
                    // skip a damaged line instead of crashing the whole program
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load data: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------- helpers

    private boolean validCgpa(double c) {
        return !Double.isNaN(c) && c >= 0 && c <= 10;
    }
}
