package cosc345.app.controller;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

import cosc345.app.R;
import cosc345.app.model.Difficulty;

public class ScalesMenu extends VoiceControlActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scales_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Button easyBtn = findViewById(R.id.scalesMenu_easyBtn);
        easyBtn.setOnClickListener(view -> ScalesMenu.this.openExercise(Difficulty.EASY));
        Button mediumBtn = findViewById(R.id.scalesMenu_mediumBtn);
        mediumBtn.setOnClickListener(view -> ScalesMenu.this.openExercise(Difficulty.MEDIUM));
        Button hardBtn = findViewById(R.id.scalesMenu_hardBtn);
        hardBtn.setOnClickListener(view -> ScalesMenu.this.openExercise(Difficulty.HARD));
        findViewById(R.id.scalesMenu_helpBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.scalesMenu_difficultyHelpTitle)
                    .setMessage(R.string.scalesMenu_difficultyHelpText)
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
        Intent intent = new Intent(ScalesMenu.this, ScalesExercise.class);
        intent.putExtra("EXTRA_DIFFICULTY", difficulty.toString());
        startActivity(intent);
    }
}
