
import java.util.ArrayList;
import java.util.OptionalDouble;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraCalculator {
	ArrayList<Sighting> visionObjects = new ArrayList();
	ArrayList<Sighting> validSightings = new ArrayList();
	Camera camera;
	VisionTarget visionTarget;
	NetworkTable table;
	NetworkTableEntry pitch;

	/**
	 * Creates the CVCamera calculator
	 * 
	 * @param c the CVCamera the calculator uses to get its image data
	 * @param v the vision target it finds
	 */
	public CameraCalculator(Camera c, VisionTarget v) {
		this.camera = c;
		this.visionTarget = v;
		table = NetworkTableInstance.getDefault().getTable("RobotData");
		pitch = table.getEntry("pitch");
	}

	/**
	 * Updates the data from the CVCamera
	 * 
	 * @param polys the data from the CVCamera
	 */
	void updateObjects(ArrayList<Sighting> polys) {
		visionObjects.clear();
		visionObjects.addAll(polys);
		visionObjects = visionTarget.validateSightings(visionObjects);
		calculateDistance();
		calculateDistanceAA();
		calculateDistanceHA();
		calculateDistanceHAA();
		calculateDistanceHAAD();
		calculateAngle();
		calculateRotation();
		eliminateBadTargets();
		validSightings = new ArrayList(visionObjects);
	}

	/**
	 * Calculates the distance to the specified vision target
	 */
	public void calculateDistance() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle;
			double changeInY = visionTarget.height - camera.verticalOffset;
			double distanceToTarget = changeInY / Math.sin(verticalAngle);
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance no assist", horizontalDistance);

			 p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
	public void calculateDistanceAA() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle + (pitch.getDouble(0)) * Math.PI / 180.0;
			double changeInY = visionTarget.height - camera.verticalOffset;
			double distanceToTarget = changeInY / Math.sin(verticalAngle);
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance angle assist", horizontalDistance);

			//p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
	public double r=16.25;//15.3 nsp
	public void calculateDistanceHAA() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle + (pitch.getDouble(0)) * Math.PI / 180.0;
			double changeInY = visionTarget.height - camera.verticalOffset-r*Math.sin(pitch.getDouble(0)*Math.PI/180.0);
			double distanceToTarget = changeInY / Math.sin(verticalAngle);
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance height Fangle assist", horizontalDistance);

			//p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
	
	public void calculateDistanceHAAD() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle + (pitch.getDouble(0)) * Math.PI / 180.0;
			double changeInY = visionTarget.height - camera.verticalOffset-r*Math.sin(pitch.getDouble(0)*Math.PI/180.0);
			double distanceToTarget = changeInY / Math.sin(verticalAngle)-(r-r*Math.cos(pitch.getDouble(0)*Math.PI/180.0));
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance height angle dist assist", horizontalDistance);
			//System.out.println("HAAD "+horizontalDistance);
			//p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
	
	public void calculateDistanceHA() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle;
			double changeInY = visionTarget.height - camera.verticalOffset-r*Math.sin(pitch.getDouble(0)*Math.PI/180.0);
			SmartDashboard.putNumber("heightOffset", r*Math.sin(pitch.getDouble(0)*Math.PI/180.0));
			double distanceToTarget = changeInY / Math.sin(verticalAngle);
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance height assist only", horizontalDistance);

			//p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
/*
	public double d1 = 16.5, d2 = 18.9, phi1 = 18.6 * Math.PI / 180.0, phi2 = 16.1 * Math.PI / 180.0;

	public void calculateDistanceHA() {
		for (Sighting p : visionObjects) {
			double midY = p.centerY;
			double angleFromCameraToTarget = getYAngle(midY);
			p.rawV = OptionalDouble.of(angleFromCameraToTarget);
			double verticalAngle = angleFromCameraToTarget + camera.vAngle + (pitch.getDouble(0)) * Math.PI / 180.0;
			double verticalOffset;
			double currPitch = pitch.getDouble(0) * Math.PI / 180.0;
			if (currPitch > 0) {
				verticalOffset = d1 * Math.sin(currPitch + phi1);
			} else {
				verticalOffset = d2 * Math.sin(currPitch + phi2);
			}
			SmartDashboard.putNumber("verticaloffset", verticalOffset);
			double changeInY = visionTarget.height - verticalOffset;
			double distanceToTarget = changeInY / Math.sin(verticalAngle);
			double horizontalDistance = distanceToTarget * Math.cos(verticalAngle);
			SmartDashboard.putNumber("final distance height assist", horizontalDistance);

			p.distance = OptionalDouble.of(horizontalDistance);
		}
	}
*/
		/**
	 * Calculates the angle to the specified vision target
	 */
	public void calculateAngle() {
		for (Sighting p : visionObjects) {
			double midX = p.centerX;
			System.out.println("midx"+midX);
			p.rawH = OptionalDouble.of(getXAngle(midX));
			double horizontalAngle = Math.PI / 2 - getXAngle(midX);
			double distance = p.distance.getAsDouble();
			double f = Math.sqrt(distance * distance + Math.pow(camera.horizontalOffset, 2)
					- 2 * distance * camera.horizontalOffset * Math.cos(horizontalAngle));
			double c = Math.asin(camera.horizontalOffset * Math.sin(horizontalAngle) / f);
			double b = Math.PI - horizontalAngle - c;
			p.angle = OptionalDouble.of((Math.PI / 2 - b) - camera.hAngle);
		}
	}

	/**
	 * Calculates the horizontal rotation of the vision target relative to the
	 * CVCamera
	 */
	public void calculateRotation() {
		for (Sighting s : visionObjects) {
			s.relativeAspectRatio = s.aspectRatio / visionTarget.aspectRatio;
			if (Math.abs(s.relativeAspectRatio) < 1)
				s.rotation = OptionalDouble.of(Math.acos(s.relativeAspectRatio));
		}
	}

	/**
	 * Finds the horizontal angle to a specific pixel on the CVCamera
	 * 
	 * @param x the pixel
	 * @return the angle
	 */
	public double getXAngle(double x) {
		System.out.println("xPixels "+camera.xPixels);
		System.out.println("hFOV "+camera.hFOV);
		camera.hFOV=SmartDashboard.getNumber("HORIZONTAL FOV", camera.hFOV);
		double HF = (camera.xPixels / 2) / Math.tan(camera.hFOV / 2);
		double cx = (camera.xPixels / 2) - 0.5;
		System.out.println(Math.atan(cx-x)/HF);
		return Math.atan((cx - x) / HF);
	}

	/**
	 * Eliminates sightings that have invalid distances, angles, or are considered
	 * incorrect for some reason.
	 */
	public void eliminateBadTargets() {
		visionObjects.removeIf(s -> !visionTarget.estimationIsGood(s));
	}

	/**
	 * Finds the vertical angle to a specific pixel on the CVCamera
	 * 
	 * @param y the pixel
	 * @return the angle
	 */
	public double getYAngle(double y) {
		double VF = (camera.yPixels / 2) / Math.tan(camera.vFOV / 2);
		double cy = (camera.yPixels / 2) - 0.5;
		return Math.atan((cy - y) / VF);
	}
	
	/*
	 * Returns FOV with a known angle
	 * @param ang the known angle in degrees
	 * @param pixels Number of pixels of the desired dimension to calculate
	 * @return the camera's FOV in that dimension
	 */
	public double calcFOV(double pixels, double y) {
		double c = (pixels / 2.0) - 0.5;
		double f = (c-y)/(17.75/61.0);
		double fov = 2.0 * Math.atan((pixels/2.0)/f);
		System.out.println(" pix:"+pixels+" y:"+y+" c:"+c+" f:"+f+" fov:"+Math.toDegrees(fov));
		return fov;
	}

	/**
	 * Returns the most recent target sightings
	 * 
	 * @return the most recent distance calculated to the specified target or -1 if
	 *         the target is not found.
	 */
	public ArrayList<Sighting> getSightings() {
		return new ArrayList<>(validSightings);
	}

	/**
	 * Tells whether or not the specified vision target can be seen
	 * 
	 * @return whether or not the specified vision target can be seen
	 */
	public int sightingCount() {
		return validSightings.size();
	}
}