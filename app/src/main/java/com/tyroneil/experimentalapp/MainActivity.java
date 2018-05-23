package com.tyroneil.experimentalapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class MainActivity extends Activity {
    public static final String EXTRA_MESSAGE = "com.tyroneil.experimentalapp.MESSAGE";

    private TextureView previewTextureView;
    private Button capture;
    private Button changeShutterParameters;
    private Button displayCameraCharacteristics;

    // shared variable
    private CameraManager cameraManager;
    private String[] cameraIdList;

    private Handler callbackHandler;

    // variable for preview
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private CameraCaptureSession previewSession;

    private int previewFormat;
    private Size previewSize;

    // current variable
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCharacteristics cameraCharacteristics;
    private StreamConfigurationMap streamConfigurationMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewTextureView = (TextureView) findViewById(R.id.previewTextureView);
        capture = (Button) findViewById(R.id.capture);
        changeShutterParameters = (Button) findViewById(R.id.changeShutterParameters);
        displayCameraCharacteristics = (Button) findViewById(R.id.displayCameraCharacteristics);

        previewTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                createPreview(0);
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        changeShutterParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeShutterParameters();
            }
        });
        displayCameraCharacteristics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCameraCharacteristics();
            }
        });

        cameraManager = (CameraManager) this.getSystemService(CameraManager.class);
        displayCameraCharacteristics();
    }

    /**
     * The process to create a preview needs to wait for 'CameraDevice', 'CameraCaptureSession' to
     * callback, so there will be three stages.
     *
     * Stage 0: Use 'CameraManager' to query a cameraId, then open the camera.
     * Stage 1:
     *     'CameraDevice.StateCallback.onOpened()' call 'createPreview()' with {@param 1}.
     *     Use info from 'CameraCharacteristics' to set preview image format and size.
     *     Use 'CameraDevice.createCaptureRequest()' to create a 'CaptureRequest.Builder'.
     *     Use 'CaptureRequest.Builder.addTarget()' to set TextureView as the output target.
     *     Use 'CameraDevice.createCaptureSession()' to create a 'CameraCaptureSession'.
     * Stage 2:
     *     'CameraCaptureSession.StateCallback.onConfigured()' call 'createPreview()' with {@param 2}.
     *     Use 'CameraCaptureSession.setRepeatingRequest(CaptureRequest.Builder.build())' to
     *     submit 'CaptureRequest' to 'CameraCaptureSession'.
     *
     * @param stage the stage of creating (int 0, 1, 2)
     */
    private void createPreview(int stage) {
        if (stage == 0) {
            try {
                cameraIdList = cameraManager.getCameraIdList();
                cameraId = cameraIdList[0];  // (TO_DO) make this changeable
                cameraManager.openCamera(cameraId, cameraStateCallback, callbackHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else if (stage == 1) {  // got cameraDevice
            try {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewFormat = ImageFormat.YUV_420_888;  // (TO_DO) make this changeable
            previewSize = streamConfigurationMap.getOutputSizes(previewFormat)[0];
            SurfaceTexture previewSurfaceTexture = previewTextureView.getSurfaceTexture();
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(previewSurfaceTexture);
            try {
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(previewSurface);
                previewRequest = previewRequestBuilder.build();
                cameraDevice.createCaptureSession(Arrays.asList(previewSurface), previewSessionStateCallback, callbackHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (stage == 2) {  // got previewSession
            try {
                previewSession.setRepeatingRequest(previewRequest, null, callbackHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // will call 'createPreview(1)'
    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createPreview(1);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    // will call 'createPreview(2)'
    private CameraCaptureSession.StateCallback previewSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            previewSession = session;
            createPreview(2);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };


    // (TO_DO)
    private void takePhoto() {
    }


    // (TO_DO)
    private void changeShutterParameters() {
    }


    private void displayCameraCharacteristics() {
        Intent intent = new Intent(this, MessageDisplayActivity.class);
        String messageText = "Camera Id List: {";
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String e : cameraIdList) {
                messageText += e + ", ";
            } messageText += "}\n" + "\n\n";

            for (String cameraId : cameraIdList) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
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

                messageText += repStr(" ", 4) + "SENSOR_INFO_EXPOSURE_TIME_RANGE:";
                if ((cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)) != null) {
                    messageText += "\n" + repStr(" ", 8) + (cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)).toString() + "\n";
                } else {
                    messageText += " null\n";
                }

                messageText += repStr(" ", 4) + "REQUEST_AVAILABLE_CAPABILITIES:\n" + repStr(" ", 8) + "{";
                int[] request_avaliable_capabilities = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                Arrays.sort(request_avaliable_capabilities);
                for (int e : request_avaliable_capabilities) {
                    messageText += e + ", ";
                } messageText += "}\n";

                messageText += repStr(" ", 4) + "SCALER_STREAM_CONFIGURATION_MAP:\n" + repStr(" ", 8) + "Output Sizes:\n";
                streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                for (int e : streamConfigurationMap.getOutputFormats()) {
                    messageText += repStr(" ", 8) + " Format: " + e + "\n";
                    for (Size s : (streamConfigurationMap.getOutputSizes(e))) {
                        messageText += repStr(" ", 8) + "  " + s.toString() + "\n";
                    }
                } messageText += "}\n";
/*
                // Full output of SCALER_STREAM_CONFIGURATION_MAP
                String scaler_stream_configuration_map = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).toString();
                messageText += (
                        repStr(" ", 8)
                        + scaler_stream_configuration_map
                                .replace("([", "(\n" + repStr(" ", 8) + " [")
                                .replace("]), ", "]),\n\n" + repStr(" ", 8) + "")
                                .replace("], [", "],\n" + repStr(" ", 8) + " [")
                                .replace(", min_duration:", ",\n" + repStr(" ", 8) + "  min_duration:")
                                .replace(", out:", ",\n" + repStr(" ", 8) + "  out:")
                        + "\n"
                );
*/

                messageText += "\n\n";
            }
        } catch (CameraAccessException e) {
            e.getStackTrace();
        }
        intent.putExtra(MainActivity.EXTRA_MESSAGE, messageText);
        startActivity(intent);
    }

    // CameraCharacteristics value Int to String (TO_DO)
    private String[] CCIntToString(String cCKey, int[] intValue) {
        String[] stringValue = new String[intValue.length];
        if (cCKey == "REQUEST_AVAILABLE_CAPABILITIES") {
            for (int e : intValue) {
            }
        } else if (cCKey == "SCALER_STREAM_CONFIGURATION_MAP") {
            for (int e : intValue) {
            }

        }
        return stringValue;
    }

    private static String repStr(String str, int count) {
        String repeatedString = "";
        for (int i = 0; i < count; i += 1) {
            repeatedString += str;
        }
        return repeatedString;
    }
}
