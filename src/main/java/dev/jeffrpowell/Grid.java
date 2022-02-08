package dev.jeffrpowell;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Grid {
    private final Deque<GridBranch> branches;
    
    public Grid(List<Piece> allPieces, LocalDate targetDate) {
        branches = new ArrayDeque<>();
        branches.add(new GridBranch(allPieces, targetDate));
    }

    public Optional<GridBranch> tryToFindSolution(boolean log) {
        int totalBranches = 0;
        while(!branches.isEmpty()) {
            GridBranch branch = branches.pop();
            totalBranches++;
            if (branch.containsAPossibleSolution()) {
                List<GridBranch> downstreamBranches = branch.getDownstreamBranches();
                if (downstreamBranches.isEmpty()) {
                    return Optional.of(branch);
                }
                else {
                    branch.getDownstreamBranches().forEach(branches::push);
                }
            }
        }
        if (log) {
            System.out.println("Sample grid total branches tried: " + totalBranches);
        }
        return Optional.empty();
    }
}
