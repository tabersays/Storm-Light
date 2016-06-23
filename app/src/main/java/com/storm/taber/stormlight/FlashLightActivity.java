package com.storm.taber.stormlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class FlashLightActivity extends AppCompatActivity {

    Boolean isOn;
    CameraManager cameraManager;
    Camera camera;
    Camera.Parameters camParams;
    String rearCamera;
    Button toggleButton;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                String[] cameraIds = cameraManager.getCameraIdList();
                rearCamera = null;

                for(int i = 0; i < cameraIds.length; i++) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
                    Integer orientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if(orientation != null && orientation == CameraCharacteristics.LENS_FACING_BACK){
                        rearCamera = cameraIds[i];
                        break;
                    }
                }
                if(rearCamera != null)
                    cameraManager.setTorchMode(rearCamera, true);
            }
            else {
                int cameraId = 0;
                for (int i = 0; i < camera.getNumberOfCameras(); i++) {
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    camera.getCameraInfo(i, cameraInfo);
                    if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                        cameraId = i;
                        break;
                    }
                }
                camera = Camera.open(cameraId);
                camParams = camera.getParameters();
                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(camParams);
                camera.startPreview();
            }
            isOn = true;
        } catch(Throwable throwable) {
            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
        toggleButton = (Button) findViewById(R.id.button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCamera();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flash_light, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(null);
    }
    @TargetApi(23)
    public void toggleCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(isOn){
                    cameraManager.setTorchMode(rearCamera, false);
                    toggleButton.setBackgroundResource(R.drawable.offbut);
                }
                else{
                    cameraManager.setTorchMode(rearCamera, true);
                    toggleButton.setBackgroundResource(R.drawable.onbut);
                }
            }
            else {
                if(isOn){
                    camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    toggleButton.setBackgroundResource(R.drawable.offbut);
                }
                else{
                    camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    toggleButton.setBackgroundResource(R.drawable.onbut);
                }
                camera.setParameters(camParams);
                camera.startPreview();
            }
            isOn = !isOn;
        }
        catch (Throwable throwable) {
            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
