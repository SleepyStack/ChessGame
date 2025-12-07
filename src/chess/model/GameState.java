package chess.model;

import java.util.List;

public class GameState {
    private Board board;
    private Color toMove;
    private final java.util.List<Move> history = new java.util.ArrayList<>();

    public GameState() {
        board = new Board();
        toMove = Color.WHITE;
    }

    public Board board() { return board; }
    public Color toMove() { return toMove; }
    public List<Move> history() { return history; }

    //TRIES TO MAKE MOVE IF LEGAL
    public boolean makeMove(Move m) {
        Piece p = board.get(m.fromRow, m.fromCol);
        if (p == null || p.color() != toMove) return false;
        if (!MoveGenerator.isLegal(board, m, toMove)) return false;
        board.applyMove(m);
        history.add(m);
        toMove = toMove.opposite();
        return true;
    }

    public boolean isInCheck(Color color) {
        return board.isKingInCheck(color);
    }

    public boolean isCheckmate(Color color) {
        if (!isInCheck(color)) return false;
        List<Move> moves = MoveGenerator.legalMoves(board, color);
        return moves.isEmpty();
    }

    public boolean isStalemate(Color color) {
        if (isInCheck(color)) return false;
        List<Move> moves = MoveGenerator.legalMoves(board, color);
        return moves.isEmpty();
    }
}