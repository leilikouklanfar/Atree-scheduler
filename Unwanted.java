/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Unwanted class contains information regarding courses that must not be
 * assigned to a certain slot.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Unwanted {
    // boolean isLab;
    Course course;
    Slot slot;

    /**
     *
     * Constructor of Unwanted which initializes the courses and the slot it can not
     * be assigned to.
     *
     * @param course a course
     * @param slot   a course
     */
    public Unwanted(Course course, Slot slot) {
        this.course = course;
        this.slot = slot;
    }

    // Getter and Setter
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }
}
