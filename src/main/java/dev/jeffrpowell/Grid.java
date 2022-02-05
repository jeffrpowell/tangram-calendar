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

    public Optional<GridBranch> tryToFindSolution() {
        while(!branches.isEmpty()) {
            GridBranch branch = branches.getFirst();
            if (branch.containsAPossibleSolution()) {
                return Optional.of(branch);
            }
            else {
                branches.addAll(branch.getDownstreamBranches());
            }
        }
        return Optional.empty();
    }
}
