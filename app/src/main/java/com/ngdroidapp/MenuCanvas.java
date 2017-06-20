package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;

/**
 * Created by noyan on 27.06.2016.
 * Nitra Games Ltd.
 */

public class MenuCanvas extends BaseCanvas {

    private Bitmap background, buttons;
    private Rect backgroundsrc, backgrounddst, playsrc, playdst, exitsrc, exitdst;
    private GameCanvas gc;

    public MenuCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {

       // gc = new GameCanvas(root); //GameCanvas objesini oluşturduk

        background = Utils.loadImage(root, "images/bg.jpg");
        backgroundsrc = new Rect();
        backgrounddst = new Rect();

        buttons = Utils.loadImage(root, "images/buttons.png");
        playsrc = new Rect();
        playdst = new Rect();
        exitsrc = new Rect();
        exitdst = new Rect();

    }

    public void update() {
        backgroundsrc.set(0, 0, 1080, 1920);
        backgrounddst.set(0, 0, 1080, 1920);

        playsrc.set(0,0,256,256);
        playdst.set(getWidthHalf() - 192, getHeightHalf() -64, getWidthHalf() -64, getHeightHalf() +64);
        exitsrc.set(512,0,768,256);
        exitdst.set(getWidthHalf() +64, getHeightHalf()-64, getWidthHalf() +192, getHeightHalf() +64);

    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(background, backgroundsrc, backgrounddst,null);

        canvas.drawBitmap(buttons,playsrc,playdst,null);
        canvas.drawBitmap(buttons,exitsrc,exitdst,null);
    }

    public void keyPressed(int key) {
    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return false;
    }

    public void touchDown(int x, int y) {
    }

    public void touchMove(int x, int y) {
    }

    public void touchUp(int x, int y) {
        if(exitdst.contains(x, y)){
            System.exit(0);
        }
        if(playdst.contains(x, y)){
            gc = new GameCanvas(root); //GameCanvas objesini oluşturduk
            root.canvasManager.setCurrentCanvas(gc);
        }
    }

    public void surfaceChanged(int width, int height) {
        Log.i("MC", "surfaceChanged");
    }

    public void surfaceCreated() {
        Log.i("MC", "surfaceCreated");
    }

    public void surfaceDestroyed() {
        Log.i("MC", "surfaceDestroyed");
    }

    public void pause() {
        Log.i("MC", "pause");
    }

    public void resume() {
        Log.i("MC", "resume");
    }

    public void reloadTextures() {
        Log.i("MC", "reloadTextures");
    }

    public void showNotify() {
        Log.i("MC", "showNotify");
    }

    public void hideNotify() {
        Log.i("MC", "hideNotify");
    }

}
