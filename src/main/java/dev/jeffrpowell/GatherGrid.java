package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public class GatherGrid {
    private final Deque<GridBranch> branches;
    private final List<GridBranch> solutions;
    
    public GatherGrid(List<Piece> allPieces) {
        this.branches = new ArrayDeque<>();
        this.branches.add(new GridBranch(allPieces));
        this.solutions = new ArrayList<>();
    }

    public List<GridBranch> tryToFindSolutions(boolean log) {
        int totalBranches = 0;
        int solutionBranches = 0;
        while(!branches.isEmpty()) {
            GridBranch branch = branches.pop();
            if (branch.containsAPossibleSolution()) {
                List<GridBranch> downstreamBranches = branch.getDownstreamBranches();
                if (downstreamBranches.isEmpty()) {
                    totalBranches++;
                    solutionBranches++;
                    Set<Point2D> uncoveredPts = branch.getUncovered();
                    if (GridConstants.isValidSolution(uncoveredPts)) {
                        solutions.add(branch);
                    }
                }
                else {
                    branch.getDownstreamBranches().forEach(branches::push);
                }
            }
            else {
                totalBranches++;
            }
        }
        if (log) {
            System.out.println("Sample grid total branches tried: " + totalBranches);
            System.out.println("Sample grid total branches leading to a potential solution: " + solutionBranches);
        }
        return solutions;
    }
}
