package chess.model;

public class Move {
    public final int fromRow, fromCol;
    public final int toRow, toCol;
    public final Piece.Type promotion; // null if not a promotion

    public Move(int fr, int fc, int tr, int tc) {
        this(fr, fc, tr, tc, null);
    }

    public Move(int fr, int fc, int tr, int tc, Piece.Type promotion) {
        this.fromRow = fr;
        this.fromCol = fc;
        this.toRow = tr;
        this.toCol = tc;
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)->(%d,%d)%s", fromRow, fromCol, toRow, toCol,
                promotion != null ? " promote to " + promotion : "");
    }
}
