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
    
    public Grid(List<Piece> allPieces) {
        this.allPieces = allPieces;
        this.placedPieces = new HashMap<>();
        this.covered = new HashMap<>();
    }

    public void registerTargetDate(LocalDate d) {
        covered.put(GridConstants.getMonthLocation(d.getMonth()), true);
        covered.put(GridConstants.getDayLocation(d.getDayOfMonth()), true);
        covered.put(GridConstants.getDayOfWeekLocation(d.getDayOfWeek()), true);
    }

    public boolean tryToFindSolution() {
        return false;
    }
    
}
