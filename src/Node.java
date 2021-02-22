import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Node class contains a list that can carry slots. This counts as a single
 * state for our search.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Node {

    private ArrayList<Slot> slist = new ArrayList<Slot>();
    private ArrayList<Course> clist = new ArrayList<Course>();
    private int upperbound = Integer.MAX_VALUE;

    /**
     * Constructor of Node which initializes the array list of slots.
     *
     * @param slotList list of slots in this node
     */
    public Node(ArrayList<Slot> slotList) {
        slist = slotList;
        this.makeCourseList();
    }

    /**
     * Divides parent node into multiple children nodes that are cases of the course
     * inserted to all possible slots. Only passes list of children nodes that
     * satisfies the hard constraints.
     *
     * @param node   parent node
     * @param course course to be inserted
     * @param hardC  hard constraint object
     * @return Filtered children nodes that fit hard constraints.
     */
    LinkedList<Node> div(Node node, Course course, HConst hardC, Eval eval) {

        LinkedList<Node> subproblem = new LinkedList<Node>();

        for (int j = 0; j < node.getSlist().size(); j++) {
            Node child = new Node(node);
            if (child.getSlist().get(j).insertCourse(course)) {
                if (hardC.checkHc(child)) {
                    subproblem.add(child);
                }
            }
        }
        return subproblem;
    }

    /**
     * Node constructor used to create deep copy of the node. Used in div()
     *
     * @param node Node to be copied.
     */
    public Node(Node node) {
        // Copy of node's list;
        ArrayList<Slot> copy = new ArrayList<Slot>();
        for (int i = 0; i < node.getSlist().size(); i++) {
            copy.add(new Slot(node.getSlist().get(i)));
        }
        this.slist = copy;
        this.makeCourseList();
    }

    /**
     * Returns total number of courses in this node.
     *
     * @return total number of courses in node
     */
    public int getTotalCourses() {
        int result = 0;
        for (int i = 0; i < slist.size(); i++) {
            result = result + slist.get(i).getList().size();
        }
        return result;
    }

    /**
     * Prints all assigned courses in this node and returns the sorted list of
     * assigned courses:slot.
     *
     * @return list of assigned courses:slot
     */
    ArrayList<String> printNode() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < slist.size(); i++) {
            String slot = slist.get(i).getShortSlotid();
            for (int j = 0; j < slist.get(i).getList().size(); j++) {
                String course = String.format("%-28s", slist.get(i).getList().get(j).getCourseid());
                list.add(course + ": " + slot);
            }
        }

        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        return list;
    }

    /**
     * Initialize the course list of for the given slots slist.
     */
    public void makeCourseList() {
        for (int i = 0; i < slist.size(); i++) {
            for (int j = 0; j < slist.get(i).getList().size(); j++) {
                this.clist.add(slist.get(i).getList().get(j));
            }
        }
        for (int i = 0; i < slist.size(); i++) {
            slist.get(i).setSlotToCourses();
        }
    }

    public int getUpperbound() {
        return upperbound;
    }

    public void setUpperbound(int upperbound) {
        this.upperbound = upperbound;
    }

    public ArrayList<Course> getClist() {
        return clist;
    }

    public void setClist(ArrayList<Course> clist) {
        this.clist = clist;
    }

    // Getters and Setters
    public ArrayList<Slot> getSlist() {
        return slist;
    }

    public void setSlist(ArrayList<Slot> slist) {
        this.slist = slist;
    }
}
