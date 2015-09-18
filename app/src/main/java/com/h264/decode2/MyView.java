package com.h264.decode2;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.util.jar.Attributes;


/**
 * Created by Administrator on 2015/8/7.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback{



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

        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width = 720;
        lp.height = 288 * 720 / 352;
        holder.setFixedSize(352,288);
        this.setLayoutParams(lp);
        myThread.isRun = true;
        //myThread.start();
        // 启动自定义线程
        new Thread(myThread).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 用于surfaceView区域变化时，例如横屏竖屏，我们暂时不用
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myThread.isRun = false;
    }

}
class MyThread implements Runnable{

    //private ByteBuffer buffer = null;
    private Bitmap videoBit = Bitmap.createBitmap(352,288, Bitmap.Config.RGB_565);
    static final String path = Environment.getExternalStorageDirectory().getPath() + "/sender.264";
    //static final String path = Environment.getExternalStorageDirectory().getPath() + "/sduni.sdp";
    FFmpegNative ffmpeg = new FFmpegNative(352,288,path);
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
                        canvas.drawBitmap(videoBit, 0, 0, p);
                        myVideo.vbuffer.position(0);
                        count++;
                        //buffer = null;
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

}
