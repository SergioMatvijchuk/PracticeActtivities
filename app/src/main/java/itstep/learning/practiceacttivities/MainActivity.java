package itstep.learning.practiceacttivities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    SecretTextView secretTextView;

    private Handler handler = new Handler();
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        //смена изображений на главном фоне
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //текст инициализация
        secretTextView = (SecretTextView)findViewById(R.id.textview);
        secretTextView.setDuration(3000);
        secretTextView.setIsVisible(true);

        //запуск анимации текста
        runnable = new Runnable() {
            @Override
            public void run() {
                secretTextView.toggle();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnable);


      findViewById(R.id.btn_view_start_game).setOnClickListener(this::onGameButtonClick);

    }

    private void onGameButtonClick(View view){

        Intent intent = new Intent(MainActivity.this , GameActivity.class);
        startActivity(intent);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем выполнение Runnable при уничтожении активности
        handler.removeCallbacks(runnable);
    }



}