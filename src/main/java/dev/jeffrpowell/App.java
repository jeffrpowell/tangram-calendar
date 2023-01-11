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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App 
{
    static int attempts = 0;
    static long startTime = 0L;
    static Mode mode = Mode.SOLUTION;
    static Set<GridBranch> solutions = new HashSet<>();
    enum Mode {SOLUTION("solution"), HINT("hint");
        String print;
        Mode(String print) {
            this.print = print;
        }

        static Mode fromArgs(String[] args) {
            if (args.length > 0 && args[0].equals("hint")) {
                return HINT;
            }
            return SOLUTION;
        }

        @Override
        public String toString() {
            return print;
        }
    }
    public static void main( String[] args )
    {
        System.out.println("TANGRAM CALENDAR SOLVER");
        mode = Mode.fromArgs(args);
        System.out.println("Running in " + mode + " mode");
        solve(5);
    }

    private static void solve(int hintThreshold) {
        List<List<Piece>> pieces = PieceFactory.createPieces().stream()
            .map(PieceFactory::generateDerivativePieces)
            .collect(Collectors.toList());
        
        List<Integer> maxIndexes = pieces.stream()
            .map(List::size)
            .collect(Collectors.toList());

        List<Integer> splitPermutationNum = 
            IntStream.generate(() -> 0)
                .limit(pieces.size())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        LocalDate targetDate = LocalDate.now();
        System.out.println("Solving for " + targetDate + " (" + targetDate.getDayOfWeek() + ")");
        startTime = System.nanoTime();
        while ((mode == Mode.SOLUTION && !splitPermutationNum.equals(maxIndexes)) || (mode == Mode.HINT && solutions.size() < hintThreshold)) {
            attempts++;
            List<Piece> pieceChoices = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                pieceChoices.add(pieces.get(i).get(splitPermutationNum.get(i)));
            }
            Optional<GridBranch> result = runSnipeGrid(pieceChoices, targetDate);
            result.ifPresent(solutions::add);
            splitPermutationNum = getNextPermutation(splitPermutationNum, maxIndexes);
            if (splitPermutationNum.stream().allMatch(i -> i == 0)) {
                break;
            }
        }
        if (mode == Mode.SOLUTION) {
            System.out.println("Elapsed time (nanoseconds): " + (System.nanoTime() - startTime));
            System.out.println("Solutions found: " + solutions.size());
            System.out.println("Attempted rotation permutations (out of 8.3MM): " + attempts);
        }
        else {
            boolean goodHintFound = produceHint();
            if (!goodHintFound) {
                solve(hintThreshold + 3);
            }
        }
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

    private static Optional<GridBranch> runSnipeGrid(List<Piece> pieceChoices, LocalDate targetDate) {
        SnipeGrid nextGrid = new SnipeGrid(pieceChoices, targetDate);
        Optional<GridBranch> result = nextGrid.tryToFindSolution();
        if (result.isPresent()) {
            printSolution(result.get());
        }
        return result;
    }

    private static void printSolution(GridBranch branch) {
        if (mode == Mode.HINT) {
            System.out.println("Solution " + (solutions.size()+1) + " found");
        }
        System.out.println("Rotation permutations attempted so far: " + attempts);
        long elapsedSeconds = Math.max((System.nanoTime() - startTime) / 1_000_000_000L, 1L);
        System.out.println("Elapsed seconds: " + elapsedSeconds);
        System.out.println("Running average speed: " + (attempts / elapsedSeconds) + " attempts/second"); // 180-190 attempts/second w/o the mod operation above
        if (mode == Mode.SOLUTION) {
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

    private static boolean produceHint() {
        Map<TranslatedPiece, Long> allSolvedPieces = solutions.stream().map(GridBranch::getSolutionPieces).flatMap(List::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<TranslatedPiece> frequentSolutionPieces = allSolvedPieces.entrySet().stream().filter(e -> e.getValue() > 2).map(Map.Entry::getKey).collect(Collectors.toList());
        Set<Point2D> committedPts = new HashSet<>();
        Set<Piece> committedPieces = new HashSet<>();
        List<TranslatedPiece> filteredFrequentSolutionPieces = new ArrayList<>();
        for (TranslatedPiece piece : frequentSolutionPieces) {
            if (piece.getLocations().stream().anyMatch(committedPts::contains) || committedPieces.contains(piece.getOriginPiece())) {
                continue;
            }
            committedPts.addAll(piece.getLocations());
            committedPieces.add(piece.getOriginPiece());
            filteredFrequentSolutionPieces.add(piece);
        }
        if (filteredFrequentSolutionPieces.size() < 2) {
            System.out.println("Unlucky set of solutions, there were not enough pieces that were frequently in the same spot. Shuffling pieces and trying a few more.");
            return false;
        }
        Map<Point2D, String> printInstructions = GridConstants.generateGrid().keySet().stream().collect(Collectors.toMap(Function.identity(), k -> "."));
        for (int i = 0; i < Math.min(filteredFrequentSolutionPieces.size(), 4); i++) {
            TranslatedPiece p = filteredFrequentSolutionPieces.get(i);
            for (Point2D pt : p.getLocations()) {
                printInstructions.put(pt, Integer.toString(i));
            }
        }
        System.out.println("\nToday's hint:");
        System.out.println(Point2DUtils.pointsToString(printInstructions));
        return true;
    }
}
