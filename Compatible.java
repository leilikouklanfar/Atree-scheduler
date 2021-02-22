/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Compatible class holds information regarding two courses that are
 * incompatible.
 *
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Compatible {
    private Course course1;
    private Course course2;

    /**
     *
     * Constructor of Compatible which initializes the two compatible courses.
     *
     * @param course1 a course
     * @param course2 a course
     */
    public Compatible(Course course1, Course course2) {
        this.course1 = course1;
        this.course2 = course2;
    }

    // Getter and Setters
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
