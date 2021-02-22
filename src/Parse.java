
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * Used to Parse all the text file fields. The text file must have the form
 * described in the assignment specs which can be found at:
 *
 * https://pages.cpsc.ucalgary.ca/~denzinge/courses/433-fall2020/assigninput.html
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */

class Parse {
    private String name;

    String getName() {
        return this.name;
    }

    // Compiling patterns has some overhead, so we only do it once.
    private Pattern slotParse, courseParse, labParse, identifierParse, prefParse, pairParse;
    // Used for building the initial structures.
    // Later cast to arrays.
    private ArrayList<Slot> slots;
    private ArrayList<Course> courses;
    private ArrayList<Compatible> compList;
    private ArrayList<Unwanted> untList;
    private ArrayList<PartAssign> prtList;

    private Node node;
    private ArrayList<Preference> prefList;
    private ArrayList<Pair> pairlist;

    int totalAvailableCS;
    int totalAvailableLS;
    int totalC;
    int totalL;

    Parse() {
        courses = new ArrayList<>();
        slots = new ArrayList<>();
        compList = new ArrayList<>();
        untList = new ArrayList<>();
        prtList = new ArrayList<>();
        prefList = new ArrayList<>();
        pairlist = new ArrayList<>();
        totalAvailableCS = 0;
        totalAvailableLS = 0;
        totalC = 0;
        totalL = 0;

        String day = "([A-Z]{2})", time = "([0-9]{1,2}:[0-9]{2})", value = "([0-9]*)", separator = "[\\s]*,[\\s]*",
                courseIdent = "([A-Z]{4})", courseNumber = "([0-9]{3})", lectureNumber = "([0-9]{2})",
                assignment = "([0-9A-Z\\s]*)", whitespaces = "[\\s]*";

        // Matches and extracts lines of the form: DD, HH:MM, INT, INT
        slotParse = Pattern.compile(day + separator + time + separator + value + separator + value + whitespaces);

        // Matches and extracts lines of the form: CourseCode, CourseNum, LEC, LecNum
        courseParse = Pattern
                .compile(courseIdent + whitespaces + courseNumber + whitespaces + "LEC" + whitespaces + lectureNumber);

        // Matches and extracts lines of the form: SENG 311 (Optional: LEC 01) TUT 01
        labParse = Pattern.compile(courseIdent + whitespaces + courseNumber + "[\\s]+(?:LEC[\\s]+([0-9]{2})[\\s]+)"
                + "?(?:TUT|LAB)[\\s]+([0-9]{2})");

        // Matches and extracts lines of the form: (Assignable Name), Day, Time
        identifierParse = Pattern.compile(assignment + separator + day + separator + time);

        // Matches and extracts lines of the form: DD, HH:MM, (Assignable Name), INT
        prefParse = Pattern.compile(day + separator + time + separator + assignment + separator + value);

        // Matches and extracts lines of the form: (Assignable Name), (Assignable Name)
        pairParse = Pattern.compile(assignment + "[\\s]*,[\\s]*" + assignment);
    }

    /**
     * Parse the name field of the input and stores it in name.
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedName(BufferedReader buff) throws IOException {
        String line;
        while ((line = buff.readLine()) != null) {
            if (line.isEmpty()) {
                return;
            }
            name = line;
        }
    }

    /**
     * Parse the course slot and its course max/min values from the buffered input
     * and stores it in slots.
     *
     * Has the syntax: Day, Start time, coursemax, coursemin
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedCourseSlots(BufferedReader buff) throws IOException {
        String line;
        while ((line = buff.readLine()) != null) {
            Matcher split = slotParse.matcher(line);

            if (line.trim().isEmpty()) {
                return; // If the line is not whitespace and we can't parse it, we have a problem.
            }
            if (split.find()) {
                // Shouldn't need input sanitation here. The regex should only match things that
                // are okay to parse.
                Slot slot = new Slot(split.group(1), split.group(2), Integer.parseInt(split.group(3)),
                        Integer.parseInt(split.group(4)), false);

                if (slot.getDay().equals("TU") && slot.getTime().equals("11:00")) {
                    slot.setCoursemax(0);
                }
                if (isValidCourseSlots(slot.getDay(), slot.getTime())) {
                    slots.add(slot);
                    totalAvailableCS += slot.getCoursemax();
                    totalC++;
                }

            } else {
                throw new IOException("Error parsing line as course slot: " + line);
            }
        }
    }

    /**
     * Parse the lab slot and its lab max/min values from the buffered input and
     * stores it in slots.
     *
     * Has the syntax: Day, Start time, labmax, labmin
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedLabSlots(BufferedReader buff) throws IOException {
        String line;
        while ((line = buff.readLine()) != null) {
            Matcher split = slotParse.matcher(line);

            if (line.trim().isEmpty()) {
                return; // If the line is not whitespace and we can't parse it, we have a problem.
            }
            if (split.find()) {
                // All lab slots must be different from all course slots.
                // Even if they have the same day and time. (And duration.)
                Slot slot = new Slot(split.group(1), split.group(2), Integer.parseInt(split.group(3)),
                        Integer.parseInt(split.group(4)), true);
                // ONLY ADD VALID SLOTS TO THE LIST

                if (isValidLabSlots(slot.getDay(), slot.getTime())) {
                    slots.add(slot);
                    totalAvailableLS += slot.getCoursemax();
                }

                if (slot.getDay().equals("TU") && slot.getTime().equals("18:00")) {
                    totalL++;
                }

            } else {
                throw new IOException("Error parsing line as a lab slot: " + line);
            }
        }
    }

    /**
     * Parse the courses from the buffered input and stores it in courses.
     *
     * Has the syntax: Course-Code Course-Number LEC Lecture-number
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedCourses(BufferedReader buff) throws IOException {
        String line;
        while ((line = buff.readLine()) != null) {
            Matcher split = courseParse.matcher(line);
            // CourseCode, CourseNum, LEC, LecNum
            if (line.trim().isEmpty()) {
                return; // If the line is not whitespace and we can't parse it, we have a problem.
            }
            if (split.find()) {
                Course course = new Course(split.group(1), split.group(2), split.group(3));
                courses.add(course);
            } else {
                throw new IOException("Error parsing line as a lab slot: " + line);
            }
        }
    }

    /**
     * Parse the labs from the buffered input and stores it in courses.
     *
     * Has the syntax: Course-Code Course-Number LEC Lecture-number (TUT|LAB)
     * Lab-Number
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedLabs(BufferedReader buff) throws IOException {
        String line;
        while ((line = buff.readLine()) != null) {
            Matcher split = labParse.matcher(line);
            if (line.trim().isEmpty()) {
                return;
            }
            if (split.find()) {
                if (split.group(3) != null) {
                    if (line.contains("TUT")) {
                        Course lab = new Course(split.group(1), split.group(2), split.group(3), "TUT", split.group(4));
                        courses.add(lab);
                    }
                    if (line.contains("LAB")) {
                        Course lab = new Course(split.group(1), split.group(2), split.group(3), "LAB", split.group(4));
                        courses.add(lab);
                    }
                } else {
                    if (line.contains("TUT")) {
                        Course lab = new Course(split.group(1), split.group(2), "TUT", split.group(4));
                        ArrayList<String> matching = getMatchingSectionNumbers(lab);
                        lab.setMatchingSections(matching);
                        courses.add(lab);
                    }
                    if (line.contains("LAB")) {
                        Course lab = new Course(split.group(1), split.group(2), "LAB", split.group(4));
                        ArrayList<String> matching = getMatchingSectionNumbers(lab);
                        lab.setMatchingSections(matching);
                        courses.add(lab);
                    }
                }
            }
        }
    }

    /**
     * Parse the not compatible values from the buffered input and stores it in
     * compList.
     *
     * Has the syntax: Identifier Name, Identifier Name
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedNotCompat(BufferedReader buff) throws IOException {
        String line;
        Course course1 = null;
        Course course2 = null;
        while ((line = buff.readLine()) != null) {
            Matcher split = pairParse.matcher(line);
            if (line.trim().isEmpty()) {
                return;
            }
            if (split.find()) {
                String first = split.group(1);
                String second = split.group(2);
                // First course
                // If TUT,LAB
                if (first.contains("TUT") || first.contains("LAB")) {
                    course1 = generateLab(first);
                } // If Course
                else {
                    course1 = generateCourse(first);
                }
                if (second.contains("TUT") || second.contains("LAB")) {
                    course2 = generateLab(second);
                } // If Course
                else {
                    course2 = generateCourse(second);
                }
            }
            Compatible comp = new Compatible(course1, course2);
            compList.add(comp);
        }
    }

    /**
     * Parse the unwanted values from the buffered input and stores it in untList.
     *
     * Has the syntax: Identifier Name, Day, Time
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedUnwanted(BufferedReader buff) throws IOException {
        String line;
        Course course = null;
        Slot slot = null;
        while ((line = buff.readLine()) != null) {
            Matcher split = identifierParse.matcher(line);
            if (line.trim().isEmpty()) {
                return;
            }
            if (split.find()) {
                String name = split.group(1);
                String day = split.group(2);
                String time = split.group(3);
                // First course
                // If TUT,LAB
                if (name.contains("TUT") || name.contains("LAB")) {
                    course = generateLab(name);
                } // If Course
                else {
                    course = generateCourse(name);
                }
                if (course.isLab()) {
                    slot = new Slot(day, time, 0, 0, true);
                } // If Course
                else {
                    slot = new Slot(day, time, 0, 0, false);
                }
            }
            Unwanted unt = new Unwanted(course, slot);
            untList.add(unt);
        }
    }

    /**
     * Parse the partial assignment values from the buffered input and stores it in
     * prtList.
     *
     * Has the syntax: Identifier Name, Day, Time
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedPartAssign(BufferedReader buff) throws IOException {
        String line;
        Course course = null;
        Slot slot = null;
        while ((line = buff.readLine()) != null) {
            Matcher split = identifierParse.matcher(line);
            if (line.trim().isEmpty()) {
                return;
            }
            if (split.find()) {
                String name = split.group(1);
                String day = split.group(2);
                String time = split.group(3);
                // First course
                // If TUT,LAB
                if (name.contains("TUT") || name.contains("LAB")) {
                    course = generateLab(name);
                } // If Course
                else {
                    course = generateCourse(name);
                }
                if (course.isLab()) {
                    slot = new Slot(day, time, 0, 0, true);
                } // If Course
                else {
                    slot = new Slot(day, time, 0, 0, false);
                }
            }
            PartAssign prt = new PartAssign(course, slot);
            prtList.add(prt);
        }
    }

    /**
     * Parse the preference values from the buffered input and stores it in
     * prefList.
     *
     * Has the syntax: Day, Time, Identifier Name, Preference Value
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedPrefs(BufferedReader buff) throws IOException {
        String line;
        Course course = null;
        Slot slot = null;

        while ((line = buff.readLine()) != null) {
            Matcher split = prefParse.matcher(line);

            if (line.trim().isEmpty()) {
                return; // If the line is not whitespace and we can't parse it, we have a problem.
            }
            if (split.find()) {
                // Group1 contains day, 2 contains time, 3 contains assignable, 4 contains
                // value.

                String day = split.group(1);
                String time = split.group(2);
                String name = split.group(3);
                String value = split.group(4);

                // If TUT,LAB
                if (name.contains("TUT") || name.contains("LAB")) {
                    course = generateLab(name);
                } // If Course
                else {
                    course = generateCourse(name);
                }
                if (course.isLab()) {
                    slot = new Slot(day, time, 0, 0, true);
                } // If Course
                else {
                    slot = new Slot(day, time, 0, 0, false);
                }
                ArrayList<String> inCourses = new ArrayList<String>();
                for (int i = 0; i < courses.size(); i++) {
                    inCourses.add(courses.get(i).getCourseid());
                }
                ArrayList<String> inSlots = new ArrayList<String>();
                for (int i = 0; i < slots.size(); i++) {
                    inSlots.add(slots.get(i).getShortSlotid());
                }
                if (inCourses.contains(course.getCourseid()) && inSlots.contains(slot.getShortSlotid())) {
                    Preference pref = new Preference(course, slot, Integer.parseInt(value));
                    prefList.add(pref);
                }
            }
        }
    }

    /**
     * Parse the pair values from the buffered input and stores it in pair.
     *
     * Has the syntax: Identifier Name, Identifier Name
     *
     * @param buff buffered input
     * @throws IOException
     */
    void getParsedPair(BufferedReader buff) throws IOException {
        String line;
        Course course1 = null;
        Course course2 = null;
        while ((line = buff.readLine()) != null) {
            Matcher split = pairParse.matcher(line);

            if (line.trim().isEmpty()) {
                return; // If the line is not whitespace and we can't parse it, we have a problem.
            }
            if (split.find()) {
                String first = split.group(1);
                String second = split.group(2);
                // First course
                // If TUT,LAB
                if (first.contains("TUT") || first.contains("LAB")) {
                    course1 = generateLab(first);
                } // If Course
                else {
                    course1 = generateCourse(first);
                }
                if (second.contains("TUT") || second.contains("LAB")) {
                    course2 = generateLab(second);
                } // If Course
                else {
                    course2 = generateCourse(second);
                }
            }
            Pair pair = new Pair(course1, course2);
            pairlist.add(pair);
        }
    }

    /**
     * Find and returns the courses for lab.
     *
     * @param lab the lab
     * @return the courses for the lab
     */
    ArrayList<String> getMatchingSectionNumbers(Course lab) {
        // For all courses
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            String matcher1 = c.getCoursename() + c.getCoursenumber();
            String matcher2 = lab.getCoursename() + lab.getCoursenumber();
            // If there is some course
            if (matcher1.matches(matcher2) && !c.isLab()) {
                result.add(c.getLecturenumber());
            }
        }
        if (result.size() == 0) {
            result.add("none");
        }

        return result;
    }

    /**
     * Parse through a line containing a lab and generate a Course object out of it.
     *
     * @param line the course taken as a line from the input
     * @return the course as a workable data type
     */
    private Course generateCourse(String line) throws IOException {
        Course c = null;
        Matcher courseSplit = courseParse.matcher(line);
        // CourseCode, CourseNum, LEC, LecNum
        if (line.trim().isEmpty()) {
            return c; // If the line is not whitespace and we can't parse it, we have a problem.
        }
        if (courseSplit.find()) {
            c = new Course(courseSplit.group(1), courseSplit.group(2), courseSplit.group(3));
        } else {
            throw new IOException("Error parsing line as a lab slot: " + line);
        }
        return c;
    }

    /**
     * Parse through a line containing a lab and generate a Course object out of it.
     *
     * Labs can either have "TUT" or "LAB" in its identifier
     *  -   CPSC 433 LEC 01 TUT 01
     *  -   CPSC 433 LEC 02 LAB 02
     *
     * Labs can either have a lecture denoted to it as "LEC" or none
     *  -   SENG 311 LEC 01 TUT 01
     *  -   CPSC 567 TUT 01
     *
     * @param line the tut/lab taken as a line from the input
     * @return the tut/lab as a workable data type
     */
    private Course generateLab(String line) {
        Course c = null;
        Matcher labSplit = labParse.matcher(line);
        // if line is empty return null
        if (line.trim().isEmpty()) {
            return c;
        }
        if (labSplit.find()) {
            if (labSplit.group(3) != null) {
                if (line.contains("TUT")) {
                    c = new Course(labSplit.group(1), labSplit.group(2), labSplit.group(3), "TUT", labSplit.group(4));
                }
                if (line.contains("LAB")) {
                    c = new Course(labSplit.group(1), labSplit.group(2), labSplit.group(3), "LAB", labSplit.group(4));
                }
            } else {
                if (line.contains("TUT")) {
                    c = new Course(labSplit.group(1), labSplit.group(2), "TUT", labSplit.group(4));
                }
                if (line.contains("LAB")) {
                    c = new Course(labSplit.group(1), labSplit.group(2), "LAB", labSplit.group(4));
                }
            }
        }
        return c;
    }

    /**
     * Transforms time from a String to an integer value, removing the ":". HH:MM
     * becomes HHMM
     *
     * @param tString the time as string
     * @return time as integer
     */
    int getTimeAsInt(String tString) {

        String[] timeInParts = tString.split(":");
        return (Integer.parseInt(timeInParts[0]) * 100) + Integer.parseInt(timeInParts[1]);
    }

    /**
     * Given the day and time, it checks if the slot is a valid lab slot based on
     * the slots listed as valid in assignment specs.
     *
     * @param day   the day of the week
     * @param sTime the slot time
     * @return true if it is a valid lab slot, false otherwise
     */
    private boolean isValidLabSlots(String day, String sTime) {
        int time = getTimeAsInt(sTime);
        if (day.equals("MO")) {
            // MW slot
            if ((time >= 800 && time <= 2000) && ((time % 100) == 0)) {
                return true;
            }
        } else if (day.equals("TU")) {
            // TTR slot
            if ((time >= 800 && time <= 2000) && ((time % 100) == 0)) {
                return true;
            }
        } else

        {
            if ((time >= 800 && time <= 1800) && ((time % 200) == 0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given the day and time, it checks if the slot is a valid course slot based on
     * the slots listed as valid in assignment specs.
     *
     * @param day   the day of the week
     * @param sTime the slot time
     * @return true if it is a valid course slot, false otherwise
     */
    private boolean isValidCourseSlots(String day, String sTime) {
        int time = getTimeAsInt(sTime);

        if (day.equals("MO")) {
            // MW slot
            if (time >= 800 && time <= 2000 && ((time % 100) == 0)) {
                return true;
            } else if (day.equals("TU")) {
                // TTR slot
                if (time >= 800 && time <= 1830) {
                    if ((time % 100) == 0) {
                        if (time == 800 || time == 1000 || time == 1400 || time == 1700) {
                            return true;
                        }
                    } else if ((time % 100) == 30) {
                        if (time == 930 || time == 1230 || time == 1530 || time == 1830) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // getters/setters
    int getTotalAvailableLS() {
        return totalAvailableLS;

    }

    int getTotalAvailableCS() {
        return totalAvailableCS;
    }

    int getTotalL() {
        return totalL;
    }

    int getTotalC() {
        return totalC;
    }

    ArrayList<Slot> getSlots() {
        return this.slots;
    }

    ArrayList<Course> getCourses() {
        return this.courses;
    }

    ArrayList<Compatible> getCompatList() {
        return this.compList;
    }

    ArrayList<Unwanted> getUnwantedList() {
        return this.untList;
    }

    ArrayList<PartAssign> getPartialAssignList() {
        return this.prtList;
    }

    ArrayList<Preference> getPrefList() {
        return this.prefList;
    }

    ArrayList<Pair> getPairList() {
        return this.pairlist;
    }

    Node getNode() {
        return this.node;
    }
}
