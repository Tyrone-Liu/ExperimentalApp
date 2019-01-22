package com.tyroneil.longshootalpha;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class Fragment_Settings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.xml_fragment_settings, rootKey);

        EditTextPreference preference_preview_exposure_time_limit_value = (EditTextPreference) findPreference("preference_preview_exposure_time_limit_value");
        EditTextPreference preference_consecutive_capture_interval_value = (EditTextPreference) findPreference("preference_consecutive_capture_interval_value");

        preference_preview_exposure_time_limit_value.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        });
        preference_consecutive_capture_interval_value.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        });

    }
}
