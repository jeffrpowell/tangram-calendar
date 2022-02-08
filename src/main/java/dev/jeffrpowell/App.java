package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("TANGRAM CALENDAR SOLVER");
        System.out.println();
        System.out.println("Initializing all the pieces and their rotations");
        List<List<Piece>> pieces = PieceFactory.createPieces().stream()
            .map(PieceFactory::generateDerivativePieces)
            .collect(Collectors.toList());
        
        List<Integer> maxIndexes = pieces.stream()
            .map(l -> l.size())
            .collect(Collectors.toList());

        List<Integer> splitPermutationNum = 
            IntStream.generate(() -> 0)
                .limit(pieces.size())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        int attempts = 0;
        LocalDate targetDate = LocalDate.now();
        System.out.println("Solving for " + targetDate);
        long t = System.nanoTime();
        while (!splitPermutationNum.equals(maxIndexes)) {
            attempts++;
            List<Piece> pieceChoices = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                pieceChoices.add(pieces.get(i).get(splitPermutationNum.get(i)));
            }
            Grid nextGrid = new Grid(pieceChoices, targetDate);
            long gridTime = 0L;
            boolean log = false;
            if (attempts % 1000 == 0) {
                gridTime = System.nanoTime();
                log = true;
            }
            Optional<GridBranch> result = nextGrid.tryToFindSolution(log);
            if (log) {
                System.out.println("Grid time sample: " + (System.nanoTime() - gridTime));
            }
            if (result.isPresent()) {
                printSolution(result.get());
                break;
            }
            splitPermutationNum = getNextPermutation(splitPermutationNum, maxIndexes);
            if (splitPermutationNum.stream().allMatch(i -> i == 0)) {
                break;
            }
        }
        System.out.println("Elapsed time (nanoseconds): " + (System.nanoTime() - t));
        System.out.println("Attempted rotation permutations (out of 8.3MM): " + attempts);
    }

    private static List<Integer> getNextPermutation(List<Integer> lastPermNum, List<Integer> maxIndexes) {
        List<Integer> nextPermNum = new ArrayList<>(lastPermNum);
        for (int i = lastPermNum.size() - 1; i >= 0; i--) {
            int nextNum = (nextPermNum.get(i) + 1) % maxIndexes.get(i);
            nextPermNum.set(i, nextNum);
            if (nextNum != 0) {
                break;
            }
        }
        return nextPermNum;
    }

    private static void printSolution(GridBranch branch) {
        List<TranslatedPiece> solutionPieces = branch.getSolutionPieces();
        Map<Point2D, String> printInstructions = new HashMap<>();
        for (int i = 0; i < solutionPieces.size(); i++) {
            TranslatedPiece p = solutionPieces.get(i);
            for (Point2D pt : p.getLocations()) {
                printInstructions.put(pt, Integer.toString(i));
            }
        }
        System.out.println(Point2DUtils.pointsToString(printInstructions));
    }
}
