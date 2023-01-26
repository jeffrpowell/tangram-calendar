package dev.jeffpowell;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class SnipeGrid {
    private final Deque<GridBranch> branches;
    
    public SnipeGrid(List<Piece> allPieces, LocalDate targetDate) {
        branches = new ArrayDeque<>();
        branches.add(new GridBranch(allPieces, targetDate));
    }

    public Optional<GridBranch> tryToFindSolution() {
        while(!branches.isEmpty()) {
            GridBranch branch = branches.pop();
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
        return Optional.empty();
    }
}
