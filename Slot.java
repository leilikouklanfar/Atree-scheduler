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
 * A slot class contains a slot'slot information specified from the input.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Slot {
    private String day;
    private String time;
    private int coursemax;
    private int coursemin;
    private int coursenum = 0;
    private ArrayList<Course> list = new ArrayList<Course>();
    private boolean isLab = false;
    private String slotid = "empty";

    /**
     * Creates a Slot object with following parameters. Slot object contains a list
     * of courses
     *
     * @param day       Day of the course MO/TU
     * @param startTime Start time of the course '##:##'
     * @param coursemax Maximum course capacity for this slot
     * @param coursemin Minimum courses that would be nice to have in this slot.
     * @param isLab     indicates if this slot is a lab slot
     */
    public Slot(String day, String startTime, int coursemax, int coursemin, boolean isLab) {
        this.day = day;
        this.time = startTime;
        this.coursemax = coursemax;
        this.coursemin = coursemin;
        this.isLab = isLab;
        this.slotid = day + " " + startTime + " " + coursemax + " " + coursemin;
        this.setSlotToCourses();
    }

    /**
     * Constructor used to create a deep copy of the course to be used when creating
     * subproblems.
     *
     * @param slot Slot to be copied.
     */
    public Slot(Slot slot) {
        this.day = slot.day;
        this.time = slot.time;
        this.coursemax = slot.coursemax;
        this.coursemin = slot.coursemin;
        this.coursenum = slot.coursenum;
        ArrayList<Course> copy = new ArrayList<Course>();

        if (!slot.getList().isEmpty()) {
            for (int i = 0; i < slot.getList().size(); i++) {
                copy.add(new Course(slot.getList().get(i)));
            }
        }
        this.setSlotToCourses();
        this.list = copy;
        this.isLab = slot.isLab;
        this.slotid = slot.slotid;
    }

    /**
     * A method to insert a course into this slot. Course must match lab/course type
     * of this slot and this slot must not be full.
     *
     * @param course Course
     * @return return true if a course is successfully inserted
     */
    public boolean insertCourse(Course course) {
        if (coursenum < coursemax && course.isLab() && isLab) {
            list.add(course);
            this.setSlotToCourses();
            coursenum++;
            return true;
        } else if (coursenum < coursemax && !course.isLab() && !isLab) {
            list.add(course);
            this.setSlotToCourses();
            coursenum++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a course in certain index.
     *
     * @param index to remove.
     */
    public void removeCourse(int index) {
        System.out.println("In Slot: removeCourse-- " + list.get(index).getCourseid());
        list.remove(index);
        coursenum--;
    }

    /**
     * Sets the courses that are in the slot.
     */
    public void setSlotToCourses() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setMySlot(this.getShortSlotid());
        }
    }

    /**
     * Prints all courses in slot.
     */
    public void printSlot() {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getCourseid());
        }
    }

    /**
     * Return a string representing the day followed by the time which are separated
     * by a whitespace.
     *
     * @return day and time
     */
    public String getShortSlotid() {
        String shortid = day + " " + time;
        return shortid;
    }

    public void setSlotid(String slotid) {
        this.slotid = slotid;
    }

    @Override
    public String toString() {
        String type = "course";
        int max = coursemax;
        int min = coursemin;

        if (isLab) {
            type = "lab";
        }

        return "\n Day: " + day + ", Time: " + time + ", " + type + "Max: " + max + ", " + type + "Min: " + min;
    }

    // Getters and Setters
    public int getCoursenum() {
        return coursenum;
    }

    public void setCoursenum(int coursenum) {
        this.coursenum = coursenum;
    }

    public ArrayList<Course> getList() {
        return list;
    }

    public void setList(ArrayList<Course> list) {
        this.list = list;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String startTime) {
        this.time = startTime;
    }

    public int getCoursemax() {
        return coursemax;
    }

    public void setCoursemax(int coursemax) {
        this.coursemax = coursemax;
    }

    public int getCoursemin() {
        return coursemin;
    }

    public void setCoursemin(int coursemin) {
        this.coursemin = coursemin;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean isLab) {
        this.isLab = isLab;
    }

    public String getSlotid() {
        return slotid;
    }
}
