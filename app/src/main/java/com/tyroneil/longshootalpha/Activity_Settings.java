package com.tyroneil.longshootalpha;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Activity_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // setup ActionBar
        Toolbar toolbar_actionBar = (Toolbar) findViewById(R.id.activity_settings_toolbar_actionBar);
        setSupportActionBar(toolbar_actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setup preference content
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_container_settings, new Fragment_Settings())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings_about:
                // TODO: show 'about app' information
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
