package com.tyroneil.experimentalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    public static final String EXTRA_MESSAGE = "com.tyroneil.experimentalapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void messageSend(View view) {
        Intent intent = new Intent(this, MessageDisplayActivity.class);
        EditText editText = (EditText) findViewById(R.id.messageText);
        String messageText = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, messageText);
        startActivity(intent);
    }

    public void cameraView(View view) {
        Intent intent = new Intent(this, CameraViewActivity.class);
        startActivity(intent);
    }
}
