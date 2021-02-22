import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * NAME: Leili Kouklanfar, Jae Whan Yu, and Sebastian Collard
 *
 * DUE: December 9, 2020
 *
 * COURSE: CPSC 433: Artificial Intelligence
 *
 * INSTRUCTOR: Jorg Denzinger
 *
 * ASSIGNMENT: AI Assignment Search Problem
 *
 * This program creates a schedule based on the available course slots, lab
 * slots and the given sections of courses and labs. In addition, there exists
 * some general and department specific hard and soft constraints. All the hard
 * constraint must be fulfilled for the program to be able to produce a valid
 * schedule. The soft constraints must be fulfilled the best they can so that
 * where the Eval value is minimized. The Eval value is the evaluation of how
 * well an assignment fulfills the soft constraints. This is read in via
 * parameter in the following syntax(where the "-" character denoates a number
 * of whitespace characters in input) followed by the text file name obtaining
 * the data for the search instance:
 *
 * wMinFilled-wPref-wSecDiff-wPair-pen_coursemin-pen_labsmin-pen_section-pen_notpaired-fileName
 *
 *
 * Given these variables, the program uses the idea of branch and bound to
 * expand a node into several "child" nodes by assigning a course/lab in all the
 * available slots(each a child node) and picking the next child node with the
 * smallest Eval value as the next node to process.
 *
 * For further detail regarding the assignment specs(soft and hard constraints,
 * Eval value, and etc.) visit:
 *
 * https://pages.cpsc.ucalgary.ca/~denzinge/courses/433-fall2020/coursescheduling.html
 *
 * For further detail regarding the assignment implementation(syntax of input/
 * output, command line parameter, Eval calculation, and etc.) visit:
 *
 * https://pages.cpsc.ucalgary.ca/~denzinge/courses/433-fall2020/assigninput.html
 *
 *
 *
 * @author Jae Whan Yu, Sebastian Collard and Leili Kouklanfar
 *
 */

public class Main {

    /**
     * Given invalid command line parameters, the correct syntax for command line
     * parameters is printed for the user.
     *
     */
    private static void printFormat() {
        System.out.println("Usage: java Main <wMinFilled> <wPref> "
                + "<wSecDiff> <wPair> <pen_coursemin> <pen_labsmin> <pen_section> <pen_notpaired> <input file name>");
    }

    /**
     * Runs the program inorder to produce the schedule. If a valid schedule can not
     * be produced the user is prompted, otherwise the schedule is printed to the
     * screen with the eval value on the first line followed by the courses and labs
     * in alphabetical order.
     *
     * @param args the command line parameters entered
     */
    public static void main(String[] args) {
        // Penalties for not meeting minimum number of courses.
        int penCoursemin = 0;
        int penLabsmin = 0;
        int penSection = 0;
        int penNotPaired = 0;

        // Weightings of different parts of the Eval function.
        double wMinFilled = 0.0;
        double wPref = 0.0;
        double wPair = 0.0;
        double wSecDiff = 0.0;
        String fileName = "";

        Parse parse = new Parse();
        // Problem prob = null;

        // Parse command line arguments.
        if (args.length < 1) {
            printFormat();
            System.exit(1);
        }
        if (args.length > 9) {
            printFormat();
            System.exit(1);
        }
        try {
            wMinFilled = Integer.parseInt(args[0]);
            wPref = Integer.parseInt(args[1]);
            wSecDiff = Integer.parseInt(args[2]);
            wPair = Integer.parseInt(args[3]);
            penCoursemin = Integer.parseInt(args[4]);
            penLabsmin = Integer.parseInt(args[5]);
            penSection = Integer.parseInt(args[6]);
            penNotPaired = Integer.parseInt(args[7]);
            fileName = args[8];

        } catch (Exception ex) {
            printFormat();
            System.exit(1);
        }

        // Parse the input file.
        try {
            BufferedReader buff = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = buff.readLine()) != null) {
                line = line.trim();
                // Look for lines that match sections of the input.
                // For each of these lines, call the appropriate subroutine.
                if (line.equals("Name:")) {
                    parse.getParsedName(buff);
                } else if (line.equals("Course slots:")) {
                    parse.getParsedCourseSlots(buff);
                } else if (line.equals("Lab slots:")) {
                    parse.getParsedLabSlots(buff);
                } else if (line.equals("Courses:")) {
                    parse.getParsedCourses(buff);
                } else if (line.equals("Labs:")) {
                    parse.getParsedLabs(buff);
                } else if (line.equals("Not compatible:")) {
                    parse.getParsedNotCompat(buff);
                } else if (line.equals("Unwanted:")) {
                    parse.getParsedUnwanted(buff);
                } else if (line.equals("Partial assignments:")) {
                    parse.getParsedPartAssign(buff);
                } else if (line.equals("Preferences:")) {
                    parse.getParsedPrefs(buff);
                } else if (line.equals("Pair:")) {
                    parse.getParsedPair(buff);
                } else if (!line.isEmpty()) {
                    throw new IllegalStateException("Could not parse line: " + line);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.toString());
            System.exit(1);
        } catch (IllegalStateException ex) {
            System.out.print("Error parsing file: " + ex.getMessage());
            System.exit(1);
        }

        // Start Search
        Node startNode = new Node(parse.getSlots());
        ArrayList<Course> courseList = parse.getCourses();
        HConst hardC = new HConst(parse.getCompatList(), parse.getUnwantedList(), parse.getPartialAssignList());
        Eval eval = new Eval(parse.getPrefList(), parse.getPairList(), wMinFilled, wPref, wPair, wSecDiff, penCoursemin,
                penLabsmin, penSection, penNotPaired);
        int totalCourses = courseList.size();
        Search search = new Search(hardC, eval, totalCourses);
        Node node = search.Solve(startNode, courseList);

        // Search Complete, Check result
        // If the result is null or not full Solution
        if (node == null || totalCourses != node.getTotalCourses() || !hardC.checkHc(node)) {
            System.out.println("Could not find the solution for the input text file named: " + fileName);
        } else {
            // Otherwise print results.
            ArrayList<String> list = node.printNode();
            ArrayList<Course> cl = new ArrayList<Course>();
            eval.checkEval(node, cl);
            String evalString = eval.printEval();

            // Output to file
            try {
                FileWriter fw = new FileWriter("out.txt");
                for (int i = 0; i < list.size(); i++) {
                    fw.write(list.get(i) + "\node");
                }
                fw.write(evalString);
                fw.close();
            } catch (FileNotFoundException e1) {
                System.out.println("File not found.");
            } catch (IOException e1) {
                System.out.println("Error writing output file.");
            }
        }
    }
}
