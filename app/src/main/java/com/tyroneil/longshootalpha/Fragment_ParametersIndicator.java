package com.tyroneil.longshootalpha;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Locale;

public class Fragment_ParametersIndicator extends Fragment {

    // region: interface
    public interface OnIndicatorPressedListener {
        public void onIndicatorPressed(CaptureRequest.Builder requestBuilder, int typeTag);
    }
    OnIndicatorPressedListener onIndicatorPressedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onIndicatorPressedListener = (OnIndicatorPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format(
                    Locale.getDefault(),
                    "%s must implement OnIndicatorPressedListener",
                    context.toString()
            ));
        }
    }
    // endregion: interface

    // region: variables
    private CaptureRequest.Builder requestBuilder;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout_parametersIndicator = inflater.inflate(R.layout.fragment_parameters_indicator, container, false);

        button_exposureTime = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_exposureTime);
        button_sensitivity = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_sensitivity);
        button_aperture = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_aperture);
        button_flash = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_flash);
        button_whiteBalance = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_whiteBalance);
        button_opticalImageStabilization = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_opticalImageStabilization);
        button_focalLength = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_focalLength);
        button_focusDistance = (MaterialButton) layout_parametersIndicator.findViewById(R.id.fragment_parameters_indicator_focusDistance);

        button_exposureTime.setOnClickListener(onClickListener);
        button_sensitivity.setOnClickListener(onClickListener);
        button_aperture.setOnClickListener(onClickListener);
        button_flash.setOnClickListener(onClickListener);
        button_whiteBalance.setOnClickListener(onClickListener);
        button_opticalImageStabilization.setOnClickListener(onClickListener);
        button_focalLength.setOnClickListener(onClickListener);
        button_focusDistance.setOnClickListener(onClickListener);

        return layout_parametersIndicator;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onIndicatorPressedListener.onIndicatorPressed(requestBuilder, view.getId());
        }
    };

    // region: updateParametersIndicator()
    private void updateParametersIndicator() {
    }

    private void updateParametersIndicator(HashMap<Integer, Support_VariableContainer> parametersMap) {
        for (int parameter : parametersMap.keySet()) {
        }
    }
    // endregion: updateParametersIndicator()

    public void setComponentsEnabled(boolean enabled) {
        for (int i = 0; i < ((ConstraintLayout) layout_parametersIndicator).getChildCount(); i ++) {
            (((ConstraintLayout) layout_parametersIndicator).getChildAt(i)).setEnabled(enabled);
        }
    }

    public Fragment_ParametersIndicator setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }

}
