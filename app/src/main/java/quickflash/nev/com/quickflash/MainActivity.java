package quickflash.nev.com.quickflash;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    Camera camera;
    Camera.Parameters parm;
    private boolean isFlashOn;
    int requestID = (int) System.currentTimeMillis();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
                AppBegin();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(this, "Permission is revoked", Toast.LENGTH_SHORT).show();
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            AppBegin();

        }



        }



    @Override
    protected void onStop() {
        super.onStop();
        if(camera!=null){
            camera.release();
            camera=null;

        }
    }

    @Override
    protected void onResume() {
        turnOnFlash();
        finish();
        super.onResume();
    }
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parm = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error ", e.getMessage());
            }
        }
    }
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || parm== null) {
                return;
            }
            Toast.makeText(this, "Flashlight ON", Toast.LENGTH_SHORT).show();
            parm = camera.getParameters();
            parm.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parm);
            camera.startPreview();
            isFlashOn = true;
            finish();


        }

    }
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || parm == null) {
                return;
            }
            Toast.makeText(this, "Flashlight already ON,turning OFF", Toast.LENGTH_SHORT).show();
            parm = camera.getParameters();
            parm.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parm);
            camera.stopPreview();
            isFlashOn = false;



        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }



    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        AppBegin();
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                            showMessageOKCancel("You need to allow access to Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= 23){
                                            requestPermissions(new String[] {Manifest.permission.CAMERA},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                        }}
                                    });
                        return;}
                    if (Build.VERSION.SDK_INT >= 23){
                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;}
                    }
                    }
                }}



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void AppBegin()
    {
        getCamera();
        turnOnFlash();
        Intent intent = new Intent(getApplicationContext(), CameraRelease.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent= PendingIntent.getActivity(this,requestID,intent,0);
        Notification noti = new Notification.Builder(MainActivity.this)
                .setContentTitle("Flashlight")
                .setContentText("Tap to disable torchlight")
                .setSmallIcon(R.drawable.ic_flash_off_black_18dp)
                .setContentIntent(pIntent).getNotification();
        noti.flags=Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,noti);

    }


}

