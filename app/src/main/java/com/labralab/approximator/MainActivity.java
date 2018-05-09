package com.labralab.approximator;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, R.string.hint, Toast.LENGTH_SHORT).show();


        drawView = (DrawView) findViewById(R.id.area);


        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //В случае отпускания
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int x = Math.round(event.getX());
                    int y = Math.round(event.getY());

                    drawView.changeData(x, y);
                }

                return true;
            }
        });
    }
}
