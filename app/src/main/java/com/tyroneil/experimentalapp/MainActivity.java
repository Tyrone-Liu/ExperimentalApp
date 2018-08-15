package com.tyroneil.experimentalapp;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends Activity {
    // widgets in activity
    private ChangeableRatioTextureView previewTextureView;
    private Button captureButton, modeButton, settingsButton;
    private Button exposureTimeButton, sensitivityButton, focusDistanceButton, focalLengthButton, apertureButton;

    // shared variable
    private CameraManager cameraManager;
    private String[] cameraIdList;

    private Handler backgroundHandler;

    // current cameraDevice variable
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCharacteristics cameraCharacteristics;

    private CameraCaptureSession captureSession;
    private int sensorOrientation;

    // variable for preview
    private CaptureRequest.Builder previewRequestBuilder;
    private Size previewSize;

    // variable for capture
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureResult captureResult;
    private ImageReader imageReader;
    private DngCreator dngCreator;
    private int captureFormat;
    private Size captureSize;

    // capture parameters
    private int autoMode;  // CONTROL_MODE (0/1 OFF/AUTO)
    private int aeMode;  // CONTROL_AE_MODE (0/1 OFF/ON)
    private int afMode;  // CONTROL_AF_MODE (0/4 OFF/CONTINUOUS_PICTURE)
    private int awbMode;  // CONTROL_AWB_MODE (0/1 OFF/AUTO)

    private long exposureTime;  // SENSOR_EXPOSURE_TIME
    private int sensitivity;  // SENSOR_SENSITIVITY
    private float focusDistance;  // LENS_FOCUS_DISTANCE
    private float focalLength;  // LENS_FOCAL_LENGTH
    private float aperture;  // LENS_APERTURE

    private int opticalStabilizationMode;  // LENS_OPTICAL_STABILIZATION_MODE

    // debug Tool
    private TextView displayDebugMessage;
    private String debugMessage = "";
    private int debugCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewTextureView = (ChangeableRatioTextureView) findViewById(R.id.previewTextureView);
        captureButton = (Button) findViewById(R.id.capture);
        modeButton = (Button) findViewById(R.id.mode);
        settingsButton = (Button) findViewById(R.id.settings);

        exposureTimeButton = (Button) findViewById(R.id.exposureTime);
        sensitivityButton = (Button) findViewById(R.id.sensitivity);
        focusDistanceButton = (Button) findViewById(R.id.focusDistance);
        focalLengthButton = (Button) findViewById(R.id.focalLength);
        apertureButton = (Button) findViewById(R.id.aperture);

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
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCapture();
            }
        });
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings();
            }
        });

        exposureTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setExposureTime();
            }
        });
        sensitivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSensitivity();
            }
        });
        focusDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFocusDistance();
            }
        });
        focalLengthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFocalLength();
            }
        });
        apertureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAperture();
            }
        });

        displayDebugMessage = (TextView) findViewById(R.id.displayDebugMessage);  // debug
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
        // TODO: make all this parameters changeable
        // autoMode = 1;
        autoMode = 0;  // debug
        aeMode = 1;
        afMode = 4;
        awbMode = 1;

        // TODO: make capture format changeable in settings, then make this default RAW_SENSOR
        captureFormat = 256;

        if (stage == 0) {
            try {
                cameraManager = (CameraManager) this.getSystemService(CameraManager.class);
                cameraIdList = cameraManager.getCameraIdList();
                // TODO: make camera changeable in settings
                for (String e : cameraIdList) {
                    if ((cameraManager.getCameraCharacteristics(e)).get(CameraCharacteristics.LENS_FACING) == 1) {
                        cameraId = e;
                        cameraCharacteristics = cameraManager.getCameraCharacteristics(e);
                        initiateCaptureParameters(cameraCharacteristics);
                        break;
                    }
                    cameraId = cameraIdList[0];
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

            try {
                cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler);
            } catch (CameraAccessException | SecurityException e) {
                e.printStackTrace();
            }
        } else if (stage == 1) {
            // got cameraDevice
            // this stage will also be used to refresh the preview parameters
            previewSize = choosePreviewSize(cameraCharacteristics, captureFormat);

            /**
             * Check the 'SENSOR_ORIENTATION' to set width and height appropriately.
             * (Switch width and height if necessary.  )
             */
            sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == 90 || sensorOrientation == 270) {
                previewTextureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            } else {
                previewTextureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            }

            SurfaceTexture previewSurfaceTexture = previewTextureView.getSurfaceTexture();
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(previewSurfaceTexture);
            try {
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, autoMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);

                previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
                previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
                previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);
                previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);

                previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);

                previewRequestBuilder.addTarget(previewSurface);
                cameraDevice.createCaptureSession(Arrays.asList(previewSurface), captureSessionStateCallback, backgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (stage == 2) {  // got captureSession
            try {
                // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), captureSessionCaptureCallback, backgroundHandler);
                updateCaptureParametersIndicator();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Produce a 'CameraDevice', then call 'createPreview(1)'
     */
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

    /**
     * Produce a 'CameraCaptureSession', then call 'createPreview(2)'
      */
    private CameraCaptureSession.StateCallback captureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            captureSession = session;
            createPreview(2);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    private CameraCaptureSession.CaptureCallback captureSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            debugCounter ++;
            debugMessage = "Frame Counter: " + debugCounter;
            displayDebugMessage.setText(debugMessage);
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
    };


    private void initiateCaptureParameters(CameraCharacteristics cameraCharacteristics) {
        final Range<Long> SENSOR_INFO_EXPOSURE_TIME_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        final Range<Integer> SENSOR_INFO_SENSITIVITY_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        final float LENS_INFO_HYPERFOCAL_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
        final float[] LENS_INFO_AVAILABLE_FOCAL_LENGTHS = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        final float[] LENS_INFO_AVAILABLE_APERTURES = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);

        final int[] LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);

        exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();

        if (SENSOR_INFO_SENSITIVITY_RANGE.contains(200)) {
            sensitivity = 200;
        } else if (SENSOR_INFO_SENSITIVITY_RANGE.contains(400)) {
            sensitivity = 400;
        } else {
            sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getLower();
        }

        focusDistance = LENS_INFO_HYPERFOCAL_DISTANCE;
        // TODO: make these two parameters changeable through UI buttons
        focalLength = LENS_INFO_AVAILABLE_FOCAL_LENGTHS[0];
        aperture = LENS_INFO_AVAILABLE_APERTURES[0];

        opticalStabilizationMode = 0;
        for (int e : LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION) {
            if (e == 1) {
                opticalStabilizationMode = e;
                break;
            }
        }
    }


    /**
     * First, get the sensor pixel array size, which is the maximum
     * available size and the output size in RAW_SENSOR format.
     *
     * Second, get the available size in capture format (if it is not RAW_SENSOR (32)), and find
     * the maximum size that has the closest aspect ratio with the sensor pixel array size.
     *
     * Last, in the available size for SurfaceTexture.class format, find an optimized size
     * with the same aspect ratio as the size found in the second step.
     */
    private Size choosePreviewSize(CameraCharacteristics cameraCharacteristics, int captureFormat) {
        final Size SENSOR_INFO_PIXEL_ARRAY_SIZE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        Size[] previewSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
        Size[] previewSizesFiltered;
        if (captureFormat == 32) {
            previewSizesFiltered = sizesFilter(previewSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
            return previewSizesFiltered[0];  // TODO: choose optimized size, not maximum size
        }
        Size[] captureSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(captureFormat);
        Size[] captureSizesFiltered = sizesFilter(captureSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
        previewSizesFiltered = sizesFilter(previewSizes, captureSizesFiltered[0]);
        return previewSizesFiltered[0];
    }

    private Size[] sizesFilter(Size[] sizes, Size goalSize) {
        float minimumDeviation = 0.000f;
        ArrayList<Size> sizesFiltered = new ArrayList<>();
        while (sizesFiltered.isEmpty()) {
            for (Size e : sizes) {
                float deviation = ((float) e.getWidth() / e.getHeight()) - ((float) goalSize.getWidth() / goalSize.getHeight());
                if (
                        (deviation == 0.0f)
                        || ((deviation < 0.0f) && (deviation > - minimumDeviation))
                        || ((deviation > 0.0f) && (deviation < minimumDeviation))
                ) {
                    sizesFiltered.add(e);
                }
            }
            minimumDeviation += 0.005f;
        }
        sizesFiltered.sort(sizesComparator);
        return sizesFiltered.toArray(new Size[sizesFiltered.size()]);
    }

    Comparator<Size> sizesComparator = new Comparator<Size>() {
        @Override
        public int compare(Size size1, Size size0) {
            return Integer.compare(size0.getWidth() * size0.getHeight(), size1.getWidth() * size1.getHeight());
        }
    };


    private void updateCaptureParametersIndicator() {
        if (aeMode == 0 || autoMode == 0) {
            exposureTimeButton.setText("S.S.\n" + String.format("%.2f", (float) (exposureTime / 10000000) / 100) + "s");
            sensitivityButton.setText("ISO\n" + sensitivity);
        } else {
            exposureTimeButton.setText("S.S.\nAUTO");
            sensitivityButton.setText("ISO\nAUTO");
        }

        if (afMode == 0 || autoMode == 0) {
            focusDistanceButton.setText("F.D.\n" + String.format("%.3f", 1 / focusDistance) + "m");
        } else {
            focusDistanceButton.setText("F.D.\nAUTO");
        }

        focalLengthButton.setText("F.L.\n" + String.format("%.2f", focalLength) + "mm");
        apertureButton.setText("APE\nf/" + String.format("%.1f", aperture));
    }


//    private void takePhoto() {
//        try {
//            if (autoMode == 1) {
//                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            } else {
//                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);
//                previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
//                previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
//            }
//            captureRequestBuilder.addTarget(imageReader.getSurface());
//            captureSession.capture(captureRequestBuilder.build(), captureSessionCaptureCallback, backgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private ImageReader.OnImageAvailableListener captureImageAvailableListener = new ImageReader.OnImageAvailableListener() {
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//            if (captureFormat == ImageFormat.RAW_SENSOR) {
//                dngCreator = new DngCreator(cameraCharacteristics, captureResult);
////                dngCreator.setOrientation(sensorOrientation);
//                File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/LongShoot/RAW_TEST.DNG");
//                try {
//                    FileOutputStream imageOutput = new FileOutputStream(imageFile);
//                    dngCreator.writeImage(imageOutput, reader.acquireLatestImage());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    reader.close();
//                }
//            }
//        }
//    };


    /**
     * buttons that control capture
     */
    private void goCapture() {
        // TODO
    }

    private void setMode() {
        // TODO
    }

    private void goSettings() {
        // TODO
    }


    /**
     * buttons that change capture parameters
     */
    private void setExposureTime() {
        // TODO
    }

    private void setSensitivity() {
        // TODO
    }

    private void setFocusDistance() {
        // TODO
    }

    private void setFocalLength() {
        // TODO
    }

    private void setAperture() {
        // TODO
    }
}
