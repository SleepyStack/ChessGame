package chess.model;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Color color) { super(color); }

    @Override
    public Type type() { return Type.KNIGHT; }

    @Override
    public List<Move> legalMoves(Board board, int r, int c) {
        List<Move> moves = new ArrayList<>();
        int[][] deltas = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] d : deltas) {
            int rr = r + d[0], cc = c + d[1];
            if (!Board.inBounds(rr, cc)) continue;
            Piece p = board.get(rr, cc);
            if (p == null || p.color() != this.color) {
                moves.add(new Move(r, c, rr, cc));
            }
        }
        return moves;
    }
}
