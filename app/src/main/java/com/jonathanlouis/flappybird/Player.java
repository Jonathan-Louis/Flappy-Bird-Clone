package com.jonathanlouis.flappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player {
    int x, y, screenY, wingCounter = 0;
    int width, height;
    Bitmap player1, player2;

    Player(int screenX, int screenY, Resources resources){
        player1 = BitmapFactory.decodeResource(resources, R.drawable.bird1);
        player2 = BitmapFactory.decodeResource(resources, R.drawable.bird2);

        width = player1.getWidth() / 10;
        height = player1.getHeight() / 10;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        player1 = Bitmap.createScaledBitmap(player1, width, height, false);
        player2 = Bitmap.createScaledBitmap(player2, width, height, false);

        y = screenY / 2;
        x =  screenX / 4;
        this.screenY = screenY;
    }

    public Bitmap getPlayer(){
        //switch between bird models to animate wings flapping
        if(wingCounter == 0){
            wingCounter++;
            return player1;
        }

        wingCounter--;
        return player2;
    }

    public void move(){
        y -= 130 * GameView.screenRatioY;

        if(y < 0){
            y = 0;
        } else if(y > screenY){
            y = screenY;
        }
    }

    public void downMovement(){
        y += 17 * GameView.screenRatioY;

        if(y < 0){
            y = 0;
        } else if(y > screenY){
            y = screenY - player1.getHeight();
        }
    }
}
