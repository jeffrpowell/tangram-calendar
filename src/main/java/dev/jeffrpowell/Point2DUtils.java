package dev.jeffrpowell;

import java.awt.geom.Point2D;

public class Point2DUtils {
    private Point2DUtils() {}
    
    /**
     * This assumes a left-handed coordinate system, with positive-x going right and positive-y going down
     * https://en.wikipedia.org/wiki/Rotation_matrix
     * @param pt
     * @param degrees
     * @return 
     */
    public static Point2D rotatePtRightDegreesAround0(Point2D pt, double degrees) {
        double radians = degrees * Math.PI / 180.0;
        return new Point2D.Double(
            pt.getX() * Math.round(Math.cos(radians)) - pt.getY() * Math.round(Math.sin(radians)), 
            pt.getX() * Math.round(Math.sin(radians)) + pt.getY() * Math.round(Math.cos(radians))
        );
    }

    public static Point2D applyVectorToPt(Point2D vector, Point2D pt) {
        return new Point2D.Double(pt.getX() + vector.getX(), pt.getY() + vector.getY());
    }
}
