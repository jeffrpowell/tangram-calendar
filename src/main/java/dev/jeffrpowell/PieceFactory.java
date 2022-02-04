package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PieceFactory {
    private PieceFactory() {}

    public static List<Piece> createPieces() {
        return List.of(
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 1), p(2, 1)), //T
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2), p(2, 2)), //wide L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(0, 3), p(1, 3)), //tall L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2)),          //small L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 0), p(1, 2)), //U
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(0, 3)),          //|
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 1), p(1, 2)), //Utah
            new Piece(p(0, 0), p(0, 1), p(1, 1), p(1, 2)),          //Tetris s
            new Piece(p(0, 0), p(1, 0), p(1, 1), p(1, 2), p(2, 2)), //Z
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2), p(1, 3))  //Skidding s
        );
    }

    private static Point2D p(double x, double y) {
        return new Point2D.Double(x, y);
    }
    
    public static List<Piece> generateDerivativePieces(Piece p) {
        Set<Piece> pieces = new HashSet<>();
        pieces.add(p);
        pieces.add(flipPieceOverYAxis(p));
        for (int i = 0; i < 3; i++) {
            p = rotatePieceRight(p);
            pieces.add(p);
            pieces.add(flipPieceOverYAxis(p));
        }
        return new ArrayList<>(pieces);
    }

    private static Piece rotatePieceRight(Piece p) {
        Set<Point2D> pts = p.getOriginVectors();
        double maxY = pts.stream().map(Point2D::getY).max(Comparator.naturalOrder()).get();
        return new Piece(pts.stream()
            .map(pt -> PieceFactory.rotatePtRightDegreesAround0(pt, 90D))
            .map(pt -> PieceFactory.applyVectorToPt(new Point2D.Double(maxY, 0), pt))
            .collect(Collectors.toSet()));
    }

    private static Piece flipPieceOverYAxis(Piece p) {
        Set<Point2D> pts = p.getOriginVectors();
        double maxX = pts.stream().map(Point2D::getX).max(Comparator.naturalOrder()).get();
        return new Piece(pts.stream()
            .map(pt -> PieceFactory.applyVectorToPt(new Point2D.Double(-2 * pt.getX() + maxX, 0), pt))
            .collect(Collectors.toSet()));
    }

    /**
     * This assumes a left-handed coordinate system, with positive-x going right and positive-y going down
     * https://en.wikipedia.org/wiki/Rotation_matrix
     * @param pt
     * @param degrees
     * @return 
     */
    private static Point2D rotatePtRightDegreesAround0(Point2D pt, double degrees) {
        double radians = degrees * Math.PI / 180.0;
        return new Point2D.Double(
            pt.getX() * Math.round(Math.cos(radians)) - pt.getY() * Math.round(Math.sin(radians)), 
            pt.getX() * Math.round(Math.sin(radians)) + pt.getY() * Math.round(Math.cos(radians))
        );
    }

    private static Point2D applyVectorToPt(Point2D vector, Point2D pt) {
        return new Point2D.Double(pt.getX() + vector.getX(), pt.getY() + vector.getY());
    }
}
