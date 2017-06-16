package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;
import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.core.NgMediaPlayer;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {
    //global değişkenler
    private Bitmap tileset, spritesheet, bullet, enemy, explode;
    private Rect tilesrc, tiledst, spritesrc, spritedst, bulletsrc, enemysrc, enemydst, explodesrc, explodedst;

    private int kareno, animasyonno, animasyonyonu, bulletoffsetx_temp, bulletoffsety_temp, explodeframeno;

    private int hiz, hizx, hizy, spritex, spritey,  bulletspeedy, bulletspeed, enemyspeedx, enemyspeedy, enemyx, enemyy;
    private int bulletx_temp, bullety_temp; //ekrana basılacak merminin koordinatları

    private int sesefekti_patlama;
    private NgMediaPlayer arkaplan_muzik;

    private Random enemyrnd;

    private int donmenoktasi;
    private boolean enemyexist, exploded, donmeboolean;

    public Vector <Rect> bulletdst; //jenerik yapı ->
    public Vector <Integer>  bulletx2, bullety2, bulletoffsetx2, bulletoffsety2, bulletspeedx2, bulletspeedy2;

    int touchx, touchy;//Ekranda bastigimiz yerlerin koordinatlari

    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }


    public void setup() {
        try {
            sesefekti_patlama = root.soundManager.load("sounds/se2.wav");
        } catch (Exception e){
            e.printStackTrace(); //Hataya sebep olan şeyleri yazdırmak
        } //->Oluşabilecek tüm hataları düzeltmek

        //Arkaplan muzigi kısmı
        arkaplan_muzik = new NgMediaPlayer(root);
        arkaplan_muzik.load("sounds/m2.mp3");
        arkaplan_muzik.setVolume(0.5f);
        arkaplan_muzik.prepare();
        arkaplan_muzik.start();

        //Log.i(TAG, "setup");
        tileset = Utils.loadImage(root,"images/tilea2.png");
        tilesrc = new Rect();
        tiledst = new Rect();

        // region enemy
        enemy = Utils.loadImage(root, "images/mainship03.png");
        enemysrc = new Rect();
        enemydst = new Rect();
        enemyexist = true;
        enemyspeedx = 10;
        enemyspeedy = 0;
        enemyx = getWidthHalf() - 128;
        enemyy = getHeight() - 256;
        //endregion

        donmenoktasi = getWidth();
        donmeboolean = true;
        enemyrnd = new Random();

        //region explode
        explode = Utils.loadImage(root, "images/exp2_0.png");
        explodesrc = new Rect();
        explodedst = new Rect();
        explodeframeno = 0;
        exploded = false;
        //endregion

        //region spritesheet
        spritesheet = Utils.loadImage(root,"images/cowboy.png");
        spritesrc = new Rect();
        spritedst = new Rect();

        bullet = Utils.loadImage(root,"images/bullet.png");
        bulletsrc = new Rect();
        //endregion ben koydum düzelticem

        kareno=0;

        animasyonno = 1;

        animasyonyonu = 0;

        hiz = 16;
        hizx = 0;
        hizy = 0;
        spritex = 0; //->Karakterin x düzlemindeki büyüklüğü
        spritey = 0; //->Karakterin y düzlemindeki büyüklüğü

        bulletspeed = 0;

        bulletspeedy = 0;

        bulletoffsetx_temp = 256;
        bulletoffsety_temp = 128;

        bulletx_temp = 0;
        bullety_temp = 0;

        bulletdst = new Vector<>();
        bulletx2 = new Vector<>();
        bullety2 = new Vector<>();
        bulletspeedx2 = new Vector<>();
        bulletspeedy2 = new Vector<>();
        bulletoffsetx2 = new Vector<>();
        bulletoffsety2 = new Vector<>();
    }

        public void update() {
            //Log.i(TAG, "mehmet agca");

            tilesrc.set(0,0,64,64);

            //enemy hareketi
            if(donmeboolean){
                if(enemyspeedx > 0)
                {
                    donmenoktasi = enemyrnd.nextInt(getWidth() - 256 - (enemyx + 50)) + enemyx;
                }
                else if(enemyspeedx < 0)
                {
                    donmenoktasi = enemyrnd.nextInt(enemyx);
                }
                donmeboolean = false;
            }
            //enemy dönme noktasına geldi mi kontrolü
            if(enemyspeedx > 0 && enemyx > donmenoktasi){
                donmeboolean = true;
                enemyspeedx = -enemyspeedx;
            }
            else if(enemyspeedx < 0 && enemyx < donmenoktasi)
            {
                donmeboolean = true;
                enemyspeedx = -enemyspeedx;
            }

            //enemysrc dst leri set etme aşaması 16.06.2017 part 1
            if(enemyexist)
            {
                enemysrc.set(0, 0, 64, 64);
                //enemydst.set(getWidthHalf()- 128, getHeight()-256, getWidthHalf() + 128, getHeight());
                enemydst.set(enemyx, enemyy, enemyx+256, enemyy+256);
            }

            for(int i = 0; i < bulletdst.size(); i++)
            {
                //merminin rect ile uzay gemisinin rect inin kesisip kesismedigini kontrol ediyoruz 16.06.2017 part 2
                if(enemydst.intersect(bulletdst.elementAt(i))) //intersect fonksiyonu kesişip kesişmediğini kontrol eder. Keserse 1 döndürür.
                {
                    explodedst.set(bulletx2.elementAt(i) - 64, bullety2.elementAt(i) - 64, bulletx2.elementAt(i) + 64, bullety2.elementAt(i) + 64);
                    bulletx2.removeElementAt(i);
                    bullety2.removeElementAt(i);
                    bulletdst.removeElementAt(i);
                    bulletspeedx2.removeElementAt(i);
                    bulletspeedy2.removeElementAt(i);
                    enemydst.set(0, 0, 0, 0);
                    enemyexist = false;
                    exploded = true;
                    root.soundManager.play(sesefekti_patlama); //mermi değdiği anda BOM
                }

            }
            if(exploded) {
                explodesrc = getexplodeframe(explodeframeno);
                explodeframeno+=2;
            }
            if(explodeframeno > 15) {
                explodeframeno = 0;
                exploded = false;
            }

            spritex += hizx;
            spritey += hizy;
            enemyx += enemyspeedx;
            enemyy += enemyspeedy;

            if(enemyx + 256 > getWidth() || enemyx < 0)
            {
                enemyspeedx = -enemyspeedx;
            }
            //y değişmeyeceği için aynı şeyi y için yazmadık

            for(int i=0; i < bulletx2.size(); i++)
            {
                bulletx2.set(i, bulletx2.elementAt(i) + bulletspeedx2.elementAt(i));//icindeki elemani degistirmeye calisiyoruz
                bullety2.set(i, bullety2.elementAt(i) + bulletspeedy2.elementAt(i));
                if(bulletx2.elementAt(i) > getWidth() || bulletx2.elementAt(i) < 0 || bullety2.elementAt(i) > getHeight() || bullety2.elementAt(i) < 0){
                    bulletx2.removeElementAt(i);
                    bullety2.removeElementAt(i);
                    bulletdst.removeElementAt(i);
                    bulletspeedx2.removeElementAt(i);
                    bulletspeedy2.removeElementAt(i);
                }
                Log.i("Control", String.valueOf(bulletx2.size())); //mermi silindi mi kontrolü?
            }


            //bulletx_temp += bulletspeedx;
            //bullety_temp += bulletspeedy;

            if(spritex+256 > getWidth() || spritex < 0) {//x ekseni icin sona geldimi kontrolu
                hizx = 0;//spritex = getWidth() - 256;
                //animasyonno = 0;  //sona gelince durma animasyonu
            }

            if(spritey+256 > getHeight() || spritey < 0){//y ekseni icin sona geldimi kontrolu
                hizy = 0;//spritey = getHeight() -256;
                //animasyonno = 0;  //sona gelince durma animasyonu
            }

            if(animasyonno == 1)
                kareno++;
            else if(animasyonno == 0)
                kareno = 0;

            if(kareno > 8)
                kareno=1;

            if(hizx > 0)
                animasyonyonu = 0;
            else if(hizy > 0)
                animasyonyonu = 9;

            if(Math.abs(hizx) > 0 || Math.abs(hizy) > 0)
                animasyonno = 1;
            else
                animasyonno = 0;

            spritesrc.set(kareno*128, animasyonyonu*128,(kareno+1)*128, (animasyonyonu+1)*128);//Resimden aldigimiz koordinatlar
            spritedst.set(spritex, spritey, spritex+256, spritey+256);//Ekrana cizilecegi koordinatlar

            bulletsrc.set(0,0,70,70);
            //bulletdst.set(bulletx_temp, bullety_temp, bulletx_temp + 32, bullety_temp + 32);

            for(int i=0; i < bulletx2.size(); i++)
            {
                bulletdst.elementAt(i).set(bulletx2.elementAt(i), bullety2.elementAt(i), bulletx2.elementAt(i) + 32, bullety2.elementAt(i) + 32);
            }


        }



    public void draw(Canvas canvas) {
        //Log.i(TAG, "draw");
       /* for(int i=0; i < bulletx2.size(); i++)
        {
            bulletx2.set(i, bulletx2.elementAt(i) + bulletspeedx2.elementAt(i));//icindeki elemani degistirmeye calisiyoruz
            bullety2.set(i, bullety2.elementAt(i) + bulletspeedy2.elementAt(i));
            if(bulletx2.elementAt(i) > getWidth() || bulletx2.elementAt(i) < 0 || bullety2.elementAt(i) > getHeight() || bullety2.elementAt(i) < 0){

            }
        }*/



        for (int i=0; i<getWidth(); i+=128)
        {
            for(int j=0; j<getHeight(); j+=128)
            {
                tiledst.set(i,j,i+128,j+128);
                canvas.drawBitmap(tileset,tilesrc,tiledst,null);//yesil cimen zemini tum ekrana cizme
            }
        }
        canvas.drawBitmap(spritesheet,spritesrc,spritedst,null);

        for(int i = 0; i< bulletdst.size(); i++) {
            canvas.drawBitmap(bullet, bulletsrc, bulletdst.elementAt(i), null);
        }
        if(enemyexist)
        {
            canvas.drawBitmap(enemy, enemysrc, enemydst, null);
        }

        if(exploded)
        {
            canvas.drawBitmap(explode, explodesrc, explodedst, null); //Patlama efekti
        }
    }


    public Rect getexplodeframe(int frameno){  //Patlama animasyon efekti için fonksiyon tanımladık 16.06.2017 Part 3
        frameno = 15 - frameno;
        Rect temp = new Rect();
        temp.set((frameno%4)*64, (frameno/4)*64, ((frameno%4)+1)*64, ((frameno/4)+1)*64);
        return temp;
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
        if((x - touchx) > 100)//saga cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 0;

            hizx = hiz;
            hizy = 0;
        }
        else if((touchx - x) > 100)//sola cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 1;

            hizx = -hiz;
            hizy = 0;
        }
        else if((y - touchy) > 100)//asagi cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 9;

            hizy = hiz;
            hizx = 0;
        }
        else if((touchy - y) > 100)//yukari cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 5;

            hizy = -hiz;
            hizx = 0;
        }


        else//mouse ile 100px den az bir degisim yaptiysak
        {
            animasyonno = 0;

            hizx = 0;
            hizy = 0;

            bulletspeed = 32;

            if(animasyonyonu == 0)
            {

                bulletspeedx2.add(bulletspeed);
                bulletspeedy2.add(0);

                bulletoffsetx_temp = 256;
                bulletoffsety_temp = 128;
            }
            else if(animasyonyonu == 1)
            {
                bulletspeedx2.add(-bulletspeed);
                bulletspeedy2.add(0);

                bulletoffsetx_temp = 0;
                bulletoffsety_temp = 128;
            }
            else if(animasyonyonu == 9)
            {
                bulletspeedy2.add(bulletspeed);
                bulletspeedx2.add(0);

                bulletoffsetx_temp = 128;
                bulletoffsety_temp = 256;
            }
            else if(animasyonyonu == 5)
            {
                bulletspeedy2.add(-bulletspeed);
                bulletspeedx2.add(0);

                bulletoffsetx_temp = 128;
                bulletoffsety_temp = 0;
            }
            bulletx2.add(spritex+ bulletoffsetx_temp);
            bullety2.add(spritey+ bulletoffsety_temp);
            bulletx_temp = spritex + bulletoffsetx_temp;
            bullety_temp = spritey + bulletoffsety_temp;
            bulletdst.add(new Rect(bulletx_temp, bullety_temp, bulletx_temp + 32, bullety_temp + 32)); //diziye atadık her mermi bilgisini (mermi bilgisini en son ayarlarız)
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
