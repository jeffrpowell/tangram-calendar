package dev.jeffpowell;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GridBranch {
    private final List<Piece> unplacedPieces;
    private final Map<Point2D, Boolean> covered;
    private final List<GridBranch> downstreamBranches;
    private final List<TranslatedPiece> solutionPieces;
    
    /**
     * Root-node constructor for SnipeGrid
     * @param unplacedPieces
     * @param targetDate
     */
    public GridBranch(List<Piece> unplacedPieces, LocalDate targetDate) {
        this.unplacedPieces = unplacedPieces;
        this.covered = GridConstants.generateGrid(targetDate);
        this.downstreamBranches = new ArrayList<>();
        this.solutionPieces = new ArrayList<>();
    }

    /**
     * Root-node constructor for GatherGrid
     * @param unplacedPieces
     * @param targetDate
     */
    public GridBranch(List<Piece> unplacedPieces) {
        this.unplacedPieces = unplacedPieces;
        this.covered = GridConstants.generateGrid();
        this.downstreamBranches = new ArrayList<>();
        this.solutionPieces = new ArrayList<>();
    }
    
    /**
     * Branch-node constructor (i.e. caller is responsible for recording what points are covered by a placed piece)
     * @param unplacedPieces
     * @param covered
     */
    public GridBranch(List<Piece> unplacedPieces, Map<Point2D, Boolean> covered, List<TranslatedPiece> solutionPieces) {
        this.unplacedPieces = unplacedPieces;
        this.covered = covered;
        this.downstreamBranches = new ArrayList<>();
        this.solutionPieces = solutionPieces;
    }

    /**
     * Pick next pt in row-major order
     * For each piece, for each piece vector
     *      translate the piece so the looped piece vector lands on the next pt
     *      if there are no collisions with the rest of the piece, generate the next branch to try (I think there is only one possible valid placement)
     * If there are no pieces that can fit in the next pt, current branch is invalid and should be abandoned
     * @return
     */
    public boolean containsAPossibleSolution() {
        Point2D nextPt = findNextPoint();
        for (Piece piece : unplacedPieces) {
            List<TranslatedPiece> translations = translatePieceToPoint(nextPt, piece);
            Optional<TranslatedPiece> pieceLocationThatFits = translations.stream().filter(this::canPieceFit).findAny();
            if (pieceLocationThatFits.isPresent()) {
                if (unplacedPieces.size() == 1) {
                    solutionPieces.add(pieceLocationThatFits.get());
                    return true;
                }
                else {
                    createDownstreamBranch(pieceLocationThatFits.get());
                }
            }
        }
        return !downstreamBranches.isEmpty();
    }

    public List<GridBranch> getDownstreamBranches() {
        return downstreamBranches;
    }

    public List<TranslatedPiece> getSolutionPieces() {
        return solutionPieces;
    }

    List<Piece> getUnplacedPieces() {
        return unplacedPieces;
    }

    Map<Point2D, Boolean> getCovered() {
        return covered;
    }

    Point2D findNextPoint() {
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
    
    static List<TranslatedPiece> translatePieceToPoint(Point2D pt, Piece p) {
        return p.getOriginVectors().stream().map(vector -> translateVectorsToPoint(pt, vector, p)).collect(Collectors.toList());
    }

    static TranslatedPiece translateVectorsToPoint(Point2D pt, Point2D localVector, Piece p) {
        Point2D transform = new Point2D.Double(-localVector.getX(), -localVector.getY());
        return new TranslatedPiece(p, p.getOriginVectors().stream()
            .map(originVector -> Point2DUtils.applyVectorToPt(transform, originVector))
            .map(newPieceVector -> Point2DUtils.applyVectorToPt(newPieceVector, pt))
            .collect(Collectors.toSet())
        );
    }

    boolean canPieceFit(TranslatedPiece p) {
        return p.getLocations().stream().allMatch(pt -> covered.containsKey(pt) && Boolean.FALSE.equals(covered.get(pt)));
    }

    void createDownstreamBranch(TranslatedPiece p) {
        Set<Point2D> piecePts = p.getLocations();
        Map<Point2D, Boolean> newCovered = covered.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> {
                    if (piecePts.contains(entry.getKey())){
                        return true;
                    }
                    return entry.getValue();
                }
            ));
        List<Piece> newUnplacedPieces = unplacedPieces.stream().filter(piece -> !piece.equals(p.getOriginPiece())).collect(Collectors.toList());
        List<TranslatedPiece> newSolutionPieces = Stream.concat(solutionPieces.stream(), Stream.of(p)).collect(Collectors.toList());
        downstreamBranches.add(new GridBranch(newUnplacedPieces, newCovered, newSolutionPieces));
    }

    public Set<Point2D> getUncovered() {
        return covered.entrySet().stream()  
            .filter(entry -> entry.getValue() == false)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unplacedPieces == null) ? 0 : unplacedPieces.hashCode());
        result = prime * result + ((solutionPieces == null) ? 0 : solutionPieces.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridBranch other = (GridBranch) obj;
        if (unplacedPieces == null) {
            if (other.unplacedPieces != null)
                return false;
        } else if (!unplacedPieces.equals(other.unplacedPieces))
            return false;
        if (solutionPieces == null) {
            if (other.solutionPieces != null)
                return false;
        } else if (!solutionPieces.equals(other.solutionPieces))
            return false;
        return true;
    }

    
}
