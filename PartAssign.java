/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * PartAssign class contains information regarding a course that must be placed
 * in a certain time slot.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class PartAssign {
    private Course course;
    private Slot slot;

    /**
     *
     * Constructor of PartAssign which initializes the slot the course should be
     * assigned to.
     *
     * @param course a course
     * @param slot   the slot course is assigned to
     */
    public PartAssign(Course course, Slot slot) {
        this.course = course;
        this.slot = slot;
    }

    // Getter and Setters
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
