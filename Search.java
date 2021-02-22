import java.util.ArrayList;
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
 * Class search contains problem's hard and soft constraint information and
 * total number of courses required for our search.
 *
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Search {

    int totalCourses;
    HConst hc;
    Eval eval;
    Node currentBest;

    /**
     * Constructor of Search which initilizes hard constraints, eval and total
     * number of courses.
     *
     * @param hc           hardConstraints of the search
     * @param eval         Eval value
     * @param totalCourses total number of courses
     */
    public Search(HConst hc, Eval eval, int totalCourses) {
        this.totalCourses = totalCourses;
        this.hc = hc;
        this.eval = eval;

        System.out.println("totalCourses: " + totalCourses);

    }

    /**
     * Solve is a recursive method that starts with a starting node and a list of
     * courses to insert as an input. It calls itself recursively on a list of
     * subproblems until there are no more courses that can be placed in a state. If
     * there are more than one subproblems to return, return the subproblem with the
     * lowest evaluation penalty.
     *
     * @param node   - node
     * @param course - array list of courses
     * @return - the node with the best eval
     */
    Node Solve(Node node, ArrayList<Course> course) {
        // If Last course has been placed, return result
        currentBest = node;

        // System.out.println("Parent Node");
        // node.printNode();
        if (course.isEmpty() && hc.checkHc(node)) {
            return node;
        } else if (course.isEmpty() && !hc.checkHc(node)) {
            return null;
        } else {

            ArrayList<Course> courseList = new ArrayList<Course>();

            for (int i = 0; i < course.size(); i++) {
                courseList.add(course.get(i));
            }
            Course currentCourse = courseList.get(0);
            courseList.remove(0);
            LinkedList<Node> subproblems = new LinkedList<Node>();
            subproblems = node.div(node, currentCourse, hc, eval);
            LinkedList<Node> result = new LinkedList<Node>();
            for (int i = 0; i < subproblems.size(); i++) {
                result.add(Solve(subproblems.get(i), courseList));
            }

            // Return lowest eval node in valid subproblems
            int bestEval = Integer.MAX_VALUE;
            Node best = null;

            // For all resulting subproblems
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i) != null) {
                    int currentEval = eval.checkEval(result.get(i), courseList);
                    if (best == null) {
                        bestEval = currentEval;
                        best = result.get(i);
                    }
                    if (currentEval < bestEval && result.get(i).getTotalCourses() == totalCourses) {
                        bestEval = currentEval;
                        best = result.get(i);
                    }
                }
            }
            return best;
        }
    }
}
