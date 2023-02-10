package com.mcm.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ChessDelegate {
    public final String TAG = "MyActivity";
    private static final ChessModel chessModel = new ChessModel();
    private ChessView chessView;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chessView = findViewById(R.id.chess_view);

        Log.d(TAG,chessModel.toString());
        resetButton = findViewById(R.id.resetButton);

        chessView.chessDelegate = this;

        resetButton.setOnClickListener(view -> {
            chessModel.reset();
            this.chessView.text.setText("");
            chessView.invalidate();
        });
    }

    @Override
    public ChessPiece pieceAt(Square square) {
        return ChessModel.pieceAt(square);
    }

    @Override
    public void movePiece(Square from, Square to) {
        chessModel.movePiece(from, to);
        chessView.invalidate();
    }

    @Override
    public void reset(){
        chessModel.reset();
        chessView.invalidate();
    }

    @Override
    public boolean gameOver(){
        return chessModel.gameOver();
    }
}