package chess.model;

import java.util.List;

public class Queen extends Piece {
    public Queen(Color color) { super(color); }

    @Override
    public Type type() { return Type.QUEEN; }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{-1,1},{1,-1},{1,1}};
        return slideMoves(board, r, c, deltas);
    }
}
