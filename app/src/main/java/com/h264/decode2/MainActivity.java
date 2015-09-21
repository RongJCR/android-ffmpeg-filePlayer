package com.h264.decode2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.jar.Attributes;


public class MainActivity extends Activity implements View.OnClickListener {

    Button play;
    Button stop;
    MyView myView;
    public static final int WIDTH = 2048;
    public static final int HEIGHT = 1088;
    public static  final int ScreenWidth = 720;
    public static  final int ScreenHeight = 1232;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new MyView(this));
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            MyScreen.getInstance().setlandflag(true);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            MyScreen.getInstance().setlandflag(false);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "OnCreate");
        /*float xdpi = getResources().getDisplayMetrics().xdpi;
        float ydpi = getResources().getDisplayMetrics().ydpi;
        Log.d("MainActivity", "xdpi is " + xdpi + "; ydpi is " + ydpi);*/
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;
        int width = metric.widthPixels;
        int densityDpi = metric.densityDpi;
        Log.i("MainActivity", "heightPixels: " + height + "; widthPixels: " + width + "; densityDpi: " + densityDpi);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        myView = (MyView)findViewById(R.id.my_View);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            MyScreen.getInstance().setlandflag(true);
            Toast.makeText(getApplicationContext(), "切换为横屏", Toast.LENGTH_SHORT).show();
            ViewGroup.LayoutParams lp = myView.getLayoutParams();
            lp.width = ScreenHeight;
            lp.height = HEIGHT * ScreenHeight / WIDTH;
            myView.setLayoutParams(lp);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            MyScreen.getInstance().setlandflag(false);
            Toast.makeText(getApplicationContext(), "切换为竖屏", Toast.LENGTH_SHORT).show();
            ViewGroup.LayoutParams lp = myView.getLayoutParams();
            lp.width = ScreenWidth;
            lp.height = HEIGHT * ScreenWidth / WIDTH;
            myView.setLayoutParams(lp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                //Log.d("MainActivity","Clicked play");
                Toast.makeText(MainActivity.this, "别Play了，按右边那个", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop:
                Toast.makeText(MainActivity.this, "根本停不下来", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}

