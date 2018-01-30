/*
 * Copyright (c) 2016. Jacob Mansfield. All rights reserved
 */

package uk.co.bluesapphiremedia.android.zombiedice;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

public class FinalScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        Intent intent = getIntent();
        final int[] scores = intent.getIntArrayExtra("SCORES");

        // put the scoreboard in ascending order
        Arrays.sort(scores);

        // find the scoreboard in the view
        TableLayout scoreboard = (TableLayout) findViewById(R.id.scoreboard);

        // get the string resources to be formatted
        Resources res = getResources();
        String playerNameFormat = res.getString(R.string.player);

        // Set the scoreboard in the view
        Log.d("brains.PlayActivity", "onCreate: Creating scoreboard");
        // for each player entry in the scoreboard
        for (int i = 0; i < scores.length; i++) {
            Log.d("brains.PlayActivity", "onCreate: adding scoreboard entry "+i);

            // create 2 TextViews
            TextView playerName = new TextView(FinalScreenActivity.this);
            TextView playerScore = new TextView(FinalScreenActivity.this);

            // Set the size of the TextViews
            playerName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            // set the first one to the player's name string resource
            playerName.setText(String.format(playerNameFormat, i + 1));
            // set the second one to the player's score resource, which takes the score twice (once for the number itself, and once to work out which plural is needed)
            playerScore.setText(res.getQuantityString(R.plurals.brains, scores[i], scores[i]));

            // Create a TableRow to put the score into
            TableRow scoreboardEntry = new TableRow(FinalScreenActivity.this);
            // Set the size of the TableRow
            scoreboardEntry.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // Add the TextViews to the TableRow
            scoreboardEntry.addView(playerName);
            scoreboardEntry.addView(playerScore);

            // Add the TableRow to the TableLayout
            scoreboard.addView(scoreboardEntry);
        }

        // Get the title text
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(res.getQuantityString(R.plurals.win_brains, scores[scores.length - 1], scores.length - 1, scores[scores.length - 1]));

        // set the play again button
        Button playAgainButton = (Button) findViewById(R.id.play_again_button);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SelectPlayersActivity.class);
                startActivity(intent);
            }
        });

    }

}
