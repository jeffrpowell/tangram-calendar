package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App 
{
    static int attempts = 0;
    static long startTime = 0L;
    public static void main( String[] args )
    {
        System.out.println("TANGRAM CALENDAR SOLVER");
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
        LocalDate targetDate = LocalDate.now();
        System.out.println("Solving for " + targetDate + " (" + targetDate.getDayOfWeek() + ")");
        Set<GridBranch> solutions = new HashSet<>();
        startTime = System.nanoTime();
        while (!splitPermutationNum.equals(maxIndexes)) {
            attempts++;
            List<Piece> pieceChoices = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                pieceChoices.add(pieces.get(i).get(splitPermutationNum.get(i)));
            }
            boolean log = false;
            // if (attempts % 1000 == 0) {
            //     // log = true;
            //     long elapsedSeconds = (System.nanoTime() - t) / 1_000_000_000L;
            //     System.out.println("Running average speed: " + (attempts / elapsedSeconds) + " attempts/second"); // 160-180 attempts/second w/the mod operation
            // }
            Optional<GridBranch> result = runSnipeGrid(pieceChoices, targetDate, log);
            result.ifPresent(solutions::add);
            splitPermutationNum = getNextPermutation(splitPermutationNum, maxIndexes);
            if (splitPermutationNum.stream().allMatch(i -> i == 0)) {
                break;
            }
        }
        System.out.println("Elapsed time (nanoseconds): " + (System.nanoTime() - startTime));
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

    private static Optional<GridBranch> runSnipeGrid(List<Piece> pieceChoices, LocalDate targetDate, boolean log) {
        SnipeGrid nextGrid = new SnipeGrid(pieceChoices, targetDate);
        long gridTime = 0L;
        if (log) {
            gridTime = System.nanoTime();
        }
        Optional<GridBranch> result = nextGrid.tryToFindSolution(log);
        if (log) {
            System.out.println("Grid time sample: " + (System.nanoTime() - gridTime));
        }
        if (result.isPresent()) {
            printSolution(result.get());
        }
        return result;
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
        System.out.println("Rotation permutations attempted so far: " + attempts);
        long elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000L;
        System.out.println("Running average speed: " + (attempts / elapsedSeconds) + " attempts/second"); // 180-190 attempts/second w/o the mod operation above
        System.out.println(Point2DUtils.pointsToString(printInstructions));
    }
}
