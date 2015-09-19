package com.h264.decode2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;



/**
 * Created by Administrator on 2015/8/7.
 */


public class MyView extends SurfaceView implements SurfaceHolder.Callback{

    public static final int WIDTH = 2048;
    public static final int HEIGHT = 1088;
    public static int ScreenWidth = 720;
   // public static final int ScreenHeight = 1232;
    private SurfaceHolder Holder;
    private MyThread myThread;

    public MyView(Context context,AttributeSet attrs){
        super(context,attrs);
        Holder = this.getHolder();
        Holder.addCallback(this);
        myThread = new MyThread(Holder); // 创建一个绘图线程
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d("MyView", "surfaceCreated");
        if(MyScreen.getInstance().getlandflag()){
            ScreenWidth = 1232;
        }else {
            ScreenWidth = 720;
        }
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width = ScreenWidth;
        lp.height = HEIGHT * ScreenWidth / WIDTH;
        holder.setFixedSize(lp.width,lp.height);
        this.setLayoutParams(lp);
        myThread.isRun = true;
        //myThread.start();
        // 启动自定义线程
        new Thread(myThread).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 用于surfaceView区域变化时，例如横屏竖屏，我们暂时不用
        Log.d("MyView", "surfaceChanged");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("MyView","surfaceDestroyed");
        myThread.isRun = false;
    }

}
class MyThread implements Runnable{

    public static final int WIDTH = 2048;
    public static final int HEIGHT = 1088;
    public static  int ScreenWidth = 720;
    public static  int ScreenHeight = 1232;
    //private ByteBuffer buffer = null;
    private Bitmap videoBit = Bitmap.createBitmap(WIDTH,HEIGHT, Bitmap.Config.RGB_565);
    //static final String path = Environment.getExternalStorageDirectory().getPath() + "/sender.264";
    static final String path = Environment.getExternalStorageDirectory().getPath() + "/LucamVideo.264";
    //static final String path = Environment.getExternalStorageDirectory().getPath() + "/sduni.sdp";
    FFmpegNative ffmpeg = new FFmpegNative(WIDTH,HEIGHT,path);
    video myVideo = new video();
    private final SurfaceHolder holder;
    public boolean isRun;
    public MyThread(SurfaceHolder holder){
        this.holder = holder;
        isRun = true;
    }
    @Override
    public void run(){
        int count = 0;
        Canvas canvas = null;
        //ByteBuffer buffer = null;

        while(isRun){
            try{
                synchronized(holder){ // 同步加锁holder
                    canvas = holder.lockCanvas(); //锁定画布，一般在锁定后可以通过其返回的画布对象Canvas，在其上面画图
                    canvas.drawColor(Color.BLACK); // 设置画布背景颜色
                    Paint p = new Paint(); // 创建画笔
                    myVideo = ffmpeg.videoPlay();
                    isRun = myVideo.isContinue;
                    if(myVideo.vbuffer != null){
                        videoBit.copyPixelsFromBuffer(myVideo.vbuffer);
                        Bitmap showBitmap = adaptive(videoBit);
                        //canvas.drawBitmap(videoBit, 0, 0, p);
                        canvas.drawBitmap(showBitmap,0,0,p);
                        myVideo.vbuffer.position(0);
                        count++;
                        //buffer = null;
                        Log.i("MyScreen", "flag = " + MyScreen.getInstance().getlandflag());

                    }
                    Paint pt = new Paint();
                    pt.setColor(Color.WHITE);
                    /*//Rect r = new Rect(100,50,300,250);
                    //canvas.drawRect(r,p);*/
                    //canvas.drawText("这是第" + count + "帧", 100, 400, pt);
                    Thread.sleep(40);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                    holder.unlockCanvasAndPost(canvas); //结束锁定画布，并提交改变
            }
        }
    }

    // Image Scale 图像缩放、适配不同分辨率
    public Bitmap adaptive(Bitmap bitmap){
        if(MyScreen.getInstance().getlandflag()){
            ScreenWidth = 1232;
            ScreenHeight = 720;
        }else {
            ScreenWidth = 720;
            ScreenHeight = 1232;
        }
        float Imgwdh =  (float)WIDTH / HEIGHT;
        float Scrwdh =(float) ScreenWidth / ScreenHeight;
       // Log.d("MyView","width / height = " + Imgwdh);
       // Log.d("MyView", "ScreenWidth / ScreenHeight = " + Scrwdh);
        Matrix matrix = new Matrix();
        float wScale,hScale;
        if(Imgwdh > Scrwdh){
            wScale = (float)ScreenWidth / WIDTH;
            hScale = wScale;
        }else {
            hScale = (float)ScreenHeight / HEIGHT;
            wScale = hScale;
        }
        matrix.postScale(wScale, hScale);
        Bitmap newbmp = Bitmap.createBitmap(bitmap,0,0,WIDTH,HEIGHT,matrix,true);
        return newbmp;
    }

}
