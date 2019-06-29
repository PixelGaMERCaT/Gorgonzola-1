package frc.robot;

import java.io.File;
import java.util.Scanner;
/**                                          UNUSED CLASS
 * A class built to determine what robot we are running our code on. Reads a flag stored on the RoboRIO.
 * @author Jeff
 */ 
public class RobotDeterminer {
    /**
     * Reads a flag in /home/lvuser/robotID.txt and sets Globals isRobot booleans to match what it finds.
     * Defaults to GorgonzolaFinal in the case of an error.
     */
    public static void determineRobot() {
        try {
            Scanner fileReader = new Scanner(new File("/home/lvuser/robotID.txt"));
            String st = fileReader.nextLine();
            if (st.equals("NSP")) {
                Globals.isNSP = true;
                Globals.isProto = false;
            } else if (st.equals("GorgonzolaProto")) {
                Globals.isNSP = false;
                Globals.isProto = true;
            } else if (st.equals("GorgonzolaFinal")) {
                Globals.isNSP = false;
                Globals.isProto = false;
            } else {
                fileReader.close();
                throw new Exception("Unrecognized Robot Flag \n" + st + "\n Assuming Final");
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Globals.isNSP = false;
            Globals.isProto = false;
        }
    }
}