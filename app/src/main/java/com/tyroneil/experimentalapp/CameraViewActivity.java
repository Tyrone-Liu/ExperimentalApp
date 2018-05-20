package com.tyroneil.experimentalapp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;

import java.util.Arrays;

public class CameraViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
    }

    public void cameraCharacteristics(View view) {
        Intent intent = new Intent(this, MessageDisplayActivity.class);
        CameraManager cameraManager = (CameraManager) this.getSystemService(CameraManager.class);
        String messageText = "Camera Id List: {";
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String e : cameraIdList) {
                messageText += e + ", ";
            }
            messageText += "}\n" + "\n\n";

            for (String cameraId : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                messageText += (
                        "Camera Id: " + cameraId + "\n"
                                + repStr(" ", 4) + "LENS_FACING: "
                                + (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).toString() + "\n"
                                + repStr(" ", 4) + "SENSOR_INFO_PHYSICAL_SIZE: "
                                + (cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)).toString() + "\n"
                                + repStr(" ", 4) + "SENSOR_INFO_PIXEL_ARRAY_SIZE: "
                                + (cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)).toString() + "\n"
                                + repStr(" ", 4) + "INFO_SUPPORTED_HARDWARE_LEVEL: "
                                + (cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).toString() + "\n"
                );

                messageText += repStr(" ", 4) + "REQUEST_AVAILABLE_CAPABILITIES:\n" + repStr(" ", 8) + "{";
                int[] request_avaliable_capabilities = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                Arrays.sort(request_avaliable_capabilities);
                for (int e : request_avaliable_capabilities) {
                    messageText += e + ", ";
                }
                messageText += "}\n";

                messageText += repStr(" ", 4) + "SENSOR_INFO_EXPOSURE_TIME_RANGE:\n" + repStr(" ", 8);
                if ((cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)) != null) {
                    messageText += (cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)).toString() + "\n";
                } else {
                    messageText += "null\n";
                }

                messageText += "\n";
            }
        } catch (CameraAccessException e) {
            messageText += "Exception!\nStack Trace:\n" + e.getStackTrace() + "\n";
        }
        intent.putExtra(MainActivity.EXTRA_MESSAGE, messageText);
        startActivity(intent);
    }

    public static String repStr(String str, int count) {
        String repeatedString = "";
        for (int i = 0; i < count; i += 1) {
            repeatedString += str;
        }
        return repeatedString;
    }
}
