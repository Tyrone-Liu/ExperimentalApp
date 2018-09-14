package com.tyroneil.longshootalpha;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends Activity {
    private static Context context;
    static Activity activity;
    static float scale;

    // region shared variable
    private static CameraManager cameraManager;
    private static String[] cameraIdList;

    static HandlerThread cameraBackgroundThread;
    static Handler cameraBackgroundHandler;

    static final int CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE = 0;
    static final int CREATE_PREVIEW_STAGE_OPEN_CAMERA = 1;
    static final int CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION = 2;
    static final int CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST = 3;
    // endregion

    // region current cameraDevice variable
    private static String cameraId;
    private static CameraDevice cameraDevice;
    private static CameraCharacteristics cameraCharacteristics;

    static CameraCaptureSession captureSession;
    private static int sensorOrientation;
    // endregion

    // region variable for preview
    static CaptureRequest.Builder previewRequestBuilder;
    private static Size previewSize;
    // endregion

    // region variable for capture
    static CaptureRequest.Builder captureRequestBuilder;
    private CaptureResult captureResult;
    private static ImageReader imageReader;
    private DngCreator dngCreator;
    private static int captureFormat;
    private static Size captureSize;
    // endregion

    // region capture parameters
    static int autoMode;  // CONTROL_MODE (0/1 OFF/AUTO)
    static int aeMode;  // CONTROL_AE_MODE (0/1 OFF/ON)

    static long exposureTime;  // SENSOR_EXPOSURE_TIME
    static Range<Long> SENSOR_INFO_EXPOSURE_TIME_RANGE;  // constant for each camera device

    static int sensitivity;  // SENSOR_SENSITIVITY
    static Range<Integer> SENSOR_INFO_SENSITIVITY_RANGE;  // constant for each camera device

    static float aperture;  // LENS_APERTURE
    static float[] LENS_INFO_AVAILABLE_APERTURES;  // constant for each camera device

    static int awbMode;  // CONTROL_AWB_MODE
    static int[] CONTROL_AWB_AVAILABLE_MODES;  // constant for each camera devices

    static int opticalStabilizationMode;  // LENS_OPTICAL_STABILIZATION_MODE
    static int[] LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION;  // constant for each camera device

    static float focalLength;  // LENS_FOCAL_LENGTH
    static float[] LENS_INFO_AVAILABLE_FOCAL_LENGTHS;  // constant for each camera device

    static int afMode;  // CONTROL_AF_MODE (0/4 OFF/CONTINUOUS_PICTURE)
    static float focusDistance;  // LENS_FOCUS_DISTANCE
    static float LENS_INFO_HYPERFOCAL_DISTANCE;  // constant for each camera device
    static float LENS_INFO_MINIMUM_FOCUS_DISTANCE;  // constant for each camera device
    // endregion

    static TextView errorMessageTextView;

    // region debug Tool
    static TextView debugMessage0TextView;
    static TextView debugMessage1TextView;
    static String debugMessage = "";
    static String previewDebugMessage = "";
    static String captureDebugMessage = "";
    static int debugCounter0 = 0;
    static int debugCounter1 = 0;

    private static String totalResultDebugTool(TotalCaptureResult result) {
        String message = (
                "ET:" + String.format("%.4f", (double) (result.get(CaptureResult.SENSOR_EXPOSURE_TIME) / 10000) / 100000) + ", "
                        + "SE:" + result.get(CaptureResult.SENSOR_SENSITIVITY) + ", "
                        + "AP:" + result.get(CaptureResult.LENS_APERTURE) + ", "
                        + "OS:" + result.get(CaptureResult.LENS_OPTICAL_STABILIZATION_MODE) + ", "
                        + "FL:" + result.get(CaptureResult.LENS_FOCAL_LENGTH) + ", "
                        + "FD:" + String.format("%.3f", result.get(CaptureResult.LENS_FOCUS_DISTANCE)) + ", "
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
                    case CameraMetadata.CONTROL_AE_STATE_CONVERGED: message = "CONVERGED"; break;
                    case CameraMetadata.CONTROL_AE_STATE_FLASH_REQUIRED: message = "FLASH_REQUIRED"; break;
                    case CameraMetadata.CONTROL_AE_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CameraMetadata.CONTROL_AE_STATE_LOCKED: message = "LOCKED"; break;
                    case CameraMetadata.CONTROL_AE_STATE_PRECAPTURE: message = "PRECAPTURE"; break;
                    case CameraMetadata.CONTROL_AE_STATE_SEARCHING: message = "SEARCHING"; break;
                }
                break;
            case "CONTROL_AWB_STATE":
                switch (state) {
                    case CameraMetadata.CONTROL_AWB_STATE_CONVERGED: message = "CONVERGED"; break;
                    case CameraMetadata.CONTROL_AWB_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CameraMetadata.CONTROL_AWB_STATE_LOCKED: message = "LOCKED"; break;
                    case CameraMetadata.CONTROL_AWB_STATE_SEARCHING: message = "SEARCHING"; break;
                }
                break;
            case "CONTROL_AF_STATE":
                switch (state) {
                    case CameraMetadata.CONTROL_AF_STATE_ACTIVE_SCAN: message = "ACTIVE_SCAN"; break;
                    case CameraMetadata.CONTROL_AF_STATE_FOCUSED_LOCKED: message = "FOCUSED_LOCKED"; break;
                    case CameraMetadata.CONTROL_AF_STATE_INACTIVE: message = "INACTIVE"; break;
                    case CameraMetadata.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED: message = "NOT_FOCUSED_LOCKED"; break;
                    case CameraMetadata.CONTROL_AF_STATE_PASSIVE_FOCUSED: message = "PASSIVE_FOCUSED"; break;
                    case CameraMetadata.CONTROL_AF_STATE_PASSIVE_SCAN: message = "PASSIVE_SCAN"; break;
                    case CameraMetadata.CONTROL_AF_STATE_PASSIVE_UNFOCUSED: message = "PASSIVE_UNFOCUSED"; break;
                }
                break;
            case "CONTROL_AF_TRIGGER":
                switch (state) {
                    case CameraMetadata.CONTROL_AF_TRIGGER_CANCEL: message = "CANCEL"; break;
                    case CameraMetadata.CONTROL_AF_TRIGGER_IDLE: message = "IDLE"; break;
                    case CameraMetadata.CONTROL_AF_TRIGGER_START: message = "START"; break;
                }
                break;
        }
        return message;
    }
    // endregion

    // region handle activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        MainActivity.activity = this;
        scale = getResources().getDisplayMetrics().density;

        UIOperator.initiateContentCameraControl();
        UIOperator.initiateContentRangeControl();
        UIOperator.initiateContentListControl();

        errorMessageTextView = (TextView) findViewById(R.id.textView_errorMessage);
        debugMessage0TextView = (TextView) findViewById(R.id.textView_debugMessage_0);  // debug
    }

    @Override
    protected void onStart() {
        super.onStart();

        cameraBackgroundThread = new HandlerThread("CameraBackground");
        cameraBackgroundThread.start();
        cameraBackgroundHandler = new Handler(cameraBackgroundThread.getLooper());

        if ((UIOperator.previewCRTV_camera_control).isAvailable()) {
            createPreview(CREATE_PREVIEW_STAGE_OPEN_CAMERA);
        }
    }

    @Override
    protected void onStop() {
        captureSession.close();
        captureSession = null;
        cameraDevice.close();
        cameraDevice = null;

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
        if ((UIOperator.rangeControlBottomSheet).getState() != BottomSheetBehavior.STATE_HIDDEN) {
            (UIOperator.rangeControlBottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if ((UIOperator.listControlBottomSheet).getState() != BottomSheetBehavior.STATE_HIDDEN) {
            (UIOperator.listControlBottomSheet).setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
    // endregion handle activity lifecycle


    // region process of creating preview
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
        if (stage == CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE) {
            autoMode = CameraMetadata.CONTROL_MODE_AUTO;
            aeMode = CameraMetadata.CONTROL_AE_MODE_ON;
            afMode = CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE;

            // TODO: make capture format changeable in settings, then make this default RAW_SENSOR
            captureFormat = ImageFormat.JPEG;

            try {
                cameraManager = (CameraManager) MainActivity.context.getSystemService(CameraManager.class);
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

        else if (stage == CREATE_PREVIEW_STAGE_OPEN_CAMERA) {
            try {
                cameraManager.openCamera(cameraId, cameraStateCallback, cameraBackgroundHandler);
            } catch (CameraAccessException | SecurityException e) {
                displayErrorMessage(e);
            }
        }

        else if (stage == CREATE_PREVIEW_STAGE_CREATE_CAPTURE_SESSION) {
            // got cameraDevice
            // this stage will also be used to refresh the preview parameters after changed camera device
            SurfaceTexture previewSurfaceTexture = (UIOperator.previewCRTV_camera_control).getSurfaceTexture();
            previewSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(previewSurfaceTexture);
            try {
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);

                previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, autoMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);

                if (aeMode == CameraMetadata.CONTROL_AE_MODE_OFF || autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                    previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                    previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
                }
                previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);
                previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);
                previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);
                if (afMode == CameraMetadata.CONTROL_AF_MODE_OFF || autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                    previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
                }
                previewRequestBuilder.addTarget(previewSurface);

                // prepare output surface for capture
                imageReader = ImageReader.newInstance(captureSize.getWidth(), captureSize.getHeight(), captureFormat, 1);
                // the second item in outputs list is for capture
                cameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()), captureSessionStateCallback, cameraBackgroundHandler);
            } catch (CameraAccessException e) {
                displayErrorMessage(e);
            }
        }

        else if (stage == CREATE_PREVIEW_STAGE_SET_REPEATING_REQUEST) {  // got captureSession
            try {
                // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
                // previewCaptureCallback is for debug purpose
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), previewCaptureCallback, cameraBackgroundHandler);
            } catch (CameraAccessException e) {
                displayErrorMessage(e);
            }
        }
    }

    // previewCaptureCallback is for debug purpose
    static CameraCaptureSession.CaptureCallback previewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            debugCounter0 ++;
            previewDebugMessage = "# " + debugCounter0 + " preview completed" + "\n" + totalResultDebugTool(result);
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
            debugCounter0 ++;
            previewDebugMessage = "# " + debugCounter0 + " preview failed" + "\n";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage0TextView.setText(previewDebugMessage);
                }
            });
            super.onCaptureFailed(session, request, failure);
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
    };

    // region method to choose preview size
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
    private static Size chooseOutputSize(CameraCharacteristics cameraCharacteristics, int captureFormat, boolean forPreview) {
        final Size SENSOR_INFO_PIXEL_ARRAY_SIZE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        Size[] outputSizes;
        Size[] outputSizesFiltered;
        if (forPreview) {
            int maxPreviewResolution = 1440;  // TODO: make max preview resolution changeable in settings
            outputSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
            Size[] captureSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(captureFormat);
            Size[] captureSizesFiltered = sizesFilter(captureSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
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
            outputSizesFiltered = sizesFilter(outputSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
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
    // endregion

    private static void initiateCaptureParameters(CameraCharacteristics cameraCharacteristics) {
        SENSOR_INFO_EXPOSURE_TIME_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        SENSOR_INFO_SENSITIVITY_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        LENS_INFO_AVAILABLE_APERTURES = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        CONTROL_AWB_AVAILABLE_MODES = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        LENS_INFO_AVAILABLE_FOCAL_LENGTHS = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        LENS_INFO_HYPERFOCAL_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
        LENS_INFO_MINIMUM_FOCUS_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

        if (SENSOR_INFO_EXPOSURE_TIME_RANGE.contains(100000000L)) {
            exposureTime = 100000000L;  // 0.1s
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
        awbMode = CameraMetadata.CONTROL_AWB_MODE_AUTO;

        opticalStabilizationMode = 0;
        for (int e : LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION) {
            if (e == 1) {
                opticalStabilizationMode = e;
                break;
            }
        }

        focalLength = LENS_INFO_AVAILABLE_FOCAL_LENGTHS[0];
        focusDistance = LENS_INFO_HYPERFOCAL_DISTANCE;
    }
    // endregion

    // region process of taking photo(s)
    static void takePhoto() {
        try {  // TODO: if any auto mode is on, lock result state first!  Or capture with auto mode may result strangely
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL);
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF_KEEP_STATE);

            if (aeMode == CameraMetadata.CONTROL_AE_MODE_OFF || autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                captureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
            } else {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, true);
            }

            captureRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);

            if (!(awbMode == CameraMetadata.CONTROL_AWB_MODE_OFF || autoMode == CameraMetadata.CONTROL_MODE_OFF)) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, true);
            }

            captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);
            captureRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);

            if (afMode == CameraMetadata.CONTROL_AF_MODE_OFF || autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                captureRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
            } else {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            }
            captureRequestBuilder.addTarget(imageReader.getSurface());

            captureSession.capture(captureRequestBuilder.build(), captureCallback, cameraBackgroundHandler);
        } catch (CameraAccessException e) {
            displayErrorMessage(e);
        }
    }

    private static CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            // region debug in captureCallback
            debugCounter1 ++;
            captureDebugMessage = "# " + debugCounter1 + " capture completed" + "\n" + totalResultDebugTool(result);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage1TextView.setText(captureDebugMessage);
                }
            });
            // endregion debug in captureCallback
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            // region debug in captureCallback
            debugCounter1 ++;
            captureDebugMessage = "# " + debugCounter1 + " capture completed" + "\n";
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    debugMessage1TextView.setText(captureDebugMessage);
                }
            });
            // endregion debug in captureCallback
            super.onCaptureFailed(session, request, failure);
        }
    };
    // endregion

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
