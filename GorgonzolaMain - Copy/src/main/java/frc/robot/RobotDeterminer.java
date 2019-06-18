package frc.robot;

import java.io.File;
import java.util.Scanner;

public class RobotDeterminer {
    public static void determineRobot() {
        try {
            Scanner fileReader = new Scanner(new File("/home/lvuser/robotID.txt"));
            String st = fileReader.nextLine();
            if (st.equals("NSP")) {
                Globals.isNSP = true;
                Globals.isProto = false;
                Globals.isAdelost = false;
            } else if (st.equals("GorgonzolaProto")) {
                Globals.isNSP = false;
                Globals.isProto = true;
                Globals.isAdelost = false;
            } else if (st.equals("GorgonzolaFinal")) {
                Globals.isNSP = false;
                Globals.isProto = false;
                Globals.isAdelost = false;
            } else {
                throw new Exception("Unrecognized Robot Flag \n" + st + "\n Assuming Final");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Globals.isNSP = false;
            Globals.isProto = false;
            Globals.isAdelost = false;
        }
    }
}