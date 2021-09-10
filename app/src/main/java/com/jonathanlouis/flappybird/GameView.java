package com.jonathanlouis.flappybird;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, gameOver = false;
    private Background background1, background2;
    int screenX, screenY;
    private int score = 0;
    private int time = 0, timeBetweenPipes = 50;
    Player player;
    List<Pipe> pipeList;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private GameActivity gameActivity;
    private SharedPreferences preferences;
    private Random random;

    public GameView(GameActivity gameActivity, int screenX, int screenY) {
        super(gameActivity);

        this.gameActivity = gameActivity;
        this.preferences = gameActivity.getSharedPreferences("game", Context.MODE_PRIVATE);

        pipeList = new ArrayList<>();

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.x = screenX;

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = ((float) background1.background.getWidth()) / screenX;
        screenRatioY = ((float) background1.background.getHeight()) / screenY;

        paint = new Paint();
        paint.setTextSize(200);

        player = new Player(screenX, screenY, getResources());

        random = new Random();
    }

    @Override
    public void run() {

        while(isPlaying){

            update();
            draw();
            sleep();

            if(gameOver){
                for(Pipe p : pipeList){
                    if(p.x < (screenX / 4)){
                        score++;
                    }
                }
                score /= 2;
                if(preferences.getInt("highScore", 0) < score){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("highScore", score);
                    editor.apply();
                }
                draw();
                break;
            }
        }

        try {
            Thread.sleep(3000);
            gameActivity.startActivity(new Intent(gameActivity, MainActivity.class));
            gameActivity.finish();
        } catch (InterruptedException e){
            e.printStackTrace();
        }


    }

    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void update(){

        //each update will move the background 10 pixels to the left
        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;


        //reset background once off the screen
        if(background1.x + background1.background.getWidth() < 0){
            background1.x = screenX;
        }
        if(background2.x + background2.background.getWidth() < 0){
            background2.x = screenX;
        }

        player.downMovement();
        if(checkGameOver(pipeList)){
            isPlaying = false;
            gameOver = true;
        }

        for(Pipe p : pipeList){
            p.movePipe();
        }

        if(time > timeBetweenPipes){
            time = 0;
            timeBetweenPipes = (int)(random.nextFloat() * 100) + 20;
            createPipe();
        }

        time++;
    }

    private void draw(){
        //check that surface view has been created
        if(getHolder().getSurface().isValid()){

            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            canvas.drawBitmap(player.getPlayer(), player.x, player.y, paint);

            for(Pipe p : pipeList){
                canvas.drawBitmap(p.pipe, p.x, p.y, paint);
            }

            if(gameOver){
                canvas.drawText("Score is: " + score + "!", screenX / 2, screenY / 2, paint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep(){
        try {
            Thread.sleep(15);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            player.move();
        }
        return true;
    }

    private boolean checkGameOver(List<Pipe> pipes) {
        for (Pipe p : pipes) {
            //if player is not at pipe continue to next
            if (Math.abs(p.x - player.x) > p.width) {
                continue;
            }

            //check if player hits a pipe
            if(p.top){
                if((p.y + p.height) > player.y){
//                    System.out.println("top true");
                    return true;
                }
            } else {
                if(p.y < (player.y + player.height)){
//                    System.out.println("bottom true");
                    return true;
                }
            }

        }

        return false;
    }

    private void createPipe(){
        pipeList.add(new Pipe(screenX, screenY, getResources(), false));
        pipeList.add(new Pipe(screenX, screenY, getResources(), true));
    }
}