package itstep.learning.android_first_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeListener implements View.OnTouchListener /*View.OnTouchListener - лбое взаимодействие с екраном*/{

    private final GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility") //так как свайп не предвещает клик , то ставим етот атриут
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public OnSwipeListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new SwipeGestureListener());
    }

    private final class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener /*наследуемся от жестового класса , здесь есть разные жесты - вверх . скролл , зажатие и т.д.*/ {

        private final static int minVelocity = 150; // минимальная скорость
        private final static int minDistance = 100;   //минимальное растояние
        private final static double minRation = 1.0 / 2.0;   //минимальное соотношение сторон координат , чтобы држать ршение про свайп.

        @Override /* (точка прикосновения . точка отпускания  ,  )*/
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {

            boolean isHandled = false;
            if(e1 == null) return false;
            float deltaX = e2.getX() - e1.getX();            //e1 - точка начала жеста ( х , у )
            float deltaY = e2.getY() - e1.getY();            //е2 - точка конца жеста ( х, у)
            // float velocityX скорость движения по х, float velocityY - скорость движения по У

            float distanceX = Math.abs(deltaX) ;
            float distanceY = Math.abs( deltaY);  //abs - без минуса
            if(distanceX  * minRation > distanceY && distanceX >= minDistance ){ //значит горизонтальный свайп
                //
                if(Math.abs(velocityX  )>= minVelocity){ //анализируем только скорость Х
                    if(deltaX > 0){
                        onSwipeRight();
                    }
                    else {
                        onSwipeLeft();
                    }
                    isHandled = true;
                }


            }
            else if(distanceY * minRation > distanceX && distanceY >= minDistance ) {  // вертикальный свайп
                if(Math.abs(velocityY  )>= minVelocity){ //анализируем только скорость Х
                    if(deltaX > 0){
                        onSwipeBottom();
                    }
                    else {
                        onSwipeTop();
                    }
                    isHandled = true;
                }
            }


            return isHandled;

        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true; //"True - значит что событие оработано нашим детектором , то есть лбое прикосновение к нашей панели приводит к
            //появлении события down ,а down мы будем обрабатывать сами .
        }




    }
    public void onSwipeBottom() { }
    public void onSwipeLeft()   { }
    public void onSwipeRight()  { }
    public void onSwipeTop()    { }

}


/*
 * Детектор жестов , свайпы
 *
 *GestureDetector- детектор жестов , нужен для распознавания жестов и запуск
 * обработчиков ( Listeners) в зависимости от определенного жеста\
 * Детектор наблдает за некоторым контекстом ( представленияем , область екрана)
 *
 *Определение свайпов базируется на анализе действия onFling  - жест , который состоит из
 * прикосновение  проведения , поднятия и отжимания екрана устройства а так же нивелирвоания
 * события onDown , которое может привести к синтезу "Tap" , "Click"
 *
 *
 * В основе определения свайпов - анализ скорости и растояния жестового проведения
 * а так же возможности отнесения его к одного из направлений ( в данном направлении их 4)
 *
 * */
