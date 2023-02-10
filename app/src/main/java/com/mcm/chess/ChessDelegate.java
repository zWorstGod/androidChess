package com.mcm.chess;

public interface ChessDelegate {
    ChessPiece pieceAt(Square square);
    void movePiece(Square from, Square to);
    void reset();
    boolean gameOver();
}