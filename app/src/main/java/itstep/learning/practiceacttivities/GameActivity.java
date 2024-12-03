package itstep.learning.practiceacttivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import itstep.learning.android_first_project.OnSwipeListener;

public class GameActivity extends AppCompatActivity {
    private final int N = 4;

    private final String bestScore_filename = "best_score.2048";

    private int cells[][] = new int[N][N];

    private final TextView[][] tvCells = new TextView[N][N];

    private int score, bestScore;

    private final Random random = new Random();

    private Animation spawnAnimation, collapseAnimation, bellAnimation;

    private int[][] undo;

    private int prevScore;

    private TextView tvScore , tvBestScore;





    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        LinearLayout gameField = findViewById(R.id.app_game_fields);

        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.game_spawn);
        collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.game_collapse);
        tvScore = findViewById(R.id.app_game_score);
        tvBestScore = findViewById(R.id.app_game_best_result);


        findViewById(R.id.btn_new_game).setOnClickListener(v -> {
            initField();
            spawnCell();
            showField();
        });

        findViewById(R.id.btn_new_undo).setOnClickListener(v -> undoMove());
        //делаем поле квадратным ето можно сделать только здесь
        //так как до отрисовки мы не знаем ширину или длину и не можем дину =  ширине
        gameField.post(() -> {
            int vw = this.getWindow().getDecorView().getWidth();
            int fieldMargin = 20;


            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                    vw - 2 * fieldMargin,
                    vw - 2 * fieldMargin

            );

            ll.setMargins(fieldMargin, fieldMargin, fieldMargin, fieldMargin);
            ll.gravity = Gravity.CENTER;
            gameField.setLayoutParams(ll);

        });

        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this) {

            @Override
            public void onSwipeBottom() {
                if (canMoveBottom()) {
                    saveField();
                    moveDown();
                    spawnCell();
                    showField();
                }

            }

            @Override
            public void onSwipeLeft() {
                if (canMoveLeft()) {
                    saveField();
                    moveLeft();
                    spawnCell();
                    showField();
                }

            }

            @Override
            public void onSwipeRight() {

                if (canMoveRight()) {
                    saveField();
                    moveRight();
                    spawnCell();
                    showField();
                }

            }

            @Override
            public void onSwipeTop() {
                if (canMoveTop()) {
                    saveField();
                    moveUp();
                    spawnCell();
                    showField();
                }
            }


        });


        initField();
        spawnCell();
        showField();
    }


    public boolean canMoveRight() {

        for (int i = 0; i < N; i++) {
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j - 1] != 0 && (cells[i][j] == 0 || cells[i][j - 1] == cells[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canMoveLeft() {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                if (cells[i][j + 1] != 0 && (cells[i][j] == cells[i][j + 1] || cells[i][j] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canMoveBottom() {
        try {
            for (int j = 0; j < N; j++) {
                for (int i = N - 1; i > 0; i--) {
                    if (cells[i - 1][j] != 0 && (cells[i][j] == cells[i - 1][j] || cells[i][j] == 0)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("GameActivity::canMoveBottom" , ex.getMessage() != null ? ex.getMessage() : "Error reading file");

        }

        return false;
    }

    public boolean canMoveTop() {
        for (int j = 0; j < N; j++) {
            for (int i = 1; i < N; i++) {
                if (cells[i][j] != 0 && (cells[i][j] == cells[i - 1][j] || cells[i - 1][j] == 0)) {
                    return true;
                }

            }
        }
        return false;
    }

    public void moveRight() {
        for (int i = 0; i < N; i++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        wasShift = true;
                    }
                }
            } while (wasShift);

            //collapse
            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == cells[i][j - 1] && cells[i][j] != 0) {
                    score += cells[i][j];
                    cells[i][j] *= 2;
                    tvCells[i][j].setTag(collapseAnimation);

                    score += cells[i][j];

                    for (int k = j - 1; k > 0; k--) {
                        cells[i][k] = cells[i][k - 1];
                    }
                    cells[i][0] = 0;
                }
            }

        }
    }

    public void moveLeft() {
        for (int i = 0; i < N; i++) {
            boolean wasShift;
            do {//сдвиг
                wasShift = false;
                for (int j = 1; j < N; j++) {
                    if (cells[i][j - 1] == 0 && cells[i][j] != 0) {
                        cells[i][j - 1] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
                    }
                }
            } while (wasShift);

            for (int j = 0; j < N - 1; j++) {
                //слияние
                if (cells[i][j] == cells[i][j + 1] && cells[i][j + 1] != 0) {
                    score += cells[i][j];
                    cells[i][j] *= 2;
                    tvCells[i][j].setTag(collapseAnimation);
                    score += cells[i][j];
                    cells[i][j + 1] = 0;

                    for (int k = j + 1; k < N - 1; k++) {
                        cells[i][k] = cells[i][k + 1];
                    }
                    cells[i][N - 1] = 0;
                }
            }
            wasShift = false;
            do {
                wasShift = false;
                for (int j = 1; j < N; j++) {
                    if (cells[i][j - 1] == 0 && cells[i][j] != 0) {
                        cells[i][j - 1] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
                    }
                }
            } while (wasShift);

        }
    }

    public void moveUp() {
        for (int j = 0; j < N; j++) {
            boolean wasShift;
            do {
                //сдвиг всего наверх
                wasShift = false;
                for (int i = 1; i < N; i++) {
                    if (cells[i][j] != 0 && cells[i - 1][j] == 0) {
                        score += cells[i][j];
                        cells[i - 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
                    }

                }
            } while (wasShift);

            for (int i = 0; i < N - 1; i++) {
                //слияние
                if (cells[i][j] == cells[i + 1][j] && cells[i][j] != 0) {
                    score += cells[i][j];
                    cells[i][j] *= 2;
                    tvCells[i][j].setTag(collapseAnimation);
                    score += cells[i][j];
                    cells[i + 1][j] = 0;


                    //сдвиг вниз после слива
                    for (int k = i + 1; k < N - 1; k++) {
                        cells[k][j] = cells[k + 1][j];
                    }
                    cells[N - 1][j] = 0;
                }
            }
            // Еще один сдвиг после слияния, если нужно

            do {
                wasShift = false;
                for (int i = 1; i < N; i++) {
                    if (cells[i][j] != 0 && cells[i - 1][j] == 0) {
                        cells[i - 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
                    }
                }
            } while (wasShift);
        }
    }

    public void moveDown() {
        for (int j = 0; j < N; j++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int i = N - 1; i > 0; i--) {
                    if (cells[i][j] == 0 && cells[i - 1][j] != 0) {
                        score += cells[i][j];
                        cells[i][j] = cells[i - 1][j];
                        cells[i - 1][j] = 0;
                        wasShift = true;
                    }

                }
            } while (wasShift);

            for (int i = N - 1; i > 0; i--) {

                if (cells[i][j] == cells[i - 1][j] && cells[i - 1][j] != 0) {
                    cells[i][j] *= 2;
                    tvCells[i][j].setTag(collapseAnimation);
                    score += cells[i][j];
                    cells[i - 1][j] = 0;
                    for (int k = i - 1; k > 0; k--) {
                        cells[k][j] = cells[k - 1][j];
                    }
                    cells[0][j] = 0;
                }
            }
        }
    }

    public void initField() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cells[i][j] = 0;

                tvCells[i][j] = findViewById(
                        getResources().getIdentifier(
                                "game_cell_" + i + j,
                                "id",
                                getPackageName()
                        ));

            }
        }

        score = 0;
        loadBestScore();
    }



    public void showField() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j].setText(String.valueOf(cells[i][j]));

                tvCells[i][j].setBackgroundColor(getResources().getColor(
                        getResources().getIdentifier(
                                cells[i][j] <= 2024 ? "game_tile_" + cells[i][j] : "game_tile_other",
                                "color",
                                getPackageName()
                        ),
                        getTheme()
                ));


                tvCells[i][j].setTextColor(getResources().getColor(
                        getResources().getIdentifier(
                                cells[i][j] <= 2024 ? "game_text_" + cells[i][j] : "game_text_other",
                                "color",
                                getPackageName()

                        ),
                        getTheme()
                ));


                if (tvCells[i][j].getTag() instanceof Animation) {

                    tvCells[i][j].startAnimation((Animation) tvCells[i][j].getTag());
                    tvCells[i][j].setTag(null);
                }
            }
        }
        tvScore.setText(String.valueOf(score));
        if(bestScore < score){
            bestScore = score;
            saveBestScore();

        }
        tvBestScore.setText(String.valueOf(bestScore));    }


    private boolean spawnCell() {
        //собрать данные про пустые клетки
        // выбрать одну клетку случайно
        //поставить в неё 2 с вероятность 0.9 или 4 с вероятность 0.1

        List<Coordinates> freeCells = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    freeCells.add(new Coordinates(i, j));
                }
            }
        }
        if (freeCells.isEmpty()) return false;

        Coordinates randomCoordinates = freeCells.get(random.nextInt(freeCells.size()));

        cells[randomCoordinates.x][randomCoordinates.y] = random.nextInt(10) == 0 ? 4 : 2;

        tvCells[randomCoordinates.x][randomCoordinates.y].setTag(spawnAnimation);
        return true;
    }


    private void saveField() {
        prevScore = score;
        undo = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(cells[i], 0, undo[i], 0, N);
        }
    }


    private void saveBestScore(){
        try(
                FileOutputStream fos = openFileOutput(bestScore_filename , Context.MODE_PRIVATE);
                DataOutputStream writer = new DataOutputStream(fos);
                )
        {
            writer.writeInt(bestScore);
            writer.flush();
        }
        catch (Exception e){
         Log.e("GameActivity::saveBestScore" , e.getMessage() != null ? e.getMessage() : "Error writing file");
        }
    }
    public void loadBestScore() {
        try(FileInputStream fis = openFileInput(bestScore_filename)){
            DataInputStream reader = new DataInputStream( fis );
            bestScore = reader.readInt();
        }
        catch (IOException e){
            Log.e("GameActivity::loadBestScore" , e.getMessage() != null ? e.getMessage() : "Error reading file");
        }
    }

    private void undoMove() {
        if (undo == null) {
            showUndoMessage();
            return;
        }
        score = prevScore;
        for (int j = 0; j < N; j++) {
            System.arraycopy(undo[j], 0, cells[j], 0, N);
        }
        undo = null;
        showField();
    }


    private void showUndoMessage(){
        new AlertDialog
                .Builder(this , androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark)
                .setTitle("Ограничение")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Отмена ход невозможна")
                .setNeutralButton("Закрыть" , (dlg, btn) -> {})
                .setPositiveButton("Подписка" , (dlg, btn) -> {} )
                .setNegativeButton("Выйти" , (dlg, btn) -> finish() )
                .setCancelable( false )
                .show();
    }


    static class Coordinates {
        int x, y;

        public Coordinates(int x, int y) {
            this.y = y;
            this.x = x;
        }
    }


}