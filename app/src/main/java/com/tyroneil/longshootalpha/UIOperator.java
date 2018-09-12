package com.tyroneil.longshootalpha;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class UIOperator extends Activity {
    // region content camera control
    static ChangeableRatioTextureView previewCRTV_camera_control;
    static Button modeButton_camera_control, captureButton_camera_control, settingsButton_camera_control;
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
    static TextView titleTextView_range_control, valueMinimumTextView_range_control, valueMaximumTextView_range_control;
    static SeekBar rangeSeekBar_range_control;
    static CheckBox autoCheckBox_range_control;
    static EditText valueEditText_range_control;
    static Button applyButton_range_control;
    static final int EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME = 0;
    static final int EDIT_TEXT_VALUE_TYPE_SENSITIVITY = 1;
    static final int EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE = 2;
    // endregion

    // region content capture parameter list control
    static BottomSheetBehavior listControlBottomSheet;
    static TextView titleTextView_list_control;
    static RadioGroup listRadioGroup_list_control;
    static Button dismissButton_list_control;
    // endregion

    static void initiateContentCameraControl() {
        previewCRTV_camera_control = (ChangeableRatioTextureView) MainActivity.activity.findViewById(R.id.cRTV_camera_control_preview);
        modeButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_mode);
        captureButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_capture);
        settingsButton_camera_control = (Button) MainActivity.activity.findViewById(R.id.button_camera_control_settings);

        // content parameters indicator
        setExposureTimeButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setExposureTime);
        setSensitivityButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setSensitivity);
        setApertureButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAperture);
        setAutoWhiteBalance_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAutoWhiteBalance);
        setOpticalStabilization_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setOpticalStabilization);
        setFocalLengthButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocalLength);
        setFocusDistanceButton_parameters_indicator = (Button) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocusDistance);

        previewCRTV_camera_control.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                MainActivity.createPreview(0);
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

    // region initiate bottom sheets (range_control, list_control)
    static void initiateContentRangeControl() {
        rangeControlBottomSheet = BottomSheetBehavior.from(MainActivity.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control));

        titleTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_title);
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
        if (MainActivity.aeMode == MainActivity.AE_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
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

        if (MainActivity.afMode == MainActivity.AF_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
            if (MainActivity.focusDistance == 0.0f) {
                setFocusDistanceButton_parameters_indicator.setText("F.D.\n" + "Infi");
            } else {
                setFocusDistanceButton_parameters_indicator.setText("F.D.\n" + String.format("%.2f", 1 / MainActivity.focusDistance) + "m");
            }
        } else {
            setFocusDistanceButton_parameters_indicator.setText("F.D.\nAUTO");
        }

        setFocalLengthButton_parameters_indicator.setText("F.L.\n" + String.format("%.2f", MainActivity.focalLength) + "mm");
        setApertureButton_parameters_indicator.setText("APE\nf/" + String.format("%.1f", MainActivity.aperture));
    }

    static void updatePreviewParameters() {
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, MainActivity.autoMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, MainActivity.aeMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, MainActivity.afMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, MainActivity.awbMode);

        if (MainActivity.aeMode == MainActivity.AE_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
            MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, MainActivity.exposureTime);
            MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, MainActivity.sensitivity);
        }
        if (MainActivity.afMode == MainActivity.AF_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
            MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, MainActivity.focusDistance);
        }
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, MainActivity.focalLength);
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, MainActivity.aperture);

        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, MainActivity.opticalStabilizationMode);

        try {
            MainActivity.captureSession.stopRepeating();
            MainActivity.captureSession.setRepeatingRequest(MainActivity.previewRequestBuilder.build(), null, MainActivity.backgroundHandler);
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
            }

            else if (((Button) view).getId() == R.id.button_camera_control_settings) {
            }
        }
    };
    // endregion

    // region onClickListener for buttons in content_parameters_indicator, type range_control
    static View.OnClickListener onClickListener_parameters_indicator_range_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            valueEditText_range_control.setText("");

            if (  // parameters controlled by aeMode
                       (((Button) view).getId() == R.id.button_parameters_indicator_setExposureTime)
                    || (((Button) view).getId() == R.id.button_parameters_indicator_setSensitivity)
            ) {
                if (MainActivity.aeMode == MainActivity.AE_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.aeMode = MainActivity.AE_MODE_ON;
                            } else {
                                MainActivity.aeMode = MainActivity.AE_MODE_OFF;
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
                                    rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME);
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
                                rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME);
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
                                    rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_SENSITIVITY);
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
                                rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_SENSITIVITY);
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

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setFocusDistance) {
                if (MainActivity.afMode == MainActivity.AF_MODE_OFF || MainActivity.autoMode == MainActivity.AUTO_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((CheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.afMode = MainActivity.AF_MODE_CONTINUOUS_PICTURE;
                            } else {
                                MainActivity.afMode = MainActivity.AF_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                titleTextView_range_control.setText(R.string.textView_range_control_title_focusDistance);
                valueMinimumTextView_range_control.setText("MIN\n" + String.format("%.2f", (1 / MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE) * 100) + " cm");
                valueMaximumTextView_range_control.setText("MAX\n" + "Infinity");

                valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                if (MainActivity.focusDistance == 0.0f) {
                    valueEditText_range_control.setHint("Infinity m");
                } else {
                    valueEditText_range_control.setHint(String.format("%.4f", 1 / MainActivity.focusDistance) + " m");
                }
                valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (!((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE);
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
                            rangeControlBottomSheet_applyValueEditTextValue(EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE);
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
                                valueEditText_range_control.setHint(String.format("%.4f", 1 / MainActivity.focusDistance) + " m");
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

    static void rangeControlBottomSheet_applyValueEditTextValue(int editTextValueType) {
        if (editTextValueType == EDIT_TEXT_VALUE_TYPE_EXPOSURE_TIME) {
            double rawValue = Double.valueOf((valueEditText_range_control.getText()).toString());
            MainActivity.exposureTime = (long) (rawValue * 1000000000L);
            if (MainActivity.exposureTime < MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
            } else if (MainActivity.exposureTime > MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
            }

        } else if (editTextValueType == EDIT_TEXT_VALUE_TYPE_SENSITIVITY) {
            MainActivity.sensitivity = Integer.valueOf((valueEditText_range_control.getText()).toString());
            if (MainActivity.sensitivity < MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
            } else if (MainActivity.sensitivity > MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
            }

        } else if (editTextValueType == EDIT_TEXT_VALUE_TYPE_FOCUS_DISTANCE) {
            float rawValue = Float.valueOf((valueEditText_range_control.getText()).toString());
            MainActivity.focusDistance = 1 / rawValue;
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
    // endregion

    // region onClickListener for buttons in content_parameters_indicator, type list_control
    static View.OnClickListener onClickListener_parameters_indicator_list_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listControlBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            listRadioGroup_list_control.removeAllViews();

            // region debug
            RadioButton radioButton;
            for (int i = 1; i <= 50; i ++) {
                radioButton = new RadioButton(MainActivity.activity);
                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                radioButton.setButtonTintList(new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_checked},
                                new int[]{android.R.attr.state_checked}
                        },
                        new int[] {
                                MainActivity.activity.getColor(R.color.colorPrimary),
                                MainActivity.activity.getColor(R.color.colorPrimary)
                        }
                ));
                radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                radioButton.setText("RadioButton" + i);
                radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                listRadioGroup_list_control.addView(radioButton);
            }
            // endregion

            if (((Button) view).getId() == R.id.button_parameters_indicator_setAperture) {
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setAutoWhiteBalance) {
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setOpticalStabilization) {
            }

            else if (((Button) view).getId() == R.id.button_parameters_indicator_setFocalLength) {
            }
        }
    };
    // endregion

}
