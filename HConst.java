import java.util.ArrayList;
import java.util.HashMap;

/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * HConst or Hard Constraint class is used to determine hard constraint.
 * Contains various parameters to evaluate a node to see if it satisfies the
 * hard constraints.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class HConst {
    ArrayList<Compatible> comp;
    ArrayList<Unwanted> unt;
    ArrayList<PartAssign> prt;

    /**
     * HC constructor, Creates HC object used to determine hard constraint of a node
     * state.
     *
     * @param comp list of incompatible courses
     * @param unt  list of unwanted courses in slot
     * @param prt  list of partial assignments that needs to be satisfied.
     */
    public HConst(ArrayList<Compatible> comp, ArrayList<Unwanted> unt, ArrayList<PartAssign> prt) {
        this.comp = comp;
        this.unt = unt;
        this.prt = prt;
    }

    /**
     * Checks for hard constraint of a node state. Only returns true if all hard
     * constraint checks are met.
     *
     * @param node Node to be checked
     * @return returns true if all hard constraint requirements are met
     */
    public boolean checkHc(Node node) {
        // For all compatibles
        if (compatCheck(node) && unwantCheck(node) && partialCheck(node) && check9(node) && check500(node)
                && tuesdayCheck(node) && timeCheck(node) && overLapCheck(node) && sectionLabOverlapCheck(node)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if for a course section, there is no corresponding lab in same time
     * slot. Compares two matcher string list created from all courses slots with
     * string list created from all lab slots in this node.
     *
     * @return If there is disjoint with two lists, return true
     */
    private boolean sectionLabOverlapCheck(Node node) {
        ArrayList<Slot> sl = node.getSlist();
        ArrayList<Integer> indexOfLabSlots = new ArrayList<Integer>();
        HashMap<String, Integer> overLapMap = new HashMap<>();
        int i = 1;

        for (int slot = 0; slot < sl.size(); slot++) {
            ArrayList<Course> courses = sl.get(slot).getList();
            for (int j = 0; j < courses.size(); j++) {
                if (!sl.get(slot).isLab()) {
                    Course course = courses.get(j);
                    String name = course.getCoursename();
                    String number = course.getCoursenumber();
                    String section = course.getLecturenumber();
                    String day = sl.get(slot).getDay();
                    String time = sl.get(slot).getTime();
                    overLapMap.put(name + number + section + day + time, i);
                    i++;
                } else {
                    indexOfLabSlots.add(j);
                }
            }
        }

        for (int j = 0; j < indexOfLabSlots.size(); j++) {
            ArrayList<Course> labs = sl.get(indexOfLabSlots.get(j)).getList();

            for (int l = 0; l < labs.size(); l++) {
                Course lab = labs.get(l);

                String name = lab.getCoursename();
                String number = lab.getCoursenumber();
                String section = lab.getLecturenumber();
                String day = sl.get(j).getDay();
                String time = sl.get(j).getTime();

                // For those pesky labs that don't have coursenumbers
                if (section.matches("none")) {
                    for (int k = 0; k < lab.getMatchingSections().size(); k++) {
                        String sect = lab.getMatchingSections().get(k);
                        if (overLapMap.containsKey(name + number + sect + day + time)) {
                            return false;
                        } else {
                            overLapMap.put(name + number + sect + day + time, (-1 * i));
                            i++;
                        }
                    }
                } else if (overLapMap.containsKey(name + number + section + day + time)) {
                    return false;
                } else {
                    overLapMap.put(name + number + section + day + time, (-1 * i));
                    i++;
                }
            }
        }

        return true;

    }

    /**
     * Checks all slots in a Node state to see if the special CPSC courses 813 and
     * 913 don't overlap with CPSC 313 and CPSC 413 respectively.
     *
     * @param node Node state to be checked.
     * @return true special course overlap requirement is met
     */
    private boolean overLapCheck(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // Separate lab list to check separately for overlap.
        ArrayList<Slot> labList = new ArrayList<Slot>();
        // For all slots
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            // If current slot is lab
            if (slot.isLab()) {
                labList.add(slot);
            }
        }
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            // If CPSC 813 is in slot slot
            if (checkCoursenum(slot, "813")) {
                // If CPSC 313 is in current slot slot or CPSC 313 is in one of the labs that
                // have
                // the same time.
                if (checkCoursenum(slot, "313") || overLapLabCheck(slot, labList, "313")) {
                    result = false;
                }
            }
            // If CPSC 913 is in slot slot
            if (checkCoursenum(slot, "913")) {
                // If CPSC 413 is in current slot slot or CPSC 413 is in one of the labs that
                // have
                // the same time.
                if (checkCoursenum(slot, "413") || overLapLabCheck(slot, labList, "413")) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * A method that checks a list of labs to find a labslot with overlapping
     * timeslot as input slot slot Then checks if the input course is in that
     * labslot. Used in overLapCheck()
     *
     * @param slot      Slot to check
     * @param lablist   list of labs to check for overlapping times with slot
     * @param coursenum coursenumber of the course to check if it exists in
     *                  overlapping lab slot
     * @return returns true if course exists in labslot that has overlapping time
     *         with slot.
     */
    private boolean overLapLabCheck(Slot slot, ArrayList<Slot> lablist, String coursenum) {
        boolean result = true;
        // Input slot'slot day and time
        String day = slot.getDay();
        String time = slot.getTime();
        // For all labs in input lablist
        for (int i = 0; i < lablist.size(); i++) {
            String labDay = lablist.get(i).getDay();
            String labTime = lablist.get(i).getTime();
            // If input coursenumber exists in input slot and current lab'slot day and time
            // matches input slot'slot day and time
            if (checkCoursenumLab(slot, coursenum) && day.matches(labDay) && time.matches(labTime)) {
                result = result && false;
            }
        }
        return result;
    }

    /**
     * Checks all slots in a Node to see if the special CPSC courses 813 and 913 are
     * placed into correct timeslots.
     *
     * @param node Node to check for constraint
     * @return returns true if CPSC 813 or 913 has been placed into correct
     *         timeslots.
     */
    private boolean timeCheck(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // For all slots
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            int time = 0;
            // Convert slot time to number
            if (slot.getTime().length() > 4) {
                String split = slot.getTime().substring(0, 2) + slot.getTime().substring(3, 5);
                time = Integer.parseInt(split);
            } else {
                String split = slot.getTime().substring(0, 1) + slot.getTime().substring(2, 4);
                time = Integer.parseInt(split);
            }

            // If current slot has courses with 813
            if (checkCoursenum(slot, "813")) {
                // If current slot is TU and between 18:00~1900
                if (slot.getDay().matches("TU") && time >= 1800 && time <= 1900) {
                    result = true;
                } else {
                    result = result && false;
                }
            }
            // If current slot has courses with 913
            if (checkCoursenum(slot, "913")) {
                // If current slot is TU and between 18:00~19:00
                if (slot.getDay().matches("TU") && time >= 1800 && time <= 1900) {
                    result = result && true;
                } else {
                    result = result && false;
                }
            }
        }
        return result;
    }

    /**
     * Checks if the CPSC lab course is inside a slot.
     *
     * @param slot      Slot to check
     * @param coursenum Course number to check
     * @return returns true if course with specific course number is inside a lab
     *         slot.
     */
    private boolean checkCoursenumLab(Slot slot, String coursenum) {
        ArrayList<String> inslot = new ArrayList<String>();
        // For all courses in input slot
        for (int j = 0; j < slot.getList().size(); j++) {
            // Only grab CPSC labs to check if inside input slot
            if (slot.getList().get(j).isLab() && slot.getList().get(j).getCoursename().matches("CPSC")) {
                inslot.add(slot.getList().get(j).getCoursenumber());
            }
        }
        // If specified input coursenumber is in input slot.
        if (inslot.contains(coursenum)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the CPSC course with a specific course number is inside a course
     * slot.
     *
     * @param slot      Slot to check
     * @param coursenum Course number to check
     * @return returns true if course with specific course number is inside a course
     *         slot.
     */
    private boolean checkCoursenum(Slot slot, String coursenum) {
        ArrayList<String> inslot = new ArrayList<String>();
        // For all courses in input slot
        for (int j = 0; j < slot.getList().size(); j++) {
            // Only grab CPSC courses to check if inside input slot
            if (!slot.getList().get(j).isLab() && slot.getList().get(j).getCoursename().matches("CPSC")) {
                inslot.add(slot.getList().get(j).getCoursenumber());
            }
        }

        // If specified input coursenumber is in input slot.
        if (inslot.contains(coursenum)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks all Tuesday 11:00~12:30 slots in a Node and checks if any CPSC courses
     * are inside.
     *
     * @param node Node to check
     * @return return true if no courses are found in Tuesday 11:00~12:30 slots
     */
    private boolean tuesdayCheck(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // For all slots in
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);
            int time = 0;
            // Convert slot time to number
            if (slot.getTime().length() > 4) {
                String split = slot.getTime().substring(0, 2) + slot.getTime().substring(3, 5);
                time = Integer.parseInt(split);
            } else {
                String split = slot.getTime().substring(0, 1) + slot.getTime().substring(2, 4);
                time = Integer.parseInt(split);
            }
            // If current slot is TU 11:00~12:30
            if (slot.getDay().matches("TU") && time >= 1100 && time <= 1230) {
                ArrayList<Course> courseList = slot.getList();
                // For all courses in current slots
                for (int j = 0; j < courseList.size(); j++) {
                    // If CPSC
                    if (courseList.get(j).getCoursename().matches("CPSC")) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks all slots in a Node to see if 500 level courses are all in separate
     * time slots. check500Slot() method is used to count 500 level courses in a
     * slot for comparison.
     *
     * @param node Node to check
     * @return return true if no multiple 500 level courses are found in a slot.
     */
    private boolean check500(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // For all slots in node
        for (int i = 0; i < slotList.size(); i++) {
            Slot slot = slotList.get(i);

            // If course and there are more than one 500 course
            if (!slotList.get(i).isLab() && check500Slot(slot) >= 2) {
                result = result && false;
            } else {
                result = result && true;
            }
        }
        return result;
    }

    /**
     * Counts how many 500+ course are in a course slot.
     *
     * @param slot the slot to search
     * @return count number 500+ level courses
     */
    private int check500Slot(Slot slot) {
        int count = 0;
        // For all courses
        for (int j = 0; j < slot.getList().size(); j++) {
            Course course = slot.getList().get(j);
            int coursenum = Integer.parseInt(course.getCoursenumber());
            // If course section, CPSC course and more than 500
            if (!course.isLab() && course.getCoursename().matches("CPSC") && coursenum >= 500) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks all slots in a node to see if all night courses are placed in correct
     * time slots. check9Slot method is used to check if a slot has a course that
     * should be placed in a night slot.
     *
     * @param node Node to check.
     * @return returns true of all night courses are in correct time slots.
     */
    private boolean check9(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // For all slots in node
        for (int i = 0; i < slotList.size(); i++) {
            int time = 0;
            Slot slot = slotList.get(i);
            // If slot has ##:## time format
            if (slot.getTime().length() > 4) {
                time = Integer.parseInt(slot.getTime().substring(0, 2));
            } else {
                time = Integer.parseInt(slot.getTime().substring(0, 1));
            }
            if (time < 18 && check9Slot(slot)) {
                result = result && false;
            } else {
                result = result && true;
            }
        }
        return result;
    }

    /**
     * Checks if there is a nightcourse in this slot.
     *
     * @param slot slot to check
     * @return result true if there is a night course, false otherwise
     */
    private boolean check9Slot(Slot slot) {
        boolean result = false;
        // For all courses in slot
        for (int j = 0; j < slot.getList().size(); j++) {
            // If current course is CPSC course
            if (!slot.getList().get(j).isLab() && slot.getList().get(j).getCoursename().matches("CPSC")) {
                // If course is night course
                Course course = slot.getList().get(j);
                if (course.getLecturenumber().charAt(0) == '9') {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Checks all partial assignment list to see if they are fulfilled in this node.
     *
     * @param node Node to check
     * @return Returns true if all partial assignments are fulfilled.
     */
    private boolean partialCheck(Node node) {
        boolean result = true;
        ArrayList<Slot> slotList = node.getSlist();
        // For unwanted list
        for (int i = 0; i < prt.size(); i++) {
            String p_slot = prt.get(i).getSlot().getShortSlotid();
            String pCourse = prt.get(i).getCourse().getCourseid();
            // For all slots in node
            for (int j = 0; j < slotList.size(); j++) {
                Slot slot = slotList.get(j);
                // If current slot has partial assign course
                if (slotCheck(slot, pCourse)) {
                    // If current slot'slot day+time matches partial assign course
                    if (slot.getShortSlotid().equals(p_slot)) {
                        result = true;
                    } else {
                        result = false;
                    }
                } else {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Checks all unwanted list to see if they are fulfilled in this node.
     *
     * @param node Node to check
     * @return Returns true if all unwanted requirements are fulfilled.
     */
    private boolean unwantCheck(Node node) {
        boolean result = true;
        // For unwanted list
        for (int i = 0; i < unt.size(); i++) {
            String uslot = unt.get(i).getSlot().getShortSlotid();
            String ucourse = unt.get(i).getCourse().getCourseid();
            // For all slots in Node
            for (int j = 0; j < node.getSlist().size(); j++) {
                Slot slot = node.getSlist().get(j);
                // If current slot is unwanted slot
                if (slot.getShortSlotid().matches(uslot)) {
                    // If there is unwanted course in current slot
                    if (slotCheck(slot, ucourse)) {
                        result = result && false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns true if specific course is in slot.
     *
     * @param slot   slot being searched
     * @param course course looked for
     * @return true if course is in slot, false otherwise
     */
    private boolean slotCheck(Slot slot, String course) {
        ArrayList<String> inslot = new ArrayList<String>();
        // For all courses in input slot
        for (int j = 0; j < slot.getList().size(); j++) {
            inslot.add(slot.getList().get(j).getCourseid());
        }
        // If input course is in input slot
        if (inslot.contains(course)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks all slots to see if it fulfills the compatibility requirement.
     *
     * @param node Slot to check
     * @return return true if all slots fulfill the compatibility requirement.
     */
    private boolean compatCheck(Node node) {
        boolean result = true;
        ArrayList<Slot> slots = node.getSlist();
        // For all slots in node
        for (int i = 0; i < slots.size(); i++) {

            // If current slot passes all checks in incompatibility list
            if (compatCheckSlot(slots.get(i))) {
                result = result && true;
            } else {
                result = result && false;
            }
        }
        return result;
    }

    /**
     * Checks inside a single slot to see if it fills the compatibility requirements
     * in a compatibility list. Used with compatCheck() method.
     *
     * @param slot slot to check
     * @return returns true if all compatibility requirements are met for the slot
     */
    private boolean compatCheckSlot(Slot slot) {

        boolean fResult = true;
        // For all incompatible list
        for (int i = 0; i < comp.size(); i++) {
            // Current two incompatible courses
            String course1 = comp.get(i).getCourse1().getCourseid();
            String course2 = comp.get(i).getCourse2().getCourseid();
            ArrayList<String> inslot = new ArrayList<String>();

            // For all courses in input slot
            for (int j = 0; j < slot.getList().size(); j++) {
                inslot.add(slot.getList().get(j).getCourseid());
            }
            // System.out.println(inslot.toString());
            // System.out.println(course1);
            // System.out.println(course2);

            // If two incompatible courses are in this slot.
            if (inslot.contains(course1) && inslot.contains(course2)) {
                fResult = fResult && false;
            } else {
                fResult = fResult && true;
            }
        }
        // System.out.println(fResult);
        return fResult;
    }
}
