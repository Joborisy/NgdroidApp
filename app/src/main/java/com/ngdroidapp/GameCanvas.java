package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {
        private Bitmap tileset, spritesheet;
        private Rect tilesrc, tiledst, spritesrc, spritedst;
        private int kareno, spritex, spritey, animasyonno, animasyonyonu, hizx, hizy, hiz; //Shift f6 refactor kısatuşu dokunmatiğe geldik(7)
        //interaktif değişkenler
        int touchx, touchy;

    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        tileset = Utils.loadImage(root,"images/tilea2.png");
        tilesrc=new Rect();
        tiledst=new Rect();

        spritesheet = Utils.loadImage(root, "images/cowboy.png");
        spritedst = new Rect();
        spritesrc = new Rect();

        kareno = 0;
        spritex = 0;
        spritey = 0;
        animasyonno = 1;
        animasyonyonu = 0;

        hizy = 16;
        hizx = 0;
        hiz = 16;
    }



    public void update() {



    }

    public void draw(Canvas canvas) {
        tilesrc.set(0, 0, 64, 64);

        for(int i=0; i < getWidth();i+=128){
            for(int j=0; j < getHeight(); j+=128){

                tiledst.set(i, j, i+128, j+128);
                canvas.drawBitmap(tileset, tilesrc, tiledst, null);

            }
        }
        spritex += hizx;
        spritey += hizy;

        if(spritex + 256 > getWidth()){
            spritex = getWidth() - 256; // karakterin ekran sınırında kalmasını sağladı (4)
            animasyonno = 0; //ekranın sonuna gelince animasyonnoyu 0'a eşitleyip karakterin hareketini başlangıç hareketine aldık, yani karakter durdu (5)
        }

        if(spritey + 256 > getHeight())
        {
            spritey = getHeight() - 256;
        }

        if( animasyonno == 1) {
            kareno++; //Kareyi arttırarak kovboyu yatay düzlemde yürütüyoruz (3)
        }


        else if(animasyonno == 0)
        {
            kareno = 0;
        }

        if(kareno > 8) {
            kareno = 1;
        }

        if(hizx > 0){animasyonyonu = 0;}

        else if(hizy > 0){animasyonyonu = 9;}

        spritesrc.set(kareno*128, animasyonyonu*128, (kareno+1)*128, (animasyonyonu+1)*128); //resimden kesip koordinatları aldık (1.1) yatay hareket için animasyon yönü ayarladık(6)
        spritedst.set(spritex, spritey, (spritex + 256), (spritey + 256)); //ekrana koordinatlardaki resmi çizdirdik (1.2) -> Olduğu yerde hareket etti Animasyon (2)

        canvas.drawBitmap(spritesheet, spritesrc, spritedst, null);
    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return true;
    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void touchDown(int x, int y) {
        touchx = x;
        touchy = y;
    }

    public void touchMove(int x, int y) {
    }

    public void touchUp(int x, int y) {
        if(x - touchx > 100) {
            hizx = hiz;
            hizy=0;
            animasyonno = 1;
            animasyonyonu = 0;
        }
        else if(touchx - x > 100) {
            hizx = -hiz;
            hizy=0;
            animasyonyonu = 1;
            animasyonno=1;
        }
        else if(y-touchy > 100){
            hizy = hiz;
            animasyonyonu = 9;
            animasyonno=1;
        }
        else if(touchy - y > 100) {
            hizy = -hiz;
            animasyonyonu = 5;
            animasyonno=1;
        }
        else {
            hizx = 0;
            hizy = 0;
            animasyonno = 0;
        }
    }


    public void pause() {

    }


    public void resume() {

    }


    public void reloadTextures() {

    }


    public void showNotify() {
    }

    public void hideNotify() {
    }

}
