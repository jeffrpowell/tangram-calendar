package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.util.Map;
import java.lang.StringBuilder;

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

    public static String pointsToString(Map<Point2D, String> pts) {
        Point2D min = pts.keySet().stream().reduce(new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE), (accum, next) -> new Point2D.Double(Math.min(accum.getX(), next.getX()), Math.min(accum.getY(), next.getY())));
        Point2D max = pts.keySet().stream().reduce(new Point2D.Double(Integer.MIN_VALUE, Integer.MIN_VALUE), (accum, next) -> new Point2D.Double(Math.max(accum.getX(), next.getX()), Math.max(accum.getY(), next.getY())));
        StringBuilder builder = new StringBuilder("(").append(min.getX()).append(",").append(min.getY()).append(") -> (").append(max.getX()).append(",").append(min.getY()).append(")\n");
        for (double row = min.getY(); row < max.getY() + 1; row++) {
            for (double col = min.getX(); col < max.getX() + 1; col++) {
                Point2D pt = new Point2D.Double(col, row);
                builder.append(pts.containsKey(pt) ? pts.get(pt): ".");
            }
            builder.append("\n");
        }
        builder.append("(").append(min.getX()).append(",").append(max.getY()).append(") -> (").append(max.getX()).append(",").append(max.getY()).append(")\n");
        return builder.toString();
    }
}
