package com.dican732.cosc345app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMenuButtons();
    }

    private void setupMenuButtons() {
        setupMenuButton(R.id.intervalsMenuBtn, IntervalsMenu.class);
        setupMenuButton(R.id.melodiesMenuBtn, MelodiesMenu.class);
        setupMenuButton(R.id.rhythmsMenuBtn, RhythmsMenu.class);
    }

    private void setupMenuButton(int btnResourceId, final Class<?> activityToOpen) {
        Button btn = findViewById(btnResourceId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Add slide left animations for when switching between activities. */
                startActivity(new Intent(MainActivity.this, activityToOpen));
            }
        });
    }
}
