/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Pair class contains information regarding courses that would be nice to be
 * together in same time slot.
 *
 * @author Wahaj Hassan, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Pair {
    private Course course1;
    private Course course2;

    /**
     * Constructor of Pair which initializes the two courses that are a pair(course1
     * is paired with course2).
     *
     * @param course1 a course
     * @param course2 a course
     */
    public Pair(Course course1, Course course2) {
        this.course1 = course1;
        this.course2 = course2;
    }

    // Getter and Setter
    public Course getCourse1() {
        return course1;
    }

    public void setCourse1(Course course1) {
        this.course1 = course1;
    }

    public Course getCourse2() {
        return course2;
    }

    public void setCourse2(Course course2) {
        this.course2 = course2;
    }
}
