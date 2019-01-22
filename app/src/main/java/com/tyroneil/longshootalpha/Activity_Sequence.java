package com.tyroneil.longshootalpha;

import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

public class Activity_Sequence extends AppCompatActivity implements
        Fragment_ParametersIndicator.ParametersIndicatorCallback,
        Fragment_AdjustPanel.AdjustPanelCallback
{

    // region: variables: shared
    private static FragmentManager fragmentManager;

    private Fragment_ParametersIndicator currentParametersIndicator;
    // region: variables: shared

    // region: activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar_actionBar = (Toolbar) findViewById(R.id.activity_sequence_toolbar_actionBar);
        setSupportActionBar(toolbar_actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    // endregion: activity lifecycle

    // region: setup options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_sequence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_sequence_start:
                // TODO: start the sequence
                return true;

            case R.id.action_sequence_loop:
                // TODO: set this sequence on loop
                menuItem.setChecked(! menuItem.isChecked());
                return false;

            case R.id.action_sequence_clear:
                // TODO: clear everything in the sequence
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    // endregion: setup options menu

    // region: override interface methods
    @Override
    public void onIndicatorPressed(
            Fragment_ParametersIndicator parametersIndicator,
            CaptureRequest.Builder requestBuilder,
            int indicatorId
    ) {
        currentParametersIndicator = parametersIndicator;

        Fragment_AdjustPanel fragment_adjustPanel = new Fragment_AdjustPanel();
        fragment_adjustPanel
                .setRequestBuilder(requestBuilder)
                .showNow(fragmentManager, String.valueOf(indicatorId));
    }

    @Override
    public void onAdjustPanelParametersChanged(HashMap<CaptureRequest.Key, Object> parametersMap) {
        // TODO: update parameters indicator
    }

    @Override
    public void onAdjustPanelStateChanged(Fragment_AdjustPanel adjustPanel, Integer typeTag) {
        if (adjustPanel == null) {
            currentParametersIndicator = null;
        }
    }
    // endregion: override interface methods

}
