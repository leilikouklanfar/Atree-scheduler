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
 * Course class is used to store all information specified for a course in the
 * input file.
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */
public class Course {

    private String coursename;
    private String coursenumber;
    private String lecturenumber = "none";
    private String tutLab;
    private String tutLabNumber;
    private String courseid = "empty";
    private boolean evening = false;
    private boolean isLab = false;
    private ArrayList<Course> incompatible;
    private ArrayList<Slot> unwanted;
    private String mySlot = "default";
    // All matching section number list to use for labs open to all sections
    private ArrayList<String> matchingSections = new ArrayList<String>();

    /**
     * Constructs a course object.
     *
     * @param coursename    Course name that is 4 characters
     * @param coursenumber  Course number that is 3 digits
     * @param lecturenumber Lecture number that is two digits
     */
    public Course(String coursename, String coursenumber, String lecturenumber) {
        this.coursename = coursename;
        this.coursenumber = coursenumber;
        this.lecturenumber = lecturenumber;
        this.courseid = coursename + " " + coursenumber + " LEC " + lecturenumber;
    }

    /**
     * Constructs a course object that is either a lab or tutorial.
     *
     * @param coursename    Course name that is 4 characters
     * @param coursenumber  Course number that is 3 digits
     * @param lecturenumber Lecture number that is two digits
     * @param tutLab        3 characters either TUT or LAB
     * @param tutLabNumber  2 digit course number
     */
    public Course(String coursename, String coursenumber, String lecturenumber, String tutLab, String tutLabNumber) {
        this.coursename = coursename;
        this.coursenumber = coursenumber;
        this.lecturenumber = lecturenumber;
        this.tutLab = tutLab;
        this.tutLabNumber = tutLabNumber;
        isLab = true;
        this.courseid = coursename + " " + coursenumber + " LEC " + lecturenumber + " " + tutLab + " " + tutLabNumber;
    }

    /**
     * Constructs a course object that is either a lab or tutorial that has the
     * format ABCD 123 TUT/LAB 12.
     *
     * @param coursename   4 character course name
     * @param coursenumber 3 digit course number
     * @param tutLab       3 characters either TUT or LAB
     * @param tutLabNumber 2 digit lab/tut number
     */
    public Course(String coursename, String coursenumber, String tutLab, String tutLabNumber) {
        this.coursename = coursename;
        this.coursenumber = coursenumber;
        this.tutLab = tutLab;
        this.tutLabNumber = tutLabNumber;
        isLab = true;
        this.courseid = coursename + " " + coursenumber + " " + tutLab + " " + tutLabNumber;
    }

    /**
     * A method used to create a deep copy of input course.
     *
     * @param course Course object to be copied.
     */
    public Course(Course course) {
        this.coursename = course.coursename;
        this.coursenumber = course.coursenumber;
        this.lecturenumber = course.lecturenumber;
        this.tutLab = course.tutLab;
        this.tutLabNumber = course.tutLabNumber;
        this.courseid = course.courseid;
        this.evening = course.evening;
        this.isLab = course.isLab;
    }

    // Getters and setters
    public String getMySlot() {
        return mySlot;
    }

    public void setMySlot(String mySlot) {
        this.mySlot = mySlot;
    }

    public ArrayList<String> getMatchingSections() {
        return matchingSections;
    }

    public void setMatchingSections(ArrayList<String> matchingSections) {
        this.matchingSections = matchingSections;
    }

    public String getCoursenumber() {
        return coursenumber;
    }

    public void setCoursenumber(String coursenumber) {
        this.coursenumber = coursenumber;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getLecturenumber() {
        return lecturenumber;
    }

    public void setLecturenumber(String lecturenumber) {
        this.lecturenumber = lecturenumber;
    }

    public String getCourseid() {
        return courseid;
    }

    public String getTutLab() {
        return tutLab;
    }

    public void setTutLab(String tutLab) {
        this.tutLab = tutLab;
    }

    public String getTutLabNumber() {
        return tutLabNumber;
    }

    public void setTutLabNumber(String tutLabNumber) {
        this.tutLabNumber = tutLabNumber;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean isLab) {
        this.isLab = isLab;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public void setEvening(boolean bool) {
        evening = bool;
    }

    public ArrayList<Course> getIncompatible() {
        return incompatible;
    }

    public void setIncompatible(ArrayList<Course> incompatible) {
        this.incompatible = incompatible;
    }

    public boolean isEvening() {
        return evening;
    }

    public ArrayList<Slot> getUnwanted() {
        return unwanted;
    }

    public void setUnwanted(ArrayList<Slot> unwanted) {
        this.unwanted = unwanted;
    }
}
