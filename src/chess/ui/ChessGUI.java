package chess.ui;

import chess.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Improved Swing GUI for interacting with GameState.
 *
 * - Larger piece rendering using generated icons (unicode glyphs with outline)
 * - Clear contrast between piece colors and board squares
 * - Highlights: selected square (cyan), legal moves (yellow), last move (light green)
 * - Side panel with move history, status label and Restart button
 *
 * Note: Fully-qualify java.awt.Color and chess.model.Color where ambiguous.
 */
public class ChessGUI {
    private final JFrame frame;
    private final JButton[][] squares = new JButton[8][8];
    private final GameState state = new GameState();
    private int selR = -1, selC = -1;
    private Move lastMove = null;

    // UI components
    private final JTextArea historyArea = new JTextArea(20, 20);
    private final JLabel statusLabel = new JLabel(" ");
    private final int squareSize = 80; // preferred pixel size of each square

    public ChessGUI() {
        frame = new JFrame("Simple Chess (Improved GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        refresh();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = createSquareButton(r, c);
                squares[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        // Side panel
        JPanel side = new JPanel();
        side.setLayout(new BorderLayout(8, 8));
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(historyArea);
        side.add(scroll, BorderLayout.CENTER);

        JPanel topSide = new JPanel(new BorderLayout());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        topSide.add(statusLabel, BorderLayout.CENTER);
        JButton restart = new JButton("Restart");
        restart.addActionListener((ActionEvent e) -> {
            restartGame();
        });
        topSide.add(restart, BorderLayout.EAST);
        side.add(topSide, BorderLayout.NORTH);

        // root layout
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.add(boardPanel, BorderLayout.CENTER);
        root.add(side, BorderLayout.EAST);

        frame.getContentPane().add(root);
    }

    private JButton createSquareButton(int r, int c) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(squareSize, squareSize));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(java.awt.Color.DARK_GRAY));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(0);
        final int rr = r, cc = c;
        btn.addActionListener((ActionEvent e) -> onSquareClicked(rr, cc));
        return btn;
    }

    public void show() {
        frame.setVisible(true);
    }

    private void restartGame() {
        selR = selC = -1;
        lastMove = null;
        historyArea.setText("");
        // recreate game state
        // since GameState has no reset, create a new instance by reflection-like recreate:
        // simpler: create a new ChessGUI; but to keep this class, reassign state via replacement
        // For clarity, we'll create a fresh GameState in place (using field access).
        // Using direct field assignment here:
        // (Note: state is final; easier approach is to reset board via new GameState and replace UI by rebuilding)
        // We'll perform a simple full re-create of the UI to ensure fresh state:
        SwingUtilities.invokeLater(() -> {
            JFrame top = frame;
            top.dispose();
            ChessGUI newGui = new ChessGUI();
            newGui.show();
        });
    }

    private void onSquareClicked(int r, int c) {
        Piece p = state.board().get(r, c);
        if (selR == -1) {
            // select a piece
            if (p != null && p.color() == state.toMove()) {
                selR = r;
                selC = c;
                highlightLegalMoves();
            }
        } else {
            // attempt move
            List<Move> moves = MoveGenerator.legalMovesForPiece(state.board(), selR, selC);
            Move chosen = null;
            for (Move m : moves) {
                if (m.toRow == r && m.toCol == c) {
                    chosen = m;
                    break;
                }
            }
            if (chosen != null) {
                // handle promotion choice
                Piece moving = state.board().get(selR, selC);
                if (moving instanceof Pawn && (chosen.toRow == 0 || chosen.toRow == 7)) {
                    Piece.Type[] choices = {Piece.Type.QUEEN, Piece.Type.ROOK, Piece.Type.BISHOP, Piece.Type.KNIGHT};
                    String[] names = {"Queen", "Rook", "Bishop", "Knight"};
                    int sel = JOptionPane.showOptionDialog(frame,
                            "Choose promotion piece:",
                            "Pawn Promotion",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, names, names[0]);
                    if (sel >= 0) chosen = new Move(chosen.fromRow, chosen.fromCol, chosen.toRow, chosen.toCol, choices[sel]);
                }

                boolean ok = state.makeMove(chosen);
                if (!ok) {
                    JOptionPane.showMessageDialog(frame, "Illegal move.");
                } else {
                    lastMove = chosen;
                    appendHistory(chosen);
                    // show status and check conditions
                    chess.model.Color next = state.toMove();
                    updateStatus();
                    if (state.isCheckmate(next)) {
                        JOptionPane.showMessageDialog(frame, "Checkmate! " + next.opposite() + " wins.");
                    } else if (state.isStalemate(next)) {
                        JOptionPane.showMessageDialog(frame, "Stalemate!");
                    } else if (state.isInCheck(next)) {
                        // subtle UI hint: updateStatus will show "(in check)"
                    }
                }
            }
            selR = -1;
            selC = -1;
            refresh();
        }
    }

    private void appendHistory(Move m) {
        String line = String.format("%d. %s\n", state.history().size(), m.toString());
        historyArea.append(line);
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private void updateStatus() {
        chess.model.Color toMove = state.toMove();
        String text = toMove + " to move";
        if (state.isInCheck(toMove)) text += " (in check)";
        statusLabel.setText(text);
    }

    private void highlightLegalMoves() {
        refresh();
        if (selR == -1) return;
        List<Move> moves = MoveGenerator.legalMovesForPiece(state.board(), selR, selC);
        for (Move m : moves) {
            squares[m.toRow][m.toCol].setBackground(java.awt.Color.YELLOW);
        }
        squares[selR][selC].setBackground(java.awt.Color.CYAN);
    }

    private void refresh() {
        Board b = state.board();
        // First color squares and reset icons/text
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = squares[r][c];
                Piece p = b.get(r, c);
                // base square color
                boolean dark = (r + c) % 2 == 1;
                java.awt.Color lightSq = new java.awt.Color(240, 217, 181);
                java.awt.Color darkSq = new java.awt.Color(181, 136, 99);
                btn.setBackground(dark ? darkSq : lightSq);
                btn.setIcon(null);
                btn.setText("");
            }
        }

        // highlight last move
        if (lastMove != null) {
            squares[lastMove.fromRow][lastMove.fromCol].setBackground(new java.awt.Color(166, 231, 161));
            squares[lastMove.toRow][lastMove.toCol].setBackground(new java.awt.Color(166, 231, 161));
        }

        // draw pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = squares[r][c];
                Piece p = b.get(r, c);
                if (p != null) {
                    btn.setIcon(createPieceIcon(p, squareSize - 12));
                }
            }
        }

        updateStatus();
        frame.repaint();
    }

    /**
     * Create an ImageIcon rendering the unicode glyph for a piece with an outline so it's
     * visible on both light and dark squares.
     */
    private ImageIcon createPieceIcon(Piece p, int size) {
        String glyph = unicodeFor(p);
        int w = size, h = size;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // choose font size relative to image
            float fontSize = size * 0.9f;
            Font font = new Font("Serif", Font.PLAIN, Math.round(fontSize));
            FontRenderContext frc = g2.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, glyph);
            Shape shape = gv.getOutline();

            // center the glyph
            Rectangle bounds = shape.getBounds();
            double tx = (w - bounds.getWidth()) / 2.0 - bounds.getX();
            double ty = (h - bounds.getHeight()) / 2.0 - bounds.getY();
            AffineTransform transform = AffineTransform.getTranslateInstance(tx, ty);
            Shape centered = transform.createTransformedShape(shape);

            // determine fill and outline colors based on piece color
            java.awt.Color fill = (p.color() == chess.model.Color.WHITE) ? java.awt.Color.WHITE : java.awt.Color.BLACK;
            java.awt.Color outline = (p.color() == chess.model.Color.WHITE) ? java.awt.Color.BLACK : java.awt.Color.WHITE;

            // draw outline thicker than fill for visibility
            g2.setColor(outline);
            float strokeWidth = Math.max(2.0f, size * 0.06f);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(centered);

            g2.setColor(fill);
            g2.fill(centered);
        } finally {
            g2.dispose();
        }
        return new ImageIcon(img);
    }

    private String unicodeFor(Piece p) {
        if (p == null) return "";
        if (p.color() == chess.model.Color.WHITE) {
            switch (p.type()) {
                case PAWN: return "\u2659";
                case KNIGHT: return "\u2658";
                case BISHOP: return "\u2657";
                case ROOK: return "\u2656";
                case QUEEN: return "\u2655";
                case KING: return "\u2654";
            }
        } else {
            switch (p.type()) {
                case PAWN: return "\u265F";
                case KNIGHT: return "\u265E";
                case BISHOP: return "\u265D";
                case ROOK: return "\u265C";
                case QUEEN: return "\u265B";
                case KING: return "\u265A";
            }
        }
        return "?";
    }
}