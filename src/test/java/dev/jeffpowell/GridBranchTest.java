package dev.jeffpowell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import dev.jeffpowell.GridBranch;
import dev.jeffpowell.Piece;
import dev.jeffpowell.TranslatedPiece;

public class GridBranchTest {
    GridBranch instance;

    private static final Supplier<Piece> TETRIS_S = () -> new Piece(p(0, 0), p(0, 1), p(1, 1), p(1, 2));
    private static final Supplier<Piece> TEST_PIECE = () -> new Piece(p(0, 0), p(0, 1));

    @Before
    public void setup() {
        instance = new GridBranch(List.of(TETRIS_S.get()), LocalDate.of(2022, Month.FEBRUARY, 5));
    }

    private static Point2D p(double x, double y) {
        return new Point2D.Double(x, y);
    }

    @Test
    public void testCanPieceFit() {
        //instance = new GridBranch(unplacedPieces, covered, solutionPieces)
    }

    @Test
    public void testContainsAPossibleSolution() {

    }

    @Test
    public void testCreateDownstreamBranch() {
        List<Piece> unplacedPieces = List.of(TETRIS_S.get(), TEST_PIECE.get());
        Map<Point2D, Boolean> initialCovered = Map.of(
            p(0, 0), true,
            p(0, 1), false,
            p(1, 1), false,
            p(1, 0), false
        );
        List<TranslatedPiece> solutionPieces = List.of();

        instance = new GridBranch(unplacedPieces, initialCovered, solutionPieces);

        Map<Point2D, Boolean> expectedCovered = Map.of(
            p(0, 0), true,
            p(0, 1), false,
            p(1, 1), true,
            p(1, 0), true
        );

        TranslatedPiece translatedPiece = new TranslatedPiece(TEST_PIECE.get(), Set.of(p(1,0), p(1,1)));

        assertTrue(instance.getDownstreamBranches().isEmpty());
        instance.createDownstreamBranch(translatedPiece);
        assertFalse(instance.getDownstreamBranches().isEmpty());
        GridBranch generatedBranch = instance.getDownstreamBranches().get(0);
        assertEquals(List.of(TETRIS_S.get()), generatedBranch.getUnplacedPieces());
        assertNotEquals(unplacedPieces, generatedBranch.getUnplacedPieces());
        assertEquals(List.of(translatedPiece), generatedBranch.getSolutionPieces());
        assertNotEquals(solutionPieces, generatedBranch.getSolutionPieces());
        assertEquals(expectedCovered, generatedBranch.getCovered());
        assertNotEquals(initialCovered, generatedBranch.getCovered());
    }

    @Test
    public void testFindNextPoint() {
        Point2D expected = p(1, 0);
        instance = new GridBranch(List.of(), Map.of(
            p(0, 0), true,
            p(0, 1), false,
            p(1, 1), false,
            expected, false
        ), List.of());
        Point2D actual = instance.findNextPoint();
        assertEquals(expected, actual);
    }

    @Test
    public void testTranslatePieceToPoint() {
        Point2D targetPt = p(2, 3);
        Piece piece = TETRIS_S.get();
        List<TranslatedPiece> actual = GridBranch.translatePieceToPoint(targetPt, piece);
        List<TranslatedPiece> expected = List.of(
            new TranslatedPiece(piece, Set.of(p(2,3), p(2,4), p(3,4), p(3,5))),
            new TranslatedPiece(piece, Set.of(p(2,2), p(2,3), p(3,3), p(3,4))),
            new TranslatedPiece(piece, Set.of(p(1,2), p(1,3), p(2,3), p(2,4))),
            new TranslatedPiece(piece, Set.of(p(1,1), p(1,2), p(2,2), p(2,3)))
        );
        assertTrue(expected.stream().allMatch(actual::contains));
    }
}
