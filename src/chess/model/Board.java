package chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents 8x8 chess board.
 FIXXED - Circular Recursion Via Move Verification for King Threat.
 */
public class Board implements Cloneable {
    private final Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        setupInitial();
    }

    private void setupInitial() {
        // Place pawns
        for (int c = 0; c < 8; c++) {
            board[1][c] = new Pawn(Color.BLACK);
            board[6][c] = new Pawn(Color.WHITE);
        }

        // Rooks
        board[0][0] = new Rook(Color.BLACK);
        board[0][7] = new Rook(Color.BLACK);
        board[7][0] = new Rook(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);

        // Knights
        board[0][1] = new Knight(Color.BLACK);
        board[0][6] = new Knight(Color.BLACK);
        board[7][1] = new Knight(Color.WHITE);
        board[7][6] = new Knight(Color.WHITE);

        // Bishops
        board[0][2] = new Bishop(Color.BLACK);
        board[0][5] = new Bishop(Color.BLACK);
        board[7][2] = new Bishop(Color.WHITE);
        board[7][5] = new Bishop(Color.WHITE);

        // Queens
        board[0][3] = new Queen(Color.BLACK);
        board[7][3] = new Queen(Color.WHITE);

        // Kings
        board[0][4] = new King(Color.BLACK);
        board[7][4] = new King(Color.WHITE);
    }

    public static boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    public Piece get(int r, int c) {
        if (!inBounds(r, c)) return null;
        return board[r][c];
    }

    public void set(int r, int c, Piece p) {
        board[r][c] = p;
    }

    /**
     * Apply a move to this board. Note: no validation here.
     */
    public void applyMove(Move m) {
        Piece p = get(m.fromRow, m.fromCol);
        if (p == null) return;
        // handle castling
        if (p instanceof King && Math.abs(m.toCol - m.fromCol) == 2) {
            // king-side or queen-side
            if (m.toCol == 6) { // king-side
                Piece rook = get(m.fromRow, 7);
                set(m.fromRow, 5, rook);
                set(m.fromRow, 7, null);
                if (rook != null) rook.setHasMoved(true);
            } else if (m.toCol == 2) { // queen-side
                Piece rook = get(m.fromRow, 0);
                set(m.fromRow, 3, rook);
                set(m.fromRow, 0, null);
                if (rook != null) rook.setHasMoved(true);
            }
        }

        set(m.toRow, m.toCol, p);
        set(m.fromRow, m.fromCol, null);
        p.setHasMoved(true);

        // Handle pawn promotion
        if (p instanceof Pawn) {
            if ((p.color() == Color.WHITE && m.toRow == 0) ||
                    (p.color() == Color.BLACK && m.toRow == 7)) {
                Piece promoted;
                if (m.promotion == null) {
                    promoted = new Queen(p.color()); // default to queen
                } else {
                    promoted = pieceForType(m.promotion, p.color());
                }
                set(m.toRow, m.toCol, promoted);
            }
        }
    }

    private Piece pieceForType(Piece.Type type, Color color) {
        switch (type) {
            case PAWN: return new Pawn(color);
            case KNIGHT: return new Knight(color);
            case BISHOP: return new Bishop(color);
            case ROOK: return new Rook(color);
            case QUEEN: return new Queen(color);
            case KING: return new King(color);
            default: throw new IllegalArgumentException("Unknown type");
        }
    }

    @Override
    public Board clone() {
        Board b = new Board(false);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = this.get(r, c);
                if (p != null) {
                    b.set(r, c, p.clone());
                } else {
                    b.set(r, c, null);
                }
            }
        }
        return b;
    }

    // Private constructor for cloning
    private Board(boolean empty) {
        if (empty) {
            board = new Piece[8][8];
        } else {
            board = new Piece[8][8];
        }
    }

    public int[] findKing(Color color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = get(r, c);
                if (p instanceof King && p.color() == color) {
                    return new int[]{r, c};
                }
            }
        return null;
    }

    public boolean isKingInCheck(Color color) {
        int[] pos = findKing(color);
        if (pos == null) return false; // should not happen
        return isSquareAttacked(pos[0], pos[1], color.opposite());
    }

    /**
     * Was Causing Circular Recursion ( Now Fixed )
     */
    public boolean isSquareAttacked(int r, int c, Color byColor) {
        for (int rr = 0; rr < 8; rr++) {
            for (int cc = 0; cc < 8; cc++) {
                Piece p = get(rr, cc);
                if (p == null || p.color() != byColor) continue;
                if (pieceAttacksSquare(p, rr, cc, r, c)) return true;
            }
        }
        return false;
    }

    private boolean pieceAttacksSquare(Piece p, int pr, int pc, int tr, int tc) {
        int dr = tr - pr;
        int dc = tc - pc;

        switch (p.type()) {
            case PAWN: {
                int dir = (p.color() == Color.WHITE) ? -1 : 1;
                // pawns attack one square diagonally forward
                if (dr == dir && Math.abs(dc) == 1) return true;
                return false;
            }
            case KNIGHT: {
                int[][] deltas = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
                for (int[] d : deltas) {
                    if (dr == d[0] && dc == d[1]) return true;
                }
                return false;
            }
            case BISHOP: {
                if (Math.abs(dr) != Math.abs(dc) || dr == 0) return false;
                int stepR = Integer.signum(dr);
                int stepC = Integer.signum(dc);
                int rr = pr + stepR, cc = pc + stepC;
                while (rr != tr || cc != tc) {
                    if (get(rr, cc) != null) return false;
                    rr += stepR; cc += stepC;
                }
                return true;
            }
            case ROOK: {
                if (dr != 0 && dc != 0) return false;
                int stepR = Integer.signum(dr);
                int stepC = Integer.signum(dc);
                // if both zero it is same square (not an attack)
                if (dr == 0 && dc == 0) return false;
                int rr = pr + stepR, cc = pc + stepC;
                while (rr != tr || cc != tc) {
                    if (get(rr, cc) != null) return false;
                    rr += stepR; cc += stepC;
                }
                return true;
            }
            case QUEEN: {
                // combine rook and bishop
                if (dr == 0 && dc == 0) return false;
                if (Math.abs(dr) == Math.abs(dc) || dr == 0 || dc == 0) {
                    int stepR = Integer.signum(dr);
                    int stepC = Integer.signum(dc);
                    int rr = pr + stepR, cc = pc + stepC;
                    while (rr != tr || cc != tc) {
                        if (get(rr, cc) != null) return false;
                        rr += stepR; cc += stepC;
                    }
                    return true;
                }
                return false;
            }
            case KING: {
                // King attacks adjacent squares (no castling here)
                return Math.max(Math.abs(dr), Math.abs(dc)) == 1;
            }
            default:
                return false;
        }
    }

    public List<int[]> positionsOf(Color color) {
        List<int[]> out = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = get(r, c);
                if (p != null && p.color() == color) out.add(new int[]{r, c});
            }
        return out;
    }
}