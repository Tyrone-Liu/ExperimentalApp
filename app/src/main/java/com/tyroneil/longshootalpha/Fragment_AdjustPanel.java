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
        public void onAdjustPanelStateChanged(int state);
        public void onAdjustPanelParametersChanged(CaptureRequest.Builder requestBuilder, HashMap<Integer, Support_VariableContainer> parametersMap);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        switch (Integer.valueOf(getTag())) {
            case R.id.fragment_parameters_indicator_exposureTime:
            case R.id.fragment_parameters_indicator_sensitivity:
            case R.id.fragment_parameters_indicator_focusDistance:
                layout_adjustPanel = inflater.inflate(R.layout.reuse_content_capture_parameter_range_control, container, false);
                break;

            case R.id.fragment_parameters_indicator_aperture:
            case R.id.fragment_parameters_indicator_opticalImageStabilization:
            case R.id.fragment_parameters_indicator_focalLength:
                layout_adjustPanel = inflater.inflate(R.layout.reuse_content_capture_parameter_list_control, container, false);
                break;

            case R.id.fragment_parameters_indicator_flash:
            case R.id.fragment_parameters_indicator_whiteBalance:
        }

        return layout_adjustPanel;
    }

    public Fragment_AdjustPanel setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }

}
