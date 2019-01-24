package com.tyroneil.longshootalpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.text.InputType;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

class UIOperator {
    // region: content camera
    static Animation focusAssistantIndicatorFadeOut;
    static Animation focusAssistantIndicatorFadeIn;
    static AppCompatImageView focusAssistantIndicatorImageView_camera_control;
    static Support_FlexibleRatioTextureView previewFRTV_camera_control;
    // endregion: content camera

    // region: content capture parameters indicator
    static ConstraintLayout indicatorConstraintLayout;
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
    static int seekBarLength;
    // endregion: content capture parameter range control

    // region: content capture parameter list control
    static BottomSheetBehavior listControlBottomSheet;
    static TextView titleTextView_list_control;
    static RadioGroup listRadioGroup_list_control;
    static MaterialButton dismissButton_list_control;
    // endregion: content capture parameter list control

    // region: CONTROL_BOTTOM_SHEET_TYPE variables
    static final int CONTROL_BOTTOM_SHEET_TYPE_NULL = 0;
    static int viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_NULL;
    static int[] viewingControlBottomSheet_radioButtonIdArray = new int[0];

    static final int CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME = 1;
    static final int CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY = 2;
    static final int CONTROL_BOTTOM_SHEET_TYPE_APERTURE = 3;
    static final int CONTROL_BOTTOM_SHEET_TYPE_TORCH = 4;
    static final int CONTROL_BOTTOM_SHEET_TYPE_AWB_MODES = 5;
    static final int CONTROL_BOTTOM_SHEET_TYPE_OIS_MODES = 6;
    static final int CONTROL_BOTTOM_SHEET_TYPE_FOCAL_LENGTH = 7;
    static final int CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE = 8;

    static final int CONTROL_BOTTOM_SHEET_CATEGORY_AE_MODE = 1;
    static final int CONTROL_BOTTOM_SHEET_CATEGORY_AF_MODE = 2;
    // endregion: CONTROL_BOTTOM_SHEET_TYPE variables

    // region: initiate layouts (camera_control, range_control, list_control)
    static void initiateContentCameraControl() {
        focusAssistantIndicatorImageView_camera_control = (AppCompatImageView) Activity_Camera.activity.findViewById(R.id.activity_camera_imageView_focusAssistantIndicator);
        previewFRTV_camera_control = (Support_FlexibleRatioTextureView) Activity_Camera.activity.findViewById(R.id.activity_camera_fRTV_cameraPreview);

        // content parameters indicator
        setExposureTimeButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_exposureTime);
        setSensitivityButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_sensitivity);
        setApertureButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_aperture);
        setTorchButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_flash);
        setAutoWhiteBalance_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_whiteBalance);
        setOpticalStabilization_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_opticalImageStabilization);
        setFocalLengthButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_focalLength);
        setFocusDistanceButton_parameters_indicator = (MaterialButton) Activity_Camera.activity.findViewById(R.id.fragment_parameters_indicator_button_focusDistance);

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

        Activity_Camera.createPreview(Activity_Camera.CREATE_PREVIEW_STAGE_INITIATE_CAMERA_CANDIDATE);
        previewFRTV_camera_control.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Activity_Camera.createPreview(Activity_Camera.CREATE_PREVIEW_STAGE_OPEN_CAMERA);
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
        previewFRTV_camera_control.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (
                        Activity_Camera.sharedPreferences.getBoolean("preference_focus_assistant", true)
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

                    switch (Activity_Camera.sensorOrientation) {
                        default:
                        case 0:
                        case 180:
                            scaledX = displayX * ((float) Activity_Camera.previewViewWidth / v.getWidth());
                            scaledY = displayY * ((float) Activity_Camera.previewViewHeight / v.getHeight());
                            break;

                        case 90:
                        case 270:
                            scaledX = displayX * ((float) Activity_Camera.previewViewHeight / v.getWidth());
                            scaledY = displayY * ((float) Activity_Camera.previewViewWidth / v.getHeight());
                            break;
                    }

                    switch (Activity_Camera.sensorOrientation) {
                        case 0:
                            Activity_Camera.focusAssistantX = scaledX;
                            Activity_Camera.focusAssistantY = scaledY;
                            break;

                        case 180:
                            Activity_Camera.focusAssistantX = (float) Activity_Camera.previewViewWidth - scaledX;
                            Activity_Camera.focusAssistantY = (float) Activity_Camera.previewViewHeight - scaledY;
                            break;

                        case 90:
                            Activity_Camera.focusAssistantX = scaledY;
                            Activity_Camera.focusAssistantY = (float) Activity_Camera.previewViewHeight - scaledX;
                            break;

                        case 270:
                            Activity_Camera.focusAssistantX = (float) Activity_Camera.previewViewWidth - scaledY;
                            Activity_Camera.focusAssistantY = scaledX;
                            break;
                    }

                    // left
                    if (Activity_Camera.focusAssistantX - (Activity_Camera.focusAssistantWidth / 2.0f) < 0.0f) {
                        Activity_Camera.focusAssistantX = Activity_Camera.focusAssistantWidth / 2.0f;
                    }
                    // top
                    if (Activity_Camera.focusAssistantY - (Activity_Camera.focusAssistantHeight / 2.0f) < 0.0f) {
                        Activity_Camera.focusAssistantY = Activity_Camera.focusAssistantHeight / 2.0f;
                    }
                    // right
                    if (Activity_Camera.focusAssistantX + (Activity_Camera.focusAssistantWidth / 2.0f) > (float) Activity_Camera.previewViewWidth) {
                        Activity_Camera.focusAssistantX = (float) Activity_Camera.previewViewWidth - (Activity_Camera.focusAssistantWidth / 2.0f);
                    }
                    // bottom
                    if (Activity_Camera.focusAssistantY + (Activity_Camera.focusAssistantHeight / 2.0f) > (float) Activity_Camera.previewViewHeight) {
                        Activity_Camera.focusAssistantY = (float) Activity_Camera.previewViewHeight - (Activity_Camera.focusAssistantHeight / 2.0f);
                    }

                    // QUESTION: why not return true here, and return false down there?
                }
                return true;  // QUESTION: why this needs to be true to prevent weird problems?
            }
        });

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
        rangeControlConstraintLayout = Activity_Camera.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control);
        rangeControlBottomSheet = BottomSheetBehavior.from(Activity_Camera.activity.findViewById(R.id.bottomSheet_capture_parameter_range_control));

        titleTextView_range_control = (TextView) Activity_Camera.activity.findViewById(R.id.textView_range_control_title);
        informationTextView_range_control = (TextView) Activity_Camera.activity.findViewById(R.id.textView_range_control_information);
        valueMinimumTextView_range_control = (TextView) Activity_Camera.activity.findViewById(R.id.textView_range_control_valueMinimum);
        valueMaximumTextView_range_control = (TextView) Activity_Camera.activity.findViewById(R.id.textView_range_control_valueMaximum);
        rangeSeekBar_range_control = (SeekBar) Activity_Camera.activity.findViewById(R.id.seekBar_range_control_range);
        autoCheckBox_range_control = (MaterialCheckBox) Activity_Camera.activity.findViewById(R.id.checkBox_range_control_auto);
        valueEditText_range_control = (TextInputEditText) Activity_Camera.activity.findViewById(R.id.editText_range_control_value);
        zoomOutButton_range_control = (MaterialButton) Activity_Camera.activity.findViewById(R.id.button_range_control_zoomOut);
        zoomInButton_range_control = (MaterialButton) Activity_Camera.activity.findViewById(R.id.button_range_control_zoomIn);
        applyButton_range_control = (MaterialButton) Activity_Camera.activity.findViewById(R.id.button_range_control_apply);

        rangeControlConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    return previewFRTV_camera_control.dispatchTouchEvent(event);
                }
                return true;  // QUESTION: why this needs to be true to prevent weird problems?
            }
        });

        seekBarLength = rangeSeekBar_range_control.getMax() - rangeSeekBar_range_control.getMin();

        rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        rangeControlBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_NULL;
                    InputMethodManager inputMethodManager = (InputMethodManager) Activity_Camera.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(bottomSheet.getWindowToken(), 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    static void initiateContentListControl() {
        listControlBottomSheet = BottomSheetBehavior.from(Activity_Camera.activity.findViewById(R.id.bottomSheet_capture_parameter_list_control));

        titleTextView_list_control = (TextView) Activity_Camera.activity.findViewById(R.id.textView_list_control_title);
        listRadioGroup_list_control = (RadioGroup) Activity_Camera.activity.findViewById(R.id.radioGroup_list_control_list);
        dismissButton_list_control = (MaterialButton) Activity_Camera.activity.findViewById(R.id.button_list_control_dismiss);

        dismissButton_list_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        listControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        listControlBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_NULL;
                    viewingControlBottomSheet_radioButtonIdArray = new int[0];
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
    // endregion: initiate layouts (camera_control, range_control, list_control)

    static void updateCaptureParametersIndicator() {
        if (Activity_Camera.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            if (Activity_Camera.exposureTime < 1E3) {
                setExposureTimeButton_parameters_indicator.setText(
                        String.format(Locale.getDefault(), "S.S.\n%dns", Activity_Camera.exposureTime)
                );
            } else if (Activity_Camera.exposureTime < 1E6) {
                setExposureTimeButton_parameters_indicator.setText(
                        Activity_Camera.activity.getString(
                                R.string.fragment_parameters_indicator_button_exposureTime,
                                (double) (Activity_Camera.exposureTime / 1E1) / 1E2, "µs"
                        )
                );
            } else if (Activity_Camera.exposureTime < 1E9) {
                setExposureTimeButton_parameters_indicator.setText(
                        Activity_Camera.activity.getString(
                                R.string.fragment_parameters_indicator_button_exposureTime,
                                (double) (Activity_Camera.exposureTime / 1E4) / 1E2, "ms"
                        )
                );
            } else {
                setExposureTimeButton_parameters_indicator.setText(
                        Activity_Camera.activity.getString(
                                R.string.fragment_parameters_indicator_button_exposureTime,
                                (double) (Activity_Camera.exposureTime / 1E7) / 1E2, "s"
                        )
                );
            }
            setSensitivityButton_parameters_indicator.setText(
                    Activity_Camera.activity.getString(
                            R.string.fragment_parameters_indicator_button_sensitivity,
                            Activity_Camera.sensitivity
                    )
            );
            setApertureButton_parameters_indicator.setText(
                    Activity_Camera.activity.getString(
                            R.string.fragment_parameters_indicator_button_aperture,
                            Activity_Camera.aperture
                    )
            );
        } else {
            setExposureTimeButton_parameters_indicator.setText("S.S.\nAUTO");
            setSensitivityButton_parameters_indicator.setText("ISO\nAUTO");
            setApertureButton_parameters_indicator.setText("APE\nAUTO");
        }
        setTorchButton_parameters_indicator.setText(
                Activity_Camera.activity.getString(
                        R.string.fragment_parameters_indicator_button_flash,
                        listControlBottomSheet_intValueToString(CONTROL_BOTTOM_SHEET_TYPE_TORCH, Activity_Camera.flashMode, true)
                )
        );
        setAutoWhiteBalance_parameters_indicator.setText(
                Activity_Camera.activity.getString(
                        R.string.fragment_parameters_indicator_button_whiteBalance,
                        listControlBottomSheet_intValueToString(CONTROL_BOTTOM_SHEET_TYPE_AWB_MODES, Activity_Camera.awbMode, true)
                )
        );
        setOpticalStabilization_parameters_indicator.setText(
                Activity_Camera.activity.getString(
                        R.string.fragment_parameters_indicator_button_opticalImageStabilization,
                        listControlBottomSheet_intValueToString(CONTROL_BOTTOM_SHEET_TYPE_OIS_MODES, Activity_Camera.opticalStabilizationMode, true)
                )
        );
        setFocalLengthButton_parameters_indicator.setText(
                Activity_Camera.activity.getString(
                        R.string.fragment_parameters_indicator_button_focalLength,
                        Activity_Camera.focalLength
                )
        );

        if (Activity_Camera.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            if (Activity_Camera.focusDistance == 0.0f) {
                setFocusDistanceButton_parameters_indicator.setText("F.D.\nInf.");
            } else {
                setFocusDistanceButton_parameters_indicator.setText(
                        Activity_Camera.activity.getString(
                                R.string.fragment_parameters_indicator_button_focusDistance,
                                1f / Activity_Camera.focusDistance
                        )
                );
            }
        } else {
            setFocusDistanceButton_parameters_indicator.setText("F.D.\nAUTO");
        }
    }

    static void updatePreviewParameters() {
        Activity_Camera.previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, Activity_Camera.autoMode);
        Activity_Camera.previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, Activity_Camera.aeMode);
        Activity_Camera.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, Activity_Camera.afMode);

        if (Activity_Camera.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            if (
                    Activity_Camera.sharedPreferences.getBoolean("preference_preview_exposure_time_limit", true)
                    && Activity_Camera.exposureTime > 1E9 * Double.valueOf(Activity_Camera.sharedPreferences.getString("preference_preview_exposure_time_limit_value", "0.5"))
            ) {
                Activity_Camera.previewRequestBuilder.set(
                        CaptureRequest.SENSOR_EXPOSURE_TIME,
                        (long) (1E9 * Double.valueOf(Activity_Camera.sharedPreferences.getString("preference_preview_exposure_time_limit_value", "0.5")))
                );
            } else {
                Activity_Camera.previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, Activity_Camera.exposureTime);
            }
            Activity_Camera.previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, Activity_Camera.sensitivity);
            Activity_Camera.previewRequestBuilder.set(CaptureRequest.LENS_APERTURE, Activity_Camera.aperture);
        }
        Activity_Camera.previewRequestBuilder.set(CaptureRequest.FLASH_MODE, Activity_Camera.flashMode);

        Activity_Camera.previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, Activity_Camera.awbMode);

        Activity_Camera.previewRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, Activity_Camera.opticalStabilizationMode);
        Activity_Camera.previewRequestBuilder.set(CaptureRequest.LENS_FOCAL_LENGTH, Activity_Camera.focalLength);
        if (Activity_Camera.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
            Activity_Camera.previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, Activity_Camera.focusDistance);
        } else {
            Activity_Camera.previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        }

        try {
            // Set {@param CaptureCallback} to 'null' if preview does not need and additional process.
            Activity_Camera.captureSession.setRepeatingRequest(Activity_Camera.previewRequestBuilder.build(), Activity_Camera.previewCaptureCallback, Activity_Camera.cameraBackgroundHandler);
        } catch (CameraAccessException e) {
            Activity_Camera.displayErrorMessage(e);
        }
    }

    static void updateControlBottomSheet(int controlBottomSheetCategory) {
        if (viewingControlBottomSheet != CONTROL_BOTTOM_SHEET_TYPE_NULL) {
            if (controlBottomSheetCategory == CONTROL_BOTTOM_SHEET_CATEGORY_AE_MODE) {
                if (
                        viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME
                        || viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY
                ) {
                    rangeControlBottomSheet_setupInformationTextView(viewingControlBottomSheet);
                    rangeControlBottomSheet_setupValueEditTextHint(viewingControlBottomSheet);

                    if (viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
                        rangeControlBottomSheet_setupRangeSeekBarProgress(
                                viewingControlBottomSheet,
                                Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower(),
                                0L, 0L, Activity_Camera.exposureTime - Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                        );
                    } else if (viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
                        rangeControlBottomSheet_setupRangeSeekBarProgress(
                                viewingControlBottomSheet,
                                Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower(),
                                0, 0, Activity_Camera.sensitivity - Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                        );
                    }
                }

                else if (viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_APERTURE) {
                    if (viewingControlBottomSheet_radioButtonIdArray.length != 0) {
                        listRadioGroup_list_control.check(viewingControlBottomSheet_radioButtonIdArray[
                                Support_Utility.arrayIndexOf(Activity_Camera.LENS_INFO_AVAILABLE_APERTURES, Activity_Camera.aperture)
                        ]);
                    }
                }
            }

            else if (controlBottomSheetCategory == CONTROL_BOTTOM_SHEET_CATEGORY_AF_MODE) {
                if (viewingControlBottomSheet == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
                    rangeControlBottomSheet_setupInformationTextView(viewingControlBottomSheet);
                    rangeControlBottomSheet_setupValueEditTextHint(viewingControlBottomSheet);

                    rangeControlBottomSheet_setupRangeSeekBarProgress(
                            viewingControlBottomSheet,
                            Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE,
                            0.0f, 0.0f, Activity_Camera.focusDistance
                    );
                }
            }
        }
    }

    // region: onClickListener, type toggle_control
    static View.OnClickListener onClickListener_parameters_indicator_toggle_control = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_flash) {
                if (Activity_Camera.FLASH_INFO_AVAILABLE) {
                    if (Activity_Camera.flashMode == CaptureRequest.FLASH_MODE_OFF) {
                        Activity_Camera.flashMode = CaptureRequest.FLASH_MODE_TORCH;
                    } else {
                        Activity_Camera.flashMode = CaptureRequest.FLASH_MODE_OFF;
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
            // clear information TextView
            valueMinimumTextView_range_control.setText(R.string.textView_range_control_valueMinimum);
            valueMaximumTextView_range_control.setText(R.string.textView_range_control_valueMaximum);
            valueEditText_range_control.setText("");

            // region: parameters controlled by aeMode
            if (
                       (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_exposureTime)
                    || (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_sensitivity)
            ) {
                if (Activity_Camera.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((MaterialCheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                Activity_Camera.aeMode = CaptureRequest.CONTROL_AE_MODE_ON;
                            } else {
                                Activity_Camera.aeMode = CaptureRequest.CONTROL_AE_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_exposureTime) {
                    viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME;

                    titleTextView_range_control.setText(R.string.textView_range_control_title_exposureTime);
                    rangeControlBottomSheet_setupInformationTextView(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
                    rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
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
                                rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    final long progressLength = Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                    final Support_VariableContainer<Long> progressLeftOffset = new Support_VariableContainer<Long>(0L);
                    final Support_VariableContainer<Long> progressRightOffset = new Support_VariableContainer<Long>(0L);
                    final Support_VariableContainer<Long> progressLeftLength = new Support_VariableContainer<Long>(
                            Activity_Camera.exposureTime - Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                    );
                    rangeControlBottomSheet_setupRangeSeekBar(
                            CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                            progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                            progressLeftLength
                    );

                    zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomOutRangeSeekBar(
                                    CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                    zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomInRangeSeekBar(
                                    CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                }

                else if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_sensitivity) {
                    viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY;

                    titleTextView_range_control.setText(R.string.textView_range_control_title_sensitivity);

                    rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY);
                    valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                    rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY);
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
                                rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY);
                            } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }
                    });

                    final int progressLength = Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                    final Support_VariableContainer<Integer> progressLeftOffset = new Support_VariableContainer<Integer>(0);
                    final Support_VariableContainer<Integer> progressRightOffset = new Support_VariableContainer<Integer>(0);
                    final Support_VariableContainer<Integer> progressLeftLength = new Support_VariableContainer<Integer>(
                            Activity_Camera.sensitivity - Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                    );
                    rangeControlBottomSheet_setupRangeSeekBar(
                            CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY, seekBarLength, progressLength,
                            progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                            progressLeftLength
                    );

                    zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomOutRangeSeekBar(
                                    CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                    zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rangeControlBottomSheet_zoomInRangeSeekBar(
                                    CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY, seekBarLength, progressLength,
                                    progressLeftOffset, progressRightOffset,
                                    progressLeftLength
                            );
                        }
                    });
                }
            }
            // endregion: parameters controlled by aeMode

            // region: parameters controlled by afMode
            else if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_focusDistance) {
                viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE;

                if (Activity_Camera.afMode == CaptureRequest.CONTROL_AF_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF) {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(false);
                } else {
                    rangeControlBottomSheet_setAutoCheckBoxChecked(true);
                }
                autoCheckBox_range_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (((MaterialCheckBox) buttonView).isPressed()) {
                            if (isChecked) {
                                Activity_Camera.afMode = CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
                            } else {
                                Activity_Camera.afMode = CaptureRequest.CONTROL_AF_MODE_OFF;
                            }
                            rangeControlBottomSheet_setAutoCheckBoxChecked(isChecked);
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });

                titleTextView_range_control.setText(R.string.textView_range_control_title_focusDistance);
                rangeControlBottomSheet_setupInformationTextView(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
                rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
                valueEditText_range_control.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueEditText_range_control.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (! ((valueEditText_range_control.getText()).toString()).equals("")) {
                                rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
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
                            rangeControlBottomSheet_applyValueEditTextValue(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
                        } else if (((valueEditText_range_control.getText()).toString()).equals("")) {
                            rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                });

                final float progressLength = Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                final Support_VariableContainer<Float> progressLeftOffset = new Support_VariableContainer<Float>(0.0f);
                final Support_VariableContainer<Float> progressRightOffset = new Support_VariableContainer<Float>(0.0f);
                final Support_VariableContainer<Float> progressRightLength = new Support_VariableContainer<Float>(Activity_Camera.focusDistance);
                rangeControlBottomSheet_setupRangeSeekBar(
                        CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
                        progressLeftOffset.getVariable(), progressRightOffset.getVariable(),
                        progressRightLength
                );

                zoomOutButton_range_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rangeControlBottomSheet_zoomOutRangeSeekBar(
                                CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
                                progressLeftOffset, progressRightOffset,
                                progressRightLength
                        );
                    }
                });
                zoomInButton_range_control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rangeControlBottomSheet_zoomInRangeSeekBar(
                                CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE, seekBarLength, progressLength,
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
            zoomOutButton_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
            zoomInButton_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
            applyButton_range_control.setEnabled(false);
            valueEditText_range_control.setEnabled(false);
        } else {
            autoCheckBox_range_control.setChecked(false);
            rangeSeekBar_range_control.setEnabled(true);
            zoomOutButton_range_control.setEnabled(true);
            zoomInButton_range_control.setEnabled(true);
            zoomOutButton_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
            zoomInButton_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
            applyButton_range_control.setEnabled(true);
            valueEditText_range_control.setEnabled(true);
        }
    }

    static void rangeControlBottomSheet_applyValueEditTextValue(int valueEditTextType) {
        if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
            double rawValue = Double.valueOf((valueEditText_range_control.getText()).toString());
            Activity_Camera.exposureTime = (long) (rawValue * 1E9);
            if (Activity_Camera.sharedPreferences.getBoolean("preference_capture_exposure_time_limit", true)) {
                if (Activity_Camera.exposureTime < Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                    Activity_Camera.exposureTime = Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                } else if (Activity_Camera.exposureTime > Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                    Activity_Camera.exposureTime = Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
                }
            }
        }

        else if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
            Activity_Camera.sensitivity = Integer.valueOf((valueEditText_range_control.getText()).toString());
            if (Activity_Camera.sensitivity < Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                Activity_Camera.sensitivity = Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
            } else if (Activity_Camera.sensitivity > Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                Activity_Camera.sensitivity = Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
            }
        }

        else if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
            float rawValue = Float.valueOf((valueEditText_range_control.getText()).toString());
            Activity_Camera.focusDistance = 1f / rawValue;
            if (Activity_Camera.focusDistance < 0.0f) {
                Activity_Camera.focusDistance = 0.0f;
            } else if (Activity_Camera.focusDistance > Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                Activity_Camera.focusDistance = Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
            }
        }

        rangeControlBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        updateCaptureParametersIndicator();
        updatePreviewParameters();
    }

    static void rangeControlBottomSheet_setupInformationTextView(int informationTextViewType) {
        String informationText = "";

        if (informationTextViewType == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
            // # demand information:
            // Exposure Value
            double exposureValue = (
                    Math.log(Math.pow(Activity_Camera.aperture, 2) / ((double) Activity_Camera.exposureTime / 1E9))
                    / Math.log(2)
            );
            informationText = String.format(Locale.getDefault(), "EV: %.1f", exposureValue);
        }

        else if (informationTextViewType == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
            // # demand information:
            // Circle of Confusion (mm)
            // Hyperfocal Distance (m)
            // Near Point Distance (m)
            // Far Point Distance (m)
            // Depth of Field (m)

            float hyperfocalDistance = (  // unit: mm
                    (
                            (Activity_Camera.focalLength * Activity_Camera.focalLength)
                            / (Activity_Camera.aperture * Activity_Camera.CIRCLE_OF_CONFUSION)
                    ) + Activity_Camera.focalLength
            );

            String nearPointDistanceText;
            String farPointDistanceText;
            String depthOfFieldText;
            if (Activity_Camera.focusDistance != 0f) {
                float nearPointDistance = (  // unit: mm
                        ((1000f / Activity_Camera.focusDistance) * (hyperfocalDistance - Activity_Camera.focalLength))
                        / (hyperfocalDistance - 2 * Activity_Camera.focalLength + (1000f / Activity_Camera.focusDistance))
                );
                nearPointDistanceText = String.format(Locale.getDefault(), "%.4fm", nearPointDistance / 1000f);
                if (hyperfocalDistance > (1000f / Activity_Camera.focusDistance)) {
                    float farPointDistance = (  // unit: mm
                            ((1000f / Activity_Camera.focusDistance) * (hyperfocalDistance - Activity_Camera.focalLength))
                            / (hyperfocalDistance - (1000f / Activity_Camera.focusDistance))
                    );
                    farPointDistanceText = String.format(Locale.getDefault(), "%.4fm", farPointDistance / 1000f);
                    depthOfFieldText = String.format(Locale.getDefault(), "%.4fm", (farPointDistance - nearPointDistance) / 1000f);
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
                    String.format(Locale.getDefault(), "CoC: %.6fmm, H: %.6fm\n", Activity_Camera.CIRCLE_OF_CONFUSION, hyperfocalDistance / 1000f)
                    + String.format(Locale.getDefault(), "D_N: %s, D_F: %s\n", nearPointDistanceText, farPointDistanceText)
                    + String.format(Locale.getDefault(), "DoF: %s", depthOfFieldText)
            );
        }

        informationTextView_range_control.setText(informationText);
    }

    static void rangeControlBottomSheet_setupValueEditTextHint(int valueEditTextType) {
        if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
            if (Activity_Camera.exposureTime < 1E3) {
                valueEditText_range_control.setHint(
                        String.format(Locale.getDefault(), "%.9f s", (double) Activity_Camera.exposureTime / 1E9)
                );
            } else if (Activity_Camera.exposureTime < 1E6) {
                valueEditText_range_control.setHint(
                        String.format(Locale.getDefault(), "%.7f s", (double) (Activity_Camera.exposureTime / 1E1) / 1E8)
                );
            } else if (Activity_Camera.exposureTime < 1E9) {
                valueEditText_range_control.setHint(
                        String.format(Locale.getDefault(), "%.4f s", (double) (Activity_Camera.exposureTime / 1E4) / 1E5)
                );
            } else {
                valueEditText_range_control.setHint(
                        String.format(Locale.getDefault(), "%.1f s", (double) (Activity_Camera.exposureTime / 1E7) / 1E2)
                );
            }
        }

        else if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
            valueEditText_range_control.setHint(String.valueOf(Activity_Camera.sensitivity));
        }

        else if (valueEditTextType == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
            if (Activity_Camera.focusDistance == 0.0f) {
                valueEditText_range_control.setHint("Infinity m");
            } else {
                valueEditText_range_control.setHint(
                        String.format(Locale.getDefault(), "%.4f m", 1f / Activity_Camera.focusDistance)
                );
            }
        }
    }

    // region: rangeControlBottomSheet_setupRangeSeekBar
    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final long progressLength, final long progressLeftOffset, final long progressRightOffset, final Support_VariableContainer<Long> progressLeftOrRightLength) {
        if (progressLeftOffset != 0L || progressRightOffset != 0L) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
        }

        if (type == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
            valueMinimumTextView_range_control.setText(
                    Activity_Camera.activity.getString(
                            R.string.textView_range_control_valueMinimum,
                            rangeControlBottomSheet_formatTimeString(Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower() + progressLeftOffset)
                    )
            );
            valueMaximumTextView_range_control.setText(
                    Activity_Camera.activity.getString(
                            R.string.textView_range_control_valueMaximum,
                            rangeControlBottomSheet_formatTimeString(Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper() - progressRightOffset)
                    )
            );

            rangeControlBottomSheet_setupRangeSeekBarProgress(
                    type, progressLength,
                    progressLeftOffset, progressRightOffset, progressLeftOrRightLength.getVariable()
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        Activity_Camera.exposureTime = ((long) (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((double) (progress - rangeSeekBar_range_control.getMin()) / seekBarLength)
                                ) + progressLeftOffset + Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()
                        );
                        if (Activity_Camera.exposureTime < Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower()) {
                            Activity_Camera.exposureTime = Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower();
                        } else if (Activity_Camera.exposureTime > Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper()) {
                            Activity_Camera.exposureTime = Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getUpper();
                        }
                        progressLeftOrRightLength.setVariable(Activity_Camera.exposureTime - Activity_Camera.SENSOR_INFO_EXPOSURE_TIME_RANGE.getLower());

                        rangeControlBottomSheet_setupInformationTextView(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
                        rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME);
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

    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final int progressLength, final int progressLeftOffset, final int progressRightOffset, final Support_VariableContainer<Integer> progressLeftOrRightLength) {
        if (progressLeftOffset != 0 || progressRightOffset != 0) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
        }

        if (type == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
            valueMinimumTextView_range_control.setText(
                    Activity_Camera.activity.getString(
                            R.string.textView_range_control_valueMinimum,
                            String.valueOf(Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower() + progressLeftOffset)
                    )
            );
            valueMaximumTextView_range_control.setText(
                    Activity_Camera.activity.getString(
                            R.string.textView_range_control_valueMaximum,
                            String.valueOf(Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper() - progressRightOffset)
                    )
            );

            rangeControlBottomSheet_setupRangeSeekBarProgress(
                    type, progressLength,
                    progressLeftOffset, progressRightOffset, progressLeftOrRightLength.getVariable()
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        Activity_Camera.sensitivity = ((int) (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((float) (progress - rangeSeekBar_range_control.getMin()) / seekBarLength)
                                ) + progressLeftOffset + Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower()
                        );
                        if (Activity_Camera.sensitivity < Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower()) {
                            Activity_Camera.sensitivity = Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower();
                        } else if (Activity_Camera.sensitivity > Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper()) {
                            Activity_Camera.sensitivity = Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getUpper();
                        }
                        progressLeftOrRightLength.setVariable(Activity_Camera.sensitivity - Activity_Camera.SENSOR_INFO_SENSITIVITY_RANGE.getLower());

                        rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY);
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

    static void rangeControlBottomSheet_setupRangeSeekBar(int type, final int seekBarLength, final float progressLength, final float progressLeftOffset, final float progressRightOffset, final Support_VariableContainer<Float> progressLeftOrRightLength) {
        if (progressLeftOffset != 0.0f || progressRightOffset != 0.0f) {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.ITALIC));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
        } else {
            valueMinimumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMaximumTextView_range_control.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            valueMinimumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
            valueMaximumTextView_range_control.setTextColor(Activity_Camera.activity.getColor(R.color.colorPrimary));
        }

        if (type == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
            valueMinimumTextView_range_control.setText(
                    Activity_Camera.activity.getString(
                            R.string.textView_range_control_valueMinimum,
                            String.format(Locale.getDefault(), "%.4f m", 1f / (Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE - progressLeftOffset))
                    )
            );
            if (progressRightOffset == 0.0f) {
                valueMaximumTextView_range_control.setText(
                        Activity_Camera.activity.getString(
                                R.string.textView_range_control_valueMaximum,
                                "Infinity"
                        )
                );
            } else {
                valueMaximumTextView_range_control.setText(
                        Activity_Camera.activity.getString(
                                R.string.textView_range_control_valueMaximum,
                                String.format(Locale.getDefault(), "%.4f m", 1f / progressRightOffset)
                        )
                );
            }

            rangeControlBottomSheet_setupRangeSeekBarProgress(
                    type, progressLength,
                    progressLeftOffset, progressRightOffset, progressLeftOrRightLength.getVariable()
            );
            rangeSeekBar_range_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        Activity_Camera.focusDistance = (
                                (progressLength - progressLeftOffset - progressRightOffset)
                                * ((float) (rangeSeekBar_range_control.getMax() - progress) / seekBarLength)
                                + progressRightOffset
                        );
                        if (Activity_Camera.focusDistance < 0.0f) {
                            Activity_Camera.focusDistance = 0.0f;
                        } else if (Activity_Camera.focusDistance > Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE) {
                            Activity_Camera.focusDistance = Activity_Camera.LENS_INFO_MINIMUM_FOCUS_DISTANCE;
                        }
                        progressLeftOrRightLength.setVariable(Activity_Camera.focusDistance);

                        rangeControlBottomSheet_setupInformationTextView(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
                        rangeControlBottomSheet_setupValueEditTextHint(CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE);
                        updateCaptureParametersIndicator();
                        updatePreviewParameters();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (Activity_Camera.sharedPreferences.getBoolean("preference_focus_assistant", true)) {
                        focusAssistantIndicatorImageView_camera_control.clearAnimation();
                        Activity_Camera.previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(
                                (int) (Activity_Camera.focusAssistantX - (Activity_Camera.focusAssistantWidth / 2.0f)),
                                (int) (Activity_Camera.focusAssistantY - (Activity_Camera.focusAssistantHeight / 2.0f)),
                                (int) (Activity_Camera.focusAssistantX + (Activity_Camera.focusAssistantWidth / 2.0f)),
                                (int) (Activity_Camera.focusAssistantY + (Activity_Camera.focusAssistantHeight / 2.0f))
                        ));
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (Activity_Camera.sharedPreferences.getBoolean("preference_focus_assistant", true)) {
                        Activity_Camera.previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, new Rect(
                                0, 0, Activity_Camera.previewViewWidth, Activity_Camera.previewViewHeight
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
            Support_VariableContainer<Long> progressLeftOffset, Support_VariableContainer<Long> progressRightOffset,
            Support_VariableContainer<Long> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
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
            Support_VariableContainer<Integer> progressLeftOffset, Support_VariableContainer<Integer> progressRightOffset,
            Support_VariableContainer<Integer> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
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
            Support_VariableContainer<Float> progressLeftOffset, Support_VariableContainer<Float> progressRightOffset,
            Support_VariableContainer<Float> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
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
            Support_VariableContainer<Long> progressLeftOffset, Support_VariableContainer<Long> progressRightOffset,
            Support_VariableContainer<Long> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
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
            Support_VariableContainer<Integer> progressLeftOffset, Support_VariableContainer<Integer> progressRightOffset,
            Support_VariableContainer<Integer> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
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
            Support_VariableContainer<Float> progressLeftOffset, Support_VariableContainer<Float> progressRightOffset,
            Support_VariableContainer<Float> progressLeftOrRightLength
    ) {
        if (type == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
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
    // endregion: rangeControlBottomSheet_zoomRangeSeekBar

    // region: rangeControlBottomSheet_setupRangeSeekBarProgress
    static void rangeControlBottomSheet_setupRangeSeekBarProgress(
            int rangeSeekBarType, long progressLength,
            long progressLeftOffset, long progressRightOffset, long progressLeftOrRightLength
    ) {
        if (rangeSeekBarType == CONTROL_BOTTOM_SHEET_TYPE_EXPOSURE_TIME) {
            long progressLeftLength = progressLeftOrRightLength;
            rangeSeekBar_range_control.setProgress((int)
                    (seekBarLength * ((double) (progressLeftLength - progressLeftOffset) / (progressLength - progressLeftOffset - progressRightOffset)))
                    + rangeSeekBar_range_control.getMin()
            );
        }
    }

    static void rangeControlBottomSheet_setupRangeSeekBarProgress(
            int rangeSeekBarType, int progressLength,
            int progressLeftOffset, int progressRightOffset, int progressLeftOrRightLength
    ) {
        if (rangeSeekBarType == CONTROL_BOTTOM_SHEET_TYPE_SENSITIVITY) {
            int progressLeftLength = progressLeftOrRightLength;
            rangeSeekBar_range_control.setProgress((int)
                    (seekBarLength * ((double) (progressLeftLength - progressLeftOffset) / (progressLength - progressLeftOffset - progressRightOffset)))
                    + rangeSeekBar_range_control.getMin()
            );
        }
    }

    static void rangeControlBottomSheet_setupRangeSeekBarProgress(
            int rangeSeekBarType, float progressLength,
            float progressLeftOffset, float progressRightOffset, float progressLeftOrRightLength
    ) {
        if (rangeSeekBarType == CONTROL_BOTTOM_SHEET_TYPE_FOCUS_DISTANCE) {
            float progressRightLength = progressLeftOrRightLength;
            rangeSeekBar_range_control.setProgress(
                    rangeSeekBar_range_control.getMax() - (int) (
                            seekBarLength * (progressRightLength - progressRightOffset) / (progressLength - progressLeftOffset - progressRightOffset)
                    )
            );
        }
    }
    // endregion: rangeControlBottomSheet_setupRangeSeekBarProgress

    static String rangeControlBottomSheet_formatTimeString(long time) {
        if (time < 1E3) {
            return time + " ns";
        } else if (time < 1E6) {
            return String.format(Locale.getDefault(), "%.2f µs", (double) (time / 1E0) / 1E3);
        } else if (time < 1E9) {
            return String.format(Locale.getDefault(), "%.2f ms", (double) (time / 1E3) / 1E3);
        } else {
            return String.format(Locale.getDefault(), "%.2f s", (double) (time / 1E6) / 1E3);
        }
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

            // region: parameters controlled by aeMode
            if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_aperture) {
                viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_APERTURE;

                titleTextView_list_control.setText(R.string.textView_list_control_title_aperture);

                radioButtonIdArray = new int[(Activity_Camera.LENS_INFO_AVAILABLE_APERTURES).length];
                for (int i = 0; i < (Activity_Camera.LENS_INFO_AVAILABLE_APERTURES).length; i ++) {
                    radioButton = new MaterialRadioButton(Activity_Camera.activity);
                    // TODO: find a way to apply style
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setPadding((int) (8f * Activity_Camera.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(String.format(Locale.getDefault(), "f/%s", Activity_Camera.LENS_INFO_AVAILABLE_APERTURES[i]));
                    if (! (Activity_Camera.aeMode == CaptureRequest.CONTROL_AE_MODE_OFF || Activity_Camera.autoMode == CaptureRequest.CONTROL_MODE_OFF)) {
                        radioButton.setEnabled(false);
                        radioButton.setButtonTintList(new ColorStateList(
                                new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                                new int[] {Activity_Camera.activity.getColor(R.color.colorSecondaryDown), Activity_Camera.activity.getColor(R.color.colorSecondaryDown)}
                        ));
                    radioButton.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondaryDown));
                    } else {
                        radioButton.setButtonTintList(new ColorStateList(
                                new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                                new int[] {Activity_Camera.activity.getColor(R.color.colorSecondary), Activity_Camera.activity.getColor(R.color.colorSecondary)}
                        ));
                    radioButton.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
                    }
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                viewingControlBottomSheet_radioButtonIdArray = radioButtonIdArray;
                listRadioGroup_list_control.check(radioButtonIdArray[Support_Utility.arrayIndexOf(Activity_Camera.LENS_INFO_AVAILABLE_APERTURES, Activity_Camera.aperture)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            Activity_Camera.aperture = Activity_Camera.LENS_INFO_AVAILABLE_APERTURES[Support_Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }
            // endregion: parameters controlled by aeMode

            else if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_whiteBalance) {
                viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_AWB_MODES;

                titleTextView_list_control.setText(R.string.textView_list_control_title_autoWhiteBalance);

                radioButtonIdArray = new int[(Activity_Camera.CONTROL_AWB_AVAILABLE_MODES).length];
                for (int i = 0; i < (Activity_Camera.CONTROL_AWB_AVAILABLE_MODES).length; i ++) {
                    radioButton = new MaterialRadioButton(Activity_Camera.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {Activity_Camera.activity.getColor(R.color.colorSecondary), Activity_Camera.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * Activity_Camera.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(listControlBottomSheet_intValueToString(CONTROL_BOTTOM_SHEET_TYPE_AWB_MODES, Activity_Camera.CONTROL_AWB_AVAILABLE_MODES[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                viewingControlBottomSheet_radioButtonIdArray = radioButtonIdArray;
                listRadioGroup_list_control.check(radioButtonIdArray[Support_Utility.arrayIndexOf(Activity_Camera.CONTROL_AWB_AVAILABLE_MODES, Activity_Camera.awbMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            Activity_Camera.awbMode = Activity_Camera.CONTROL_AWB_AVAILABLE_MODES[Support_Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_opticalImageStabilization) {
                viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_OIS_MODES;

                titleTextView_list_control.setText(R.string.textView_list_control_title_opticalStabilization);

                radioButtonIdArray = new int[(Activity_Camera.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length];
                for (int i = 0; i < (Activity_Camera.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION).length; i ++) {
                    radioButton = new MaterialRadioButton(Activity_Camera.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {Activity_Camera.activity.getColor(R.color.colorSecondary), Activity_Camera.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * Activity_Camera.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(listControlBottomSheet_intValueToString(CONTROL_BOTTOM_SHEET_TYPE_OIS_MODES, Activity_Camera.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[i], false));
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                viewingControlBottomSheet_radioButtonIdArray = radioButtonIdArray;
                listRadioGroup_list_control.check(radioButtonIdArray[Support_Utility.arrayIndexOf(Activity_Camera.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, Activity_Camera.opticalStabilizationMode)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            Activity_Camera.opticalStabilizationMode = Activity_Camera.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION[Support_Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
                            updateCaptureParametersIndicator();
                            updatePreviewParameters();
                        }
                    }
                });
            }

            else if (((MaterialButton) view).getId() == R.id.fragment_parameters_indicator_button_focalLength) {
                viewingControlBottomSheet = CONTROL_BOTTOM_SHEET_TYPE_FOCAL_LENGTH;

                titleTextView_list_control.setText(R.string.textView_list_control_title_focalLength);

                radioButtonIdArray = new int[(Activity_Camera.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length];
                for (int i = 0; i < (Activity_Camera.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length; i ++) {
                    radioButton = new MaterialRadioButton(Activity_Camera.activity);
                    // region: basic radio button settings
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setButtonTintList(new ColorStateList(
                            new int[][] {new int[] {-android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}},
                            new int[] {Activity_Camera.activity.getColor(R.color.colorSecondary), Activity_Camera.activity.getColor(R.color.colorSecondary)}
                    ));
                    radioButton.setPadding((int) (8f * Activity_Camera.scale + 0.5f), radioButton.getPaddingTop(), radioButton.getPaddingRight(), radioButton.getPaddingBottom());
                    radioButton.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                    radioButton.setTextColor(Activity_Camera.activity.getColor(R.color.colorSecondary));
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    // endregion: basic radio button settings
                    radioButton.setText(Activity_Camera.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[i] + " mm");
                    listRadioGroup_list_control.addView(radioButton);
                    radioButtonIdArray[i] = radioButton.getId();
                }
                viewingControlBottomSheet_radioButtonIdArray = radioButtonIdArray;
                listRadioGroup_list_control.check(radioButtonIdArray[Support_Utility.arrayIndexOf(Activity_Camera.LENS_INFO_AVAILABLE_FOCAL_LENGTHS, Activity_Camera.focalLength)]);

                listRadioGroup_list_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (((MaterialRadioButton) group.findViewById(checkedId)).isPressed()) {
                            Activity_Camera.focalLength = Activity_Camera.LENS_INFO_AVAILABLE_FOCAL_LENGTHS[Support_Utility.arrayIndexOf(radioButtonIdArray, checkedId)];
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

        if (intValueType == CONTROL_BOTTOM_SHEET_TYPE_TORCH) {
            switch (intValue) {
                case 0: string = "OFF"; break;
                case 2: string = "ON"; break;
            }
        } else if (intValueType == CONTROL_BOTTOM_SHEET_TYPE_AWB_MODES) {
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
        } else if (intValueType == CONTROL_BOTTOM_SHEET_TYPE_OIS_MODES) {
            switch (intValue) {
                case 0: string = "OFF"; break;
                case 1: string = "ON"; break;
            }
        }

        return string;
    }
    // endregion: onClickListener, type list_control

}
