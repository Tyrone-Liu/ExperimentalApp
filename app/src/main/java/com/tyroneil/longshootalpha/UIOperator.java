package com.tyroneil.longshootalpha;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

class UIOperator {
    // region: content camera control
    static Animation focusAssistantIndicatorFadeOut;
    static Animation focusAssistantIndicatorFadeIn;
    static AppCompatImageView focusAssistantIndicatorImageView_camera_control;
    static ChangeableRatioTextureView previewCRTV_camera_control;
    static MaterialButton sequenceButton_camera_control, captureButton_camera_control, settingsButton_camera_control;
    static ProgressBar capturingProgressBar_camera_control;
    // endregion: content camera control

    // region: content capture parameters indicator
    static MaterialButton
            setExposureTimeButton_parameters_indicator,
            setSensitivityButton_parameters_indicator,
            setApertureButton_parameters_indicator,
            setTorchButton_parameters_indicator,
            setAutoWhiteBalance_parameters_indicator,
            setOpticalStabilization_parameters_indicator,
            setFocalLengthButton_parameters_indicator,
            setFocusDistanceButton_parameters_indicator;
    // endregion: content capture parameters indicator

    // region: content capture parameter range control
    static ConstraintLayout rangeControlConstraintLayout;
    static BottomSheetBehavior rangeControlBottomSheet;
    static TextView titleTextView_range_control, informationTextView_range_control, valueMinimumTextView_range_control, valueMaximumTextView_range_control;
    static SeekBar rangeSeekBar_range_control;
    static MaterialCheckBox autoCheckBox_range_control;
    static TextInputEditText valueEditText_range_control;
    static MaterialButton zoomOutButton_range_control, zoomInButton_range_control, applyButton_range_control;
    static final int RANGE_CONTROL_TYPE_EXPOSURE_TIME = 0;
    static final int RANGE_CONTROL_TYPE_SENSITIVITY = 1;
    static final int RANGE_CONTROL_TYPE_FOCUS_DISTANCE = 2;
    // endregion: content capture parameter range control

    // region: content capture parameter list control
    static BottomSheetBehavior listControlBottomSheet;
    static TextView titleTextView_list_control;
    static RadioGroup listRadioGroup_list_control;
    static MaterialButton dismissButton_list_control;
    static final int LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_TORCH = 0;
    static final int LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES = 1;
    static final int LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES = 2;
    // endregion: content capture parameter list control

    // region: initiate layouts (camera_control, range_control, list_control)
    static void initiateContentCameraControl() {
        focusAssistantIndicatorImageView_camera_control = (AppCompatImageView) MainActivity.activity.findViewById(R.id.imageView_camera_control_focusAssistantIndicator);
        previewCRTV_camera_control = (ChangeableRatioTextureView) MainActivity.activity.findViewById(R.id.cRTV_camera_control_preview);
        sequenceButton_camera_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_camera_control_sequence);
        captureButton_camera_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_camera_control_capture);
        settingsButton_camera_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_camera_control_settings);
        capturingProgressBar_camera_control = (ProgressBar) MainActivity.activity.findViewById(R.id.progressBar_camera_control_capturing);

        // content parameters indicator
        setExposureTimeButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setExposureTime);
        setSensitivityButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setSensitivity);
        setApertureButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAperture);
        setTorchButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setTorch);
        setAutoWhiteBalance_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setAutoWhiteBalance);
        setOpticalStabilization_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setOpticalStabilization);
        setFocalLengthButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocalLength);
        setFocusDistanceButton_parameters_indicator = (MaterialButton) MainActivity.activity.findViewById(R.id.button_parameters_indicator_setFocusDistance);

        /**
         * The {@link AlphaAnimation} starts form {@value 15.00f} will make the view fully visible
         * for some time at the beginning of the animation.
         */
        focusAssistantIndicatorFadeOut = new AlphaAnimation(15.00f, 0.00f);
        focusAssistantIndicatorFadeOut.setDuration((long) 3E3);

        focusAssistantIndicatorFadeIn = new AlphaAnimation(0.00f, 1.00f);
        focusAssistantIndicatorFadeIn.setDuration((long) (0.5d * 1E3));
        focusAssistantIndicatorFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                focusAssistantIndicatorImageView_camera_control.startAnimation(focusAssistantIndicatorFadeOut);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        focusAssistantIndicatorImageView_camera_control.setVisibility(View.INVISIBLE);

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
        previewCRTV_camera_control.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getBoolean("preference_focus_assistant", true)
                        && event.getActionMasked() == MotionEvent.ACTION_UP
                ) {
                    float displayX = event.getX();
                    float displayY = event.getY();
                    if (displayX > v.getWidth()) {displayX = v.getWidth();}
                    if (displayY > v.getHeight()) {displayY = v.getHeight();}

                    focusAssistantIndicatorImageView_camera_control.setTranslationX(
                            displayX - ((float) focusAssistantIndicatorImageView_camera_control.getWidth() / 2.0f)
                    );
                    focusAssistantIndicatorImageView_camera_control.setTranslationY(
                            displayY - ((float) focusAssistantIndicatorImageView_camera_control.getHeight() / 2.0f)
                    );
                    focusAssistantIndicatorImageView_camera_control.startAnimation(focusAssistantIndicatorFadeOut);

                    float scaledX;
                    float scaledY;

                    switch (MainActivity.sensorOrientation) {
                        default:
                        case 0:
                        case 180:
                            scaledX = displayX * ((float) MainActivity.previewViewWidth / v.getWidth());
                            scaledY = displayY * ((float) MainActivity.previewViewHeight / v.getHeight());
                            break;

                        case 90:
                        case 270:
                            scaledX = displayX * ((float) MainActivity.previewViewHeight / v.getWidth());
                            scaledY = displayY * ((float) MainActivity.previewViewWidth / v.getHeight());
                            break;
                    }

                    switch (MainActivity.sensorOrientation) {
                        case 0:
                            MainActivity.focusAssistantX = scaledX;
                            MainActivity.focusAssistantY = scaledY;
                            break;

                        case 180:
                            MainActivity.focusAssistantX = (float) MainActivity.previewViewWidth - scaledX;
                            MainActivity.focusAssistantY = (float) MainActivity.previewViewHeight - scaledY;
                            break;

                        case 90:
                            MainActivity.focusAssistantX = scaledY;
                            MainActivity.focusAssistantY = (float) MainActivity.previewViewHeight - scaledX;
                            break;

                        case 270:
                            MainActivity.focusAssistantX = (float) MainActivity.previewViewWidth - scaledY;
                            MainActivity.focusAssistantY = scaledX;
                            break;
                    }

                    // left
                    if (MainActivity.focusAssistantX - (MainActivity.focusAssistantWidth / 2.0f) < 0.0f) {
                        MainActivity.focusAssistantX = MainActivity.focusAssistantWidth / 2.0f;
                    }
                    // top
                    if (MainActivity.focusAssistantY - (MainActivity.focusAssistantHeight / 2.0f) < 0.0f) {
                        MainActivity.focusAssistantY = MainActivity.focusAssistantHeight / 2.0f;
                    }
                    // right
                    if (MainActivity.focusAssistantX + (MainActivity.focusAssistantWidth / 2.0f) > (float) MainActivity.previewViewWidth) {
                        MainActivity.focusAssistantX = (float) MainActivity.previewViewWidth - (MainActivity.focusAssistantWidth / 2.0f);
                    }
                    // bottom
                    if (MainActivity.focusAssistantY + (MainActivity.focusAssistantHeight / 2.0f) > (float) MainActivity.previewViewHeight) {
                        MainActivity.focusAssistantY = (float) MainActivity.previewViewHeight - (MainActivity.focusAssistantHeight / 2.0f);
                    }

                    // QUESTION: why not return true here, and return false down there?
                }
                return true;  // QUESTION: why this needs to be true to prevent weird problems?
            }
        });

        sequenceButton_camera_control.setOnClickListener(onClickListener_camera_control);
        captureButton_camera_control.setOnClickListener(onClickListener_camera_control);
        settingsButton_camera_control.setOnClickListener(onClickListener_camera_control);

        setExposureTimeButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
        setSensitivityButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
        setApertureButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setTorchButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_toggle_control);
        setAutoWhiteBalance_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setOpticalStabilization_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setFocalLengthButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_list_control);
        setFocusDistanceButton_parameters_indicator.setOnClickListener(onClickListener_parameters_indicator_range_control);
    }

    static void initiateContentRangeControl() {
        rangeControlConstraintLayout = MainActivity.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control);
        rangeControlBottomSheet = BottomSheetBehavior.from(MainActivity.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control));

        titleTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_title);
        informationTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_information);
        valueMinimumTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_valueMinimum);
        valueMaximumTextView_range_control = (TextView) MainActivity.activity.findViewById(R.id.textView_range_control_valueMaximum);
        rangeSeekBar_range_control = (SeekBar) MainActivity.activity.findViewById(R.id.seekBar_range_control_range);
        autoCheckBox_range_control = (MaterialCheckBox) MainActivity.activity.findViewById(R.id.checkBox_range_control_auto);
        valueEditText_range_control = (TextInputEditText) MainActivity.activity.findViewById(R.id.editText_range_control_value);
        zoomOutButton_range_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_range_control_zoomOut);
        zoomInButton_range_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_range_control_zoomIn);
        applyButton_range_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_range_control_apply);

        rangeControlConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    return previewCRTV_camera_control.dispatchTouchEvent(event);
                }
                return true;  // QUESTION: why this needs to be true to prevent weird problems?
            }
        });

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
        dismissButton_list_control = (MaterialButton) MainActivity.activity.findViewById(R.id.button_list_control_dismiss);

        dismissButton_list_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    // endregion: initiate layouts (camera_control, range_control, list_control)

    static void updateCaptureParametersIndicator() {
        if (MainActivity.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            if (MainActivity.exposureTime < 1E3) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + MainActivity.exposureTime + "ns");
            } else if (MainActivity.exposureTime < 1E6) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 1E1) / 1E2) + "µs");
            } else if (MainActivity.exposureTime < 1E9) {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 1E4) / 1E2) + "ms");
            } else {
                setExposureTimeButton_parameters_indicator.setText("S.S.\n" + String.format("%.1f", (double) (MainActivity.exposureTime / 1E7) / 1E2) + "s");
            }
            setSensitivityButton_parameters_indicator.setText("ISO\n" + MainActivity.sensitivity);
        } else {
            setExposureTimeButton_parameters_indicator.setText("S.S.\nAUTO");
            setSensitivityButton_parameters_indicator.setText("ISO\nAUTO");
        }

        setApertureButton_parameters_indicator.setText("APE\nf/" + String.format("%.1f", MainActivity.aperture));
        setTorchButton_parameters_indicator.setText("Tor.\n" + listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_TORCH, MainActivity.flashMode, true));
        setAutoWhiteBalance_parameters_indicator.setText("AWB\n" + listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES, MainActivity.awbMode, true));
        setOpticalStabilization_parameters_indicator.setText("OIS\n" + listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES, MainActivity.opticalStabilizationMode, true));
        setFocalLengthButton_parameters_indicator.setText("F.L.\n" + String.format("%.2f", MainActivity.focalLength) + "mm");

        if (MainActivity.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
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

        if (MainActivity.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            if (
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getBoolean("preference_preview_exposure_time_limit", true)
                    && MainActivity.exposureTime > 1E9 * Double.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getString("preference_preview_exposure_time_limit_value", "0.5"))
            ) {
                MainActivity.previewRequestBuilder.set(
                        CaptureRequest.SENSOR_EXPOSURE_TIME,
                        (long) (1E9 * Double.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getString("preference_preview_exposure_time_limit_value", "0.5")))
                );
            } else {
                MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, MainActivity.exposureTime);
            }
            MainActivity.previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, MainActivity.sensitivity);
        }
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, MainActivity.aperture);
        MainActivity.previewRequestBuilder.set(CaptureRequest.FLASH_MODE, MainActivity.flashMode);

        MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, MainActivity.awbMode);

        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, MainActivity.opticalStabilizationMode);
        MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, MainActivity.focalLength);
        if (MainActivity.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            MainActivity.previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, MainActivity.focusDistance);
        } else {
            MainActivity.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        }

        try {
            // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
            // previewCaptureCallback is for debug purpose
            MainActivity.captureSession.setRepeatingRequest(MainActivity.previewRequestBuilder.build(), MainActivity.previewCaptureCallback, MainActivity.cameraBackgroundHandler);
        } catch (CameraAccessException e) {
            MainActivity.displayErrorMessage(e);
        }
    }

    // region: onClickListener, content_camera_control
    static View.OnClickListener onClickListener_camera_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((MaterialButton) view).getId() == R.id.button_camera_control_sequence) {
                Intent openSequence = new Intent(MainActivity.context, SequenceActivity.class);
                MainActivity.activity.startActivity(openSequence);
            }

            else if (((MaterialButton) view).getId() == R.id.button_camera_control_capture) {
                MainActivity.takePhoto();
            }

            else if (((MaterialButton) view).getId() == R.id.button_camera_control_settings) {
                Intent openSettings = new Intent(MainActivity.activity, SettingsActivity.class);
                MainActivity.activity.startActivity(openSettings);
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
    // endregion: onClickListener, content_camera_control

    // region: onClickListener, type toggle_control
    static View.OnClickListener onClickListener_parameters_indicator_toggle_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setTorch) {
                if (MainActivity.FLASH_INFO_AVAILABLE) {
                    if (MainActivity.flashMode == CaptureRequest.FLASH_MODE_OFF) {
                        MainActivity.flashMode = CaptureRequest.FLASH_MODE_TORCH;
                    } else {
                        MainActivity.flashMode = CaptureRequest.FLASH_MODE_OFF;
                    }
                    updateCaptureParametersIndicator();
                    updatePreviewParameters();
                }
            }
        }
    };
    // endregion: onClickListener, type toggle_control

    // region: onClickListener, type range_control
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

            final int seekBarLength = rangeSeekBar_range_control.getMax() - rangeSeekBar_range_control.getMin();

            // region: parameters controlled by aeMode
            if (
                       (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setExposureTime)
                    || (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setSensitivity)
            ) {
                if (MainActivity.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((MaterialCheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.aeMode = CaptureRequest.CONTROL_AE_MODE_ON;
                            } else {
                                MainActivity.aeMode = CaptureRequest.CONTROL_AE_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setExposureTime) {
                    titleTextView_range_control.setText(R.string.textView_range_control_title_exposureTime);
                    // setup information TextView
                    rangeControlBottomSheet_setInformationTextViewText(RANGE_CONTROL_TYPE_EXPOSURE_TIME);
                    informationTextView_range_control.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    if (MainActivity.exposureTime < 1E3) {
                        valueEditText_range_control.setHint(String.format("%.9f", (double) MainActivity.exposureTime / 1E9) + " s");
                    } else if (MainActivity.exposureTime < 1E6) {
                        valueEditText_range_control.setHint(String.format("%.7f", (double) (MainActivity.exposureTime / 1E1) / 1E8) + " s");
                    } else if (MainActivity.exposureTime < 1E9) {
                        valueEditText_range_control.setHint(String.format("%.4f", (double) (MainActivity.exposureTime / 1E4) / 1E5) + " s");
                    } else {
                        valueEditText_range_control.setHint(String.format("%.1f", (double) (MainActivity.exposureTime / 1E7) / 1E2) + " s");
                    }
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_EXPOSURE_TIME);
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
                            if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_EXPOSURE_TIME);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    final long progressLength = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    final VariableContainer<Long> progressLeftOffset = new VariableContainer<Long>(0L);
                    final VariableContainer<Long> progressRightOffset = new VariableContainer<Long>(0L);
                    final VariableContainer<Long> progressLeftLength = new VariableContainer<Long>(
                            MainActivity.exposureTime - MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                    );
                    rangeControlBottomSheet_setupRangeSeekBar(
                            RANGE_CONTROL_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                            progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                            progressLeftLength
                    );

                    zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomOutRangeSeekBar(
                                    RANGE_CONTROL_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                    zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomInRangeSeekBar(
                                    RANGE_CONTROL_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                }

                else if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setSensitivity) {
                    titleTextView_range_control.setText(R.string.textView_range_control_title_sensitivity);

                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    valueEditText_range_control.setHint(String.valueOf(MainActivity.sensitivity));
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_SENSITIVITY);
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
                            if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_SENSITIVITY);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    final int progressLength = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    final VariableContainer<Integer> progressLeftOffset = new VariableContainer<Integer>(0);
                    final VariableContainer<Integer> progressRightOffset = new VariableContainer<Integer>(0);
                    final VariableContainer<Integer> progressLeftLength = new VariableContainer<Integer>(
                            MainActivity.sensitivity - MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                    );
                    rangeControlBottomSheet_setupRangeSeekBar(
                            RANGE_CONTROL_TYPE_SENSITIVITY, seekBarLength, progressLength,
                            progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                            progressLeftLength
                    );

                    zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomOutRangeSeekBar(
                                    RANGE_CONTROL_TYPE_SENSITIVITY, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                    zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomInRangeSeekBar(
                                    RANGE_CONTROL_TYPE_SENSITIVITY, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                }
            }
            // endregion: parameters controlled by aeMode

            // region: parameters controlled by afMode
            else if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setFocusDistance) {
                if (MainActivity.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || MainActivity.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((MaterialCheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                MainActivity.afMode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
                            } else {
                                MainActivity.afMode = CaptureRequest.CONTROL_AF_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                titleTextView_range_control.setText(R.string.textView_range_control_title_focusDistance);
                // setup information TextView
                rangeControlBottomSheet_setInformationTextViewText(RANGE_CONTROL_TYPE_FOCUS_DISTANCE);
                informationTextView_range_control.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

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
                            if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_FOCUS_DISTANCE);
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
                        if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                            rangeControlBottomSheet_applyValueEditTextValue(RANGE_CONTROL_TYPE_FOCUS_DISTANCE);
                        } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                            rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                });

                final float progressLength = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                final VariableContainer<Float> progressLeftOffset = new VariableContainer<Float>(0.0f);
                final VariableContainer<Float> progressRightOffset = new VariableContainer<Float>(0.0f);
                final VariableContainer<Float> progressRightLength = new VariableContainer<Float>(MainActivity.focusDistance);
                rangeControlBottomSheet_setupRangeSeekBar(
                        RANGE_CONTROL_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
                        progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                        progressRightLength
                );

                zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rangeControlBottomSheet_zoomOutRangeSeekBar(
                                RANGE_CONTROL_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
                                progressLeftOffset, progressRightOffset,
                                progressRightLength
                        );
                    }
                });
                zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rangeControlBottomSheet_zoomInRangeSeekBar(
                                RANGE_CONTROL_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
                                progressLeftOffset, progressRightOffset,
                                progressRightLength
                        );
                    }
                });
            }
            // endregion: parameters controlled by afMode
        }
    };

    static void rangeControlBottomSheet_setAutoCheckBoxChecked(boolean isChecked) {
        if (isChecked) {
            autoCheckBox_range_control.setChecked(true);
            rangeSeekBar_range_control.setEnabled(false);
            zoomOutButton_range_control.setEnabled(false);
            zoomInButton_range_control.setEnabled(false);
            zoomOutButton_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
            zoomInButton_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
            applyButton_range_control.setEnabled(false);
            valueEditText_range_control.setEnabled(false);
        } else {
            autoCheckBox_range_control.setChecked(false);
            rangeSeekBar_range_control.setEnabled(true);
            zoomOutButton_range_control.setEnabled(true);
            zoomInButton_range_control.setEnabled(true);
            zoomOutButton_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
            zoomInButton_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
            applyButton_range_control.setEnabled(true);
            valueEditText_range_control.setEnabled(true);
        }
    }

    static void rangeControlBottomSheet_applyValueEditTextValue(int valueEditTextType) {
        if (valueEditTextType == RANGE_CONTROL_TYPE_EXPOSURE_TIME) {
            double rawValue = Double.valueOf((valueEditText_range_control.getText()).toString());
            MainActivity.exposureTime = (long) (rawValue * 1E9);
            if (MainActivity.exposureTime < MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
            } else if (MainActivity.exposureTime > MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
            }

        } else if (valueEditTextType == RANGE_CONTROL_TYPE_SENSITIVITY) {
            MainActivity.sensitivity = Integer.valueOf((valueEditText_range_control.getText()).toString());
            if (MainActivity.sensitivity < MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
            } else if (MainActivity.sensitivity > MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
            }

        } else if (valueEditTextType == RANGE_CONTROL_TYPE_FOCUS_DISTANCE) {
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

    // region: rangeControlBottomSheet_setupRangeSeekBar
    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final long progressLength, final long progressLeftOffset, final long progressRightOffset, final VariableContainer<Long> progressLeftOrRightLength) {
        if (progressLeftOffset != 0L || progressRightOffset != 0L) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
        }

        if (type == RANGE_CONTROL_TYPE_EXPOSURE_TIME) {
            valueMinimumTextView_range_control.setText("MIN\n" + rangeControlBottomSheet_formatTimeString(MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower() + progressLeftOffset));
            valueMaximumTextView_range_control.setText("MAX\n" + rangeControlBottomSheet_formatTimeString(MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - progressRightOffset));

            long progressLeftLength = progressLeftOrRightLength.getVariable();
            rangeSeekBar_range_control.setProgress((int)
                    (seekBarLength * ((double) (progressLeftLength - progressLeftOffset) / (progressLength - progressLeftOffset - progressRightOffset)))
                    + rangeSeekBar_range_control.getMin()
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MainActivity.exposureTime = ((long) (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((double) (progress - rangeSeekBar_range_control.getMin()) / seekBarLength)
                                ) + progressLeftOffset + MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                        );
                        if (MainActivity.exposureTime < MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                            MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                        } else if (MainActivity.exposureTime > MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                            MainActivity.exposureTime = MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
                        }
                        progressLeftOrRightLength.setVariable(MainActivity.exposureTime - MainActivity.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower());

                        if (MainActivity.exposureTime < 1E3) {
                            valueEditText_range_control.setHint(String.format("%.9f", (double) MainActivity.exposureTime / 1E9) + " s");
                        } else if (MainActivity.exposureTime < 1E6) {
                            valueEditText_range_control.setHint(String.format("%.7f", (double) (MainActivity.exposureTime / 1E1) / 1E8) + " s");
                        } else if (MainActivity.exposureTime < 1E9) {
                            valueEditText_range_control.setHint(String.format("%.4f", (double) (MainActivity.exposureTime / 1E4) / 1E5) + " s");
                        } else {
                            valueEditText_range_control.setHint(String.format("%.1f", (double) (MainActivity.exposureTime / 1E7) / 1E2) + " s");
                        }
                        rangeControlBottomSheet_setInformationTextViewText(RANGE_CONTROL_TYPE_EXPOSURE_TIME);
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
    }

    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final int progressLength, final int progressLeftOffset, final int progressRightOffset, final VariableContainer<Integer> progressLeftOrRightLength) {
        if (progressLeftOffset != 0 || progressRightOffset != 0) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
        }

        if (type == RANGE_CONTROL_TYPE_SENSITIVITY) {
            valueMinimumTextView_range_control.setText("MIN\n" + String.valueOf(MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower() + progressLeftOffset));
            valueMaximumTextView_range_control.setText("MAX\n" + String.valueOf(MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - progressRightOffset));

            int progressLeftLength = progressLeftOrRightLength.getVariable();
            rangeSeekBar_range_control.setProgress((int)
                    (seekBarLength * ((double) (progressLeftLength - progressLeftOffset) / (progressLength - progressLeftOffset - progressRightOffset)))
                    + rangeSeekBar_range_control.getMin()
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MainActivity.sensitivity = ((int) (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((float) (progress - rangeSeekBar_range_control.getMin()) / seekBarLength)
                                ) + progressLeftOffset + MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                        );
                        if (MainActivity.sensitivity < MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                            MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                        } else if (MainActivity.sensitivity > MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                            MainActivity.sensitivity = MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
                        }
                        progressLeftOrRightLength.setVariable(MainActivity.sensitivity - MainActivity.SENSOR_INFO_SENSITIVITY_RANGE.getLower());

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

    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final float progressLength, final float progressLeftOffset, final float progressRightOffset, final VariableContainer<Float> progressLeftOrRightLength) {
        if (progressLeftOffset != 0.0f || progressRightOffset != 0.0f) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(MainActivity.activity.getColor(R.color.colorPrimary));
        }

        if (type == RANGE_CONTROL_TYPE_FOCUS_DISTANCE) {
            valueMinimumTextView_range_control.setText("MIN\n" + String.format("%.4f", 1f / (MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE - progressLeftOffset)) + " m");
            if (progressRightOffset == 0.0f) {
                valueMaximumTextView_range_control.setText("MAX\n" + "Infinity");
            } else {
                valueMaximumTextView_range_control.setText("MAX\n" + String.format("%.4f", 1f / progressRightOffset) + " m");
            }

            float progressRightLength = progressLeftOrRightLength.getVariable();
            rangeSeekBar_range_control.setProgress(
                    rangeSeekBar_range_control.getMax() - (int)
                    (seekBarLength * (progressRightLength - progressRightOffset) / (progressLength - progressLeftOffset - progressRightOffset))
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MainActivity.focusDistance = (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((float) (rangeSeekBar_range_control.getMax() - progress) / seekBarLength)
                                + progressRightOffset
                        );
                        if (MainActivity.focusDistance < 0.0f) {
                            MainActivity.focusDistance = 0.0f;
                        } else if (MainActivity.focusDistance > MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                            MainActivity.focusDistance = MainActivity.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                        }
                        progressLeftOrRightLength.setVariable(MainActivity.focusDistance);

                        if (MainActivity.focusDistance == 0.0f) {
                            valueEditText_range_control.setHint("Infinity");
                        } else {
                            valueEditText_range_control.setHint(String.format("%.4f", 1f / MainActivity.focusDistance) + " m");
                        }
                        rangeControlBottomSheet_setInformationTextViewText(RANGE_CONTROL_TYPE_FOCUS_DISTANCE);
                        updateCaptureParametersIndicator();
                        updatePreviewParameters();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getBoolean("preference_focus_assistant", true)) {
                        focusAssistantIndicatorImageView_camera_control.clearAnimation();
                        MainActivity.previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(
                                (int) (MainActivity.focusAssistantX - (MainActivity.focusAssistantWidth / 2.0f)),
                                (int) (MainActivity.focusAssistantY - (MainActivity.focusAssistantHeight / 2.0f)),
                                (int) (MainActivity.focusAssistantX + (MainActivity.focusAssistantWidth / 2.0f)),
                                (int) (MainActivity.focusAssistantY + (MainActivity.focusAssistantHeight / 2.0f))
                        ));
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (PreferenceManager.getDefaultSharedPreferences(MainActivity.context).getBoolean("preference_focus_assistant", true)) {
                        MainActivity.previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(
                                0, 0, MainActivity.previewViewWidth, MainActivity.previewViewHeight
                        ));
                        updatePreviewParameters();
                        focusAssistantIndicatorImageView_camera_control.startAnimation(focusAssistantIndicatorFadeIn);
                    }
                }
            });
        }
    }
    // endregion: rangeControlBottomSheet_setupRangeSeekBar

    // region: rangeControlBottomSheet_zoomRangeSeekBar
    static void rangeControlBottomSheet_zoomOutRangeSeekBar(
            int type, int seekBarLength, long progressLength,
            VariableContainer<Long> progressLeftOffset, VariableContainer<Long> progressRightOffset,
            VariableContainer<Long> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_EXPOSURE_TIME) {
            long progressLeftLength = progressLeftOrRightLength.getVariable();

            long newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) * 2;
            if (newProgressLength > progressLength) {
                newProgressLength = progressLength;
            } else if (newProgressLength / 2 < 1L) {
                newProgressLength = 1L * 2;
            }
            long newProgressLeftOffset;
            long newProgressRightOffset;

            if (progressLeftLength - (newProgressLength / 2) <= 0L) {
                newProgressLeftOffset = - progressLeftOffset.getVariable();
                newProgressRightOffset = - progressRightOffset.getVariable() + progressLength - newProgressLength;
            } else if (progressLeftLength + (newProgressLength / 2) >= progressLength) {
                newProgressLeftOffset = - progressLeftOffset.getVariable() + progressLength - newProgressLength;
                newProgressRightOffset = - progressRightOffset.getVariable();
            } else {
                newProgressLeftOffset = progressLeftLength - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = (progressLength - progressLeftLength) - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }

    static void rangeControlBottomSheet_zoomOutRangeSeekBar(
            int type, int seekBarLength, int progressLength,
            VariableContainer<Integer> progressLeftOffset, VariableContainer<Integer> progressRightOffset,
            VariableContainer<Integer> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_SENSITIVITY) {
            int progressLeftLength = progressLeftOrRightLength.getVariable();

            int newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) * 2;
            if (newProgressLength > progressLength) {
                newProgressLength = progressLength;
            } else if (newProgressLength / 2 < 1) {
                newProgressLength = 1 * 2;
            }
            int newProgressLeftOffset;
            int newProgressRightOffset;

            if (progressLeftLength - (newProgressLength / 2) <= 0) {
                newProgressLeftOffset = - progressLeftOffset.getVariable();
                newProgressRightOffset = - progressRightOffset.getVariable() + progressLength - newProgressLength;
            } else if (progressLeftLength + (newProgressLength / 2) >= progressLength) {
                newProgressLeftOffset = - progressLeftOffset.getVariable() + progressLength - newProgressLength;
                newProgressRightOffset = - progressRightOffset.getVariable();
            } else {
                newProgressLeftOffset = progressLeftLength - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = (progressLength - progressLeftLength) - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }

    static void rangeControlBottomSheet_zoomOutRangeSeekBar(
            int type, int seekBarLength, float progressLength,
            VariableContainer<Float> progressLeftOffset, VariableContainer<Float> progressRightOffset,
            VariableContainer<Float> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_FOCUS_DISTANCE) {
            float progressRightLength = progressLeftOrRightLength.getVariable();

            float newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) * 2;
            if (newProgressLength > progressLength) {
                newProgressLength = progressLength;
            } else if (newProgressLength / 2 < Float.MIN_NORMAL) {
                newProgressLength = (float) 1E-6;
            }
            float newProgressLeftOffset;
            float newProgressRightOffset;

            if (progressRightLength + (newProgressLength / 2) >= progressLength) {
                newProgressLeftOffset = - progressLeftOffset.getVariable();
                newProgressRightOffset = - progressRightOffset.getVariable() + progressLength - newProgressLength;
            } else if (progressRightLength - (newProgressLength / 2) <= 0.0f) {
                newProgressLeftOffset = - progressLeftOffset.getVariable() + progressLength - newProgressLength;
                newProgressRightOffset = - progressRightOffset.getVariable();
            } else {
                newProgressLeftOffset = (progressLength - progressRightLength) - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = progressRightLength - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }


    static void rangeControlBottomSheet_zoomInRangeSeekBar(
            int type, int seekBarLength, long progressLength,
            VariableContainer<Long> progressLeftOffset, VariableContainer<Long> progressRightOffset,
            VariableContainer<Long> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_EXPOSURE_TIME) {
            long progressLeftLength = progressLeftOrRightLength.getVariable();

            long newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) / 2;
            long newProgressLeftOffset = 0L;
            long newProgressRightOffset = 0L;

            if (progressLeftLength + (newProgressLength / 2) >= progressLength - progressRightOffset.getVariable()) {
                newProgressLeftOffset = newProgressLength;
            } else if (progressLeftLength - (newProgressLength / 2) <= progressLeftOffset.getVariable()) {
                newProgressRightOffset = newProgressLength;
            } else {
                newProgressLeftOffset = progressLeftLength - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = (progressLength - progressLeftLength) - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }

    static void rangeControlBottomSheet_zoomInRangeSeekBar(
            int type, int seekBarLength, int progressLength,
            VariableContainer<Integer> progressLeftOffset, VariableContainer<Integer> progressRightOffset,
            VariableContainer<Integer> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_SENSITIVITY) {
            int progressLeftLength = progressLeftOrRightLength.getVariable();

            int newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) / 2;
            int newProgressLeftOffset = 0;
            int newProgressRightOffset = 0;

            if (progressLeftLength + (newProgressLength / 2) >= progressLength - progressRightOffset.getVariable()) {
                newProgressLeftOffset = newProgressLength;
            } else if (progressLeftLength - (newProgressLength / 2) <= progressLeftOffset.getVariable()) {
                newProgressRightOffset = newProgressLength;
            } else {
                newProgressLeftOffset = progressLeftLength - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = (progressLength - progressLeftLength) - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }

    static void rangeControlBottomSheet_zoomInRangeSeekBar(
            int type, int seekBarLength, float progressLength,
            VariableContainer<Float> progressLeftOffset, VariableContainer<Float> progressRightOffset,
            VariableContainer<Float> progressLeftOrRightLength
    ) {
        if (type == RANGE_CONTROL_TYPE_FOCUS_DISTANCE) {
            float progressRightLength = progressLeftOrRightLength.getVariable();

            float newProgressLength = (progressLength - progressLeftOffset.getVariable() - progressRightOffset.getVariable()) / 2;
            float newProgressLeftOffset = 0.0f;
            float newProgressRightOffset = 0.0f;

            if (progressRightLength - (newProgressLength / 2) <= progressRightOffset.getVariable()) {
                newProgressLeftOffset = newProgressLength;
            } else if (progressRightLength + (newProgressLength / 2) >= progressLength - progressLeftOffset.getVariable()) {
                newProgressRightOffset = newProgressLength;
            } else {
                newProgressLeftOffset = (progressLength - progressRightLength) - (newProgressLength / 2) - progressLeftOffset.getVariable();
                newProgressRightOffset = progressRightLength - (newProgressLength / 2) - progressRightOffset.getVariable();
            }

            progressLeftOffset.setVariable(progressLeftOffset.getVariable() + newProgressLeftOffset);
            progressRightOffset.setVariable(progressRightOffset.getVariable() + newProgressRightOffset);

            rangeControlBottomSheet_setupRangeSeekBar(
                    type, seekBarLength, progressLength,
                    progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                    progressLeftOrRightLength
            );
        }
    }
    // endregion: rangeControlBottomSheet_zoomOutRangeSeekBar

    static String rangeControlBottomSheet_formatTimeString(long time) {
        if (time < 1E3) {
            return time + " ns";
        } else if (time < 1E6) {
            return String.format("%.2f", (double) (time / 1E0) / 1E3) + " µs";
        } else if (time < 1E9) {
            return String.format("%.2f", (double) (time / 1E3) / 1E3) + " ms";
        } else {
            return String.format("%.2f", (double) (time / 1E6) / 1E3) + " s";
        }
    }

    static void rangeControlBottomSheet_setInformationTextViewText(int informationTextViewType) {
        String informationText = "";

        if (informationTextViewType == RANGE_CONTROL_TYPE_EXPOSURE_TIME) {
            // # demand information:
            // Exposure Value

            double exposureValue = (
                    Math.log(Math.pow(MainActivity.aperture, 2) / ((double) MainActivity.exposureTime / 1E9))
                    / Math.log(2)
            );

            informationText = String.format(Locale.getDefault(), "EV: %.1f", exposureValue);
        }

        else if (informationTextViewType == RANGE_CONTROL_TYPE_FOCUS_DISTANCE) {
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

            informationText = (
                    "CoC: " + String.format("%.6f", MainActivity.CIRCLE_OF_CONFUSION) + "mm, "
                    + "H: " + String.format("%.6f", hyperfocalDistance / 1000f) + "m\n"
                    + "D_N: " + nearPointDistanceText + ", "
                    + "D_F: " + farPointDistanceText + "\n"
                    + "DoF: " + depthOfFieldText
            );
        }

        informationTextView_range_control.setText(informationText);
    }
    // endregion: onClickListener, type range_control

    // region: onClickListener, type list_control
    static View.OnClickListener onClickListener_parameters_indicator_list_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            listControlBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

            titleTextView_list_control.setText(R.string.textView_list_control_title);
            listRadioGroup_list_control.removeAllViews();

            MaterialRadioButton radioButton;
            final int[] radioButtonIdArray;

            if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setAperture) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_aperture);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_APERTURES).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_APERTURES).length; i ++) {
                    radioButton = new MaterialRadioButton(MainActivity.activity);
                    // TODO: find a way to apply style
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText("f/" + MainActivity.LENS_INFO_AVAILABLE_APERTURES[i]);
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[Utility.arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_APERTURES, MainActivity.aperture)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.aperture = MainActivity.LENS_INFO_AVAILABLE_APERTURES[Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setAutoWhiteBalance) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_autoWhiteBalance);

                radioButtonIdArray = new int[(MainActivity.CONTROL_AWB_AVAILABLE_MODES).length];
                for (int i = 0; i < (MainActivity.CONTROL_AWB_AVAILABLE_MODES).length; i ++) {
                    radioButton = new MaterialRadioButton(MainActivity.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES, MainActivity.CONTROL_AWB_AVAILABLE_MODES[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[Utility.arrayIndexOf(MainActivity.CONTROL_AWB_AVAILABLE_MODES, MainActivity.awbMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.awbMode = MainActivity.CONTROL_AWB_AVAILABLE_MODES[Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setOpticalStabilization) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_opticalStabilization);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length; i ++) {
                    radioButton = new MaterialRadioButton(MainActivity.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(listControlBottomSheet_intValueToString(LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_OIS_MODES, MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[Utility.arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, MainActivity.opticalStabilizationMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.opticalStabilizationMode = MainActivity.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((MaterialButton) view).getId() == R.id.button_parameters_indicator_setFocalLength) {
                titleTextView_list_control.setText(R.string.textView_list_control_title_focalLength);

                radioButtonIdArray = new int[(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length];
                for (int i = 0; i < (MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length; i ++) {
                    radioButton = new MaterialRadioButton(MainActivity.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {MainActivity.activity.getColor(R.color.colorSecondary), MainActivity.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * MainActivity.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(MainActivity.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[i] + " mm");
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                listRadioGroup_list_control.check(radioButtonIdArray[Utility.arrayIndexOf(MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS, MainActivity.focalLength)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            MainActivity.focalLength = MainActivity.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
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

        if (intValueType == LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_TORCH) {
            switch (intValue) {
                case 0: string = "OFF"; break;
                case 2: string = "ON"; break;
            }
        } else if (intValueType == LIST_CONTROL_INT_VALUE_TO_STRING_TYPE_AWB_MODES) {
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
    // endregion: onClickListener, type list_control

}
