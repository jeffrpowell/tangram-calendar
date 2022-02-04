package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return false;
    }
    
}
