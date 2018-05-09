package com.labralab.approximator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

/**
 * Класс для отрисовки содержимого экрана
 */

public class DrawView extends View {

    private Display display;
    private  int displayHeight;
    private  int displayWidth;
    private int x0;
    private int y0;

    private int horizontalStep;
    private int verticalStep;

    boolean isDataChanged = false;

    double[][] data;

    Paint point;
    Paint line;
    Paint helpLine;
    Paint pointsOnLine;
    Paint approxLine;
    Paint interpolLine;


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        getDisplayParams(getContext(), canvas);
        setMainContent(canvas);

        if(!isDataChanged){
            setBasicData();
        }

        drawPoints(canvas);
        drawLine(canvas);

    }


    //Отрисовка всех стандартных компонентов
    private void setMainContent (Canvas canvas){

        line = new Paint();
        pointsOnLine = new Paint();
        helpLine = new Paint();

        //Цвет фона
        canvas.drawColor(Color.WHITE);

        //Параметры линий осей координат
        line.setColor(Color.BLACK);
        line.setStrokeWidth(5);

        //Параметры точек на осях
        pointsOnLine.setColor(Color.BLACK);
        pointsOnLine.setStrokeWidth(20);

        //Вспомогательные линии
        helpLine.setColor(Color.GRAY);
        helpLine.setStrokeWidth(2);

        canvas.drawLine(0, 0, 0, y0 - 20, line);
        canvas.drawLine(0, 0, x0, 0, line);

        //Отрисовка вертикальных вспомагательных линий
        int step = horizontalStep;
        canvas.drawLine(step, 0, step, y0 - 20, helpLine);
        for (int i = 2; i < 6; i++){
            canvas.drawLine(step * i, 0, step * i, y0 - 20 , helpLine);
        }

        //Отрисовка горизонтальных вспомагательных линий
        int step2 = verticalStep;
        canvas.drawLine(0, step2, x0, step2, helpLine);
        for (int i = 2; i < 4; i++){
            canvas.drawLine(0, (step2 * i), x0, (step2 * i), helpLine);
        }



    }

    //Рисуем или переписовываем точки
    private void drawPoints(Canvas canvas){

        point = new Paint();

        point.setColor(Color.RED);
        point.setStrokeWidth(20);

        for(int i = 0; i < 5; i++){
            canvas.drawPoint((float)data[i][0], (float)data[i][1], point);
        }

    }


    //Считываем параметры дисплея устройства для коректного отображения
    private void getDisplayParams(Context context, Canvas canvas){

        MainActivity mainActivity = (MainActivity) context;
        display = mainActivity.getWindowManager().getDefaultDisplay();
        displayHeight = display.getHeight();
        displayWidth = display.getWidth();

        y0 = displayHeight - 100;
        x0 = displayWidth - 100;

        horizontalStep = displayWidth / 6;
        verticalStep = displayHeight / 4;

        canvas.translate(50, displayHeight - 100);
        canvas.scale(1,-1);

    }

    private void setBasicData(){


        //Задаем точкам положение по умолчанию
        data = new double[5][2];

        int centerLine = displayHeight / 2;
        int step = horizontalStep;

        data[0][0] = step;
        data[0][1] = centerLine;

        for (int i = 2; i <= data.length; i++){
            data[i - 1][0] = step * i;
        }

        data[0][1] = 240;
        data[1][1] = 150;
        data[2][1] = 400;
        data[3][1] = 350;
        data[4][1] = 300;

    }

    void changeData(int x, int y){

        isDataChanged = true;

        //Определяем к каой из точек обращается пользыватель
        if(x <= horizontalStep * 1.5){
            data[0][1] = displayHeight - y - 120;
        }else if(x <= horizontalStep * 2.5){
            data[1][1] = displayHeight - y - 120;
        }else if(x <= horizontalStep * 3.5){
            data[2][1] = displayHeight - y - 120;
        }else if(x <= horizontalStep * 4.5){
            data[3][1] = displayHeight - y - 120;
        }else if(x <= horizontalStep * 5.5){
            data[4][1] = displayHeight - y - 120;
        }else if(x <= displayWidth){
            data[5][1] = displayHeight - y - 120;
        }

        invalidate();

    }

    private void drawLine(Canvas canvas) {

        //Параметры апроксиммированной кривой
        approxLine = new Paint();
        approxLine.setColor(getResources().getColor(R.color.colorApprox));
        approxLine.setStrokeWidth(6);
        approxLine.setStyle(Paint.Style.STROKE);

        //Параметры интерполированной кривой
        interpolLine = new Paint();
        interpolLine.setColor(getResources().getColor(R.color.colorIntrpol));
        interpolLine.setStrokeWidth(6);
        interpolLine.setStyle(Paint.Style.STROKE);

        double[][] r1 = new double[1500][2];

        //Постоение интерполированной кривой
        Interpolation program = new Interpolation();
        double[][] r = program.getLine(data);

        for (int i = 0; i < r.length; i++) {
            canvas.drawPoint((float) r[i][0], (float) r[i][1], interpolLine);
        }
        //*** ПРИМЕЧАНИЕ: все кривые строю путем наненсения большого количества точек на canvas,
        //*** потому что, посторение отрезками в таких условия берет очнь много оперативной памяти.


        //позучаем значения коэввициентов полинома третьей степени
        double[] resultCoeff = Approx.calcCoefficients(data, 3);

        //Наполнение списка парамтров для построения апроксимированной кривой
        for (int i = 0; i < 1400; i++) {

            double x = i;
            double y = resultCoeff[0] + resultCoeff[1] * x
                    + resultCoeff[2] * Math.pow(x, 2) + resultCoeff[3] * Math.pow(x, 3);

            r1[i][1] = y;
            r1[i][0] = x;

        }

        //Построение апроксимированной кривой
        for (int i = 0; i < 1400; i++) {
            canvas.drawPoint((float) r1[i][0], (float) r1[i][1], approxLine);
        }
    }
}
