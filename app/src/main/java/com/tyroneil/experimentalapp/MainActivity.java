package com.tyroneil.experimentalapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.text.InputType;
import android.util.Range;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends Activity implements View.OnClickListener {
    // widgets in content_main
    private ChangeableRatioTextureView previewTextureView;
    private Button captureButton, modeButton, settingsButton;
    private Button exposureTimeButton, sensitivityButton, focusDistanceButton, focalLengthButton, apertureButton;

    // widgets in content_main_bottom_sheet_seek_bar
    private BottomSheetBehavior seekBarBottomSheet;

    private SeekBar seekBar;
    private Button applyButton;
    private CheckBox autoCheckBox;
    private EditText valueEditText;
    final int EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME = 0;
    final int EDIT_TEXT_VALUE_TYPE_SENSITIVITY = 1;
    final int EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE = 2;

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
    private final int AUTO_MODE_OFF = 0;
    private final int AUTO_MODE_AUTO = 1;

    private int aeMode;  // CONTROL_AE_MODE (0/1 OFF/ON)
    private final int AE_MODE_OFF = 0;
    private final int AE_MODE_ON = 1;

    private int afMode;  // CONTROL_AF_MODE (0/4 OFF/CONTINUOUS_PICTURE)
    private final int AF_MODE_OFF = 0;
    private final int AF_MODE_CONTINUOUS_PICTURE = 4;

    private int awbMode;  // CONTROL_AWB_MODE (0/1 OFF/AUTO)
    private final int AWB_MODE_OFF = 0;
    private final int AWB_MODE_AUTO = 1;

    private long exposureTime;  // SENSOR_EXPOSURE_TIME
    Range<Long> SENSOR_INFO_EXPOSURE_TIME_RANGE;  // constant for each camera device

    private int sensitivity;  // SENSOR_SENSITIVITY
    Range<Integer> SENSOR_INFO_SENSITIVITY_RANGE;  // constant for each camera device

    private float focusDistance;  // LENS_FOCUS_DISTANCE
    float LENS_INFO_HYPERFOCAL_DISTANCE;  // constant for each camera device
    float LENS_INFO_MINIMUM_FOCUS_DISTANCE;  // constant for each camera device

    private float focalLength;  // LENS_FOCAL_LENGTH
    float[] LENS_INFO_AVAILABLE_FOCAL_LENGTHS;  // constant for each camera device

    private float aperture;  // LENS_APERTURE
    float[] LENS_INFO_AVAILABLE_APERTURES;  // constant for each camera device

    private int opticalStabilizationMode;  // LENS_OPTICAL_STABILIZATION_MODE
    int[] LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION;  // constant for each camera device

    // debug Tool
    private TextView errorMessageTextView;

    private TextView debugMessageTextView;
    private String debugMessage = "";
    private int debugCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateContentMain();
        initiateContentMainBottomSheetSeekBar();

        errorMessageTextView = (TextView) findViewById(R.id.errorMessageTextView);
        debugMessageTextView = (TextView) findViewById(R.id.debugMessageTextView);  // debug
    }

    private void initiateContentMain() {
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
        captureButton.setOnClickListener(this);
        modeButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);

        exposureTimeButton.setOnClickListener(this);
        sensitivityButton.setOnClickListener(this);
        focusDistanceButton.setOnClickListener(this);
        focalLengthButton.setOnClickListener(this);
        apertureButton.setOnClickListener(this);
    }

    private void initiateContentMainBottomSheetSeekBar() {
        seekBarBottomSheet = BottomSheetBehavior.from(findViewById(R.id.seekBarBottomSheet));

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        applyButton = (Button) findViewById(R.id.apply);
        autoCheckBox = (CheckBox) findViewById(R.id.auto);
        valueEditText = (EditText) findViewById(R.id.value);

        seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        seekBarBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(bottomSheet.getWindowToken(), 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (seekBarBottomSheet.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
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
        autoMode = AUTO_MODE_AUTO;
        aeMode = AE_MODE_ON;
        afMode = AF_MODE_CONTINUOUS_PICTURE;
        awbMode = AWB_MODE_AUTO;

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
                errorMessageTextView.setBackgroundResource(R.color.colorSurface);
                errorMessageTextView.setText(e.toString());
            }

            try {
                cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler);
            } catch (CameraAccessException | SecurityException e) {
                errorMessageTextView.setBackgroundResource(R.color.colorSurface);
                errorMessageTextView.setText(e.toString());
            }
        } else if (stage == 1) {
            // got cameraDevice
            // this stage will also be used to refresh the preview parameters after changed camera device
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

                if (aeMode == AE_MODE_OFF || autoMode == AUTO_MODE_OFF) {
                    previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
                    previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
                }
                if (afMode == AF_MODE_OFF || autoMode == AUTO_MODE_OFF) {
                    previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
                }
                previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);
                previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);

                previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);

                previewRequestBuilder.addTarget(previewSurface);
                cameraDevice.createCaptureSession(Arrays.asList(previewSurface), captureSessionStateCallback, backgroundHandler);
            } catch (CameraAccessException e) {
                errorMessageTextView.setBackgroundResource(R.color.colorSurface);
                errorMessageTextView.setText(e.toString());
            }
        } else if (stage == 2) {  // got captureSession
            try {
                // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                updateCaptureParametersIndicator();
            } catch (CameraAccessException e) {
                errorMessageTextView.setBackgroundResource(R.color.colorSurface);
                errorMessageTextView.setText(e.toString());
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
        int maxPreviewResolution = 1080;  // TODO: make max preview resolution changeable in settings
        Size[] previewSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
        Size[] previewSizesFiltered;
        if (captureFormat == 32) {
            previewSizesFiltered = sizesFilter(previewSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
        } else {
            Size[] captureSizes = (cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(captureFormat);
            Size[] captureSizesFiltered = sizesFilter(captureSizes, SENSOR_INFO_PIXEL_ARRAY_SIZE);
            previewSizesFiltered = sizesFilter(previewSizes, captureSizesFiltered[0]);
        }
        for (Size e : previewSizesFiltered) {
            if ((e.getWidth() <= e.getHeight()) && (e.getWidth() <= maxPreviewResolution)) {
                return e;
            } else if ((e.getWidth() > e.getHeight()) && (e.getHeight() <= maxPreviewResolution)) {
                return e;
            }
        }
        return previewSizesFiltered[previewSizesFiltered.length - 1];
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


    private void initiateCaptureParameters(CameraCharacteristics cameraCharacteristics) {
        SENSOR_INFO_EXPOSURE_TIME_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
        SENSOR_INFO_SENSITIVITY_RANGE = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        LENS_INFO_HYPERFOCAL_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
        LENS_INFO_MINIMUM_FOCUS_DISTANCE = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        LENS_INFO_AVAILABLE_FOCAL_LENGTHS = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        LENS_INFO_AVAILABLE_APERTURES = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);

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

        focusDistance = LENS_INFO_HYPERFOCAL_DISTANCE;
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

    private void updateCaptureParametersIndicator() {
        if (aeMode == AE_MODE_OFF || autoMode == AUTO_MODE_OFF) {
            exposureTimeButton.setText("S.S.\n" + String.format("%.2f", (double) (exposureTime / 1000000) / 1000) + "s");
            sensitivityButton.setText("ISO\n" + sensitivity);
        } else {
            exposureTimeButton.setText("S.S.\nAUTO");
            sensitivityButton.setText("ISO\nAUTO");
        }

        if (afMode == AF_MODE_OFF || autoMode == AUTO_MODE_OFF) {
            if (focusDistance == 0.0f) {
                focusDistanceButton.setText("F.D.\n" + "Infinity");
            } else {
                focusDistanceButton.setText("F.D.\n" + String.format("%.2f", 1 / focusDistance) + "m");
            }
        } else {
            focusDistanceButton.setText("F.D.\nAUTO");
        }

        focalLengthButton.setText("F.L.\n" + String.format("%.2f", focalLength) + "mm");
        apertureButton.setText("APE\nf/" + String.format("%.1f", aperture));
    }


    private void updatePreviewParameters() {
        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, autoMode);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, afMode);
        previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awbMode);

        if (aeMode == AE_MODE_OFF || autoMode == AUTO_MODE_OFF) {
            previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTime);
            previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
        }
        if (afMode == AF_MODE_OFF || autoMode == AUTO_MODE_OFF) {
            previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
        }
        previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, focalLength);
        previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, aperture);

        previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, opticalStabilizationMode);

        try {
            captureSession.stopRepeating();
            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            errorMessageTextView.setBackgroundResource(R.color.colorSurface);
            errorMessageTextView.setText(e.toString());
        }
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


    @Override
    public void onClick(View view) {
        if (  // buttons that control capture
                (((Button) view).getId() == R.id.capture)
                || (((Button) view).getId() == R.id.mode)
                || (((Button) view).getId() == R.id.settings)
        ) {
            // TODO
        }

        else if (  // buttons that change capture parameters
                (((Button) view).getId() == R.id.exposureTime)
                || (((Button) view).getId() == R.id.sensitivity)
                || (((Button) view).getId() == R.id.focusDistance)
        ) {
            seekBarBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            valueEditText.setText("");

            if ((((Button) view).getId() == R.id.exposureTime) || (((Button) view).getId() == R.id.sensitivity)) {
                if (aeMode == AE_MODE_OFF || autoMode == AUTO_MODE_OFF) {
                    seekBarBottomSheet_setAutoChecked(false);
                } else {
                    seekBarBottomSheet_setAutoChecked(true);
                }
                autoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                aeMode = AE_MODE_ON;
                            } else {
                                aeMode = AE_MODE_OFF;
                            }
                            seekBarBottomSheet_setAutoChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                if (((Button) view).getId() == R.id.exposureTime) {
                    valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    valueEditText.setHint("S.S. " + String.format("%.4f", (double) (exposureTime / 10000) / 100000) + " s");
                    valueEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText.getText()).toString()).equals("")) {
                                    seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME);
                                    return true;
                                } else if (((valueEditText.getText()).toString()).equals("")) {
                                    seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    applyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (! ((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME);
                            } else if (((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    long progressLeftLength = exposureTime - SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    final long progressLength = SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    seekBar.setProgress((int) (seekBar.getMax() * ((double) progressLeftLength / progressLength)) + seekBar.getMin());
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                exposureTime = (
                                        (long) (progressLength * ((double) (progress - seekBar.getMin()) / (seekBar.getMax() - seekBar.getMin())))
                                        + SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                                );
                                if (exposureTime < SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                                    exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                                } else if (exposureTime > SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                                    exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
                                }
                                valueEditText.setHint("S.S. " + String.format("%.4f", (double) (exposureTime / 10000) / 100000) + " s");
                                updateCaptureParametersIndicator();
                                updatePreviewParameters();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });

                } else if (((Button) view).getId() == R.id.sensitivity) {
                    valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    valueEditText.setHint("ISO " + sensitivity);
                    valueEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText.getText()).toString()).equals("")) {
                                    seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_SENSITIVITY);
                                    return true;
                                } else if (((valueEditText.getText()).toString()).equals("")) {
                                    seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    applyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (! ((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_SENSITIVITY);
                            } else if (((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    long progressLeftLength = sensitivity - SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    final long progressLength = SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    seekBar.setProgress((int) (seekBar.getMax() * ((float) progressLeftLength / progressLength)) + seekBar.getMin());
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                sensitivity = (
                                        (int) (progressLength * ((float) (progress - seekBar.getMin()) / (seekBar.getMax() - seekBar.getMin())))
                                        + SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                                );
                                if (sensitivity < SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                                    sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                                } else if (sensitivity > SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                                    sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
                                }
                                valueEditText.setHint("ISO " + sensitivity);
                                updateCaptureParametersIndicator();
                                updatePreviewParameters();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                }

            } else if (((Button) view).getId() == R.id.focusDistance) {
                if (afMode == AF_MODE_OFF || autoMode == AUTO_MODE_OFF) {
                    seekBarBottomSheet_setAutoChecked(false);
                } else {
                    seekBarBottomSheet_setAutoChecked(true);
                }
                autoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                afMode = AF_MODE_CONTINUOUS_PICTURE;
                            } else {
                                afMode = AF_MODE_OFF;
                            }
                            seekBarBottomSheet_setAutoChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (focusDistance == 0.0f) {
                    valueEditText.setHint("F.D. Infinity m");
                } else {
                    valueEditText.setHint("F.D. " + String.format("%.4f", 1 / focusDistance) + " m");
                }
                valueEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (! ((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE);
                                return true;
                            } else if (((valueEditText.getText()).toString()).equals("")) {
                                seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                return true;
                            }
                        }
                        return false;
                    }
                });

                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (! ((valueEditText.getText()).toString()).equals("")) {
                            seekBarBottomSheet_applyEditTextValue(EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE);
                        } else if (((valueEditText.getText()).toString()).equals("")) {
                            seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                });

                //  TODO: present focus distance better on the seek bar
                float progressLeftLength = LENS_INFO_MINIMUM_FOCUS_DISTANCE - focusDistance;
                seekBar.setProgress((int) (seekBar.getMax() * (progressLeftLength / LENS_INFO_MINIMUM_FOCUS_DISTANCE)) + seekBar.getMin());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            focusDistance = LENS_INFO_MINIMUM_FOCUS_DISTANCE * ((float) (seekBar.getMax() - progress) / (seekBar.getMax() - seekBar.getMin()));
                            if (focusDistance < 0.0f) {
                                focusDistance = 0.0f;
                            } else if (focusDistance > LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                                focusDistance = LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                            }
                            if (focusDistance == 0.0f) {
                                valueEditText.setHint("F.D. Infinity m");
                            } else {
                                valueEditText.setHint("F.D. " + String.format("%.4f", 1 / focusDistance) + " m");
                            }
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
        }

        else if (  // buttons that change capture parameters
                (((Button) view).getId() == R.id.focalLength)
                || (((Button) view).getId() == R.id.aperture)
        ) {
            // TODO
        }
    }

    private void seekBarBottomSheet_setAutoChecked(boolean isChecked) {
        if (isChecked) {
            autoCheckBox.setChecked(true);
            seekBar.setEnabled(false);
            applyButton.setEnabled(false);
            valueEditText.setEnabled(false);
        } else {
            autoCheckBox.setChecked(false);
            seekBar.setEnabled(true);
            applyButton.setEnabled(true);
            valueEditText.setEnabled(true);
        }
    }

    private void seekBarBottomSheet_applyEditTextValue(int editTextValueType) {
        if (editTextValueType == EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME) {
            double rawValue = Double.valueOf((valueEditText.getText()).toString());
            exposureTime = (long) (rawValue * 1000000000L);
            if (exposureTime < SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
            } else if (exposureTime > SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                exposureTime = SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
            }

        } else if (editTextValueType == EDIT_TEXT_VALUE_TYPE_SENSITIVITY) {
            sensitivity = Integer.valueOf((valueEditText.getText()).toString());
            if (sensitivity < SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getLower();
            } else if (sensitivity > SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                sensitivity = SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
            }

        } else if (editTextValueType == EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE) {
            float rawValue = Float.valueOf((valueEditText.getText()).toString());
            focusDistance = 1 / rawValue;
            if (focusDistance < 0.0f) {
                focusDistance = 0.0f;
            } else if (focusDistance > LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                focusDistance = LENS_INFO_MINIMUM_FOCUS_DISTANCE;
            }
        }

        seekBarBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        updateCaptureParametersIndicator();
        updatePreviewParameters();
    }
}
