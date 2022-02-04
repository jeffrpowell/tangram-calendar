package dev.jeffrpowell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App 
{
    public static void main( String[] args )
    {
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
        long t = System.nanoTime();
        while (!splitPermutationNum.equals(maxIndexes)) {
            attempts++;
            List<Piece> pieceChoices = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                pieceChoices.add(pieces.get(i).get(splitPermutationNum.get(i)));
            }
            Grid nextGrid = new Grid(pieceChoices);
            if (nextGrid.tryToFindSolution()) {
                printSolution(nextGrid);
                break;
            }
            splitPermutationNum = getNextPermutation(splitPermutationNum, maxIndexes);
            if (splitPermutationNum.stream().allMatch(i -> i == 0)) {
                break;
            }
        }
        System.out.println("Elapsed time (nanoseconds): " + (System.nanoTime() - t));
        System.out.println("Attempted rotation permutations: " + attempts);
    }

    private static List<Integer> splitPermutationNum(Long perm) {
        String permStr = perm.toString();
        List<Integer> ints = new ArrayList<>();
        for (char c : permStr.toCharArray()) {
            ints.add(Integer.parseInt(String.valueOf(c)));
        }
        return ints;
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

    private static void printSolution(Grid g) {
        
    }
}
