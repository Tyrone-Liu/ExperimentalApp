package com.tyroneil.longshootalpha;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static Context context;
    static AppCompatActivity activity;
    static float scale;

    // region: android runtime permission request constant
    private static final int PERMISSION_GROUP_REQUEST_CODE_ALL = 0;
    private static final int PERMISSION_GROUP_REQUEST_CODE_CAMERA = 1;
    private static final int PERMISSION_GROUP_REQUEST_CODE_STORAGE = 2;
    private static final int PERMISSION_GROUP_REQUEST_CODE_LOCATION = 3;
    // endregion: android runtime permission request constant

    // region: shared variable
    private static LocationManager locationManager;

    private static CameraManager cameraManager;
    private static String[] cameraIdList;

    private static HandlerThread cameraBackgroundThread;
    static Handler cameraBackgroundHandler;

    static final int CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE = 0;
    static final int CREATE_PREVIEW_STAGE_OPEN_CAMERA = 1;
    private static final int CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION = 2;
    private static final int CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST = 3;
    // endregion: shared variable

    // region: current cameraDevice variable
    private static String cameraId;
    private static CameraDevice cameraDevice;
    private static CameraCharacteristics cameraCharacteristics;

    static CameraCaptureSession captureSession;
    private static int sensorOrientation;
    // endregion: current cameraDevice variable

    // region: variable for preview
    static CaptureRequest.Builder previewRequestBuilder;
    private static Size previewSize;
    // endregion: variable for preview

    // region: variable for capture
    private static Location captureLocation;
    private static CaptureRequest.Builder captureRequestBuilder;
    private static int captureFormat;
    private static Size captureSize;
    private static TotalCaptureResult totalCaptureResult;
    private static ImageReader imageReader;
    private static Date imageTimeStamp;
    private static String imageFileTimeStampName;
    // endregion: variable for capture

    // region: capture parameters
    static int autoMode;  // CONTROL_MODE (0/1 OFF/AUTO)
    static int aeMode;  // CONTROL_AE_MODE (0/1 OFF/ON)

    static long exposureTime;  // SENSOR_EXPOSURE_TIME
    static Range<Long> SENSOR_INFO_EXPOSURE_TIME_RANGE;  // constant for each camera device

    static int sensitivity;  // SENSOR_SENSITIVITY
    static Range<Integer> SENSOR_INFO_SENSITIVITY_RANGE;  // constant for each camera device

    static float aperture;  // LENS_APERTURE
    static float[] LENS_INFO_AVAILABLE_APERTURES;  // constant for each camera device

    static int flashMode;  // FLASH_MODE
    static boolean FLASH_INFO_AVAILABLE;  // constant for each camera device

    static int awbMode;  // CONTROL_AWB_MODE
    static int[] CONTROL_AWB_AVAILABLE_MODES;  // constant for each camera devices

    static int opticalStabilizationMode;  // LENS_OPTICAL_STABILIZATION_MODE
    static int[] LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION;  // constant for each camera device

    static float focalLength;  // LENS_FOCAL_LENGTH
    static float[] LENS_INFO_AVAILABLE_FOCAL_LENGTHS;  // constant for each camera device

    static int afMode;  // CONTROL_AF_MODE (0/4 OFF/CONTINUOUS_PICTURE)
    static float focusDistance;  // LENS_FOCUS_DISTANCE
    static float LENS_INFO_MINIMUM_FOCUS_DISTANCE;  // constant for each camera device
    static float CIRCLE_OF_CONFUSION;  // unit: mm, constant for each camera device

    static Rect SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT;  // coordinate range for zoom and focus assistant
    static Rect SENSOR_INFO_ACTIVE_ARRAY_RECT;  // coordinate range for zoom and focus assistant
    static int SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_WIDTH;  // coordinate range for zoom and focus assistant
    static int SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_HEIGHT;  // coordinate range for zoom and focus assistant
    static int SENSOR_INFO_ACTIVE_ARRAY_RECT_WIDTH;  // coordinate range for zoom and focus assistant
    static int SENSOR_INFO_ACTIVE_ARRAY_RECT_HEIGHT;  // coordinate range for zoom and focus assistant
    static float focusAssistantWidth;
    static float focusAssistantHeight;
    static float focusAssistantWidthCenter;
    static float focusAssistantHeightCenter;
    // endregion: capture parameters

    static TextView errorMessageTextView;

    // region: debug Tool
    static TextView debugMessage0TextView;
    static TextView debugMessage1TextView;
    static String debugMessage = "";
    static String previewDebugMessage = "";
    static String captureDebugMessage = "";
    static int previewDebugCounter = 0;
    static int captureDebugCounter = 0;

    private static String totalResultDebugTool(CaptureRequest request, TotalCaptureResult result) {
        String message = (
                "ET:" + String.format("%.4f", (double) (result.get(CaptureResult.SENSOR_EXPOSURE_TIME) / 1E4) / 1E5) + ", "
                        + "SE:" + result.get(CaptureResult.SENSOR_SENSITIVITY) + ", "
                        + "AP:" + result.get(CaptureResult.LENS_APERTURE) + ", "
                        + "OS:" + result.get(CaptureResult.LENS_OPTICAL_STABILIZATION_MODE) + ", "
                        + "FL:" + result.get(CaptureResult.LENS_FOCAL_LENGTH) + ", "
                        + "\n"
                        + "Request FD:" + String.format("%.5f", request.get(CaptureRequest.LENS_FOCUS_DISTANCE)) + ", "
                        + "Result FD:" + String.format("%.5f", result.get(CaptureResult.LENS_FOCUS_DISTANCE))
                        + "\n"
                        + "CONTROL_MODE: " + result.get(CaptureResult.CONTROL_MODE) + "\n"
                        + "AE_MODE: " + result.get(CaptureResult.CONTROL_AE_MODE) + ", State: " + stateToStringDebugTool("CONTROL_AE_STATE", result.get(CaptureResult.CONTROL_AE_STATE)) + "\n"
                        + "AWB_MODE: " + result.get(CaptureResult.CONTROL_AWB_MODE) + ", State: " + stateToStringDebugTool("CONTROL_AWB_STATE", result.get(CaptureResult.CONTROL_AWB_STATE)) + "\n"
                        + "AF_MODE: " + result.get(CaptureResult.CONTROL_AF_MODE) + ", State: " + stateToStringDebugTool("CONTROL_AF_STATE", result.get(CaptureResult.CONTROL_AF_STATE)) + ", Trigger: " + stateToStringDebugTool("CONTROL_AF_TRIGGER", result.get(CaptureResult.CONTROL_AF_TRIGGER)) + "\n"
        );
        return message;
    }
    private static String stateToStringDebugTool(String key, int state) {
        String message = "";
        switch (key) {
            case "CONTROL_AE_STATE":
                switch (state) {
                    case CaptureResult.CONTROL_AE_STATE_CONVERGED: message = "CONVERGED"; break;
                    case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED: message = "FLASH_REQUIRED"; break;
                    case CaptureResult.CONTROL_AE_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CaptureResult.CONTROL_AE_STATE_LOCKED: message = "LOCKED"; break;
                    case CaptureResult.CONTROL_AE_STATE_PRECAPTURE: message = "PRECAPTURE"; break;
                    case CaptureResult.CONTROL_AE_STATE_SEARCHING: message = "SEARCHING"; break;
                }
                break;
            case "CONTROL_AWB_STATE":
                switch (state) {
                    case CaptureResult.CONTROL_AWB_STATE_CONVERGED: message = "CONVERGED"; break;
                    case CaptureResult.CONTROL_AWB_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CaptureResult.CONTROL_AWB_STATE_LOCKED: message = "LOCKED"; break;
                    case CaptureResult.CONTROL_AWB_STATE_SEARCHING: message = "SEARCHING"; break;
                }
                break;
            case "CONTROL_AF_STATE":
                switch (state) {
                    case CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN: message = "ACTIVE_SCAN"; break;
                    case CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED: message = "FOCUSED_LOCKED"; break;
                    case CaptureResult.CONTROL_AF_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED: message = "NOT_FOCUSED_LOCKED"; break;
                    case CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED: message = "PASSIVE_FOCUSED"; break;
                    case CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN: message = "PASSIVE_SCAN"; break;
                    case CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED: message = "PASSIVE_UNFOCUSED"; break;
                }
                break;
            case "CONTROL_AF_TRIGGER":
                switch (state) {
                    case CaptureResult.CONTROL_AF_TRIGGER_CANCEL: message = "CANCEL"; break;
                    case CaptureResult.CONTROL_AF_TRIGGER_IDLE: message = "IDLE"; break;
                    case CaptureResult.CONTROL_AF_TRIGGER_START: message = "START"; break;
                }
                break;
        }
        return message;
    }

    static SimpleDateFormat debugDateFormat = new SimpleDateFormat("HH.mm.ss.SSS");
    static final String LOG_TAG_LSA_CAPTURE_LAG = "LSA_CAPTURE_LAG";
    static final String LOG_TAG_LSA_DEBUG = "LSA_DEBUG";
    static final String LOG_TAG_LSA_WARN = "LSA_WARN";
    // endregion: debug Tool

    // region: handle activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        activity = this;
        scale = getResources().getDisplayMetrics().density;
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

        errorMessageTextView = (TextView) findViewById(R.id.textView_errorMessage);
        debugMessage0TextView = (TextView) findViewById(R.id.textView_debugMessage_0);  // debug
        debugMessage1TextView = (TextView) findViewById(R.id.textView_debugMessage_1);  // debug

        // region: android runtime permission
        boolean allPermissionGranted = true;
        String[] allPermissionDemanded = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        for (String permission : allPermissionDemanded) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionGranted = false;
            }
        }
        if (! allPermissionGranted) {
            activity.requestPermissions(allPermissionDemanded, PERMISSION_GROUP_REQUEST_CODE_ALL);
        } else {
            UIOperator.initiateContentCameraControl();
            UIOperator.initiateContentRangeControl();
            UIOperator.initiateContentListControl();
        }
        // endregion: android runtime permission
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_GROUP_REQUEST_CODE_ALL) {
            for (String permission : permissions) {
                switch (permission) {
                    case Manifest.permission.CAMERA:
                        if (grantResults[Utility.arrayIndexOf(permissions, permission)] == PackageManager.PERMISSION_GRANTED) {
                            UIOperator.initiateContentCameraControl();
                            UIOperator.initiateContentRangeControl();
                            UIOperator.initiateContentListControl();
                            createPreview(CREATE_PREVIEW_STAGE_OPEN_CAMERA);
                        } else {
                            // TODO: warn user this app is gonna close, because a camera app is pointless without the camera
                        }
                        break;

                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        if (grantResults[Utility.arrayIndexOf(permissions, permission)] != PackageManager.PERMISSION_GRANTED) {
                            // TODO: warn user this function will not work
                        }
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        if (grantResults[Utility.arrayIndexOf(permissions, permission)] != PackageManager.PERMISSION_GRANTED) {
                            // TODO: warn user they can not save the capture image
                            if (UIOperator.captureButton_camera_control != null) {
                                UIOperator.cameraControl_setCaptureButtonEnabled(false);
                            }
                        }
                        break;

                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        if (grantResults[Utility.arrayIndexOf(permissions, permission)] != PackageManager.PERMISSION_GRANTED) {
                            // TODO: warn user they will not have location tag saved in the image file
                        }
                        break;
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        if (grantResults[Utility.arrayIndexOf(permissions, permission)] != PackageManager.PERMISSION_GRANTED) {
                            // TODO: warn user they will not have location tag saved in the image file
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        cameraBackgroundThread = new HandlerThread("CameraBackground");
        cameraBackgroundThread.start();
        cameraBackgroundHandler = new Handler(cameraBackgroundThread.getLooper());

        if (
                UIOperator.previewCRTV_camera_control != null
                && (UIOperator.previewCRTV_camera_control).isAvailable()
        ) {
            createPreview(CREATE_PREVIEW_STAGE_OPEN_CAMERA);
        }
    }

    @Override
    protected void onStop() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        cameraBackgroundThread.quitSafely();
        try {  // if previous statement is using '.quit()', then there is not necessary to use '.join()'
            cameraBackgroundThread.join();
            cameraBackgroundThread = null;
            cameraBackgroundHandler = null;
        } catch (InterruptedException e) {
            displayErrorMessage(e);
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (
                UIOperator.rangeControlBottomSheet != null
                && (UIOperator.rangeControlBottomSheet).getState() != BottomSheetBehavior.STATE_HIDDEN
        ) {
            (UIOperator.rangeControlBottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (
                UIOperator.listControlBottomSheet != null
                && (UIOperator.listControlBottomSheet).getState() != BottomSheetBehavior.STATE_HIDDEN
        ) {
            (UIOperator.listControlBottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
    // endregion: handle activity lifecycle


    // region: process of creating preview
    /**
     * The process to create a preview needs to wait for 'CameraDevice', 'CameraCaptureSession' to
     * callback, so there will be three stages.
     *
     * Stage INITIATE_CAMERA_CANDIDATE: called by onCreate()
     *     Use 'CameraManager' to query a cameraId, set the previewTextureView aspect ratio.
     *
     * Stage OPEN_CAMERA: called when necessary
     *     Open camera using the selected cameraId.
     *
     * Stage CREATE_CAPTURE_SESSION: called by 'CameraDevice.StateCallback.onOpened()' with {@param CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION}.
     *     Use 'CameraDevice.createCaptureRequest()' to create a 'CaptureRequest.Builder'.
     *     Use 'CaptureRequest.Builder.addTarget()' to set TextureView as the output target.
     *     Use 'CameraDevice.createCaptureSession()' to create a 'CameraCaptureSession'.
     *
     * Stage SET_REPEATING_REQUEST: called by 'CameraCaptureSession.StateCallback.onConfigured()' with {@param CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST}.
     *     Use 'CameraCaptureSession.setRepeatingRequest(CaptureRequest.Builder.build())' to
     *     submit 'CaptureRequest' to 'CameraCaptureSession'.
     *
     * @param stage the stage of creating preview (int 0, 1, 2, 3)
     */
    static void createPreview(int stage) {
        // region: CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE
        if (stage == CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE) {
            autoMode = CaptureRequest.CONTROL_MODE_AUTO;
            aeMode = CaptureRequest.CONTROL_AE_MODE_ON;
            afMode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;

            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preference_raw_capture", true)) {
                captureFormat = ImageFormat.RAW_SENSOR;
            } else {
                captureFormat = ImageFormat.JPEG;
            }

            locationManager = context.getSystemService(LocationManager.class);
            cameraManager = (CameraManager) context.getSystemService(CameraManager.class);
            try {
                cameraIdList = cameraManager.getCameraIdList();
                // TODO: make camera changeable in settings
                for (String e : cameraIdList) {
                    if ((cameraManager.getCameraCharacteristics(e)).get(CameraCharacteristics.LENS_FACING) == 1) {
                        cameraId = e;
                        break;
                    }
                    cameraId = cameraIdList[0];
                }
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                initiateCaptureParameters(cameraCharacteristics);
                UIOperator.updateCaptureParametersIndicator();
            } catch (CameraAccessException e) {
                displayErrorMessage(e);
            }

            /**
             * Check the 'SENSOR_ORIENTATION' to set width and height appropriately.
             * (Switch width and height if necessary.  )
             */
            previewSize = chooseOutputSize(cameraCharacteristics, captureFormat, true);
            captureSize = chooseOutputSize(cameraCharacteristics, captureFormat, false);  // find optimal size for capture
            sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == 90 || sensorOrientation == 270) {
                (UIOperator.previewCRTV_camera_control).setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            } else {
                (UIOperator.previewCRTV_camera_control).setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            }
        }
        // endregion: CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE

        // region: CREATE_PREVIEW_STAGE_OPEN_CAMERA
        else if (stage == CREATE_PREVIEW_STAGE_OPEN_CAMERA) {
            // TODO: make 'minTime', 'minDistance' changeable in the settings.  Can choose provider between 'gps' and 'network'
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 30E3, 1.0f, locationListener);
            }

            try {
                cameraManager.openCamera(cameraId, cameraStateCallback, cameraBackgroundHandler);
            } catch (CameraAccessException | SecurityException e) {
                displayErrorMessage(e);
            }
        }
        // endregion: CREATE_PREVIEW_STAGE_OPEN_CAMERA

        // region: CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION
        else if (stage == CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION) {
            // got cameraDevice
            // this stage will also be used to refresh the preview parameters after changed camera device
            SurfaceTexture previewSurfaceTexture = (UIOperator.previewCRTV_camera_control).getSurfaceTexture();
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(previewSurfaceTexture);
            try {
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                initiateFocusAssistant();

                // region: setup preview request builder
                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, autoMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);

                if (aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    if (
                            PreferenceManager.getDefaultSharedPreferences(context).getBoolean("preference_preview_exposure_time_limit", true)
                            && exposureTime > 1E9 * Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("preference_preview_exposure_time_limit_value", "0.5"))
                    ) {
                        previewRequestBuilder.set(
                                CaptureRequest.SENSOR_EXPOSURE_TIME,
                                (long) (1E9 * Double.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("preference_preview_exposure_time_limit_value", "0.5")))
                        );
                    } else {
                        previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                    }
                    previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
                }
                previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);
                previewRequestBuilder.set(CaptureRequest.FLASH_MODE, flashMode);

                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);

                previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);
                previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);
                if (afMode == CaptureRequest.CONTROL_AF_MODE_OFF || autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
                } else {
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
                }
                previewRequestBuilder.addTarget(previewSurface);
                // endregion: setup preview request builder

                // prepare output surface for capture
                imageReader = ImageReader.newInstance(captureSize.getWidth(), captureSize.getHeight(), captureFormat, 1);
                imageReader.setOnImageAvailableListener(onImageAvailableListener, cameraBackgroundHandler);

                // The second item in outputs list is for capture
                // In this case, both the 'repeating request' used by preview and 'capture request'
                // used by capture will be submit to one 'capture session'.  So, the
                // 'capture session' outputs should contain both 'preview surface' and
                // 'image reader'.
                // For each capture request, their format and size are decided by their output
                // surface.
                cameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()), captureSessionStateCallback, cameraBackgroundHandler);
            } catch (CameraAccessException e) {
                displayErrorMessage(e);
            }
        }
        // endregion: CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION

        // region: CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST
        else if (stage == CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST) {  // got captureSession
            try {
                // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
                // previewCaptureCallback is for debug purpose
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), previewCaptureCallback, cameraBackgroundHandler);
            } catch (CameraAccessException e) {
                displayErrorMessage(e);
            }
        }
        // endregion: CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST
    }

    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            captureLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
            captureLocation = null;
        }
    };

    /**
     * Produce a 'CameraDevice', then call 'createPreview(1)'
     */
    private static CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createPreview(CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    /**
     * Produce a 'CameraCaptureSession', then call 'createPreview(2)'
      */
    private static CameraCaptureSession.StateCallback captureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            captureSession = session;
            createPreview(CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            session.close();
            captureSession = null;
        }

        // region: debug
        @Override
        public void onActive(CameraCaptureSession session) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureSessionStateCallback #onActive");
            super.onActive(session);
        }
        @Override
        public void onCaptureQueueEmpty(CameraCaptureSession session) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureSessionStateCallback #onCaptureQueueEmpty");
            super.onCaptureQueueEmpty(session);
        }
        @Override
        public void onReady(CameraCaptureSession session) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureSessionStateCallback #onReady");
            super.onReady(session);
        }
        @Override
        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureSessionStateCallback #onSurfacePrepared");
            super.onSurfacePrepared(session, surface);
        }
        // endregion: debug
    };

    // region: method to choose preview size
    /**
     * First, get the SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE, which is the maximum
     * available size and the output size in RAW_SENSOR format.
     *
     * Second, get the available size in capture format (if it is not RAW_SENSOR (32)), and find
     * the maximum size that has the closest aspect ratio with the sensor pixel array size.
     *
     * Last, in the available size for SurfaceTexture.class format, find an optimized size
     * with the same aspect ratio as the size found in the second step.
     */
    private static Size chooseOutputSize(CameraCharacteristics cameraCharacteristics, int captureFormat, boolean forPreview) {
        final Size SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE = new Size(
                SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.right - SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.left,
                SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.bottom - SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.top
        );
        Size[] outputSizes;
        Size[] outputSizesFiltered;
        if (forPreview) {
            int maxPreviewResolution = 1440;  // TODO: make max preview resolution changeable in settings
            outputSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
            Size[] captureSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(captureFormat);
            Size[] captureSizesFiltered = sizesFilter(captureSizes, SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
            outputSizesFiltered = sizesFilter(outputSizes, captureSizesFiltered[0]);
            for (Size e : outputSizesFiltered) {
                if ((e.getWidth() <= e.getHeight()) && (e.getWidth() <= maxPreviewResolution)) {
                    return e;
                } else if ((e.getWidth() > e.getHeight()) && (e.getHeight() <= maxPreviewResolution)) {
                    return e;
                }
            }
        } else {
            outputSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(captureFormat);
            outputSizesFiltered = sizesFilter(outputSizes, SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
        }
        return outputSizesFiltered[0];
    }

    private static Size[] sizesFilter(Size[] sizes, Size goalSize) {
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

    static Comparator<Size> sizesComparator = new Comparator<Size>() {
        @Override
        public int compare(Size size1, Size size0) {
            return Integer.compare(size0.getWidth() * size0.getHeight(), size1.getWidth() * size1.getHeight());
        }
    };
    // endregion: method to choose preview size

    private static void initiateCaptureParameters(CameraCharacteristics cameraCharacteristics) {
        SENSOR_INFO_EXPOSURE_TIME_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        SENSOR_INFO_SENSITIVITY_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        LENS_INFO_AVAILABLE_APERTURES = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        FLASH_INFO_AVAILABLE = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        CONTROL_AWB_AVAILABLE_MODES = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        LENS_INFO_AVAILABLE_FOCAL_LENGTHS = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        LENS_INFO_MINIMUM_FOCUS_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

        // calculate circle of confusion
        SizeF SENSOR_INFO_PHYSICAL_SIZE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        Size SENSOR_INFO_PIXEL_ARRAY_SIZE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        CIRCLE_OF_CONFUSION = SENSOR_INFO_PHYSICAL_SIZE.getWidth() * (2f / SENSOR_INFO_PIXEL_ARRAY_SIZE.getWidth());
        // calculate circle of confusion

        // coordinate range for zoom and focus assistant
        SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
        SENSOR_INFO_ACTIVE_ARRAY_RECT = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_WIDTH = SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.right - SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.left;
        SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_HEIGHT = SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.bottom - SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT.top;
        SENSOR_INFO_ACTIVE_ARRAY_RECT_WIDTH = SENSOR_INFO_ACTIVE_ARRAY_RECT.right - SENSOR_INFO_ACTIVE_ARRAY_RECT.left;
        SENSOR_INFO_ACTIVE_ARRAY_RECT_HEIGHT = SENSOR_INFO_ACTIVE_ARRAY_RECT.bottom - SENSOR_INFO_ACTIVE_ARRAY_RECT.top;
        // coordinate range for zoom and focus assistant

        if (SENSOR_INFO_EXPOSURE_TIME_RANGE.contains((long) 1E8)) {
            exposureTime = (long) 1E8;  // 0.1s
        } else {
            exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
        }

        if (SENSOR_INFO_SENSITIVITY_RANGE.contains(200)) {
            sensitivity = 200;
        } else if (SENSOR_INFO_SENSITIVITY_RANGE.contains(400)) {
            sensitivity = 400;
        } else {
            sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getLower();
        }

        aperture = LENS_INFO_AVAILABLE_APERTURES[0];
        flashMode = CaptureRequest.FLASH_MODE_OFF;
        awbMode = CaptureRequest.CONTROL_AWB_MODE_AUTO;

        opticalStabilizationMode = 0;
        for (int e : LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION) {
            if (e == 1) {
                opticalStabilizationMode = e;
                break;
            }
        }

        focalLength = LENS_INFO_AVAILABLE_FOCAL_LENGTHS[0];
        focusDistance = 1000f / (((focalLength * focalLength) / (aperture * CIRCLE_OF_CONFUSION)) + focalLength);
    }

    private static void initiateFocusAssistant() {
        float previewCRTV_DP_width = UIOperator.previewCRTV_camera_control.getWidth() / scale;
        float previewCRTV_DP_height = UIOperator.previewCRTV_camera_control.getHeight() / scale;
        if (sensorOrientation == 90 || sensorOrientation == 270) {
            focusAssistantWidth = previewCRTV_DP_height;
            focusAssistantHeight = previewCRTV_DP_width;
        } else {
            focusAssistantWidth = previewCRTV_DP_width;
            focusAssistantHeight = previewCRTV_DP_height;
        }

        if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && (
                previewRequestBuilder.get(CaptureRequest.DISTORTION_CORRECTION_MODE) == null
                || previewRequestBuilder.get(CaptureRequest.DISTORTION_CORRECTION_MODE) != CaptureRequest.DISTORTION_CORRECTION_MODE_OFF
                )
        ) {
            focusAssistantWidthCenter = SENSOR_INFO_ACTIVE_ARRAY_RECT_WIDTH / 2.0f;
            focusAssistantHeightCenter = SENSOR_INFO_ACTIVE_ARRAY_RECT_HEIGHT / 2.0f;
        } else {
            focusAssistantWidthCenter = SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_WIDTH / 2.0f;
            focusAssistantHeightCenter = SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_RECT_HEIGHT / 2.0f;
        }
    }

    // previewCaptureCallback is for debug purpose
    static CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            previewDebugCounter++;
            previewDebugMessage = "# " + previewDebugCounter + " preview completed" + "\n" + totalResultDebugTool(request, result);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage0TextView.setText(previewDebugMessage);
                }
            });
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            previewDebugCounter++;
            previewDebugMessage = "# " + previewDebugCounter + " preview failed" + "\n";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage0TextView.setText(previewDebugMessage);
                }
            });
            super.onCaptureFailed(session, request, failure);
        }
    };

    // endregion: process of creating preview


    // region: process of taking photo(s)
    static void takePhoto() {
        try {
            // TODO: make captureRequest template changeable between 'TEMPLATE_MANUAL' and 'TEMPLATE_STILL_CAPTURE' in settings.
            // 'TEMPLATE_MANUAL' has the least preset parameters, user
            // has the most control over camera.
            // 'TEMPLATE_STILL_CAPTURE' may be better for capture
            // because it is has more complete parameters, but it make
            // problems in capture parameters hard to discover.
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);

            // region: setup capture request builder
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, autoMode);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);

            if (captureFormat == ImageFormat.JPEG) {
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, sensorOrientation);
                captureRequestBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
                if (captureLocation != null) {
                    captureRequestBuilder.set(CaptureRequest.JPEG_GPS_LOCATION, captureLocation);
                }
            }

            if (aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                captureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
            } else {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, true);
            }

            captureRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, flashMode);

            captureRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);
            if (! (awbMode == CaptureRequest.CONTROL_AWB_MODE_OFF || autoMode == CaptureRequest.CONTROL_MODE_OFF)) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, true);
            }

            captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);
            captureRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);

            if (afMode == CaptureRequest.CONTROL_AF_MODE_OFF || autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                captureRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
            } else {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            }
            captureRequestBuilder.addTarget(imageReader.getSurface());
            // endregion: setup capture request builder

            UIOperator.cameraControl_setCaptureButtonEnabled(false);
            captureSession.capture(captureRequestBuilder.build(), captureCallback, cameraBackgroundHandler);
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " capture request submitted");  // debug
        } catch (CameraAccessException e) {
            displayErrorMessage(e);
        }
    }

    private static ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " image available");  // debug
            // TODO: make file name changeable in settings
            imageFileTimeStampName = "yyyy.MM.dd_HH.mm.ss.SSS_Z";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(imageFileTimeStampName);
            Image image = reader.acquireNextImage();
            File imageFile;
            FileOutputStream imageOutputStream = null;
            if (reader.getImageFormat() == ImageFormat.JPEG) {
                // TODO: make save file path changeable, and auto create folder if not exist
                imageFile = new File(
                          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        + "/LongShoot/IMG_" + simpleDateFormat.format(imageTimeStamp) + ".JPG"
                );
                ByteBuffer imageBuffer = image.getPlanes()[0].getBuffer();
                byte[] imageBytes = new byte[imageBuffer.remaining()];
                imageBuffer.get(imageBytes);
                try {
                    imageOutputStream = new FileOutputStream(imageFile);
                    imageOutputStream.write(imageBytes);
                } catch (IOException e) {
                    displayErrorMessage(e);
                } finally {
                    image.close();
                    if (imageOutputStream != null) {
                        try {imageOutputStream.close();}
                        catch (IOException e) {displayErrorMessage(e);}
                        UIOperator.cameraControl_setCaptureButtonEnabled(true);
                    }
                }
            }

            else if (reader.getImageFormat() == ImageFormat.RAW_SENSOR) {
                imageFile = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                + "/LongShoot/IMG_" + simpleDateFormat.format(imageTimeStamp) + ".DNG"
                );

                if (image.getTimestamp() != totalCaptureResult.get(CaptureResult.SENSOR_TIMESTAMP)) {
                    Log.d(LOG_TAG_LSA_WARN, "image and totalCaptureResult timestamp mismatch");
                }
                DngCreator dngCreator = new DngCreator(cameraCharacteristics, totalCaptureResult);

                if (sensorOrientation != 0) {
                    switch (sensorOrientation) {
                        case 90: dngCreator.setOrientation(ExifInterface.ORIENTATION_ROTATE_90); break;
                        case 180: dngCreator.setOrientation(ExifInterface.ORIENTATION_ROTATE_180); break;
                        case 270: dngCreator.setOrientation(ExifInterface.ORIENTATION_ROTATE_270); break;
                    }
                }
                if (captureLocation != null) {
                    dngCreator.setLocation(captureLocation);
                }

                try {
                    imageOutputStream = new FileOutputStream(imageFile);
                    dngCreator.writeImage(imageOutputStream, image);
                } catch (IOException e) {
                    displayErrorMessage(e);
                } finally {
                    image.close();
                    dngCreator.close();
                    if (imageOutputStream != null) {
                        try {imageOutputStream.close();}
                        catch (IOException e) {displayErrorMessage(e);}
                        UIOperator.cameraControl_setCaptureButtonEnabled(true);
                    }
                }
            }
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " image saved");  // debug

        }
    };

    // the purpose of captureCallback is to show capture progress indicator
    private static CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureCallback #onCaptureCompleted");  // debug
            // region: debug in captureCallback
            captureDebugCounter++;
            captureDebugMessage = "# " + captureDebugCounter + " capture completed" + "\n" + totalResultDebugTool(request, result);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage1TextView.setText(captureDebugMessage);
                }
            });
            // endregion: debug in captureCallback
            if (cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE) == CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN) {
                imageTimeStamp = new Date();
            } else {
                imageTimeStamp = new Date(System.currentTimeMillis() - ((long) ((double) (result.get(CaptureResult.SENSOR_TIMESTAMP) - SystemClock.elapsedRealtimeNanos()) / 1E6)));
            }
            totalCaptureResult = result;
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            // region: debug in captureCallback
            captureDebugCounter++;
            captureDebugMessage = "# " + captureDebugCounter + " capture failed" + "\n";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage1TextView.setText(captureDebugMessage);
                }
            });
            // endregion: debug in captureCallback
            super.onCaptureFailed(session, request, failure);
        }

        // region: debug
        @Override
        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureCallback #onCaptureBufferLost");  // debug
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureCallback #onCaptureProgressed");  // debug
            super.onCaptureProgressed(session, request, partialResult);
        }
        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            Log.d(LOG_TAG_LSA_CAPTURE_LAG, "  #" + debugDateFormat.format(new Date()) + " captureCallback #onCaptureSequenceCompleted");  // debug
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }
        // endregion: debug
    };
    // endregion: process of taking photo(s)


    static void displayErrorMessage(final Exception error) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorMessageTextView.setBackgroundResource(R.color.colorSurface);
                errorMessageTextView.setText(error.toString());
            }
        });
    }
}
