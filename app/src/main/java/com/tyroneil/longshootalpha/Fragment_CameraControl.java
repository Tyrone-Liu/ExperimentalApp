package com.tyroneil.longshootalpha;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Fragment_CameraControl extends Fragment {

    // region: interface
    public interface CameraControlCallback {
        /**
         * {@param cameraControlId}: {@link R.id} of the corresponding camera control button.
         *
         *
         * This method will inform {@link Activity_Camera} which control button has been pressed.
         */
        public void onCameraControlPressed(int cameraControlId);

        /**
         * {@param requestBuilder}: {@link CaptureRequest.Builder} of this fragment
         * {@param indicatorId}: {@link R.id} of the corresponding indicator button.
         *
         *
         * The {@param requestBuilder} is going to be passed to {@link Fragment_AdjustPanel}.
         *
         * The {@param indicatorId} will be used as a fragment tag for {@link Fragment_AdjustPanel},
         * to indicate the type of parameters adjustment.  It will also be used in
         * {@link Activity_Camera} to determine what type of {@link Fragment_AdjustPanel} to open.
         */
        public void onIndicatorPressed(CaptureRequest.Builder requestBuilder, int indicatorId);
    }
    private CameraControlCallback cameraControlCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cameraControlCallback = (CameraControlCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format(
                    Locale.getDefault(),
                    "%s must implement CameraControlCallback",
                    context.toString()
            ));
        }
    }
    // endregion: interface

    // region: variables: public changeable
    private CaptureRequest.Builder requestBuilder;
    private boolean compact;
    // endregion: variables: public changeable

    // region: variables
    private View layout_cameraControl;

    private ArrayList<MaterialButton> buttonsList_parametersIndicator;
    private MaterialButton
            button_exposureTime,
            button_sensitivity,
            button_flash,
            button_whiteBalance,
            button_opticalImageStabilization,
            button_focusDistance;
    private MaterialButton
            button_aperture,
            button_focalLength;

    private ProgressBar progressBar_capturing;
    private MaterialButton button_capture, button_sequence, button_settings;

    public enum BUTTON_CAPTURE_STATE {NORMAL, CAPTURING, SEQUENCING}
    // endregion: variables

    // region: fragment lifecycle
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (compact) {
            layout_cameraControl = inflater.inflate(R.layout.fragment_camera_control_compact, container, false);
        } else {
            layout_cameraControl = inflater.inflate(R.layout.fragment_camera_control, container, false);
        }
        initiateLayout(compact);
        updateParametersIndicator();

        return layout_cameraControl;
    }
    // endregion: fragment lifecycle

    // region: initiate layout
    private void initiateLayout(boolean compact) {
        if (compact) {
            button_exposureTime = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_exposureTime);
            button_sensitivity = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_sensitivity);
            button_flash = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_flash);
            button_whiteBalance = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_whiteBalance);
            button_opticalImageStabilization = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_opticalImageStabilization);
            button_focusDistance = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_focusDistance);
            buttonsList_parametersIndicator = new ArrayList<MaterialButton>(Arrays.asList(
                    button_exposureTime,
                    button_sensitivity,
                    button_flash,
                    button_whiteBalance,
                    button_opticalImageStabilization,
                    button_focusDistance
            ));

            progressBar_capturing = (ProgressBar) layout_cameraControl.findViewById(R.id.fragment_camera_control_progressBar_capturing);
            button_capture = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_camera_control_button_capture);
            button_sequence = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_camera_control_button_sequence);
            button_settings = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_camera_control_button_settings);

            button_exposureTime.setOnClickListener(onClickListener_parametersIndicator);
            button_sensitivity.setOnClickListener(onClickListener_parametersIndicator);
            button_flash.setOnClickListener(onClickListener_parametersIndicator);
            button_whiteBalance.setOnClickListener(onClickListener_parametersIndicator);
            button_opticalImageStabilization.setOnClickListener(onClickListener_parametersIndicator);
            button_focusDistance.setOnClickListener(onClickListener_parametersIndicator);

            button_capture.setOnClickListener(onClickListener_cameraControl);
            button_sequence.setOnClickListener(onClickListener_cameraControl);
            button_settings.setOnClickListener(onClickListener_cameraControl);
        } else {
            initiateLayout(! compact);

            button_aperture = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_aperture);
            button_focalLength = (MaterialButton) layout_cameraControl.findViewById(R.id.fragment_parameters_indicator_button_focalLength);
            buttonsList_parametersIndicator.addAll(Arrays.asList(
                    button_aperture,
                    button_focalLength
            ));

            button_aperture.setOnClickListener(onClickListener_parametersIndicator);
            button_focalLength.setOnClickListener(onClickListener_parametersIndicator);
        }
    }

    private View.OnClickListener onClickListener_parametersIndicator = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cameraControlCallback.onIndicatorPressed(requestBuilder, view.getId());
        }
    };

    private View.OnClickListener onClickListener_cameraControl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cameraControlCallback.onCameraControlPressed(view.getId());
        }
    };
    // endregion: initiate layout

    public void setButtonCaptureState(BUTTON_CAPTURE_STATE buttonCaptureState) {
        button_capture.setWidth(button_capture.getWidth());
        button_capture.setHeight(button_capture.getHeight());

        if (buttonCaptureState == BUTTON_CAPTURE_STATE.NORMAL) {
            setParametersIndicatorClickable(true);

            button_capture.setText(R.string.fragment_camera_control_button_capture);
            button_capture.setEnabled(true);
            progressBar_capturing.setElevation(0f);
        } else {
            setParametersIndicatorClickable(false);

            if (buttonCaptureState == BUTTON_CAPTURE_STATE.CAPTURING) {
                // from the material design documents, the elevation of a button is within [2dp, 8dp]
                // therefore, set the elevation of a progressBar to 8dp will bring it to front
                button_capture.setText(R.string.fragment_camera_control_button_capture_capturing);
                button_capture.setEnabled(false);
                progressBar_capturing.setElevation(8f * Activity_Camera.scale);
            }

            else if (buttonCaptureState == BUTTON_CAPTURE_STATE.SEQUENCING) {
                button_capture.setText(R.string.fragment_camera_control_button_capture_sequencing);
                // TODO: replace the parameter control area to progress indicator
            }
        }
    }

    private void setParametersIndicatorClickable(boolean enabled) {
        for (MaterialButton button_indicator : buttonsList_parametersIndicator) {
            button_indicator.setEnabled(enabled);
        }
    }

    public void updateParametersIndicator() {
        if (
                requestBuilder.get(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF
                || requestBuilder.get(CaptureRequest.CONTROL_MODE) == CaptureRequest.CONTROL_MODE_OFF
        ) {
            button_exposureTime.setText(getString(
                    R.string.fragment_parameters_indicator_button_exposureTime,
                    Support_Utility.formatCaptureRequestKeyValue(
                            CaptureRequest.SENSOR_EXPOSURE_TIME,
                            requestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME),
                            true
                    )
            ));
            button_sensitivity.setText(getString(
                    R.string.fragment_parameters_indicator_button_sensitivity,
                    requestBuilder.get(CaptureRequest.SENSOR_SENSITIVITY)
            ));

            if (buttonsList_parametersIndicator.contains(button_aperture)) {
                button_aperture.setText(getString(
                        R.string.fragment_parameters_indicator_button_aperture,
                        requestBuilder.get(CaptureRequest.LENS_APERTURE)
                ));
            }
        } else {
            button_exposureTime.setText(getText(R.string.fragment_parameters_indicator_button_exposureTime_auto));
            button_sensitivity.setText(getText(R.string.fragment_parameters_indicator_button_sensitivity_auto));
            if (buttonsList_parametersIndicator.contains(button_aperture)) {
                button_aperture.setText(getText(R.string.fragment_parameters_indicator_button_aperture_auto));
            }
        }

        button_flash.setText(getString(
                R.string.fragment_parameters_indicator_button_flash,
                Support_Utility.formatCaptureRequestKeyValue(
                        CaptureRequest.FLASH_MODE,
                        requestBuilder.get(CaptureRequest.FLASH_MODE),
                        true
                )
        ));
        button_whiteBalance.setText(getString(
                R.string.fragment_parameters_indicator_button_whiteBalance,
                Support_Utility.formatCaptureRequestKeyValue(
                        CaptureRequest.CONTROL_AWB_MODE,
                        requestBuilder.get(CaptureRequest.CONTROL_AWB_MODE),
                        true
                )
        ));
        button_opticalImageStabilization.setText(getString(
                R.string.fragment_parameters_indicator_button_opticalImageStabilization,
                Support_Utility.formatCaptureRequestKeyValue(
                        CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                        requestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE),
                        true
                )
        ));
        if (buttonsList_parametersIndicator.contains(button_focalLength)) {
            button_focalLength.setText(getString(
                    R.string.fragment_parameters_indicator_button_focalLength,
                    requestBuilder.get(CaptureRequest.LENS_FOCAL_LENGTH)
            ));
        }

        if (
                requestBuilder.get(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_OFF
                || requestBuilder.get(CaptureRequest.CONTROL_MODE) == CaptureRequest.CONTROL_MODE_OFF
        ) {
            button_focusDistance.setText(getString(
                    R.string.fragment_parameters_indicator_button_focusDistance,
                    Support_Utility.formatCaptureRequestKeyValue(
                            CaptureRequest.LENS_FOCUS_DISTANCE,
                            requestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE),
                            true
                    )
            ));
        } else {
            button_focusDistance.setText(getText(R.string.fragment_parameters_indicator_button_focusDistance_auto));
        }
    }

    // region: set, is, get methods
    public Fragment_CameraControl setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }

    public Fragment_CameraControl setCompact(boolean compact) {
        this.compact = compact;
        return this;
    }
    public boolean isCompact() {
        return compact;
    }
    // endregion: set, is, get methods

}
