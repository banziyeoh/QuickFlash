package quickflash.nev.com.quickflash;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.Toast;

public class CameraRelease extends Activity {
    private Camera camera;
    private Camera.Parameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Flashlight OFF", Toast.LENGTH_SHORT).show();
        if(camera !=null){
            camera.release();}
        else{
            camera = Camera.open();
            params = camera.getParameters();
            if (params != null)
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            camera.release();
        }

        finish();
    }

}

