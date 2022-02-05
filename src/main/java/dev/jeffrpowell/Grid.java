package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Grid {
    private final List<Piece> allPieces;
    private final Map<Point2D, Piece> placedPieces;
    private final Map<Point2D, Boolean> covered;
    
    public Grid(List<Piece> allPieces, LocalDate targetDate) {
        this.allPieces = allPieces;
        this.placedPieces = new HashMap<>();
        this.covered = GridConstants.generateGrid(targetDate);
    }

    /**
     * Pick next pt in row-major order
     * For each piece, for each piece vector
     *      translate the piece so the looped piece vector lands on the next pt
     *      if there are no collisions with the rest of the piece, generate the next branch to try (I think there is only one possible valid placement)
     * If there are no pieces that can fit in the next pt, current branch is invalid and should be abandoned
     * @return
     */
    public boolean tryToFindSolution() {
        boolean failed = false;
        while (!failed && covered.values().stream().anyMatch(Boolean.FALSE::equals)) {
            Point2D nextPt = findNextPoint();
            for (Piece piece : allPieces) { //TODO not including pieces already placed down
                List<Piece> translations = translatePieceToPoint(nextPt, piece);
            }
        }
        return false;
    }

    private Point2D findNextPoint() {
        double minY = covered.entrySet().stream()
            .filter(entry -> !entry.getValue())
            .map(Map.Entry::getKey)
            .min(Comparator.comparing(Point2D::getY)).get().getY();
        return covered.entrySet().stream()
            .filter(entry -> !entry.getValue())
            .map(Map.Entry::getKey)
            .filter(pt -> pt.getY() == minY)
            .min(Comparator.comparing(Point2D::getX)).get();
    }
    
    private List<Piece> translatePieceToPoint(Point2D pt, Piece p) {
        return p.getOriginVectors().stream().map(vector -> translateVectorsToPoint(pt, vector, p)).collect(Collectors.toList());
    }

    private Piece translateVectorsToPoint(Point2D pt, Point2D localVector, Piece p) {
        Point2D transform = new Point2D.Double(-localVector.getX(), -localVector.getY());
        return new Piece(p.getOriginVectors().stream()
            .map(originVector -> Point2DUtils.applyVectorToPt(transform, originVector))
            .map(newPieceVector -> Point2DUtils.applyVectorToPt(newPieceVector, pt))
            .collect(Collectors.toSet())
        );
    }
}
