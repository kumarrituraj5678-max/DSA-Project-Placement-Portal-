import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * A job opening. Applicant IDs are kept in a HashSet so that
 * duplicate applications are detected in O(1) average time.
 */
public class Job {
    private final String jobId;
    private final String company;
    private final double minCgpa;
    private final HashSet<String> applicantIds = new HashSet<>();

    public Job(String jobId, String company, double minCgpa) {
        this.jobId = jobId;
        this.company = company;
        this.minCgpa = minCgpa;
    }

    public String getJobId()   { return jobId; }
    public String getCompany() { return company; }
    public double getMinCgpa() { return minCgpa; }
    public HashSet<String> getApplicantIds() { return applicantIds; }

    public boolean hasApplied(String studentId) {
        return applicantIds.contains(studentId);
    }

    /**
     * Accepts the application only if the student is eligible AND has not
     * applied before. Returns true when the application is accepted.
     */
    public boolean apply(Student s) {
        if (s.getCgpa() < minCgpa) return false;
        return applicantIds.add(s.getId());   // add() returns false for a duplicate
    }

    /** Used only while loading from file (eligibility was checked earlier). */
    void restoreApplicant(String studentId) {
        applicantIds.add(studentId);
    }

    /**
     * Builds a max-priority queue of applicants: higher CGPA comes out first,
     * and the student ID breaks ties. Cost: O(K log K) for K applicants.
     */
    public PriorityQueue<Student> buildShortlist(StudentBST students) {
        PriorityQueue<Student> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Student::getCgpa).reversed()
                          .thenComparing(Student::getId));
        for (String id : applicantIds) {
            Student s = students.search(id);
            if (s != null) pq.add(s);
        }
        return pq;
    }

    @Override
    public String toString() {
        return jobId + " | " + company + " | Minimum CGPA " + minCgpa
                + " | Applicants: " + applicantIds.size();
    }
}
