

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.ArrayList;
import java.util.OptionalDouble;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class Sighting {
    int x, y;
    double centerX, centerY;
    int height, width;
    double area;
    double solidity;
    double aspectRatio;
    double relativeAspectRatio;
    public int pieces = 1;
    public OptionalDouble angle, distance, rotation, rawV, rawH = rawV = rotation = distance = angle = OptionalDouble.empty();
    public Rectangle bounds;
    Area shape;
    List<Point> points = new ArrayList<>();
    
    /**
     * Creates a sighting object from a contour/MatOfPoint
     * @param m the contour to create it from
     */
    public Sighting(MatOfPoint m){
        Point[] points = m.toArray();
        this.points.addAll(m.toList());
        Path2D.Double poly = new Path2D.Double();
        for(int i = 1; i < points.length; i++){
            poly.moveTo(points[i - 1].x, points[i - 1].y);
            poly.lineTo(points[i].x % points.length, points[i].y % points.length);
        }
        Rect tempRect=Imgproc.boundingRect(m);
        shape = (new Area(poly));
        height = tempRect.height;
        width = tempRect.width;
        x = tempRect.x;
        y = tempRect.y;
        bounds=new Rectangle(x,y,width,height);

        area = Imgproc.contourArea(m);
        solidity = area / (width * height);
        aspectRatio = width / height;
        centerX = x + width / 2;
        centerY = y + height / 2;
    }
    
    /**
     * Combines this sighting with another sighting
     * @param sighting the sighting to combine with this one
     */
    public void addSighting(Sighting sighting){
    	System.out.println("points size "+sighting.points.size());
    	System.out.println("This points size:"+points.size());
        pieces += sighting.pieces;
        points.addAll(sighting.points);
        System.out.println("sighting shape" +sighting.shape);
        System.out.println("this shape"+shape);
        System.out.println("sighting bounds" +sighting.shape.getBounds());
        System.out.println("this bounds"+shape.getBounds());
        System.out.println("height "+height);
        System.out.println("sighting height"+sighting.height);
        System.out.println("width "+width);
        System.out.println("sighting width"+sighting.width);

        shape.add(sighting.shape);
        bounds.add(sighting.bounds);
        //Rectangle bounds = shape.getBounds();
        System.out.println("getWidth"+bounds.getWidth());
        System.out.println("2d width"+shape.getBounds2D().getWidth());
        x = bounds.x;
        y = bounds.y;
        width = bounds.width;
        height = bounds.height;
        centerX = bounds.x + bounds.width / 2;
        centerY = bounds.y + bounds.height / 2;
        area += sighting.area;
        solidity = area / width * height;
        aspectRatio = width / height;
        rawH = rawV = rotation = distance = angle = OptionalDouble.empty();
    }
    
    /**
     * Calculates the distance to another sighting
     * @param sighting the sighting to return the distance to
     * @return the distance to the given sighting
     */
    public double distanceTo(Sighting sighting){
        double minDistance = Double.MAX_VALUE;
        for(int i = 0; i < points.size(); i++){
            Point p = points.get(i);
            Point p1 = points.get((i + 1) % points.size());
            for(int j = 0; j < sighting.points.size(); j++){
                Point p2 = sighting.points.get(j);
                Point p3 = sighting.points.get((j + 1) % sighting.points.size());
                Line2D l1 = new Line2D.Double(p.x, p.y, p1.x, p1.y);
                Line2D l2 = new Line2D.Double(p2.x, p2.y, p3.x, p3.y);
                minDistance = Math.min(minDistance, l1.ptSegDistSq(p2.x, p2.y));
                minDistance = Math.min(minDistance, l2.ptSegDistSq(p.x, p.y));
            }
        }
        return Math.sqrt(minDistance);
    }
    
    /**
     * returns a string representation of the sighting
     * @return a string representation of the sighting
     */
    @Override public String toString(){
    	return "(" + x + ", " + y + ") to (" + (x + width) + ", " + (y + height) + ")";
    }
}