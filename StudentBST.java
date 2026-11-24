import java.util.ArrayList;

/**
 * Binary Search Tree of students. The student ID is the key.
 *   insert  : O(log N) average, O(N) worst (skewed tree)
 *   search  : O(log N) average, O(N) worst
 *   inorder : O(N)
 */
public class StudentBST {

    private static class Node {
        Student data;
        Node left, right;
        Node(Student data) { this.data = data; }
    }

    private Node root;
    private int size;

    /**
     * Compares two IDs "naturally": shorter ID first, then alphabetical.
     * This makes ST99 < ST100, which plain String.compareTo would get wrong.
     */
    private int compareIds(String a, String b) {
        if (a.length() != b.length()) return a.length() - b.length();
        return a.compareTo(b);
    }

    /** Inserts a student. Returns false if the ID already exists. */
    public boolean insert(Student s) {
        if (root == null) {
            root = new Node(s);
            size++;
            return true;
        }
        Node cur = root;
        while (true) {
            int cmp = compareIds(s.getId(), cur.data.getId());
            if (cmp == 0) return false;               // duplicate ID rejected
            if (cmp < 0) {
                if (cur.left == null) { cur.left = new Node(s); size++; return true; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new Node(s); size++; return true; }
                cur = cur.right;
            }
        }
    }

    /** Returns the student with the given ID, or null if not present. */
    public Student search(String id) {
        Node cur = root;
        while (cur != null) {
            int cmp = compareIds(id, cur.data.getId());
            if (cmp == 0) return cur.data;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return null;
    }

    /** Prints all students in sorted order of ID (in-order traversal). */
    public void inorder() {
        inorder(root);
    }

    private void inorder(Node n) {
        if (n == null) return;
        inorder(n.left);
        System.out.println(n.data);
        inorder(n.right);
    }

    /** Returns all students in sorted order (used for saving to file). */
    public ArrayList<Student> getAll() {
        ArrayList<Student> list = new ArrayList<>();
        collect(root, list);
        return list;
    }

    private void collect(Node n, ArrayList<Student> list) {
        if (n == null) return;
        collect(n.left, list);
        list.add(n.data);
        collect(n.right, list);
    }

    public int size()        { return size; }
    public boolean isEmpty() { return size == 0; }
}
