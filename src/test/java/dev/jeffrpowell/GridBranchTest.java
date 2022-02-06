package dev.jeffrpowell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class GridBranchTest {
    GridBranch instance;

    private static final Supplier<Piece> TETRIS_S = () -> new Piece(p(0, 0), p(0, 1), p(1, 1), p(1, 2));

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
        List<Piece> actual = GridBranch.translatePieceToPoint(targetPt, piece);
        List<Piece> expected = List.of(
            new Piece(p(2,3), p(2,4), p(3,4), p(3,5)),
            new Piece(p(2,2), p(2,3), p(3,3), p(3,4)),
            new Piece(p(1,2), p(1,3), p(2,3), p(2,4)),
            new Piece(p(1,1), p(1,2), p(2,2), p(2,3))
        );
        assertTrue(expected.stream().allMatch(actual::contains));
    }
}
