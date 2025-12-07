package chess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Piece implements Cloneable {
    public enum Type {PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING}

    protected final Color color;
    protected boolean hasMoved = false;

    public Piece(Color color) {
        this.color = Objects.requireNonNull(color);
    }

    public Color color() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    public abstract Type type();

    public abstract List<Move> legalMoves(Board board, int r, int c);

    @Override
    public Piece clone() {
        try {
            Piece p = (Piece) super.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    protected List<Move> slideMoves(Board board, int r, int c, int[][] deltas) {
        List<Move> moves = new ArrayList<>();
        for (int[] d : deltas) {
            int rr = r + d[0], cc = c + d[1];
            while (Board.inBounds(rr, cc)) {
                Piece other = board.get(rr, cc);
                if (other == null) {
                    moves.add(new Move(r, c, rr, cc));
                } else {
                    if (other.color() != this.color) {
                        moves.add(new Move(r, c, rr, cc));
                    }
                    break;
                }
                rr += d[0];
                cc += d[1];
            }
        }
        return moves;
    }
}
