package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;
import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.core.AppManager;
import istanbul.gamelab.ngdroid.core.NgMediaPlayer;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {
    //global değişkenler
    private Bitmap tileset, spritesheet, bullet, enemy, explode, laser, buttons;
    private Rect lasersrc, laserdst1, laserdst2, restartsrc, exitsrc, restartdst, exitdst;
    private Rect tilesrc, tiledst, spritesrc, spritedst, bulletsrc, enemysrc, enemydst, explodesrc, explodedst;

    private int kareno, animasyonno, animasyonyonu, bulletoffsetx_temp, bulletoffsety_temp, explodeframeno;

    private int hiz, hizx, hizy, spritex, spritey,  bulletspeedy, bulletspeed, enemyspeedx, enemyspeedy, enemyx, enemyy, laserspeed, lasery, laserx1, laserx2;
    private int bulletx_temp, bullety_temp; //ekrana basılacak merminin koordinatları

    private int sesefekti_patlama;
    private NgMediaPlayer arkaplan_muzik;

    private Random enemyrnd;

    private int donmenoktasi;
    private boolean enemyexist, exploded, donmeboolean, spriteexist, guishow;

    public Vector <Rect> bulletdst; //jenerik yapı ->
    public Vector <Integer>  bulletx2, bullety2, bulletoffsetx2, bulletoffsety2, bulletspeedx2, bulletspeedy2;

    int touchx, touchy;//Ekranda bastigimiz yerlerin koordinatlari

    private Paint textcolor;
    private int textsize;
    private String text;
    private Rect textdst;

    /* öldükten sonraki menü
    private Bitmap menu;
    private Rect menusrc, menudst;
    private boolean menu_touch; */
    private long prevtime, time;


    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }


    public void setup() {

        //region text GAME OVER
        textcolor = new Paint();
        textcolor.setARGB(255, 255, 0, 0);
        textsize = 64;
        textcolor.setTextSize(textsize);
        text = "GAME OVER";
        textdst = new Rect();
        //textcolor.getTextBounds(text, 0, text.length(), textdst);
        textcolor.setTextAlign(Paint.Align.CENTER);
        //endregion

        //region buttons
        restartsrc = new Rect();
        restartdst = new Rect();

        exitsrc = new Rect();
        exitdst = new Rect();
        buttons = Utils.loadImage(root, "images/buttons.png");
        guishow = false;

        //endregion

        //region enemy laser
        prevtime = System.currentTimeMillis();

        laser = Utils.loadImage(root, "images/beams1.png");
        laserdst1 = new Rect(-100, -100, -100, -100);
        laserdst2 = new Rect(-50, -50, -50, -50);
        lasersrc = new Rect();
        laserspeed = 64;
        lasery = -400;
        //endregion

        //region arkaplan music
        try {
            sesefekti_patlama = root.soundManager.load("sounds/se2.wav");
        } catch (Exception e){
            e.printStackTrace(); //Hataya sebep olan şeyleri yazdırmak
        } //->Oluşabilecek tüm hataları düzeltmek

        //Arkaplan muzigi
        arkaplan_muzik = new NgMediaPlayer(root);
        arkaplan_muzik.load("sounds/m2.mp3");
        arkaplan_muzik.setVolume(0.5f);
        arkaplan_muzik.prepare();
        arkaplan_muzik.start();
        //endregion

        //region tileset
        //Log.i(TAG, "setup");
        tileset = Utils.loadImage(root,"images/tilea2.png");
        tilesrc = new Rect();
        tiledst = new Rect();
        //endregion

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

        //region enemy hareketi
        donmenoktasi = getWidth();
        donmeboolean = true;
        enemyrnd = new Random();
        //endregion

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

        spriteexist = true;

        kareno=0;

        animasyonno = 1;

        animasyonyonu = 0;

        hiz = 16;
        hizx = 0;
        hizy = 0;
        spritex = 0; //->Karakterin x düzlemindeki büyüklüğü
        spritey = 0; //->Karakterin y düzlemindeki büyüklüğü
        //endregion

        //region bullet
        bullet = Utils.loadImage(root,"images/bullet.png");
        bulletsrc = new Rect();

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
        //endregion
    }

        public void update() {
            //Log.i(TAG, "mehmet agca");
            //region playbutton

                tilesrc.set(0,0,64,64);

            //endregion

            //region buttons
            restartsrc.set(256, 0, 512, 256);
            exitsrc.set(512,0,768,256);
            restartdst.set(getWidthHalf() -192, getHeightHalf() - 64, getWidthHalf() - 64, getHeightHalf() +64);
            exitdst.set(getWidthHalf() +64, getHeightHalf() - 64, getWidthHalf() +192, getHeightHalf() +64);
            //endregion

            //region laserler hızları

            lasery -= laserspeed;
            lasersrc.set(0, 0, 64, 128);
            time = System.currentTimeMillis();
            if(time > prevtime + 1000 && enemyexist) //ilk ateş noktası
            {
                prevtime = time;
                laserx1 = enemyx;
                laserx2 = enemyx + 192;
                laserdst1.set(laserx1, enemyy -100, enemyx+64, enemyy);
                laserdst2.set(laserx2, enemyy -100, enemyx +256, enemyy);
                lasery = enemyy - 128;
            }

            laserdst2.set(laserx1, lasery, laserx1 + 64, lasery + 128);
            laserdst1.set(laserx2, lasery, laserx2 + 64, lasery + 128);
            //endregion

            //region laser kovboya değerse kovboyun hazin sonu...
            if(spritedst.intersect(laserdst1) || spritedst.intersect(laserdst2))
            {
                spritedst.set(0,0,0,0);
                spriteexist = false;
                guishow = true;
            }
            //endregion

            //region enemy hareketi
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
            //endregion

            //region enemy dönme noktasına geldi mi kontrolü
            if(enemyspeedx > 0 && enemyx > donmenoktasi){
                donmeboolean = true;
                enemyspeedx = -enemyspeedx;
            }
            else if(enemyspeedx < 0 && enemyx < donmenoktasi)
            {
                donmeboolean = true;
                enemyspeedx = -enemyspeedx;
            }
            //endregion

            //region enemy koordinatları enemy ölümü mermi hareketleri

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
                    explodedst.set(enemyx, enemyy, enemyx + 256, bullety2.elementAt(i) + 256);
                    bulletx2.removeAllElements();
                    bullety2.removeAllElements();
                    bulletdst.removeAllElements();
                    bulletspeedx2.removeAllElements();
                    bulletspeedy2.removeAllElements();
                    enemydst.set(0, 0, 0, 0);
                    enemyexist = false;
                    exploded = true;
                    guishow = true;
                    root.soundManager.play(sesefekti_patlama); //değdiği anda BOM
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
            //endregion

            //region karakter hızı
            spritex += hizx;
            spritey += hizy;
            //endregion

            //region enemy hızı
            enemyx += enemyspeedx;
            enemyy += enemyspeedy;

            //endregion

            //region enemy hareketi

            if(enemyx + 256 > getWidth() || enemyx < 0) //Sınırlardan çıkmaması için
            {
                enemyspeedx = -enemyspeedx;
            }
            //y değişmeyeceği için aynı şeyi y için yazmadık
            //endregion

            //region bullet
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
                //Log.i("Control", String.valueOf(bulletx2.size())); //mermi silindi mi kontrolü?
            }
            //endregion

            //region karakter sınırlara geldi mi
            if(spritex+256 > getWidth() || spritex < 0) {//x ekseni icin sona geldimi kontrolu
                hizx = 0;//spritex = getWidth() - 256;
                //animasyonno = 0;  //sona gelince durma animasyonu
            }

            if(spritey+256 > getHeight() || spritey < 0){//y ekseni icin sona geldimi kontrolu
                hizy = 0;//spritey = getHeight() -256;
                //animasyonno = 0;  //sona gelince durma animasyonu
            }

            //endregion

            //region karakter hareketi ve animasyonu
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
            if(spriteexist)
            {
              //  Log.i(TAG, String.valueOf(spritex));
                spritedst.set(spritex, spritey, spritex+256, spritey+256);//Ekrana cizilecegi koordinatlar
            }
            //endregion

            //region bullet koordinatları
            bulletsrc.set(0,0,70,70);
            //bulletdst.set(bulletx_temp, bullety_temp, bulletx_temp + 32, bullety_temp + 32);

            for(int i=0; i < bulletx2.size(); i++)
            {
                bulletdst.elementAt(i).set(bulletx2.elementAt(i), bullety2.elementAt(i), bulletx2.elementAt(i) + 32, bullety2.elementAt(i) + 32);
            }
            //endregion
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

        //region harita çizimi
        for (int i=0; i<getWidth(); i+=128)
        {
            for(int j=0; j<getHeight(); j+=128)
            {
                tiledst.set(i,j,i+128,j+128);
                canvas.drawBitmap(tileset,tilesrc,tiledst,null);//yesil cimen zemini tum ekrana cizme
            }
        }
        //endregion

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
        canvas.drawBitmap(laser, lasersrc, laserdst1, null);
        canvas.drawBitmap(laser, lasersrc, laserdst2, null);

        if(guishow) {
            canvas.drawText(text, getWidthHalf(), getHeightHalf() - 300, textcolor);
            canvas.drawBitmap(buttons, restartsrc, restartdst, null);
            canvas.drawBitmap(buttons, exitsrc, exitdst, null);
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

        //region karakter hareketi kontrolü
        if(spriteexist){
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
        //endregion

        //region gui kontrol

        if(guishow)
        {
            if(restartdst.contains(x, y))
            {
                //root.setup(); -> Tüm sistemi yeniden başlatır
                setup(); //Sadece GameCanvas'ı yeniden başlatır
            }
            if(exitdst.contains(x, y))
            {
                MenuCanvas mc = new MenuCanvas(root); //objemizi oluşturduk
                root.canvasManager.setCurrentCanvas(mc); //Mainmanu'ye çıkar
                //System.exit(0); -> Sistemden Çıkar
            }
        }
        //endregion
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
