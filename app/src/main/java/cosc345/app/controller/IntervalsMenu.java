package cosc345.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

import cosc345.app.MainActivity;
import cosc345.app.R;
import cosc345.app.model.Difficulty;
import cosc345.app.model.Intervals;

public class IntervalsMenu extends VoiceControlActivity {
    private Button easyBtn;
    private Button mediumBtn;
    private Button hardBtn;

    //needs voice control stuff
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


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

    public void exerciseEasy(android.view.View view){
        Intent intent = new Intent(IntervalsMenu.this, IntervalExercise.class);
        intent.putExtra("EXTRA_DIFFICULTY", "Easy");
        startActivity(intent);

    }
    public void exerciseMedium(android.view.View view){
        Intent intent = new Intent(IntervalsMenu.this, IntervalExercise.class);
        intent.putExtra("EXTRA_DIFFICULTY", "Medium");
        startActivity(intent);

    }
    public void exerciseHard(android.view.View view){
        Intent intent = new Intent(IntervalsMenu.this, IntervalExercise.class);
        intent.putExtra("EXTRA_DIFFICULTY", "Hard");
        startActivity(intent);

    }
}
