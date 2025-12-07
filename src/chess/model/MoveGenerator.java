package chess.model;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public static List<Move> legalMoves(Board board, Color toMove) {
        List<Move> out = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.get(r, c);
                if (p != null && p.color() == toMove) {
                    List<Move> pseudo = p.legalMoves(board, r, c);
                    for (Move m : pseudo) {
                        if (isLegal(board, m, toMove)) {
                            out.add(m);
                        }
                    }
                }
            }
        }
        return out;
    }

    //DRY RUN MOVE FOR KING THREAT CHECK
    public static boolean isLegal(Board board, Move m, Color toMove) {
        Board copy = board.clone();
        copy.applyMove(m);
        return !copy.isKingInCheck(toMove);
    }

    //LEGAL MOVES FOR SPECIFIC PIECE
    public static List<Move> legalMovesForPiece(Board board, int r, int c) {
        Piece p = board.get(r, c);
        if (p == null) return List.of();
        List<Move> result = new ArrayList<>();
        for (Move m : p.legalMoves(board, r, c)) {
            if (isLegal(board, m, p.color())) result.add(m);
        }
        return result;
    }
}
