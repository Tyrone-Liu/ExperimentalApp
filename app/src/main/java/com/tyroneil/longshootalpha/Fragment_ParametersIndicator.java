package com.tyroneil.longshootalpha;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class Fragment_ParametersIndicator extends Fragment {

    // region: interface
    public interface ParametersIndicatorCallback {
        /**
         * {@param parametersIndicator}: Fragment_ParametersIndicator.this
         * {@param requestBuilder}: {@link CaptureRequest.Builder} of this fragment
         * {@param indicatorId}: {@link R.id} of the corresponding indicator button.
         *
         *
         * In {@link Activity_Sequence}, after this method been called, it will register
         * {@param parametersIndicator} as {@code currentParametersIndicator}.
         *
         * The {@param requestBuilder} is going to be passed to {@link Fragment_AdjustPanel}.
         *
         * The {@param indicatorId} will be used as a fragment tag for {@link Fragment_AdjustPanel},
         * to indicate the type of parameters adjustment.  It will also be used in
         * {@link Activity_Sequence} to determine what type of {@link Fragment_AdjustPanel} to open.
         */
        public void onIndicatorPressed(
                Fragment_ParametersIndicator parametersIndicator,
                CaptureRequest.Builder requestBuilder,
                int indicatorId
        );
    }
    private ParametersIndicatorCallback parametersIndicatorCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            parametersIndicatorCallback = (ParametersIndicatorCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format(
                    Locale.getDefault(),
                    "%s must implement ParametersIndicatorCallback",
                    context.toString()
            ));
        }
    }
    // endregion: interface

    // region: variables: public changeable
    private CaptureRequest.Builder requestBuilder;
    // endregion: variables: public changeable

    // region: variables
    private View layout_parametersIndicator;

    private MaterialButton
            button_exposureTime,
            button_sensitivity,
            button_aperture,
            button_flash,
            button_whiteBalance,
            button_opticalImageStabilization,
            button_focalLength,
            button_focusDistance;
    // endregion: variables

    // region: fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout_parametersIndicator = inflater.inflate(R.layout.fragment_parameters_indicator, container, false);
        initiateLayout();
        updateParametersIndicator();

        return layout_parametersIndicator;
    }
    // endregion: fragment lifecycle

    // region: initiate layout
    private void initiateLayout() {
        button_exposureTime = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_exposureTime);
        button_sensitivity = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_sensitivity);
        button_aperture = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_aperture);
        button_flash = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_flash);
        button_whiteBalance = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_whiteBalance);
        button_opticalImageStabilization = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_opticalImageStabilization);
        button_focalLength = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_focalLength);
        button_focusDistance = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_button_focusDistance);

        button_exposureTime.setOnClickListener(onClickListener_parametersIndicator);
        button_sensitivity.setOnClickListener(onClickListener_parametersIndicator);
        button_aperture.setOnClickListener(onClickListener_parametersIndicator);
        button_flash.setOnClickListener(onClickListener_parametersIndicator);
        button_whiteBalance.setOnClickListener(onClickListener_parametersIndicator);
        button_opticalImageStabilization.setOnClickListener(onClickListener_parametersIndicator);
        button_focalLength.setOnClickListener(onClickListener_parametersIndicator);
        button_focusDistance.setOnClickListener(onClickListener_parametersIndicator);
    }

    private View.OnClickListener onClickListener_parametersIndicator = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            parametersIndicatorCallback.onIndicatorPressed(Fragment_ParametersIndicator.this, requestBuilder, view.getId());
        }
    };
    // endregion: initiate layout

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

            button_aperture.setText(getString(
                    R.string.fragment_parameters_indicator_button_aperture,
                    requestBuilder.get(CaptureRequest.LENS_APERTURE)
            ));
        } else {
            button_exposureTime.setText(getText(R.string.fragment_parameters_indicator_button_exposureTime_auto));
            button_sensitivity.setText(getText(R.string.fragment_parameters_indicator_button_sensitivity_auto));
            button_aperture.setText(getText(R.string.fragment_parameters_indicator_button_aperture_auto));
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
        button_focalLength.setText(getString(
                 R.string.fragment_parameters_indicator_button_focalLength,
                 requestBuilder.get(CaptureRequest.LENS_FOCAL_LENGTH)
        ));

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
    public Fragment_ParametersIndicator setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }
    // endregion: set, is, get methods

}
