package chess;

import chess.ui.ChessGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.show();
        });
    }
}
