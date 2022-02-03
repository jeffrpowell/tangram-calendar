package dev.jeffrpowell;

import java.util.List;
import java.awt.geom.Point2D;

public class PieceFactory {
    private PieceFactory() {}

    public static List<Piece> createPieces() {
        return List.of(
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 1), p(2, 1)), //T
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2), p(2, 2)), //wide L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(0, 3), p(1, 3)), //tall L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2)),          //small L
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 0), p(1, 2)), //U
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(0, 3)),          //|
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 1), p(1, 2)), //Utah
            new Piece(p(0, 0), p(0, 1), p(1, 1), p(1, 2)),          //Tetris s
            new Piece(p(0, 0), p(1, 0), p(1, 1), p(1, 2), p(2, 2)), //Z
            new Piece(p(0, 0), p(0, 1), p(0, 2), p(1, 2), p(1, 3))  //Skidding s
        );
    }

    private static Point2D p(double x, double y) {
        return new Point2D.Double(x, y);
    }
    
    public static List<Piece> generateDerivativePieces(Piece p) {
        return List.of(p, p, p, p, p, p, p, p);
    }
}
