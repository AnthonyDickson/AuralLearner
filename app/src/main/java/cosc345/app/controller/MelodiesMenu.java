package cosc345.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

import cosc345.app.R;
import cosc345.app.model.Difficulty;


public class MelodiesMenu extends VoiceControlActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melodies_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Button easyBtn = findViewById(R.id.melodiesMenu_easyBtn);
        easyBtn.setOnClickListener(view -> MelodiesMenu.this.openExercise(Difficulty.EASY));
        Button mediumBtn = findViewById(R.id.melodiesMenu_mediumBtn);
        mediumBtn.setOnClickListener(view -> MelodiesMenu.this.openExercise(Difficulty.MEDIUM));
        Button hardBtn = findViewById(R.id.melodiesMenu_hardBtn);
        hardBtn.setOnClickListener(view -> MelodiesMenu.this.openExercise(Difficulty.HARD));
        findViewById(R.id.melodiesMenu_helpBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.melodiesMenu_difficultyHelpTitle)
                    .setMessage(R.string.melodiesMenu_difficultyHelpText)
                    .setPositiveButton(R.string.dialogOk, null);
            builder.create()
                    .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openExercise(Difficulty difficulty) {
        Intent intent = new Intent(MelodiesMenu.this, MelodiesExercise.class);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty.toString());
        startActivity(intent);
    }
}
