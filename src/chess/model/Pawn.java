package chess.model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public Type type() {
        return Type.PAWN;
    }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        List<Move> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;
        int startRow = (color == Color.WHITE) ? 6 : 1;

        // Forward one
        int nr = r + dir;
        if (Board.inBounds(nr, c) && board.get(nr, c) == null) {
            moves.add(new Move(r, c, nr, c));
            // Forward two
            int nr2 = r + 2 * dir;
            if (r == startRow && board.get(nr2, c) == null) {
                moves.add(new Move(r, c, nr2, c));
            }
        }

        // Capturing
        for (int dc : new int[]{-1, 1}) {
            int cc = c + dc;
            if (Board.inBounds(nr, cc)) {
                Piece target = board.get(nr, cc);
                if (target != null && target.color() != color) {
                    moves.add(new Move(r, c, nr, cc));
                }
                // En passant ...Will Implement If I have time.
            }
        }

        return moves;
    }
}
