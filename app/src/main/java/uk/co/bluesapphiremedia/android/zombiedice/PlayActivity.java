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
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Log.d("brains.PlayActivity", "onCreate: Starting!");

        // Get the scores array, number of players, and current player from the inbound intent
        Intent intent = getIntent();
        final int[] scores = intent.getIntArrayExtra("SCORES");
        final int num_players = intent.getIntExtra("NUM_PLAYERS", 3);
        final int[] current_player = {intent.getIntExtra("CURRENT_PLAYER", 0)};
        final int[] winning_player = {intent.getIntExtra("WINNING_PLAYER", -1)};

        // make sure we have a score table
        assert scores != null;

        // find the scoreboard in the view
        TableLayout scoreboard = (TableLayout) findViewById(R.id.scoreboard);

        // get the string resources to be formatted
        Resources res = getResources();
        String player_name_format = res.getString(R.string.player);

        // Set the scoreboard in the view
        Log.d("brains.PlayActivity", "onCreate: Creating scoreboard");
        // for each player entry in the scoreboard
        for (int i = 0; i < num_players; i++) {
            Log.d("brains.PlayActivity", "onCreate: adding scoreboard entry "+String.valueOf(i));

            // create 2 TextViews
            TextView player_name = new TextView(PlayActivity.this);
            TextView player_score = new TextView(PlayActivity.this);

            // Set the size of the TextViews
            player_name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            // set the first one to the player's name string resource
            player_name.setText(String.format(player_name_format, i + 1));
            // set the second one to the player's score resource, which takes the score twice (once for the number itself, and once to work out which plural is needed)
            player_score.setText(res.getQuantityString(R.plurals.brains, scores[i], scores[i]));

            // Create a TableRow to put the score into
            TableRow scoreboard_entry = new TableRow(PlayActivity.this);
            // Set the size of the TableRow
            scoreboard_entry.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // Add the TextViews to the TableRow
            scoreboard_entry.addView(player_name);
            scoreboard_entry.addView(player_score);

            // Add the TableRow to the TableLayout
            scoreboard.addView(scoreboard_entry);
        }

        // set the title
        TextView title_text = (TextView) findViewById(R.id.current_player_text);
        title_text.setText(String.format(res.getString(R.string.player_turn), current_player[0] + 1));

        // Players always start with 0 shotguns. This needs to be an array so we can modify it inside the button functions
        final int[] number_shotguns = {0};

        // Make sure all three are hidden
        updateShotguns(number_shotguns[0]);

        // Find the three score indicators
        final TextView score_this_turn_text = (TextView) findViewById(R.id.score_this_turn);
        TextView score_in_bank_text = (TextView) findViewById(R.id.score_in_bank);
        final TextView score_to_win_text = (TextView) findViewById(R.id.score_to_win);

        // Set their values
        score_this_turn_text.setText("0");
        score_in_bank_text.setText(String.valueOf(scores[current_player[0]]));
        score_to_win_text.setText(String.valueOf(13-scores[current_player[0]]));

        // Set the functions for the +/- brain buttons
        final Button add_brain_button = (Button) findViewById(R.id.add_brain);
        add_brain_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        int current_score = Integer.parseInt(score_this_turn_text.getText().toString());
                        current_score++;
                        score_this_turn_text.setText(String.valueOf(current_score));
                        score_to_win_text.setText(String.valueOf(13-(current_score+scores[current_player[0]])));
                    }
                });

        final Button remove_brain_button = (Button) findViewById(R.id.remove_brain);
        remove_brain_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        int current_score = Integer.parseInt(score_this_turn_text.getText().toString());
                        if (current_score>0) {
                            current_score--;
                        } else {
                            Toast.makeText(PlayActivity.this, "Wait, what?!?", Toast.LENGTH_LONG).show();
                        }
                        score_this_turn_text.setText(String.valueOf(current_score));
                        score_to_win_text.setText(String.valueOf(13-(current_score+scores[current_player[0]])));
                    }
                });

        // same again for the shotgun buttons
        final Button add_shotgun_button = (Button) findViewById(R.id.add_shotgun);
        add_shotgun_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (number_shotguns[0] == 3) {
                            Toast.makeText(PlayActivity.this, "You're already out!", Toast.LENGTH_LONG).show();
                        } else {
                            number_shotguns[0]++;
                        }
                        updateShotguns(number_shotguns[0]);
                    }
                }
        );

        final Button remove_shotgun_button = (Button) findViewById(R.id.remove_shotgun);
        remove_shotgun_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (number_shotguns[0] == 0) {
                            Toast.makeText(PlayActivity.this, "What are you on about?", Toast.LENGTH_SHORT).show();
                        } else {
                            number_shotguns[0]--;
                        }
                        updateShotguns(number_shotguns[0]);
                    }
                }
        );

        // and finally, set up the end turn button
        final Button end_turn_button = (Button) findViewById(R.id.end_turn);
        end_turn_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the player's score
                        int current_score = Integer.parseInt(score_this_turn_text.getText().toString());

                        // Check if the player has less than 3 shotguns
                        if (number_shotguns[0]<3) {
                            // They do, so increase their 'banked' score
                            scores[current_player[0]] += current_score;
                        }

                        int next_player = current_player[0]+1;

                        if (next_player == num_players) {
                            // back to the first player
                            next_player = 0;
                        }

                        // Check if the current player is the last person who will be playing
                        if (winning_player[0] == next_player) {
                            Toast.makeText(PlayActivity.this, "Starting FinalScreen", Toast.LENGTH_LONG).show();

                            // It is, so let's go to the finishing screen
                            Intent intent = new Intent(PlayActivity.this, FinalScreenActivity.class);

                            // pack the scores
                            intent.putExtra("SCORES", scores);

                            // let's go
                            startActivity(intent);
                        } else {
                            Toast.makeText(PlayActivity.this, String.valueOf(winning_player[0])+" does not equal "+String.valueOf(next_player), Toast.LENGTH_LONG).show();
                        }

                        // Check if the current player has enough brains to finish the game
                        if ((current_score>=13) && (winning_player[0] == -1)) {
                            // They do!
                            // record them as the finishing player
                            winning_player[0] = current_player[0];
                        }

                        // increment to the next player
                        current_player[0] = next_player;

                        // go to the play activity again
                        // prepare an intent to pass over some variables
                        Intent intent = new Intent(getBaseContext(), PlayActivity.class);

                        // pack the variables into the intent
                        intent.putExtra("SCORES", scores);
                        intent.putExtra("NUM_PLAYERS", num_players);
                        intent.putExtra("CURRENT_PLAYER", current_player[0]);
                        intent.putExtra("WINNING_PLAYER", winning_player[0]);

                        // activate the intent
                        startActivity(intent);
                    }
                }
        );

        Log.d("brains.PlayActivity", "onCreate: Finished!");
    }

    private void updateShotguns(int number_shotguns) {
        // find the shotgun indicators
        ImageView shotgun1 = (ImageView) findViewById(R.id.shotgun1);
        ImageView shotgun2 = (ImageView) findViewById(R.id.shotgun2);
        ImageView shotgun3 = (ImageView) findViewById(R.id.shotgun3);

        if (number_shotguns==0) {
            shotgun1.setVisibility(ImageView.INVISIBLE);
            shotgun2.setVisibility(ImageView.INVISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (number_shotguns==1) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.INVISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (number_shotguns==2) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.VISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (number_shotguns==3) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.VISIBLE);
            shotgun3.setVisibility(ImageView.VISIBLE);
        }
    }
}

