package com.android.sro_43dopzadanya;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton[] cells = new ImageButton[9];
    private char[] board = {'_', '_', '_', '_', '_', '_', '_', '_', '_'};

    private char currentPlayer = 'X';
    private boolean gameOver = false;

    private Button btnReset;

    private static final String PREF_BOARD = "BOARD_STATE";
    private static final String PREF_PLAYER = "CURRENT_PLAYER";
    private static final String PREF_GAME_OVER = "GAME_OVER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cells[0] = findViewById(R.id.cell0);
        cells[1] = findViewById(R.id.cell1);
        cells[2] = findViewById(R.id.cell2);
        cells[3] = findViewById(R.id.cell3);
        cells[4] = findViewById(R.id.cell4);
        cells[5] = findViewById(R.id.cell5);
        cells[6] = findViewById(R.id.cell6);
        cells[7] = findViewById(R.id.cell7);
        cells[8] = findViewById(R.id.cell8);

        btnReset = findViewById(R.id.btnReset);

        for (int i = 0; i < 9; i++) {
            cells[i].setOnClickListener(this);
            cells[i].setTag(i);
        }

        btnReset.setOnClickListener(v -> resetGame());

        loadGame();
        updateUI();
    }

    @Override
    public void onClick(View v) {
        if (gameOver) return;

        int index = (int) v.getTag();

        if (board[index] != '_') return;

        board[index] = currentPlayer;
        updateCell(index);

        if (checkWinner(currentPlayer)) {
            gameOver = true;
            Toast.makeText(this, "Победил: " + currentPlayer, Toast.LENGTH_SHORT).show();
        } else if (isBoardFull()) {
            gameOver = true;
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
        } else {
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }

        saveGame();
    }

    private void updateUI() {
        for (int i = 0; i < 9; i++) {
            updateCell(i);
        }
    }

    private void updateCell(int index) {
        if (board[index] == 'X') {
            cells[index].setImageResource(R.drawable.ic_x);
        } else if (board[index] == 'O') {
            cells[index].setImageResource(R.drawable.ic_o);
        } else {
            cells[index].setImageResource(R.drawable.ic_empty);
        }
    }

    private boolean checkWinner(char player) {
        int[][] winPositions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] pos : winPositions) {
            if (board[pos[0]] == player &&
                    board[pos[1]] == player &&
                    board[pos[2]] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '_') return false;
        }
        return true;
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = '_';
        }
        currentPlayer = 'X';
        gameOver = false;
        updateUI();
        saveGame();
    }

    private void saveGame() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREF_BOARD, new String(board));
        editor.putString(PREF_PLAYER, String.valueOf(currentPlayer));
        editor.putBoolean(PREF_GAME_OVER, gameOver);

        editor.apply();
    }

    private void loadGame() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        String savedBoard = prefs.getString(PREF_BOARD, "_________");
        String savedPlayer = prefs.getString(PREF_PLAYER, "X");
        gameOver = prefs.getBoolean(PREF_GAME_OVER, false);

        if (savedBoard.length() == 9) {
            board = savedBoard.toCharArray();
        }

        currentPlayer = savedPlayer.charAt(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveGame();
    }
}