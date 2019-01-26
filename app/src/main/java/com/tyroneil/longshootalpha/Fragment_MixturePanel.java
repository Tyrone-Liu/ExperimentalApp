package com.tyroneil.longshootalpha;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class Fragment_MixturePanel extends Fragment_AdjustPanel {

    // region: variables: public changeable
    // endregion: variables: public changeable

    // region: variables
    // endregion: variables

    // region: fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layout_adjustPanel = inflater.inflate(R.layout., container, false);
        initiateLayout();
        updateAdjustPanel();

        adjustPanelCallback.onAdjustPanelStateChanged(this, Integer.valueOf(getTag()));
        return layout_adjustPanel;
    }
    // endregion: fragment lifecycle

    // region: initiate layout
    private void initiateLayout() {
    }
    // endregion: initiate layout

    @Override
    public void updateAdjustPanel() {
    }

}
