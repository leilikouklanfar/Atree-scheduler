import java.util.ArrayList;

/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Eval class is used to evaluate soft constraint. Contains various parameters
 * to evaluate a node to see how well it satisfies the soft constraints.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Eval {
    private ArrayList<Preference> pref;
    private ArrayList<Pair> pair;
    private int minFilledPen;
    private int prefPen;
    private int pairPen;
    private int eval;
    private int upperBound;
    private int secPen;
    // Weightings of different parts of the Eval function.
    private double penCoursemin = 0.0;
    private double penLabsmin = 0.0;
    private double penSection = 0.0;
    private double penNotPaired = 0.0;
    private double wMinFilled = 0.0;
    private double wPref = 0.0;
    private double wPair = 0.0;
    private double wSecDiff = 0.0;

    /**
     * Evaluation constructor. Creates Eval object used to determine evaluation of a
     * node state.
     *
     * @param pref         List of preferences
     * @param pair         List of pairs
     * @param wMinFilled   weight of penalties for slots not being filled to minimum
     *                     capacity
     * @param wPref        weight of penalties for not meeting preference
     * @param wPair        weight of penalties for pairs not being satisfied
     * @param wSecDiff     weight of penalties for same sections being assigned to
     *                     same slot
     * @param penCoursemin penalty value for course slot not being filled to minimum
     *                     capacity
     * @param penLabsmin   penalty value for lab/tutorial slot not being filled to
     *                     minimum capacity
     * @param penSection   penalty for same sections being assigned to same slot
     * @param penNotPaired penalty for pairs not being satisfied
     */
    public Eval(ArrayList<Preference> pref, ArrayList<Pair> pair, double wMinFilled, double wPref, double wPair,
            double wSecDiff, double penCoursemin, double penLabsmin, double penSection, double penNotPaired) {
        this.pref = pref;
        this.pair = pair;
        this.wMinFilled = wMinFilled;
        this.wPref = wPref;
        this.wPair = wPair;
        this.wSecDiff = wSecDiff;
        this.penCoursemin = penCoursemin;
        this.penLabsmin = penLabsmin;
        this.penSection = penSection;
        this.penNotPaired = penNotPaired;
    }

    /**
     * Evaluates a Node state as an integer depending on soft constraints.
     *
     * @param node   Node state to be evaluated.
     * @param course Courses that have not been inserted to the Node state
     * @return Integer evaluation value of input node.
     */
    public int checkEval(Node node, ArrayList<Course> course) {
        // Minfilled
        minFilledPen = (int) (minFilled(node) * wMinFilled);
        prefPen = (int) (prefPen(node) * wPref);
        pairPen = (int) (pairPen(node, course) * wPair);
        secPen = (int) (secPen(node) * wSecDiff);
        eval = minFilledPen + prefPen + pairPen + secPen;
        return eval;
    }

    /**
     * Calculates the upperBound.
     *
     * @param node node to check
     * @return upperBound the calculated upper bound
     */
    public int checkUpperBound(Node node, ArrayList<Course> course) {
        int minFilledUpper = (int) (minFilledUpper(node, course) * wMinFilled);
        upperBound = minFilledUpper;
        return upperBound;
    }

    /**
     * Prints out the penalty values stored in Eval object for most recently checked
     * node.
     *
     * @return Evaluation value of the most recently checked node.
     */
    public String printEval() {
        System.out.println("minFilled Penalty: " + minFilledPen);
        System.out.println("preference Penalty: " + prefPen);
        System.out.println("pair Penalty: " + pairPen);
        System.out.println("section Penalty: " + secPen);
        System.out.println("Eval: " + eval);
        return "Eval: " + eval;
    }

    /**
     * Evaluate section penalty. Check each slot to see if it has duplicates. For
     * each slot, amount of duplicate sections in a slot is counted then penalty
     * value is multiplied to it. Penalty values of all slots are added up and
     * returned.
     *
     * @param node Node to evaluate.
     * @return Section penalty value in integer.
     */
    private int secPen(Node node) {
        ArrayList<Slot> slotList = node.getSlist();
        int result = 0;
        // For all slots
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            result = (int) (result + penSection * duplicateInSlot(slot));
        }
        return result;
    }

    /**
     * Separate method to count duplicate sections in a slot used in secPen method.
     *
     * @param s the slot to seach
     * @return the number of duplicates found
     */
    private int duplicateInSlot(Slot s) {
        ArrayList<String> inCourses = new ArrayList<String>();
        // For all courses in slot
        for (int i = 0; i < s.getList().size(); i++) {
            Course course = s.getList().get(i);
            // If course section and CPSC
            if (course.getCoursename().matches("CPSC")) {
                inCourses.add(sectionConvert(s.getList().get(i)));
            }
        }
        ArrayList<String> dupe = new ArrayList<String>();

        // Finding duplicate courses in slot
        for (String e : inCourses) {
            if (!dupe.contains(e)) {
                dupe.add(e);
            }
        }
        return inCourses.size() - dupe.size();
    }

    /**
     * Separate method to shorten down course name so it helps with counting
     * duplicates in duplicateInSlot method.
     *
     * @param course course to work on
     * @return a string of the course name followed by the course number
     */
    private String sectionConvert(Course course) {
        return course.getCoursename() + course.getCoursenumber();
    }

    /**
     * Evaluate pair penalty. Check each slot to see if it satisfies pair
     * requirements. Checks if a slot meets the pair requirement and gets a penalty
     * value if it's not met. Values from all slots are added up.
     *
     * @param node Node state to evaluate
     * @return Returns pair penalty value for this node.
     */
    private int pairPen(Node node, ArrayList<Course> courseList) {
        int count = 0;
        // For all pair
        for (int i = 0; i < pair.size(); i++) {
            String course1 = pair.get(i).getCourse1().getCourseid();
            String c1slot = "";
            String course2 = pair.get(i).getCourse2().getCourseid();
            String c2slot = "";
            // For all courses
            node.makeCourseList();
            for (int j = 0; j < node.getClist().size(); j++) {

                Course course = node.getClist().get(j);
                String courseName = node.getClist().get(j).getCourseid();
                if (courseName.matches(course1)) {
                    c1slot = course.getMySlot();
                }
                if (courseName.matches(course2)) {
                    c2slot = course.getMySlot();
                }
            }
            if (!c1slot.matches(c2slot)) {
                count++;
            }
        }
        return (int) (penNotPaired * count);
    }

    /**
     * Evaluate preference penalty. Check each slot to see if it satisfies
     * preference requirements. Checks if a slot meets the preference requirement
     * and gets a preference penalty value if it's not met. Values from all slots
     * are added up.
     *
     * @param node Node to evaluate
     * @return returns preference penalty value for this node.
     */
    private int prefPen(Node node) {
        int result = 0;

        // For all preferences
        for (int i = 0; i < pref.size(); i++) {
            // Course in preference
            String pCourse = pref.get(i).getCourse().getCourseid();
            // Slot in preference
            String pSlot = pref.get(i).getSlot().getShortSlotid();
            boolean pLab = pref.get(i).getSlot().isLab();
            ArrayList<Slot> slotList = node.getSlist();
            // For all slots
            for (int j = 0; j < slotList.size(); j++) {
                // Current slot
                Slot slot = slotList.get(j);
                String slotName = slotList.get(j).getShortSlotid();
                boolean sLab = slot.isLab();
                // If slot name matches with preferred slot
                if (slotName.matches(pSlot) && !pLab && !sLab) {
                    // If preferred course is not in slot and in list of courses
                    if (!checkForCourse(slot, pCourse)) {
                        result = result + pref.get(i).getPenalty();
                    }
                }
                if (slotName.matches(pSlot) && pLab && sLab) {
                    // If preferred course is not in slot and in list of courses
                    if (!checkForCourse(slot, pCourse)) {
                        result = result + pref.get(i).getPenalty();
                    }
                }
            }
        }
        return result;
    }

    /**
     * A method to check if a course is in a slot.
     *
     * @param slot       Slot to check.
     * @param prefCourse Courseid name in string.
     * @return returns true if course is in slot
     */
    private boolean checkForCourse(Slot slot, String prefCourse) {
        ArrayList<String> inslot = new ArrayList<String>();
        for (int i = 0; i < slot.getList().size(); i++) {
            inslot.add(slot.getList().get(i).getCourseid());
        }
        if (inslot.contains(prefCourse)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Evaluate minimum filled penalty. Check each slot to see how many courses have
     * been filled. If a slot haven't been filled up to the minimum value, amount of
     * empty slots are added to the resulting penalty value.
     *
     * @param node Node to evaluate
     * @return minFilled penalty value
     */
    private int minFilled(Node node) {
        int result = 0;
        ArrayList<Slot> slotList = node.getSlist();
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            int coursemin = slot.getCoursemin();
            int coursefilled = slot.getList().size();

            if (slot.isLab() && coursemin >= coursefilled) {
                result = (int) (result + penLabsmin * (coursemin - coursefilled));
            }
            if (!slot.isLab() && coursemin >= coursefilled) {
                result = (int) (result + penCoursemin * (coursemin - coursefilled));
            }
        }
        return result;
    }

    /**
     * Evaluate maximum minimum filled penalty possible in this Node.
     *
     * @param node the node
     * @param cl   the array list of course and labs
     * @return result the max/min penalty
     */
    private double minFilledUpper(Node node, ArrayList<Course> cl) {
        ArrayList<int[]> slots = new ArrayList<int[]>();

        // Slot simulation
        for (int i = 0; i < node.getSlist().size(); i++) {
            Slot thisslot = node.getSlist().get(i);
            int[] slot = new int[2];
            slot[0] = thisslot.getCoursemax();
            slot[1] = thisslot.getCoursemin() - thisslot.getCoursenum();
            slots.add(slot);
        }

        int clsize = cl.size();
        while (clsize != 0) {
            clsize--;
            int imin = Integer.MAX_VALUE;
            // Find index of slot with minimum coursenum
            for (int i = 0; i < slots.size(); i++) {
                int[] current = slots.get(0);
                if (current[1] < imin && current[0] != 0) {
                    imin = i;
                }
            }
            slots.get(imin)[0]--;
            slots.get(imin)[1]--;
        }

        // Add up all minfilled;
        int result = 0;
        for (int i = 0; i < slots.size(); i++) {
            result += slots.get(i)[1];
        }

        return result;
    }
}
