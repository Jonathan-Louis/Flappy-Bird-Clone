package com.jonathanlouis.flappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Pipe {
    int x, y;
    int width, height;
    Bitmap pipe;
    boolean top;
    private Random random;

    public Pipe(int screenX, int screenY, Resources resources, boolean top) {

        random = new Random();

        this.top = top;

        x = screenX;

        if(top) {
            pipe = BitmapFactory.decodeResource(resources, R.drawable.pipetop);
            height = (int) ((pipe.getHeight() * 2) * random.nextFloat());
            y = 0;
        } else {
            pipe = BitmapFactory.decodeResource(resources, R.drawable.pipebottom);
            height = (int) ((pipe.getHeight() * 2) * random.nextFloat());
            y = screenY - height;
        }

        width = pipe.getWidth();


        pipe = Bitmap.createScaledBitmap(pipe, width, height, false);
    }

    public void movePipe(){
        x -= 10 * GameView.screenRatioX;
    }
}
