package com.h264.decode2;

import android.util.Log;

import java.net.DatagramSocket;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2015/8/1.
 *
 */
class video{
    public ByteBuffer vbuffer;
    public boolean isContinue;
}
public class FFmpegNative {
    static final String TAG = "FFmpegNative";
    static {
        /*System.loadLibrary("avutil-52");
        System.loadLibrary("swresample-0");
        System.loadLibrary("avcodec-55");
        System.loadLibrary("avformat-55");
        System.loadLibrary("swscale-2");*/
        System.loadLibrary("avutil-54");
        System.loadLibrary("swresample-1");
        System.loadLibrary("avcodec-56");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        //System.loadLibrary("SDL");
        System.loadLibrary("Myffmpeg_codec");
    }
    public native int Init(String videoPath,byte[] sdpBuf);
    public native int Decode2RGB(byte[] out);

    //int width = 0;
    //int height = 0;
     byte[] mPixel = null;
     byte[] sdpBuf = null;
     ByteBuffer buffer = null;




    public FFmpegNative(int width, int height, String path){
        //this.width = width;
        //this.height= height;

        mPixel = new byte[6144 * height * 2];
        sdpBuf = new byte[32768];
        buffer = ByteBuffer.wrap(this.mPixel);
        /*for(int i = 0; i < mPixel.length; i++){
            mPixel[i] = (byte) 0x00;
        }*/
        Log.d(TAG,"path: " + path);
        int isInit = Init(path,sdpBuf);
       // Log.d("FFmpegNative","numBytes = " + isInit);
        if(isInit == 0){
            Log.d(TAG,"Init Success!");
        }
        else{
            Log.e(TAG,"Init Failed.");
        }
    }
    public video videoPlay(){
        int isDecode = Decode2RGB(mPixel);
        video myVideo = new video();
        /*if(isDecode == 0)
            Log.d(TAG, "pFrameCtx = NULL");
        if(isDecode == -2)
            Log.d(TAG,"!=NULL");*/
        /*if(isDecode < 0){
            Log.d(TAG,"Decode finished.");
            //return null;
            this.buffer = null;
            this.isContinue = false;
            return this;

        }*/
        if(isDecode == 1){
            Log.d(TAG,"Decode finished..");
            //return null;
            myVideo.vbuffer = null;
            myVideo.isContinue = false;
            return myVideo;
        }
        else if(isDecode == 2){
            Log.d(TAG,"sws_getCachedContext failed.");
            //return null;
            myVideo.vbuffer = null;
            myVideo.isContinue = true;
            return myVideo;
        }
        else if(isDecode == 3){
            Log.d(TAG,"Decode failed.");
            //return null;
            myVideo.vbuffer = null;
            myVideo.isContinue = true;
            return myVideo;
        }
        else if (isDecode == 4){
            Log.d(TAG,"Not VideoStream.");
            //return null;
            myVideo.vbuffer = null;
            myVideo.isContinue = true;
            return myVideo;
        }
        else {
            Log.d(TAG,"Decode Success!");
            //return buffer;
            myVideo.vbuffer = buffer;
            myVideo.isContinue = true;
            return myVideo;
        }
        /*else{
            Log.d(TAG,"Decode Success!");
            return buffer;
        }*/
        //return null;
    }








}
