import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class Main.
 */
public final class Main {

    private Main() {
    }

    /** instance {@code chessBoard} of class {@link Board}.
     * that employs Chess logic
     * */
    private static Board chessBoard;

    /**
     * The main function that gets input values
     * and calls other functions.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        ErrorWriter err = new ErrorWriter();

        int whiteK = 0;
        int blackK = 0;

        Parse parser = new Parse();

        Path inputFile = Paths.get("input.txt");
        try {
            List<String> lines = Files.readAllLines(inputFile, StandardCharsets.UTF_8);


            int n = parser.parseBoardSize(lines);
            chessBoard = new Board(n);

            int m = parser.parseNumberOfPieces(lines, n);

            final int maxArrLength = 1000;
            int[] moves = new int[maxArrLength];
            int[] captures = new int[maxArrLength];
            final int numOfArguments = 4;

            // Check on number of pieces
            if (m != lines.size() - 2) {
                err.reportFatalError(new InvalidNumberOfPiecesException());
            }

            PiecePosition[] listOfFigures = new PiecePosition[maxArrLength];

            for (int i = 0; i < m; i++) {

                String[] arr = lines.get(i + 2).split(" ");
                if (arr.length != numOfArguments) {
                    err.reportFatalError(new InvalidInputException());
                }
                String pieceName = parser.parsePieceType(arr[0]);
                PieceColor color = PieceColor.parse(arr[1]);
                if (pieceName.equals("King")) {
                    if (whiteK >= 1 && color.equals(PieceColor.WHITE)) {
                        err.reportFatalError(new InvalidGivenKingsException());
                    } else if (blackK >= 1 && color.equals(PieceColor.BLACK)) {
                        err.reportFatalError(new InvalidGivenKingsException());
                    } else if (color.equals(PieceColor.WHITE)) {
                        whiteK += 1;
                    } else if (color.equals(PieceColor.BLACK)) {
                        blackK += 1;
                    }
                }
                PiecePosition pos = new PiecePosition(parser.parsePosition(arr[2], n),
                                                      parser.parsePosition(arr[2 + 1], n));

                listOfFigures[i] = pos;

                switch (pieceName) {
                    case "Knight":
                        chessBoard.addPiece(new Knight(pos, color));
                        break;
                    case "King":
                        chessBoard.addPiece(new King(pos, color));
                        break;
                    case "Pawn":
                        chessBoard.addPiece(new Pawn(pos, color));
                        break;
                    case "Bishop":
                        chessBoard.addPiece(new Bishop(pos, color));
                        break;
                    case "Rook":
                        chessBoard.addPiece(new Rook(pos, color));
                        break;
                    case "Queen":
                        chessBoard.addPiece(new Queen(pos, color));
                        break;
                    default:
                        break;
                }

            }

            if (whiteK == 0 || blackK == 0) {
                err.reportFatalError(new InvalidGivenKingsException());
            }


            for (int j = 0; j < m; j++) {
                moves[j] = chessBoard.getPiecePossibleMovesCount(chessBoard.getPiece(listOfFigures[j]));
                captures[j] = chessBoard.getPiecePossibleCapturesCount(chessBoard.getPiece(listOfFigures[j]));
            }


            try (FileWriter writer = new FileWriter("output.txt", false)) {
                for (int j = 0; j < m; j++) {
                    writer.write(moves[j] + " " + captures[j]);
                    writer.write('\n');
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            err.reportFatalError(new InvalidInputException());
        }

    }

}

/**
 * Class {@code ErrorWriter} to write exceptions to the file.
 */
class ErrorWriter {
    /**
     * Report fatal error.
     *
     * @param e the e
     */
    public void reportFatalError(Exception e) {
        try (FileWriter writer = new FileWriter("output.txt", false)) {
            writer.write(e.getMessage());
            writer.flush();
            System.exit(0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}

/**
 * Class {@code Parse} to handle input values.
 */
class Parse {

    /**
     * Instance of class {@link ErrorWriter}.
     * Used to throw exceptions
     */
    private ErrorWriter err = new ErrorWriter();

    /**
     * Function to parse the size of {@link Board}.
     *
     * @param fileContent the file content - an array of file lines
     * @return the size of Board
     */
    public int parseBoardSize(List<String> fileContent) {
        try {
            Integer.parseInt(fileContent.get(0));
        } catch (NumberFormatException e) {
            err.reportFatalError(new InvalidBoardSizeException());
        }

        int localBoardSize = Integer.parseInt(fileContent.get(0));
        final int minBoardSize = 3;
        final int maxBoardSize = 1000;
        if (localBoardSize < minBoardSize || localBoardSize > maxBoardSize) {
            err.reportFatalError(new InvalidBoardSizeException());
        }
        return localBoardSize;
    }

    /**
     * Function to parse number of pieces on the {@link Board}.
     *
     * @param fileContent    the file content
     * @param localBoardSize the local board size
     * @return the int
     */
    public int parseNumberOfPieces(List<String> fileContent, int localBoardSize) {
        try {
            Integer.parseInt(fileContent.get(1));
        } catch (NumberFormatException e) {
            err.reportFatalError(new InvalidNumberOfPiecesException());
        }
        int localNumOfPieces = Integer.parseInt(fileContent.get(1));
        if (localNumOfPieces < 2 || localNumOfPieces > Math.pow(localBoardSize, 2)) {
            err.reportFatalError(new InvalidNumberOfPiecesException());
        }
        return localNumOfPieces;
    }

    /**
     * Function to parse {@link ChessPiece} type.
     *
     * @param piece the piece
     * @return the string - an exception message
     */
    public String parsePieceType(String piece) {
        String[] listOfTypes = {"Pawn", "King", "Knight", "Rook", "Queen", "Bishop"};
        for (String elem: listOfTypes) {
            if (piece.equals(elem)) {
                return piece;
            }
        }
        err.reportFatalError(new InvalidPieceNameException());
        return piece;
    }


    /**
     * Parse position int.
     *
     * @param coordinate the coordinate of {@link ChessPiece}
     * @param n          the {@code boardSize} value
     * @return the int - if position is valid
     */
    public int parsePosition(String coordinate, int n) {
        try {
            Integer.parseInt(coordinate);
        } catch (NumberFormatException e) {
            err.reportFatalError(new InvalidPiecePositionException());
        }
        int localPosition = Integer.parseInt(coordinate);
        if (localPosition < 1 || localPosition > n) {
            err.reportFatalError(new InvalidPiecePositionException());
        }
        return localPosition;
    }
}


/**
 * Class {@code PiecePosition}, correlates coordinates to Pieces on {@link Board}.
 */
class PiecePosition {
    /** Variable describing the X coordinate of {@link ChessPiece}. */
    private int x;
    /** Variable describing the Y coordinate of {@link ChessPiece}. */
    private int y;

    /**
     * Instantiates a new Piece position.
     *
     * @param onX the x
     * @param onY the y
     */
    PiecePosition(int onX, int onY) {
        this.x = onX;
        this.y = onY;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    public String toString() {
        return x + " " + y;
    }
}

/**
 * The enum {@code PieceColor}, correlates color with {@link ChessPiece}.
 */
enum PieceColor {
    /**
     * White piece color.
     */
    WHITE,
    /**
     * Black piece color.
     */
    BLACK;

    /**
     * Function to parse color of piece on the {@link Board}.
     *
     * @param color the color
     * @return the piece color
     */
    public static PieceColor parse(String color) {
        if (!(color.equals("Black")) && !(color.equals("White"))) {
            new ErrorWriter().reportFatalError(new InvalidPieceColorException());
        } else if (color.equals("Black")) {
            return PieceColor.BLACK;
        }

        return PieceColor.WHITE;
    }
}


/**
 * The interface Bishop movement.
 */
interface BishopMovement {
    /**
     * Gets diagonal moves count.
     *
     * @param position  the position
     * @param color     the color
     * @param positions the positions Map
     * @param boardSize the board size
     * @return the diagonal moves count
     */
    default int getDiagonalMovesCount(PiecePosition position,
                                     PieceColor color,
                                     Map<String, ChessPiece> positions,
                                     int boardSize) {
        int x = position.getX();
        int y = position.getY();
        int leftDownMove = Integer.MAX_VALUE;
        int rightDownMove = Integer.MAX_VALUE;
        int leftUpMove = Integer.MAX_VALUE;
        int rightUpMove = Integer.MAX_VALUE;
        int numOfMoves = 0;
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            int moveX = pair.getValue().getPosition().getX();
            int moveY = pair.getValue().getPosition().getY();
            if (x - moveX > 0 && x - moveX == y - moveY) {
                leftDownMove = Math.min(x - moveX - 1, leftDownMove);
            } else if (x - moveX > 0 && x - moveX == moveY - y) {
                leftUpMove = Math.min(x - moveX - 1, leftUpMove);
            } else if (moveX - x > 0 && moveX - x == y - moveY) {
                rightDownMove = Math.min(moveX - x - 1, rightDownMove);
            } else if (moveX - x > 0 && moveX - x == moveY - y) {
                rightUpMove = Math.min(moveX - x - 1, rightUpMove);
            }
        }

        if (leftDownMove != Integer.MAX_VALUE) {
            numOfMoves += leftDownMove;
        } else {
            numOfMoves += Math.min(x - 1, y - 1);
        }
        if (leftUpMove != Integer.MAX_VALUE) {
            numOfMoves += leftUpMove;
        } else {
            numOfMoves += Math.min(x - 1, boardSize - y);
        }
        if (rightDownMove != Integer.MAX_VALUE) {
            numOfMoves += rightDownMove;
        } else {
            numOfMoves += Math.min(boardSize - x, y - 1);
        }
        if (rightUpMove != Integer.MAX_VALUE) {
            numOfMoves += rightUpMove;
        } else {
            numOfMoves += Math.min(boardSize - x, boardSize - y);
        }

        numOfMoves += getDiagonalCapturesCount(position, color, positions, boardSize);

        return numOfMoves;
    }


    /**
     * Get diagonal captures count int.
     *
     * @param position  the position
     * @param color     the color
     * @param positions the positions
     * @param boardSize the board size
     * @return the diagonal captures count
     */
    default int getDiagonalCapturesCount(PiecePosition position,
                                        PieceColor color,
                                        Map<String, ChessPiece> positions,
                                        int boardSize) {
        int x = position.getX();
        int y = position.getY();
        boolean leftDownCapture = false;
        boolean rightDownCapture = false;
        boolean leftUpCapture = false;
        boolean rightUpCapture = false;
        int minLeftUpDistance = Integer.MAX_VALUE;
        int minLeftDownDistance = Integer.MAX_VALUE;
        int minRightUpDistance = Integer.MAX_VALUE;
        int minRightDownDistance = Integer.MAX_VALUE;
        int numOfCaptures = 0;
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            if (color.equals(pair.getValue().getColor())) {
                int moveX = pair.getValue().getPosition().getX();
                int moveY = pair.getValue().getPosition().getY();
                if (x - moveX == y - moveY && x - moveX > 0) {
                    minLeftDownDistance = Math.min(minLeftDownDistance, x - moveX);
                }
                if (x - moveX == moveY - y && x - moveX > 0) {
                    minLeftUpDistance = Math.min(minLeftUpDistance, x - moveX);
                }
                if (moveX - x == y - moveY && moveX - x > 0) {
                    minRightDownDistance = Math.min(minRightDownDistance, moveX - x);
                }
                if (moveX - x == moveY - y && moveX - x > 0) {
                    minRightUpDistance = Math.min(minRightUpDistance, moveX - x);
                }
            }
        }
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            if (!color.equals(pair.getValue().getColor())) {
                int moveX = Integer.parseInt(pair.getKey().split(" ")[0]);
                int moveY = Integer.parseInt(pair.getKey().split(" ")[1]);
                if (x - moveX == y - moveY && x - moveX > 0 && x - moveX < minLeftDownDistance) {
                    leftDownCapture = true;
                }
                if (x - moveX == moveY - y && x - moveX > 0 && x - moveX < minLeftUpDistance) {
                    leftUpCapture = true;
                }
                if (moveX - x == y - moveY && moveX - x > 0 && moveX - x < minRightDownDistance) {
                    rightDownCapture = true;
                }
                if (moveX - x == moveY - y && moveX - x > 0 && moveX - x < minRightUpDistance) {
                    rightUpCapture = true;
                }
            }
        }
        if (leftUpCapture) {
            numOfCaptures += 1;
        }
        if (rightUpCapture) {
            numOfCaptures += 1;
        }
        if (leftDownCapture) {
            numOfCaptures += 1;
        }
        if (rightDownCapture) {
            numOfCaptures += 1;
        }
        return numOfCaptures;
    }
}

/**
 * The interface Rook movement.
 */
interface RookMovement {
    /**
     * Gets orthogonal moves count.
     *
     * @param position  the position
     * @param color     the color
     * @param positions the positions
     * @param boardSize the board size
     * @return the orthogonal moves count
     */
    default int getOrthogonalMovesCount(PiecePosition position,
                                               PieceColor color,
                                               Map<String, ChessPiece> positions,
                                               int boardSize) {
        int x = position.getX();
        int y = position.getY();
        int upMove = Integer.MAX_VALUE;
        int downMove = Integer.MAX_VALUE;
        int leftMove = Integer.MAX_VALUE;
        int rightMove = Integer.MAX_VALUE;
        int numOfMoves = 0;
        for (Map.Entry<String, ChessPiece> pair : positions.entrySet()) {
            int moveX = pair.getValue().position.getX();
            int moveY = pair.getValue().position.getY();
            if (x == moveX) {
                if (moveY - y > 0) {
                    upMove = Math.min(upMove, moveY - y - 1);
                } else if (moveY - y < 0) {
                    downMove = Math.min(downMove, y - moveY - 1);
                }
            } else if (y == moveY) {
                if (moveX - x > 0) {
                    rightMove = Math.min(rightMove, moveX - x - 1);
                } else if (moveX - x < 0) {
                    leftMove = Math.min(leftMove, x - moveX - 1);
                }
            }
        }
        if (upMove != Integer.MAX_VALUE) {
            numOfMoves += upMove;
        } else {
            numOfMoves += boardSize - y;
        }
        if (rightMove != Integer.MAX_VALUE) {
            numOfMoves += rightMove;
        } else {
            numOfMoves += boardSize - x;
        }
        if (leftMove != Integer.MAX_VALUE) {
            numOfMoves += leftMove;
        } else {
            numOfMoves += x - 1;
        }
        if (downMove != Integer.MAX_VALUE) {
            numOfMoves += downMove;
        } else {
            numOfMoves += y - 1;
        }

        numOfMoves += getOrthogonalCapturesCount(position, color, positions, boardSize);

        return numOfMoves;
    }


    /**
     * Gets orthogonal captures count.
     *
     * @param position  the position
     * @param color     the color
     * @param positions the positions
     * @param boardSize the board size
     * @return the orthogonal captures count
     */
    default int getOrthogonalCapturesCount(PiecePosition position,
                                                  PieceColor color,
                                                  Map<String, ChessPiece> positions,
                                                  int boardSize) {
        int x = position.getX();
        int y = position.getY();
        boolean leftCapture = false;
        boolean rightCapture = false;
        boolean upCapture = false;
        boolean downCapture = false;
        int minDistanceUp = Integer.MAX_VALUE;
        int minDistanceDown = Integer.MAX_VALUE;
        int minDistanceLeft = Integer.MAX_VALUE;
        int minDistanceRight = Integer.MAX_VALUE;
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            if (pair.getValue().getColor().equals(color)) {
                int moveX = pair.getValue().getPosition().getX();
                int moveY = pair.getValue().getPosition().getY();
                if (moveX == x) {
                    if (moveY - y > 0) {
                        minDistanceUp = Math.min(minDistanceUp, moveY - y);
                    } else if (moveY - y < 0) {
                        minDistanceDown = Math.min(minDistanceDown, y - moveY);
                    }
                }
                if (moveY == y) {
                    if (moveX - x > 0) {
                        minDistanceRight = Math.min(minDistanceRight, moveX - x);
                    } else if (moveX - x < 0) {
                        minDistanceLeft = Math.min(minDistanceLeft, x - moveX);
                    }
                }
            }
        }
        int numOfCaptures = 0;
        for (Map.Entry<String, ChessPiece> pair : positions.entrySet()) {
            if (!pair.getValue().getColor().equals(color)) {
                int moveX = pair.getValue().getPosition().getX();
                int moveY = pair.getValue().getPosition().getY();
                if (moveX == x) {
                    if (moveY - y > 0 && moveY - y < minDistanceUp) {
                        upCapture = true;
                    } else if (moveY - y < 0 && y - moveY < minDistanceDown) {
                        downCapture = true;
                    }
                } else if (moveY == y) {
                    if (moveX - x > 0 && moveX - x < minDistanceRight) {
                        rightCapture = true;
                    } else if (moveX - x < 0 && x - moveX < minDistanceLeft) {
                        leftCapture = true;
                    }
                }
            }
        }
        if (leftCapture) {
            numOfCaptures += 1;
        }
        if (rightCapture) {
            numOfCaptures += 1;
        }
        if (upCapture) {
            numOfCaptures += 1;
        }
        if (downCapture) {
            numOfCaptures += 1;
        }

        return numOfCaptures;
    }
}

/**
 * Class {@code ChessPiece}, super class for all Pieces.
 */
abstract class ChessPiece {
    /**
     * The Position variable, describes coordinates of {@link ChessPiece}.
     */
    protected PiecePosition position;
    /**
     * The Color, describes color of {@link ChessPiece}.
     */
    protected PieceColor color;

    /**
     * Instantiates a new Chess piece.
     *
     * @param piecePosition the piece position
     * @param pieceColor    the color
     */
    ChessPiece(PiecePosition piecePosition, PieceColor pieceColor) {
        this.position = piecePosition;
        this.color = pieceColor;
    }

    /**
     * Get position piece position.
     *
     * @return the piece position
     */
    public PiecePosition getPosition() {
        return position;
    }

    /**
     * Get color piece color.
     *
     * @return the piece color
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * Gets moves count.
     *
     * @param positions the positions
     * @param boardSize the board size
     * @return the moves count
     */
    public abstract int getMovesCount(Map<String, ChessPiece> positions, int boardSize);

    /**
     * Gets captures count.
     *
     * @param positions the positions
     * @param boardSize the board size
     * @return the captures count
     */
    public abstract int getCapturesCount(Map<String, ChessPiece> positions, int boardSize);

}

/**
 * Class Knight of type {@link ChessPiece}.
 */
class Knight extends ChessPiece {
    /**
     * Instantiates a new Knight.
     *
     * @param position the position
     * @param color    the color
     */
    Knight(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /** function that counts possible moves for Knight. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        int numOfMoves = 0;
        int coordX = this.position.getX();
        int coordY = this.position.getY();
        if (coordX - 2 > 0 && coordY - 1 > 0
                && !positions.containsKey(coordX - 2 + " " + (coordY - 1))) {
            numOfMoves += 1;
        }
        if (coordX - 2 > 0 && coordY + 1 < boardSize + 1
                && !positions.containsKey(coordX - 2 + " " + (coordY + 1))) {
            numOfMoves += 1;
        }
        if (coordX - 1 > 0 && coordY - 2 > 0
                && !positions.containsKey(coordX - 1 + " " + (coordY - 2))) {
            numOfMoves += 1;
        }
        if (coordX - 1 > 0 && coordY + 2 < boardSize + 1
                && !positions.containsKey(coordX - 1 + " " + (coordY + 2))) {
            numOfMoves += 1;
        }
        if (coordX + 1 < boardSize + 1 && coordY - 2 > 0
                && !positions.containsKey(coordX + 1 + " " + (coordY - 2))) {
            numOfMoves += 1;
        }
        if (coordX + 1 < boardSize + 1 && coordY + 2 < boardSize + 1
                && !positions.containsKey(coordX + 1 + " " + (coordY + 2))) {
            numOfMoves += 1;
        }
        if (coordX + 2 < boardSize + 1 && coordY - 1 > 0
                && !positions.containsKey(coordX + 2 + " " + (coordY - 1))) {
            numOfMoves += 1;
        }
        if (coordX + 2 < boardSize + 1 && coordY + 1 < boardSize + 1
                && !positions.containsKey(coordX + 2 + " " + (coordY + 1))) {
            numOfMoves += 1;
        }

        numOfMoves += getCapturesCount(positions, boardSize);

        return numOfMoves;
    }

    /** function that counts possible captures for Knight. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        int coordX = this.position.getX();
        int coordY = this.position.getY();
        int numOfCaptures = 0;
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            int moveX = Integer.parseInt(pair.getKey().split(" ")[0]);
            int moveY = Integer.parseInt(pair.getKey().split(" ")[1]);
            if (!(pair.getValue().getColor().equals(this.color))) {
                if (moveX == coordX - 2) {
                    if (moveY == coordY - 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 1) {
                        numOfCaptures += 1;
                    }
                } else if (moveX == coordX - 1) {
                    if (moveY == coordY - 2) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 2) {
                        numOfCaptures += 1;
                    }
                } else if (moveX == coordX + 1) {
                    if (moveY == coordY - 2) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 2) {
                        numOfCaptures += 1;
                    }
                } else if (moveX == coordX + 2) {
                    if (moveY == coordY - 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 1) {
                        numOfCaptures += 1;
                    }
                }
            }
        }
        return numOfCaptures;
    }
}

/**
 * Class King of type {@link ChessPiece}.
 */
class King extends ChessPiece {
    /**
     * Instantiates a new King.
     *
     * @param position the position
     * @param color    the color
     */
    King(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /** function that counts possible moves for King. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        int numOfMoves = 0;
        int x = this.position.getX();
        int y = this.position.getY();
        // Left
        if (x - 1 > 0 && !(positions.containsKey((x - 1) + " " + y))) {
            numOfMoves += 1;
        }
        // Left-Bottom
        if (x - 1 > 0 && y - 1 > 0 && !(positions.containsKey((x - 1) + " " + (y - 1)))) {
            numOfMoves += 1;
        }
        // Left-Top
        if (x - 1 > 0 && y < boardSize && !(positions.containsKey((x - 1) + " " + (y + 1)))) {
            numOfMoves += 1;
        }
        // Right-Bottom
        if (x < boardSize && y - 1 > 0 && !(positions.containsKey(x + 1 + " " + (y - 1)))) {
            numOfMoves += 1;
        }
        // Right-Top
        if (x < boardSize && y < boardSize && !(positions.containsKey(x + 1 + " " + (y + 1)))) {
            numOfMoves += 1;
        }
        // Right
        if (x < boardSize && !(positions.containsKey(x + 1 + " " + y))) {
            numOfMoves += 1;
        }
        // Bottom
        if (y - 1 > 0 && !(positions.containsKey(x + " " + (y - 1)))) {
            numOfMoves += 1;
        }
        // Top
        if (y < boardSize && !(positions.containsKey(x + " " + (y + 1)))) {
            numOfMoves += 1;
        }

        numOfMoves += getCapturesCount(positions, boardSize);

        return numOfMoves;
    }

    /** function that counts possible captures for King. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        int numOfCaptures = 0;
        int coordX = this.position.getX();
        int coordY = this.position.getY();
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            if (!(pair.getValue().getColor().equals(this.color))) {
                int moveX = Integer.parseInt(pair.getKey().split(" ")[0]);
                int moveY = Integer.parseInt(pair.getKey().split(" ")[1]);
                if (moveX == coordX - 1) {
                    if (moveY == coordY - 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY) {
                        numOfCaptures += 1;
                    }
                } else if (moveX == coordX + 1) {
                    if (moveY == coordY - 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY) {
                        numOfCaptures += 1;
                    }
                } else if (moveX == coordX) {
                    if (moveY == coordY - 1) {
                        numOfCaptures += 1;
                    }
                    if (moveY == coordY + 1) {
                        numOfCaptures += 1;
                    }
                }
            }
        }
        return numOfCaptures;
    }
}

/**
 * Class Pawn of type {@link ChessPiece}.
 */
class Pawn extends ChessPiece {
    /**
     * Instantiates a new Pawn.
     *
     * @param position the position
     * @param color    the color
     */
    Pawn(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /** function that counts possible moves for Pawn. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        int coordX = this.position.getX();
        int coordY = this.position.getY();
        int numOfMoves = 0;
        if (color == PieceColor.WHITE) {
            if (coordY + 1 < boardSize + 1
            && !positions.containsKey(String.valueOf(coordX) + " " + String.valueOf(coordY + 1))) {
                numOfMoves += 1;
            }
        } else if (color == PieceColor.BLACK) {
            if (coordY - 1 > 0
            && !positions.containsKey(String.valueOf(coordX) + " " + String.valueOf(coordY - 1))) {
                numOfMoves += 1;
            }
        }
        numOfMoves += getCapturesCount(positions, boardSize);
        return numOfMoves;
    }

    /** function that counts possible captures for Pawn. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        int coordX = this.position.getX();
        int coordY = this.position.getY();
        int numOfCaptures = 0;
        for (Map.Entry<String, ChessPiece> pair: positions.entrySet()) {
            String moveY = pair.getKey().split(" ")[1];
            String moveX = pair.getKey().split(" ")[0];
            if (!pair.getValue().getColor().equals(this.color)) {
                if (this.color.equals(PieceColor.WHITE)) {
                    if (moveX.equals(String.valueOf(coordX + 1)) && moveY.equals(String.valueOf(coordY + 1))) {
                        numOfCaptures += 1;
                    }
                    if (moveX.equals(String.valueOf(coordX - 1)) && moveY.equals(String.valueOf(coordY + 1))) {
                        numOfCaptures += 1;
                    }
                } else if (this.color.equals(PieceColor.BLACK)) {
                    if (moveX.equals(String.valueOf(coordX + 1)) && moveY.equals(String.valueOf(coordY - 1))) {
                        numOfCaptures += 1;
                    }
                    if (moveX.equals(String.valueOf(coordX - 1)) && moveY.equals(String.valueOf(coordY - 1))) {
                        numOfCaptures += 1;
                    }
                }
            }
        }
        return numOfCaptures;
    }
}

/**
 * Class Bishop of type {@link ChessPiece}.
 */
class Bishop extends ChessPiece implements BishopMovement {
    /**
     * Instantiates a new Bishop.
     *
     * @param position the position
     * @param color    the color
     */
    Bishop(PiecePosition position, PieceColor color) {
        super(position, color);
    }


    /** function that calls implementation of possible moves counter for Bishop. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getDiagonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /** function that calls implementation of possible captures counter for Bishop. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getDiagonalCapturesCount(this.position, this.color, positions, boardSize);
    }
}


/**
 * Class Rook of type {@link ChessPiece}.
 */
class Rook extends ChessPiece implements RookMovement {
    /**
     * Instantiates a new Rook.
     *
     * @param position the position
     * @param color    the color
     */
    Rook(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /** function that calls implementation of possible moves counter for Rook. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getOrthogonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /** function that calls implementation of possible captures counter for Rook. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getOrthogonalCapturesCount(this.position, this.color, positions, boardSize);
    }
}

/**
 * Class Queen of type {@link ChessPiece}.
 */
class Queen extends ChessPiece implements BishopMovement, RookMovement {
    /**
     * Instantiates a new Queen.
     *
     * @param position the position
     * @param color    the color
     */
    Queen(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /** function that calls implementation of possible moves counter for Queen. */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getDiagonalMovesCount(this.position, this.color, positions, boardSize)
               + getOrthogonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /** function that calls implementation of possible captures counter for Queen. */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        return getDiagonalCapturesCount(this.position, this.color, positions, boardSize)
               + getOrthogonalCapturesCount(this.position, this.color, positions, boardSize);
    }
}

/**
 * Class Board that interconnects instances of {@link ChessPiece}.
 */
class Board {

    /**
     * Instance of class {@link ErrorWriter}.
     * Used to throw exceptions
     */
    private ErrorWriter err = new ErrorWriter();
    /**
     * The Positions to pieces {@link HashMap} - used to bind {@link ChessPiece} with {@link PiecePosition}.
     */
    private Map<String, ChessPiece> positionsToPieces = new HashMap();

    /** A variable that represents size of a ChessBoard. */
    private int size;

    /**
     * Instantiates a new Board.
     *
     * @param boardSize the board size
     */
    Board(int boardSize) {
        this.size = boardSize;
    }

    /**
     * Gets piece possible moves count.
     *
     * @param piece the piece
     * @return the piece possible moves count
     */
    public int getPiecePossibleMovesCount(ChessPiece piece) {
        return piece.getMovesCount(positionsToPieces, size);
    }

    /**
     * Gets piece possible captures count.
     *
     * @param piece the piece
     * @return the piece possible captures count
     */
    public int getPiecePossibleCapturesCount(ChessPiece piece) {
        return piece.getCapturesCount(positionsToPieces, size);
    }

    /**
     * Add piece.
     *
     * @param piece the piece
     */
    public void addPiece(ChessPiece piece) {
        String coordinates = piece.position.getX() + " " + piece.position.getY();
        if (positionsToPieces.containsKey(coordinates)) {
            err.reportFatalError(new InvalidPiecePositionException());
        }
        positionsToPieces.put(coordinates, piece);
    }

    /**
     * Gets piece.
     *
     * @param position the position
     * @return the piece
     */
    public ChessPiece getPiece(PiecePosition position) {
        String coordinates = position.toString();

        for (Map.Entry<String, ChessPiece> pair : positionsToPieces.entrySet()) {
            if (pair.getKey().equals(coordinates)) {
                return pair.getValue();
            }
        }
        return null;
    }
}

/**
 * Class Invalid board size exception.
 */
class InvalidBoardSizeException extends Exception {
    public String getMessage() {
        return "Invalid board size";
    }
}

/**
 * Class Invalid number of pieces exception.
 */
class InvalidNumberOfPiecesException extends Exception {
    public String getMessage() {
        return "Invalid number of pieces";
    }
}

/**
 * Class Invalid piece name exception.
 */
class InvalidPieceNameException extends Exception {
    public String getMessage() {
        return "Invalid piece name";
    }
}

/**
 * Class Invalid piece color exception.
 */
class InvalidPieceColorException extends Exception {
    public String getMessage() {
        return "Invalid piece color";
    }
}

/**
 * Class Invalid piece position exception.
 */
class InvalidPiecePositionException extends Exception {
    public String getMessage() {
        return "Invalid piece position";
    }
}

/**
 * Class Invalid given kings exception.
 */
class InvalidGivenKingsException extends Exception {
    public String getMessage() {
        return "Invalid given Kings";
    }
}

/**
 * Class Invalid input exception.
 */
class InvalidInputException extends Exception {
    public String getMessage() {
        return "Invalid input";
    }
}
