package com.h264.decode2;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.jar.Attributes;


public class MainActivity extends Activity implements View.OnClickListener{

    Button play;
    Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new MyView(this));
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
        Log.i("MainActivity","heightPixels: " + height + "; widthPixels: " + width + "; densityDpi: " + densityDpi);
        play = (Button)findViewById(R.id.play);
        stop = (Button)findViewById(R.id.stop);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);

        
    }
   /* @Override
    protected void onDestroy(){

        super.onDestroy();
    }
*/

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
        switch (v.getId()){
            case R.id.play:
                //Log.d("MainActivity","Clicked play");
                Toast.makeText(MainActivity.this,"You clicked play",Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop:
                Toast.makeText(MainActivity.this,"You clicked stop",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }
}

