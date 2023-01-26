package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App
{
    static final String CF_BASE_URL = "https://tangram-calendar-submit.jeffpowell.dev/";
    static final Function<LocalDate, URI> CF_HINT_URI = d -> URI.create(CF_BASE_URL + "hint/" + d);
    static final Function<LocalDate, URI> CF_SOLUTION_URI = d -> URI.create(CF_BASE_URL + "solution/" + d);
    static final String MAX_DATE_FILENAME = "maxDate";
    static int attempts = 0;
    static long startTime = 0L;
    static Mode mode = Mode.SOLUTION;
    static Set<GridBranch> solutions = new HashSet<>();
    static HttpClient httpClient = HttpClient.newHttpClient();

    enum Mode {SOLUTION("solution"), HINT("hint"), CLOUDFLARE("cloudflare");
        String print;
        Mode(String print) {
            this.print = print;
        }

        static Mode fromArgs(String[] args) {
            if (args.length > 0) {
                if (args[0].equals("hint")) {
                    return HINT;
                }
                else if (args[0].equals("cloudflare")) {
                    return CLOUDFLARE;
                }
            }
            return SOLUTION;
        }

        @Override
        public String toString() {
            return print;
        }
    }
    public static void main( String[] args ) throws IOException, InterruptedException
    {
        System.out.println("TANGRAM CALENDAR SOLVER");
        mode = Mode.fromArgs(args);
        System.out.println("Running in " + mode + " mode");
        if (mode == Mode.CLOUDFLARE) {
            LocalDate maxDate = LocalDate.now();
            try(Scanner scanner = new Scanner(new File(MAX_DATE_FILENAME))){
                if (scanner.hasNext()) {
                    String maxDateStr = scanner.next();
                    maxDate = LocalDate.parse(maxDateStr);
                    System.out.println("Found maxDate file.");
                }
            }
            for (int i = 0; i < 7; i++) {
                maxDate = maxDate.plusDays(1);
                solutions.clear();
                solve(5, maxDate);
                try (PrintWriter out = new PrintWriter(MAX_DATE_FILENAME)) {
                    out.print(maxDate);
                }
            }
        }
        else {
            solve(5, LocalDate.now());
        }
    }

    private static List<String> inputStreamToStringList(InputStream inputStream) {
		return new BufferedReader(new InputStreamReader(inputStream))
			.lines().collect(Collectors.toList());
	}

    private static void solve(int hintThreshold, LocalDate targetDate) throws IOException, InterruptedException{
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
        System.out.println("Solving for " + targetDate + " (" + targetDate.getDayOfWeek() + ")");
        startTime = System.nanoTime();
        while ((mode == Mode.SOLUTION && !splitPermutationNum.equals(maxIndexes)) || ((mode == Mode.HINT || mode == Mode.CLOUDFLARE) && solutions.size() < hintThreshold)) {
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
            boolean goodHintFound = produceHint(targetDate);
            if (!goodHintFound) {
                solve(hintThreshold + 3, targetDate);
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

    private static boolean produceHint(LocalDate targetDate) throws IOException, InterruptedException{
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
        String hint = Point2DUtils.pointsToString(printInstructions);
        if (mode == Mode.CLOUDFLARE) {
            System.out.println("Sending POST " + CF_HINT_URI.apply(targetDate));
            HttpRequest hintRequest = HttpRequest.newBuilder()
                .uri(CF_HINT_URI.apply(targetDate))
                .POST(BodyPublishers.ofString(hint))
                .build();
            httpClient.send(hintRequest, BodyHandlers.ofString());
            List<TranslatedPiece> solutionPieces = solutions.stream().findAny().get().getSolutionPieces();
            printInstructions = new HashMap<>();
            for (int i = 0; i < solutionPieces.size(); i++) {
                TranslatedPiece p = solutionPieces.get(i);
                for (Point2D pt : p.getLocations()) {
                    printInstructions.put(pt, Integer.toString(i));
                }
            }
            String solution = Point2DUtils.pointsToString(printInstructions);
            System.out.println("Sending POST " + CF_SOLUTION_URI.apply(targetDate));
            HttpRequest solutionRequest = HttpRequest.newBuilder()
                .uri(CF_SOLUTION_URI.apply(targetDate))
                .POST(BodyPublishers.ofString(solution))
                .build();
            httpClient.send(solutionRequest, BodyHandlers.ofString());
        }
        else {
            System.out.println("\nToday's hint:");
            System.out.println(hint);
        }
        return true;
    }
}
