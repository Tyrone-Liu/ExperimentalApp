package com.tyroneil.longshootalpha;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class UIOperator {
    // region content camera control
    static ChangeableRatioTextureView previewCRTV_camera_control;
    static Button modeButton_camera_control, captureButton_camera_control, settingsButton_camera_control;
    static ProgressBar capturingProgressBar_camera_control;
    // endregion

    // region content capture parameters indicator
    static Button
            setExposureTimeButton_parameters_indicator,
            setSensitivityButton_parameters_indicator,
            setApertureButton_parameters_indicator,
            setAutoWhiteBalance_parameters_indicator,
            setOpticalStabilization_parameters_indicator,
            setFocalLengthButton_parameters_indicator,
            setFocusDistanceButton_parameters_indicator;
    // endregion

    // region content capture parameter range control
    static BottomSheetBehavior rangeControlBottomSheet;
    static TextView titleTextView_range_control, informationTextView_range_control, valueMinimumTextView_range_control, valueMaximumTextView_range_control;
    static SeekBar rangeSeekBar_range_control;
    static CheckBox autoCheckBox_range_control;
    static EditText valueEditText_range_control;
    static Button applyButton_range_control;
    static final int RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_EXPOSURE_TIME = 0;
    static final int RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_SENSITIVITY = 1;
    static final int RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_FOCUS_DISTANCE = 2;
    // endregion

    // region content capture parameter list control
    static BottomSheetBehavior listControlBottomSheet;
    static TextView titleTextView_list_control;
    static RadioGroup listRadioGroup_list_control;
    static Button dismissButton_list_control;
    static final int LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES = 0;
    static final int LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES = 1;
    // endregion

    // region initiate layouts (camera_control, range_control, list_control)
    static void initiateContentCameraControl() {
        previewCRTV_camera_control = (ChangeableRatioTextureView) MainActivity.activity.findViewById(R.id.cRTV_camera_control_preview);
        modeButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_mode);
        captureButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_capture);
        settingsButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_settings);
        capturingProgressBar_camera_control = (ProgressBar) MainActivity.activity.findViewById(R.id.progressBar_camera_control_capturing);

        // content parameters indicator
        setExposureTimeButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setExposureTime);
        setSensitivityButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setSensitivity);
        setApertureButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAperture);
        setAutoWhiteBalance_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAutoWhiteBalance);
        setOpticalStabilization_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setOpticalStabilization);
        setFocalLengthButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocalLength);
        setFocusDistanceButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocusDistance);

        MainActivity.createPreview(MainActivity.CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE);
        previewCRTV_camera_control.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                MainActivity.createPreview(MainActivity.CREATE_PREVIEW_STAGE_OPEN_CAMERA);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        modeButton_camera_control.setOnClickListener(onClickListener_camera_control);
        captureButton_camera_control.setOnClickListener(onClickListener_camera_control);
        settingsButton_camera_control.setOnClickListener(onClickListener_camera_control);

        setExposureTimeButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
        setSensitivityButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
        setApertureButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setAutoWhiteBalance_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setOpticalStabilization_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setFocalLengthButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setFocusDistanceButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
    }

    static void initiateContentRangeControl() {
        rangeControlBottomSheet = BottomSheetBehavior.from(MainActivity.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control));

        titleTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_title);
        informationTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_information);
        valueMinimumTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_valueMinimum);
        valueMaximumTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_valueMaximum);
        rangeSeekBar_range_control = (SeekBar) MainActivity.activity.findViewById(R.id.seekBar_range_control_range);
        autoCheckBox_range_control = (CheckBox) MainActivity.activity.findViewById(R.id.checkBox_range_control_auto);
        valueEditText_range_control = (EditText) MainActivity.activity.findViewById(R.id.editText_range_control_value);
        applyButton_range_control = (Button) MainActivity.activity.findViewById(R.id.button_range_control_apply);

        rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        rangeControlBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(bottomSheet.getWindowToken(), 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    static void initiateContentListControl() {
        listControlBottomSheet = BottomSheetBehavior.from(MainActivity.activity.findViewById(R.id.bottomSheet_capture_parameter_list_control));

        titleTextView_list_control = (TextView) MainActivity.activity.findViewById(R.id.textView_list_control_title);
        listRadioGroup_list_control = (RadioGroup) MainActivity.activity.findViewById(R.id.radioGroup_list_control_list);
        dismissButton_list_control = (Button) MainActivity.activity.findViewById(R.id.button_list_control_dismiss);

        dismissButton_list_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    // endregion

    static void updateCaptureParametersIndicator() {
        if (MainActivity.aeMode == CameraMetadata.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
            if (MainActivity.exposureTime < 1000L) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + MainActivity.exposureTime + "ns");
            } else if (MainActivity.exposureTime < 1000000L) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 10) / 100) + "µs");
            } else if (MainActivity.exposureTime < 1000000000L) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 10000) / 100) + "ms");
            } else {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 10000000) / 100) + "s");
            }
            setSensitivityButton_parameters_indicator.setText("ISO\n" + MainActivity.sensitivity);
        } else {
            setExposureTimeButton_parameters_indicator.setText("S.S.\nAUTO");
            setSensitivityButton_parameters_indicator.setText("ISO\nAUTO");
        }

        setApertureButton_parameters_indicator.setText("APE\nf/" + String.format("%.1f", MainActivity.aperture));
        setAutoWhiteBalance_parameters_indicator.setText("AWB\n" + listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES, MainActivity.awbMode, true));
        setOpticalStabilization_parameters_indicator.setText("OIS\n" + listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES, MainActivity.opticalStabilizationMode, true));
        setFocalLengthButton_parameters_indicator.setText("F.L.\n" + String.format("%.2f", MainActivity.focalLength) + "mm");

        if (MainActivity.afMode == CameraMetadata.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
            if (MainActivity.focusDistance == 0.0f) {
                setFocusDistanceButton_parameters_indicator.setText("F.D.\n" + "Infi");
            } else {
                setFocusDistanceButton_parameters_indicator.setText("F.D.\n" + String.format("%.2f", 1f / MainActivity.focusDistance) + "m");
            }
        } else {
            setFocusDistanceButton_parameters_indicator.setText("F.D.\nAUTO");
        }
    }

    static void updatePreviewParameters() {
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, MainActivity.autoMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, MainActivity.aeMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, MainActivity.afMode);

        if (MainActivity.aeMode == CameraMetadata.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
            MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, MainActivity.exposureTime);
            MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, MainActivity.sensitivity);
        }
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, MainActivity.aperture);

        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, MainActivity.awbMode);

        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, MainActivity.opticalStabilizationMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, MainActivity.focalLength);
        if (MainActivity.afMode == CameraMetadata.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
            MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, MainActivity.focusDistance);
        } else {
            MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
        }

        try {
            MainActivity.captureSession.stopRepeating();
            // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
            // previewCaptureCallback is for debug purpose
            MainActivity.captureSession.setRepeatingRequest(MainActivity.previewRequestBuilder.build(), MainActivity.previewCaptureCallback, MainActivity.cameraBackgroundHandler);
        } catch (CameraAccessException e) {
            MainActivity.displayErrorMessage(e);
        }
    }

    // region onClickListener for buttons in content_camera_control
    static View.OnClickListener onClickListener_camera_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((Button) view).getId() == R.id.button_camera_control_mode) {
            }

            else if (((Button) view).getId() == R.id.button_camera_control_capture) {
                MainActivity.takePhoto();
            }

            else if (((Button) view).getId() == R.id.button_camera_control_settings) {
            }
        }
    };

    static void cameraControl_setCaptureButtonEnabled(boolean enabled) {
        if (enabled) {
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    capturingProgressBar_camera_control.setElevation(0f);
                    captureButton_camera_control.setEnabled(true);
                    captureButton_camera_control.setText(R.string.button_camera_control_capture);
                }
            });
        } else {
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // from the material design documents, the elevation of a button is within [2dp, 8dp]
                    // therefore, set the elevation of a progressBar to 8dp will bring it to front
                    captureButton_camera_control.setWidth((UIOperator.captureButton_camera_control).getWidth());
                    captureButton_camera_control.setHeight((UIOperator.captureButton_camera_control).getHeight());
                    captureButton_camera_control.setText("");
                    captureButton_camera_control.setEnabled(false);
                    capturingProgressBar_camera_control.setElevation(8f * MainActivity.scale);
                }
            });
        }
    }
    // endregion

    // region onClickListener for buttons in content_parameters_indicator, type range_control
    static View.OnClickListener onClickListener_parameters_indicator_range_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

            titleTextView_range_control.setText(R.string.textView_range_control_title);
            // clear information TextView
            informationTextView_range_control.setText("");
            informationTextView_range_control.setTextSize(0);
            // clear information TextView
            valueMinimumTextView_range_control.setText(R.string.textView_range_control_valueMinimum);
            valueMaximumTextView_range_control.setText(R.string.textView_range_control_valueMaximum);
            valueEditText_range_control.setText("");

            // region parameters controlled by aeMode
            if (
                       (((Button) view).getId() == R.id.button_parameters_indicator_setExposureTime)
                    || (((Button) view).getId() == R.id.button_parameters_indicator_setSensitivity)
            ) {
                if (MainActivity.aeMode == CameraMetadata.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.aeMode = CameraMetadata.CONTROL_AE_MODE_ON;
                            } else {
                                MainActivity.aeMode = CameraMetadata.CONTROL_AE_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                if (((Button) view).getId() == R.id.button_parameters_indicator_setExposureTime) {
                    titleTextView_range_control.setText(R.string.textView_range_control_title_exposureTime);
                    valueMinimumTextView_range_control.setText("MIN\n" + MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower() + " ns");
                    valueMaximumTextView_range_control.setText("MAX\n" + String.format("%.4f", (double) (MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() / 10000) / 100000) + " s");

                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    if (MainActivity.exposureTime < 1000L) {
                        valueEditText_range_control.setHint(String.format("%.9f", (double) MainActivity.exposureTime / 1000000000d) + " s");
                    } else if (MainActivity.exposureTime < 1000000L) {
                        valueEditText_range_control.setHint(String.format("%.7f", (double) (MainActivity.exposureTime / 10L) / 100000000d) + " s");
                    } else if (MainActivity.exposureTime < 1000000000L) {
                        valueEditText_range_control.setHint(String.format("%.4f", (double) (MainActivity.exposureTime / 10000L) / 100000d) + " s");
                    } else {
                        valueEditText_range_control.setHint(String.format("%.1f", (double) (MainActivity.exposureTime / 10000000L) / 100d) + " s");
                    }
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_EXPOSURE_TIME);
                                    return true;
                                } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    applyButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_EXPOSURE_TIME);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    long progressLeftLength = MainActivity.exposureTime - MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    final long progressLength = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    rangeSeekBar_range_control.setProgress((int) (rangeSeekBar_range_control.getMax() * ((double) progressLeftLength / progressLength)) + rangeSeekBar_range_control.getMin());
                    rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                MainActivity.exposureTime = (
                                        (long) (progressLength * ((double) (progress - rangeSeekBar_range_control.getMin()) / (rangeSeekBar_range_control.getMax() - rangeSeekBar_range_control.getMin())))
                                        + MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                                );
                                if (MainActivity.exposureTime < MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                                    MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                                } else if (MainActivity.exposureTime > MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                                    MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
                                }
                                if (MainActivity.exposureTime < 1000L) {
                                    valueEditText_range_control.setHint(String.format("%.9f", (double) MainActivity.exposureTime / 1000000000d) + " s");
                                } else if (MainActivity.exposureTime < 1000000L) {
                                    valueEditText_range_control.setHint(String.format("%.7f", (double) (MainActivity.exposureTime / 10L) / 100000000d) + " s");
                                } else if (MainActivity.exposureTime < 1000000000L) {
                                    valueEditText_range_control.setHint(String.format("%.4f", (double) (MainActivity.exposureTime / 10000L) / 100000d) + " s");
                                } else {
                                    valueEditText_range_control.setHint(String.format("%.1f", (double) (MainActivity.exposureTime / 10000000L) / 100d) + " s");
                                }
                                updateCaptureParametersIndicator();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            updatePreviewParameters();
                        }
                    });
                }

                else if (((Button) view).getId() == R.id.button_parameters_indicator_setSensitivity) {
                    titleTextView_range_control.setText(R.string.textView_range_control_title_sensitivity);
                    valueMinimumTextView_range_control.setText("MIN\n" + String.valueOf(MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()));
                    valueMaximumTextView_range_control.setText("MAX\n" + String.valueOf(MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()));

                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    valueEditText_range_control.setHint(String.valueOf(MainActivity.sensitivity));
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_SENSITIVITY);
                                    return true;
                                } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    applyButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_SENSITIVITY);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    long progressLeftLength = MainActivity.sensitivity - MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    final long progressLength = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    rangeSeekBar_range_control.setProgress((int) (rangeSeekBar_range_control.getMax() * ((float) progressLeftLength / progressLength)) + rangeSeekBar_range_control.getMin());
                    rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                MainActivity.sensitivity = (
                                        (int) (progressLength * ((float) (progress - rangeSeekBar_range_control.getMin()) / (rangeSeekBar_range_control.getMax() - rangeSeekBar_range_control.getMin())))
                                        + MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                                );
                                if (MainActivity.sensitivity < MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                                    MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                                } else if (MainActivity.sensitivity > MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                                    MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
                                }
                                valueEditText_range_control.setHint(String.valueOf(MainActivity.sensitivity));
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
            // endregion

            // region parameters controlled by afMode
            else if (((Button) view).getId() == R.id.button_parameters_indicator_setFocusDistance) {
                if (MainActivity.afMode == CameraMetadata.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CameraMetadata.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.afMode = CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
                            } else {
                                MainActivity.afMode = CameraMetadata.CONTROL_AF_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                titleTextView_range_control.setText(R.string.textView_range_control_title_focusDistance);
                // setup information TextView
                rangeControlBottomSheet_setInformationTextViewText();
                informationTextView_range_control.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                // setup information TextView
                valueMinimumTextView_range_control.setText("MIN\n" + String.format("%.2f", (1f / MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE) * 100) + " cm");
                valueMaximumTextView_range_control.setText("MAX\n" + "Infinity");

                valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (MainActivity.focusDistance == 0.0f) {
                    valueEditText_range_control.setHint("Infinity m");
                } else {
                    valueEditText_range_control.setHint(String.format("%.4f", 1f / MainActivity.focusDistance) + " m");
                }
                valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_FOCUS_DISTANCE);
                                return true;
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                                return true;
                            }
                        }
                        return false;
                    }
                });

                applyButton_range_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                            rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_FOCUS_DISTANCE);
                        } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                            rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                });

                //  TODO: present focus distance better on the seek bar
                float progressLeftLength = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE - MainActivity.focusDistance;
                rangeSeekBar_range_control.setProgress((int) (rangeSeekBar_range_control.getMax() * (progressLeftLength / MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE)) + rangeSeekBar_range_control.getMin());
                rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            MainActivity.focusDistance = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE * ((float) (rangeSeekBar_range_control.getMax() - progress) / (rangeSeekBar_range_control.getMax() - rangeSeekBar_range_control.getMin()));
                            if (MainActivity.focusDistance < 0.0f) {
                                MainActivity.focusDistance = 0.0f;
                            } else if (MainActivity.focusDistance > MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                                MainActivity.focusDistance = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                            }
                            if (MainActivity.focusDistance == 0.0f) {
                                valueEditText_range_control.setHint("Infinity");
                            } else {
                                valueEditText_range_control.setHint(String.format("%.4f", 1f / MainActivity.focusDistance) + " m");
                            }
                            rangeControlBottomSheet_setInformationTextViewText();
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
            // endregion
        }
    };

    static void rangeControlBottomSheet_setAutoCheckBoxChecked(boolean isChecked) {
        if (isChecked) {
            autoCheckBox_range_control.setChecked(true);
            rangeSeekBar_range_control.setEnabled(false);
            applyButton_range_control.setEnabled(false);
            valueEditText_range_control.setEnabled(false);
        } else {
            autoCheckBox_range_control.setChecked(false);
            rangeSeekBar_range_control.setEnabled(true);
            applyButton_range_control.setEnabled(true);
            valueEditText_range_control.setEnabled(true);
        }
    }

    static void rangeControlBottomSheet_applyValueEditTextValue(int valueEditTextType) {
        if (valueEditTextType == RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_EXPOSURE_TIME) {
            double rawValue = Double.valueOf((valueEditText_range_control.getText()).toString());
            MainActivity.exposureTime = (long) (rawValue * 1000000000L);
            if (MainActivity.exposureTime < MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
            } else if (MainActivity.exposureTime > MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
            }

        } else if (valueEditTextType == RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_SENSITIVITY) {
            MainActivity.sensitivity = Integer.valueOf((valueEditText_range_control.getText()).toString());
            if (MainActivity.sensitivity < MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
            } else if (MainActivity.sensitivity > MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
            }

        } else if (valueEditTextType == RANGE_CONTROL_VALUE_EDIT_TEXT_TYPE_FOCUS_DISTANCE) {
            float rawValue = Float.valueOf((valueEditText_range_control.getText()).toString());
            MainActivity.focusDistance = 1f / rawValue;
            if (MainActivity.focusDistance < 0.0f) {
                MainActivity.focusDistance = 0.0f;
            } else if (MainActivity.focusDistance > MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                MainActivity.focusDistance = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
            }
        }

        rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        updateCaptureParametersIndicator();
        updatePreviewParameters();
    }

    static void rangeControlBottomSheet_setInformationTextViewText() {
        // # demand information:
        // Circle of Confusion (mm)
        // Hyperfocal Distance (m)
        // Near Point Distance (m)
        // Far Point Distance (m)
        // Depth of Field (m)

        float hyperfocalDistance = (  // unit: mm
                (
                        (MainActivity.focalLength * MainActivity.focalLength)
                        / (MainActivity.aperture * MainActivity.CIRCLE_OF_CONFUSION)
                ) + MainActivity.focalLength
        );

        String nearPointDistanceText;
        String farPointDistanceText;
        String depthOfFieldText;
        if (MainActivity.focusDistance != 0f) {
            float nearPointDistance = (  // unit: mm
                    ((1000f / MainActivity.focusDistance) * (hyperfocalDistance - MainActivity.focalLength))
                    / (hyperfocalDistance - 2 * MainActivity.focalLength + (1000f / MainActivity.focusDistance))
            );
            nearPointDistanceText = String.format("%.4f", nearPointDistance / 1000f) + "m";
            if (hyperfocalDistance > (1000f / MainActivity.focusDistance)) {
                float farPointDistance = (  // unit: mm
                        ((1000f / MainActivity.focusDistance) * (hyperfocalDistance - MainActivity.focalLength))
                        / (hyperfocalDistance - (1000f / MainActivity.focusDistance))
                );
                farPointDistanceText = String.format("%.4f", farPointDistance / 1000f) + "m";
                depthOfFieldText = String.format("%.4f", (farPointDistance - nearPointDistance) / 1000f) + "m";
            } else {
                farPointDistanceText = "Infinity";
                depthOfFieldText = "Infinity";
            }
        } else {
            nearPointDistanceText = "Infinity";
            farPointDistanceText = "Infinity";
            depthOfFieldText = "Infinity";
        }

        String informationText = (
                "CoC: " + String.format("%.6f", MainActivity.CIRCLE_OF_CONFUSION) + "mm, "
                + "H: " + String.format("%.6f", hyperfocalDistance / 1000f) + "m\n"
                + "D_N: " + nearPointDistanceText + ", "
                + "D_F: " + farPointDistanceText + "\n"
                + "DoF: " + depthOfFieldText
        );
        informationTextView_range_control.setText(informationText);
    }
    // endregion

    // region onClickListener for buttons in content_parameters_indicator, type list_control
    static View.OnClickListener onClickListener_parameters_indicator_list_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listControlBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

            titleTextView_list_control.setText(R.string.textView_list_control_title);
            listRadioGroup_list_control.removeAllViews();

            RadioButton radioButton;
            final int[] radioButtonIdArray;

            if (((Button) view).getId() == R.id.button_parameters_indicator_setAperture) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_aperture);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_APERTURES).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_APERTURES).length; i ++) {
                    radioButton = new RadioButton(MainActivity.activity);
                    // region basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}}, new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion
                    radioButton.setText("f/" + MainActivity.LENS_INFO_AVAILABLE_APERTURES[i]);
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_APERTURES, MainActivity.aperture)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((RadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.aperture = MainActivity.LENS_INFO_AVAILABLE_APERTURES[arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setAutoWhiteBalance) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_autoWhiteBalance);

                radioButtonIdArray = new int[(MainActivity.CONTROL_AWB_AVAILABLE_MODES).length];
                for (int i = 0; i < (MainActivity.CONTROL_AWB_AVAILABLE_MODES).length; i ++) {
                    radioButton = new RadioButton(MainActivity.activity);
                    // region basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}}, new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion
                    radioButton.setText(listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES, MainActivity.CONTROL_AWB_AVAILABLE_MODES[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[arrayIndexOf(MainActivity.CONTROL_AWB_AVAILABLE_MODES, MainActivity.awbMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((RadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.awbMode = MainActivity.CONTROL_AWB_AVAILABLE_MODES[arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setOpticalStabilization) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_opticalStabilization);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length; i ++) {
                    radioButton = new RadioButton(MainActivity.activity);
                    // region basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}}, new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion
                    radioButton.setText(listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES, MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, MainActivity.opticalStabilizationMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((RadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.opticalStabilizationMode = MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setFocalLength) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_focalLength);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length; i ++) {
                    radioButton = new RadioButton(MainActivity.activity);
                    // region basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}}, new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion
                    radioButton.setText(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[i] + " mm");
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS, MainActivity.focalLength)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((RadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.focalLength = MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }
        }
    };

    static String listControlBottomSheet_intValueToString(int intValueType, int intValue, boolean shortString) {
        String string = "";

        if (intValueType == LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES) {
            switch (intValue) {
                case 0: string = (shortString? "OFF" : "OFF"); break;
                case 1: string = (shortString? "AUTO" : "AUTO"); break;
                case 2: string = (shortString? "INC." : "Incandescent"); break;
                case 3: string = (shortString? "FLU." : "Fluorescent"); break;
                case 4: string = (shortString? "FLU.W." : "Fluorescent (Warm)"); break;
                case 5: string = (shortString? "DAY." : "Daylight"); break;
                case 6: string = (shortString? "DAY.C." : "Daylight (Cloudy)"); break;
                case 7: string = (shortString? "TWI." : "Twilight"); break;
                case 8: string = (shortString? "SHA." : "Shade"); break;
            }
        } else if (intValueType == LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES) {
            switch (intValue) {
                case 0: string = "OFF"; break;
                case 1: string = "ON"; break;
            }
        }

        return string;
    }

    static int arrayIndexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i] == value) {return i;}
        }
        return -1;
    }
    static int arrayIndexOf(float[] array, float value) {
        for (int i = 0; i < array.length; i ++) {
            if (array[i] == value) {return i;}
        }
        return -1;
    }
    // endregion

}
