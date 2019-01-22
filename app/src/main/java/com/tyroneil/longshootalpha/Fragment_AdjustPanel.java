package com.tyroneil.longshootalpha;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Locale;

public class Fragment_AdjustPanel extends BottomSheetDialogFragment {

    // region: interface
    public interface AdjustPanelCallback {
        /**
         * {@param adjustPanel}: Fragment_AdjustPanel.this
         * {@param typeTag}: tag of this fragment, corresponding {@link R.id} of indicator button
         *
         *
         * This method will be called in {@code onCreateView()} and {@code onDestroyView()}, to
         * inform {@link Activity_Camera} what type of adjust panel is currently opening.
         * {@param adjustPanel} is for faster access to {@link Fragment_AdjustPanel},
         * {@param typeTag} is for determining the type of adjust panel
         *
         * In {@code onCreateView()}, {@param typeTag} will be {@code Integer.valueOf(getTag())},
         * it will register {@param adjustPanel} as {@code currentAdjustPanel}.
         *
         * In {@code onDestroyView()}, {@param typeTag} will be {@code null},
         * {@code currentAdjustPanel} will be set to {@code null}.  In {@link Activity_Sequence},
         * {@code currentParametersIndicator} will also be set to {@code null}.
         */
        public void onAdjustPanelStateChanged(Fragment_AdjustPanel adjustPanel, Integer typeTag);

        /**
         * {@param parametersMap} new value of changed parameters
         *
         *
         * The {@param parametersMap} will be used to update the original
         * {@link Fragment_ParametersIndicator}.  Hopefully it will be faster then read from
         * {@link CaptureRequest.Builder}.
         *
         * In {@link Activity_Camera}, the additional job is to restart the preview request and
         * synchronize all parameters with {@code captureRequestBuilder}.
         */
        public void onAdjustPanelParametersChanged(HashMap<CaptureRequest.Key, Object> parametersMap);
    }
    AdjustPanelCallback adjustPanelCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            adjustPanelCallback = (AdjustPanelCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format(
                    Locale.getDefault(),
                    "%s must implement AdjustPanelCallback",
                    context.toString()
            ));
        }

        if (getTag() == null || getTag().equals("")) {
            throw new NullPointerException("Fragment_AdjustPanel must be assigned with a tag");
        }
    }
    // endregion: interface

    // region: variables
    private CaptureRequest.Builder requestBuilder;
    private View layout_adjustPanel;
    // endregion: variables

    // region: fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        switch (Integer.valueOf(getTag())) {
            case R.id.fragment_parameters_indicator_button_exposureTime:
            case R.id.fragment_parameters_indicator_button_sensitivity:
            case R.id.fragment_parameters_indicator_button_focusDistance:
                layout_adjustPanel = inflater.inflate(R.layout.reuse_content_capture_parameter_range_control, container, false);
                // TODO: initiate layout here
                break;

            case R.id.fragment_parameters_indicator_button_aperture:
            case R.id.fragment_parameters_indicator_button_opticalImageStabilization:
            case R.id.fragment_parameters_indicator_button_focalLength:
            case R.id.fragment_parameters_indicator_button_whiteBalance:
                layout_adjustPanel = inflater.inflate(R.layout.reuse_content_capture_parameter_list_control, container, false);
                // TODO: initiate layout here
                break;

            case R.id.fragment_parameters_indicator_button_flash:
        }

        adjustPanelCallback.onAdjustPanelStateChanged(this, Integer.valueOf(getTag()));
        return layout_adjustPanel;
    }

    @Override
    public void onDestroyView() {
        adjustPanelCallback.onAdjustPanelStateChanged(null, null);

        super.onDestroyView();
    }
    // endregion: fragment lifecycle

    public Fragment_AdjustPanel setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }

}
