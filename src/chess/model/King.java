package chess.model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(Color color) { super(color); }

    @Override
    public Type type() { return Type.KING; }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        List<Move> moves = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int rr = r + dr, cc = c + dc;
                if (!Board.inBounds(rr, cc)) continue;
                Piece p = board.get(rr, cc);
                if (p == null || p.color() != this.color) {
                    moves.add(new Move(r, c, rr, cc));
                }
            }
        }

        // Castle
        if (!hasMoved && !board.isKingInCheck(this.color)) {
            // King-side
            if (canCastle(board, r, c, 7, 6, 5)) {
                moves.add(new Move(r, c, r, 6));
            }
            // Queen-side
            if (canCastle(board, r, c, 0, 2, 3)) {
                moves.add(new Move(r, c, r, 2));
            }
        }

        return moves;
    }

    private boolean canCastle(Board board, int r, int c, int rookCol, int targetCol, int betweenCol) {
        Piece rook = board.get(r, rookCol);
        if (!(rook instanceof Rook) || rook.color() != this.color || rook.hasMoved()) return false;
        int step = (rookCol > c) ? 1 : -1;
        // Not Blocked.
        for (int col = c + step; col != rookCol; col += step) {
            if (board.get(r, col) != null) return false;
        }
        // Under Threat??
        int col1 = c + step;
        int col2 = c + 2 * step;
        if (board.isSquareAttacked(r, col1, this.color.opposite())) return false;
        if (board.isSquareAttacked(r, col2, this.color.opposite())) return false;
        return true;
    }
}
