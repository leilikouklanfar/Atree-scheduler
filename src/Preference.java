/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Preference class contains information regarding courses that would be
 * preferred to be placed in a certain slot and a penalty value gained when not
 * being placed in a preferred slot.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 */
public class Preference {
    private Course course;
    private Slot slot;
    private int penalty;

    /**
     *
     * Constructor of Preference.
     *
     * @param course  the course
     * @param slot    the slot
     * @param penalty the associated penalty value
     */
    public Preference(Course course, Slot slot, int penalty) {
        this.course = course;
        this.slot = slot;
        this.penalty = penalty;
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

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
}
