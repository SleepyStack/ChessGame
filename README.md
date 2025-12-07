# Offline Java Based Chess Game

A clean, easy-to-read Java implementation of chess with a minimal Swing GUI intended for a college project. The code focuses on clarity and correctness for core chess rules and is easy to extend.

Important: This project uses only Java and Swing for the frontend.

## Features
- Full object-oriented board & piece model (Pawn, Knight, Bishop, Rook, Queen, King).
- Pseudo-legal move generation per piece plus filtering into legal moves (moves that would leave the king in check are rejected).
- Castling, pawn promotion, check, checkmate and stalemate detection.
- Swing-based GUI with:
  - Click-to-select and click-to-move interaction.
  - Visual highlights (selected square, legal moves, last move).
  - Larger piece icons rendered from Unicode glyphs for good visibility on light/dark squares.
  - Move history and simple status area.
- Clean code structure suitable for study and extension.

## Limitations / Known omissions
- En-passant is intentionally left out (not implemented).
- Draw rules not implemented: fifty-move rule, threefold repetition, and insufficient material detection.
- No AI engine — two-player local play only.
- Promotion choices are offered via a dialog in the GUI; move-generation currently treats promotion at apply-time (the GUI prompts the user).

## Project layout
- src/chess/Main.java — application entry point.
- src/chess/model — board, pieces, move, move generator and game state:
  - Board.java
  - Piece.java (+ Pawn, Knight, Bishop, Rook, Queen, King)
  - Move.java
  - MoveGenerator.java
  - GameState.java
- src/chess/ui — Swing UI:
  - ChessGUI.java

## Build & run (command-line)
On Unix-like systems (Linux, macOS) with JDK installed:

1. Compile:
   javac -d out $(find src -name "*.java")

2. Run:
   java -cp out chess.Main

On Windows (PowerShell / CMD), a simple alternative:
1. Create output directory:
   mkdir out
2. Compile (PowerShell):
   Get-ChildItem -Recurse -Filter *.java | ForEach-Object { & javac -d out $_.FullName }
3. Run:
   java -cp out chess.Main

Notes:
- You can also import the `src` directory into an IDE (IntelliJ IDEA, Eclipse, NetBeans) as a plain Java project and run Main from the IDE.
- No external libraries are required.

## UI Notes / Customization
- The GUI uses Unicode chess glyphs rendered into image icons so pieces are large and visible on both light and dark squares.
- To tweak square size, edit ChessGUI.squareSize (default 80).
- If you see a compilation error related to `Color`, it's likely a name conflict between `java.awt.Color` and the project's `chess.model.Color`. The GUI fully-qualifies `java.awt.Color` and `chess.model.Color` where needed; avoid wildcard imports that might re-introduce ambiguity.

## Testing & Debugging tips
- If you encounter an infinite recursion / StackOverflowError while running, ensure Board.isSquareAttacked does not call piece.legalMoves() — attack detection must use direct attack logic to avoid recursion with King.castling checks. The shipped Board implementation uses direct attack checks.
- Add unit tests to validate:
  - Castling rights in various board states.
  - Promotion behavior.
  - Check/checkmate/stalemate patterns.
  - Pinning and discovered checks.

## Packaging
To produce a runnable JAR (optional):
1. Compile into `out` as above.
2. Create manifest (e.g., `manifest.txt`) with:
   Main-Class: chess.Main
3. Package:
   jar cfm chess.jar manifest.txt -C out .

Run:
   java -jar chess.jar

## Contributing
This project is intended as a one time college project. If you'd like to contribute:
- Fork and submit a PR with focused changes (e.g., en-passant implementation, tests, UI improvements).
- Keep changes modular and include tests when appropriate.

## Contact / Attribution
Project built for a college assignment. If you need help extending features (en-passant, engine, tests, UI polish), describe what you'd like and include any failing behavior or error traces — happy to help.
