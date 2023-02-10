package com.mcm.chess;

import androidx.annotation.NonNull;

public class ChessPiece {
    int col, row;
    ChessPlayer player;
    ChessRank rank;
    int resID;
    public ChessPiece(int col, int row, ChessPlayer player, ChessRank rank, int resID){
        this.col = col;
        this.row = row;
        this.player = player;
        this.rank = rank;
        this.resID = resID;
    }
}