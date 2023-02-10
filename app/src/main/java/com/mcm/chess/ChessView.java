package com.mcm.chess;

import static java.lang.Float.min;
import static java.lang.Integer.min;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;

@SuppressLint("SetTextI18n")
public class ChessView extends View {
    public final String TAG = "MyActivity";
    private final float scaleFactor = 1.0f;
    private float originX = 0f;
    private float originY = 0f;
    private float cellSide = 5;
    private final int darkColor = Color.parseColor("#EEEEEE");
    private final int lightColor = Color.parseColor("#BBBBBB");
    private final HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint paint = new Paint();
    private final float width = Resources.getSystem().getDisplayMetrics().widthPixels;
    private final float height = Resources.getSystem().getDisplayMetrics().heightPixels;
    ChessDelegate chessDelegate;
    private final Set<Integer> imgResIDs = Set.of(
            R.drawable.bishop_black,
            R.drawable.bishop_white,
            R.drawable.queen_black,
            R.drawable.queen_white,
            R.drawable.rook_black,
            R.drawable.rook_white,
            R.drawable.knight_black,
            R.drawable.knight_white,
            R.drawable.king_black,
            R.drawable.king_white,
            R.drawable.pawn_black,
            R.drawable.pawn_white
            );

    private int fromCol = -1;
    private int fromRow = -1;
    private float movingPieceX = -1f;
    private float movingPieceY = -1f;
    private ChessPiece movingPiece = null;
    private Bitmap movingPieceBitmap = null;
    TextView text = new TextView(this.getContext());

    // Constructor
    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Init
    {
        loadBitmaps();
    }

    // Main
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @Override @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas){
        if (canvas == null) return;
        float chessBoardSide = min(width, height) * scaleFactor;
        cellSide = chessBoardSide / 8f;
        originX = (width - chessBoardSide) / 2f;
        originY = (height - chessBoardSide) / 2f - 980;

        //chessDelegate.reset();

        drawChessboard(canvas);
        drawPieces(canvas);

        if (chessDelegate.gameOver()) {
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            text.setText("GAME OVER!");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event == null) return false;

        ChessModel chessModel = new ChessModel();
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            fromCol = (int) ((event.getX() - originX) / cellSide);
            fromRow = 8 - (int) ((event.getY() - originY) / cellSide);
            System.out.println("FROM: "+fromCol +","+ fromRow);

            ChessPiece piece = chessDelegate.pieceAt(new Square(fromCol, fromRow));
            if (piece != null) {
                movingPiece = piece;
                movingPieceBitmap = bitmaps.get(piece.resID);
            }
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            movingPieceX = event.getX();
            movingPieceY = event.getY();
            invalidate();
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                int col = (int)((event.getX() - originX) / cellSide);
                int row = 8 - (int)((event.getY() - originY) / cellSide);

                System.out.println("TO:"+col + ","+ row);
                if (fromCol != col || fromRow != row) {
                    System.out.println("Tryna move!");
                    chessDelegate.movePiece(new Square(fromCol, fromRow), new Square(col, row));
                }
                movingPiece = null;
                movingPieceBitmap = null;
                invalidate();
        }
        return true;
    }

    private void drawPieces(Canvas canvas){
        for (int row = 0; row <= 7; row++){
            for (int col = 0; col <= 7; col++) {
                ChessPiece piece = chessDelegate.pieceAt(new Square(col, row));
                if (piece != null)
                    drawPieceAt(canvas, col, row, piece.resID);
            }
        }

        if (movingPieceBitmap != null) canvas.drawBitmap(movingPieceBitmap, null, new RectF(movingPieceX - cellSide/2, movingPieceY - cellSide/2,movingPieceX + cellSide/2,movingPieceY + cellSide/2), paint);
    }
    private void drawPieceAt(Canvas canvas, int col, int row, int resID){
        canvas.drawBitmap(bitmaps.get(resID), null, new RectF(originX + col * cellSide,originY + (8 - row) * cellSide,originX + (col + 1) * cellSide,originY + ((8 - row) + 1) * cellSide), paint);
    }

    private void loadBitmaps(){
        Integer[] arr = imgResIDs.toArray(new Integer[0]);
        for (int it = 0; it < imgResIDs.size(); it++){
            bitmaps.put(arr[it], BitmapFactory.decodeResource(getResources(), arr[it]));
        }
    }

    private void drawChessboard(Canvas canvas){
        for (int row = 1; row < 9; row++) {
            for (int col = 0; col < 9; col++){
                drawSquareAt(canvas, col, row, (col + row) % 2 == 1);
            }
        }
    }
    private void drawSquareAt(Canvas canvas, int col, int row, boolean isDark) {
        if (isDark) paint.setColor(darkColor);
        else paint.setColor(lightColor);

        canvas.drawRect(originX + col * cellSide,
                        originY + row * cellSide,
                        originX + (col + 1)* cellSide,
                        originY + (row + 1) * cellSide,
                        paint);
    }
}