package chess.model;

import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color) { super(color); }

    @Override
    public Type type() { return Type.BISHOP; }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        int[][] deltas = {{-1,-1},{-1,1},{1,-1},{1,1}};
        return slideMoves(board, r, c, deltas);
    }
}
