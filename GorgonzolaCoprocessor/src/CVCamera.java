
import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CVCamera extends Camera {
	
	
	
	ArrayList<Pipeline> pipes = new ArrayList();
	NetworkTableEntry rawV0;
	NetworkTable table;
	long timestamp;
	ArrayList<NetworkTableEntry> entries, timestamps;
	NetworkTableEntry numberOfSightings;
	//VMXInterface vmx;
	/**
	 * Instantiates the CVCamera object
	 * 
	 * @param refreshRate      the number of frames to process per second
	 * @param vFOV             the vertical FOV on the CVCamera
	 * @param hFOV             the horizontal FOV on the CVCamera
	 * @param xPixels          the number of pixels in the x direction
	 * @param yPixels          the number of pixels in the y direction
	 * @param horizontalOffset the number of inches from the center of the robot the
	 *                         front bottom center of the lens of the CVCamera is
	 *                         (in the horizontal direction)
	 * @param verticalOffset   the number of inches from the ground the center of
	 *                         the CVCamera lens is.
	 * @param depthOffset      the number of inches how deep in to the robot the
	 *                         CVCamera is. Currently unused.
	 * @param hAngle           the horizontal angle of the CVCamera
	 * @param vAngle           the vertical angle of the CVCamera
	 */
	NetworkTableEntry smartDash;

	public CVCamera(int refreshRate, double vFOV, double hFOV, double xPixels, double yPixels, double horizontalOffset,
			double verticalOffset, double depthOffset, double hAngle, double vAngle) {
		super(refreshRate, vFOV, hFOV, xPixels, yPixels, horizontalOffset, verticalOffset, depthOffset, hAngle, vAngle);
		table = NetworkTableInstance.getDefault().getTable("CameraData");
		smartDash = NetworkTableInstance.getDefault().getTable("SmartDashboard").getEntry("CameraDistance");

		NetworkTableInstance.getDefault().startClientTeam(1086);
		entries = new ArrayList<NetworkTableEntry>();
		timestamps = new ArrayList<NetworkTableEntry>();
	}

	/**
	 * Adds a pipeline that processes the images the CVCamera takes
	 * 
	 * @param p the pipeline to add.
	 */
	public void addPipeline(Pipeline p) {
		pipes.add(p);
	}

	/**
	 * Initializes the CVCamera with a VisionSource
	 * 
	 * @param source the source of the CVCamera
	 * @param name   the name of the CVCamera
	 */
	public void initializeCamera(VideoSource source, String name) {
		System.out.println("starting");

		CvSink sink = new CvSink(name);
		sink.setSource(source);
		sink.setEnabled(true);
		numberOfSightings = table.getEntry("numberOfEntries");
		new Thread(() -> {
			try {
				Mat sourceMat = new Mat();
				while (!interrupted()) {
					sink.grabFrame(sourceMat, 10);
					timestamp = System.nanoTime();
					process(sourceMat);
					sleep((int) (1000.0 / REFRESH_RATE));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Processes the frames that the CVCamera processes
	 * 
	 * @param source the captured frame
	 */
	private void process(Mat source) {
		System.out.println("Processing... " + pipes.size());
		for (Pipeline pipe : pipes) {
			ArrayList<Sighting> sightings = pipe.process(source);
			System.out.println("sights before pruning" + sightings.size());
			pipe.getSupportedTargets().stream().filter(target -> calculators.containsKey(target)).forEach(target -> {
				calculators.get(target).updateObjects((ArrayList<Sighting>) sightings.clone());
				try {
					table.getKeys().forEach(o -> {
						if (!o.equals("numberOfEntries")) {
							table.getEntry(o).delete();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					entries.forEach(o -> o.delete());
					timestamps.forEach(o -> o.delete());
				}
				entries = new ArrayList<NetworkTableEntry>();
				timestamps = new ArrayList<NetworkTableEntry>();
				int i = 0; // iterator (Ideally, there will only be one/two sightings)
				System.out.println("Valid sightings:" + calculators.get(target).validSightings.size());
				numberOfSightings.setNumber(calculators.get(target).validSightings.size());

				for (Sighting s : calculators.get(target).validSightings) {
					entries.add(table.getEntry("Sighting " + i));
					timestamps.add(table.getEntry("Timestamp" + i));
					double[] sightingValues = new double[6];
					// System.out.println("\tdistance "+s.distance.getAsDouble());
					sightingValues[0] = s.distance.getAsDouble();
					// System.out.println("\tangle "+s.angle.getAsDouble());
					// System.out.println("isRightSighting "+s.isRightSighting);
					sightingValues[1] = s.angle.getAsDouble() * 180.0 / Math.PI;
					SmartDashboard.putNumber("camera angle", s.angle.getAsDouble() * 180.0 / Math.PI);
					//SmartDashboard.putNumber("vmx angle", vmx.getYaw());
					SmartDashboard.putNumber("total Angle", s.angle.getAsDouble() * 180.0 / Math.PI);

					sightingValues[2] = s.x;
					sightingValues[3] = s.y;
					sightingValues[4] = s.width;
					sightingValues[5] = s.height;
					
					entries.get(i).setDoubleArray(sightingValues);
					// System.out.println("SETTING DOUBLE ARRAYS\n\n\n");
					timestamps.get(i).setString(""+timestamp);
					smartDash.setDouble(s.distance.getAsDouble());

					i++;
				}
				// test pairs code

				// System.out.println("sightPoints:"+sightPoints);
			});
		}
	}
}