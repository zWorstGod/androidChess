package com.mcm.chess;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ChessModel {
    static final Vector<ChessPiece> piecesBox = new Vector<>();
    ChessPlayer movedLast = ChessPlayer.BLACK;
//
//    {
//        reset();
//    }

    void clear(){
        piecesBox.clear();
    }

    void addPiece(ChessPiece piece) {
        piecesBox.add(piece);
    }

    private Boolean canRookMove(Square from, Square to) {
        return from.col == to.col && isClearVerticallyBetween(from, to) ||
                from.row == to.row && isClearHorizontallyBetween(from, to);
    }

    private Boolean canKnightMove(Square from, Square to) {
        return abs(from.col - to.col) == 2 && abs(from.row - to.row) == 1 ||
                abs(from.col - to.col) == 1 && abs(from.row - to.row) == 2;
    }

    private Boolean canBishopMove(Square from, Square to) {
        if (abs(from.col - to.col) == abs(from.row - to.row))
            return isClearDiagonally(from, to);
        return false;
    }

    private Boolean canQueenMove(Square from, Square to) {
        return canRookMove(from, to) || canBishopMove(from, to);
    }

    private Boolean canKingMove(Square from, Square to) {
        if (canQueenMove(from, to)) {
            int deltaCol = abs(from.col - to.col);
            int deltaRow = abs(from.row - to.row);
            return deltaCol == 1 && deltaRow == 1 || deltaCol + deltaRow == 1;
        }
        return false;
    }

    private Boolean canPawnMove(Square from, Square to) {
        ChessPiece piece = pieceAt(from.col, from.row);
        boolean eatingAtTo = pieceAt(to.col, to.row) != null;
        System.out.println("CAN PAWN MOVE = "+eatingAtTo);
        assert piece != null;
        ChessPlayer player = piece.player;
        if (from.col == to.col) {
            if (player == ChessPlayer.WHITE) {
                if (from.row == 1)
                    return to.row == 2 || to.row == 3;
                else if (to.row - 1 == from.row && !eatingAtTo)
                    return true;
            } else {
                if (from.row == 6)
                    return to.row == 5 || to.row == 4;
                else if (to.row + 1 == from.row && !eatingAtTo)
                    return true;
            }
        } else if (from.col - 1 == to.col || from.col + 1 == to.col){
            if (player == ChessPlayer.WHITE && from.row + 1 == to.row && eatingAtTo)
                return true;
            else if (from.row - 1 == to.row && eatingAtTo)
                return true;
        }


        return false;
    }

    private Boolean isClearVerticallyBetween(Square from, Square to) {
        if (from.col != to.col) return false;

        int gap = abs(from.row - to.row) - 1;
        if (gap == 0) return true;

        int nextRow;
        for (int i = 1; i <= gap; i++) {
            if (to.row > from.row) nextRow = from.row + i;
            else nextRow = from.row - i;

            if (pieceAt(new Square(from.col, nextRow)) != null) return false;
        }
        return true;
    }

    private Boolean isClearHorizontallyBetween(Square from, Square to) {
        if (from.row != to.row) return false;
        int gap = abs(from.col - to.col) - 1;
        if (gap == 0) return true;

        int nextCol;
        for (int i = 1; i <= gap; i++){
            if (to.col > from.col) nextCol = from.col + i;
            else nextCol = from.col - i;

            if (pieceAt(new Square(from.col, nextCol)) != null) return false;
        }
        return true;
    }

    private Boolean isClearDiagonally(Square from, Square to) {
        if (abs(from.col - to.col) != abs(from.row - to.row)) return false;
        int gap = abs(from.col - to.col) - 1;

        int nextCol, nextRow;
        for (int i = 1; i <= gap; i++) {
            if (to.col > from.col) nextCol = from.col + i;
            else nextCol = from.col - i;
            if (to.row > from.row) nextRow = from.row + i;
            else nextRow = from.row - i;

            if (pieceAt(nextCol, nextRow) != null) return false;
        }
        return true;
    }

    private boolean canMove(Square from, Square to) {
        if (from.col == to.col && from.row == to.row) return false;
        ChessPiece movingPiece = pieceAt(from);
        if (movingPiece.player == movedLast) return false;
        if (movingPiece == null) return false;
        switch (movingPiece.rank) {
            case ROOK:
                return canRookMove(from, to);
            case KNIGHT:
                return canKnightMove(from, to);
            case BISHOP:
                return canBishopMove(from, to);
            case QUEEN:
                return canQueenMove(from, to);
            case KING:
                return canKingMove(from, to);
            case PAWN:
                return canPawnMove(from, to);
            default:
                return false;
        }
    }

    void movePiece(Square from, Square to) {
        if (canMove(from, to) && !gameOver()) {
            System.out.println("Not same square = "+!(from.col == to.col && from.row == to.row));
            if (from.col == to.col && from.row == to.row) return;

            ChessPiece movingPiece = pieceAt(from.col, from.row);

            System.out.println("Moving piece isn't null = "+!(movingPiece == null));
            if (movingPiece == null) return;

            ChessPiece destinationPiece = pieceAt(to.col, to.row);
            if (destinationPiece != null) {
                System.out.println(destinationPiece.player+", "+movingPiece.player);
                if (destinationPiece.player == movingPiece.player) return;
                piecesBox.remove(destinationPiece);
            }

            System.out.println("Removing piece from original place...");
            piecesBox.remove(movingPiece);
            movingPiece.col = to.col;
            movingPiece.row = to.row;
            addPiece(movingPiece);

            if (movedLast == ChessPlayer.BLACK) movedLast = ChessPlayer.WHITE;
            else movedLast = ChessPlayer.BLACK;
        }
    }

    void reset(){
        clear();

        // ROOK, KNIGHT, BISHOP
        for (int i = 0; i <= 1; i++) {
            piecesBox.add(new ChessPiece(i*7, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook_white));
            piecesBox.add(new ChessPiece(i*7, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook_black));

            piecesBox.add(new ChessPiece(1+i*5, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight_white));
            piecesBox.add(new ChessPiece(1+i*5, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight_black));

            piecesBox.add(new ChessPiece(2+i*3, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop_white));
            piecesBox.add(new ChessPiece(2+i*3, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop_black));
        }

        // PAWN
        for (int i = 0; i <= 8; i++){
            piecesBox.add(new ChessPiece(i, 1, ChessPlayer.WHITE, ChessRank.PAWN, R.drawable.pawn_white));
            piecesBox.add(new ChessPiece(i, 6, ChessPlayer.BLACK, ChessRank.PAWN, R.drawable.pawn_black));
        }

        // QUEEN
        piecesBox.add(new ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.QUEEN, R.drawable.queen_white));
        piecesBox.add(new ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN, R.drawable.queen_black));

        // KING
        piecesBox.add(new ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.KING, R.drawable.king_white));
        piecesBox.add(new ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING, R.drawable.king_black));
    }

    public static ChessPiece pieceAt(Square square) {
        return pieceAt(square.col, square.row);
    }
    public static ChessPiece pieceAt(int col, int row){
        for (int piece = 0; piece < piecesBox.size(); piece++){
            if (col == piecesBox.get(piece).col && row == piecesBox.get(piece).row) {
                return piecesBox.get(piece);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder desc = new StringBuilder(" \n");
        for(int row = 0; row <= 7; row++){
            desc.append(7 - row);
            for (int col = 0; col <= 7; col++){
                ChessPiece piece = pieceAt(col, 7 - row);
                if (piece == null) {
                    desc.append(" .");
                }
                else {
                    desc.append(" ");
                    boolean white = (piece.player == ChessPlayer.WHITE);
                    switch (piece.rank) {
                        case KING:
                            if (white) desc.append("k");
                            else desc.append("K");
                            break;
                        case QUEEN:
                            if (white) desc.append("q");
                            else desc.append("Q");
                            break;
                        case PAWN:
                            if (white) desc.append("p");
                            else desc.append("P");
                            break;
                        case ROOK:
                            if (white) desc.append("r");
                            else desc.append("R");
                            break;
                        case BISHOP:
                            if (white) desc.append("b");
                            else desc.append("B");
                            break;
                        case KNIGHT:
                            if (white) desc.append("n");
                            else desc.append("N");
                            break;
                    }
                }
            }
            desc.append("\n");
        }
        desc.append("  0 1 2 3 4 5 6 7");
        return desc.toString();
    }

    public boolean gameOver() {
        int kings = 0;
        for (ChessPiece piece : piecesBox) {
            if (piece.rank == ChessRank.KING)
                kings++;
        }
        return kings != 2;
    }
}
