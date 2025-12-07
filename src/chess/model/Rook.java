package chess.model;

import java.util.List;

public class Rook extends Piece {
    public Rook(Color color) { super(color); }

    @Override
    public Type type() { return Type.ROOK; }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}};
        return slideMoves(board, r, c, deltas);
    }
}
