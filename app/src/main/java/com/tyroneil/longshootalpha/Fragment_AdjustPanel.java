package com.tyroneil.longshootalpha;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public abstract class Fragment_AdjustPanel extends BottomSheetDialogFragment {

    // region: interface
    public interface AdjustPanelCallback {
        /**
         * This method will be called when parameters have been changed using
         * {@link Fragment_AdjustPanel}.  Then the host activity will update the original
         * {@link Fragment_ParametersIndicator}.
         *
         * In {@link Activity_Camera}, the additional job is to restart the preview request and
         * synchronize all parameters with {@code captureRequestBuilder}.
         */
        public void onAdjustPanelParametersChanged();

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
    }
    protected AdjustPanelCallback adjustPanelCallback;

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

    // region: variables: public changeable
    protected CaptureRequest.Builder requestBuilder;
    // endregion: variables: public changeable

    // region: variables
    protected View layout_adjustPanel;
    // endregion: variables

    // region: fragment lifecycle
    @Override
    public void onDestroyView() {
        adjustPanelCallback.onAdjustPanelStateChanged(null, null);

        super.onDestroyView();
    }
    // endregion: fragment lifecycle

    public abstract void updateAdjustPanel();

    // region: set, is, get methods
    public Fragment_AdjustPanel setRequestBuilder(CaptureRequest.Builder requestBuilder) {
        this.requestBuilder = requestBuilder;
        return this;
    }
    public CaptureRequest.Builder getRequestBuilder() {
        return requestBuilder;
    }
    // endregion: set, is, get methods

}
