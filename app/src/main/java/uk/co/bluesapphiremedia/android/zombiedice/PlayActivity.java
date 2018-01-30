/*
 * Copyright (c) 2016. Jacob Mansfield. All rights reserved
 */

package uk.co.bluesapphiremedia.android.zombiedice;

import android.content.Intent;
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

    int numberShotguns = 0;
    int currentScore = 0;

    private static final String TAG = "brains.PlayActivity";
    private static final String SCORES = "SCORES";
    private static final String NUM_PLAYERS = "NUM_PLAYERS";
    private static final String CURRENT_PLAYER = "CURRENT_PLAYER";
    private static final String WINNING_PLAYER = "WINNING_PLAYER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Log.d(TAG, "onCreate: Starting!");

        // Get the scores array, number of players, and current player from the inbound intent
        Intent intent = getIntent();
        final int[] scores = intent.getIntArrayExtra(SCORES);
        final int numPlayers = intent.getIntExtra(NUM_PLAYERS, 3);
        final int[] currentPlayer = {intent.getIntExtra(CURRENT_PLAYER, 0)};
        final int[] winningPlayer = {intent.getIntExtra(WINNING_PLAYER, -1)};

        // make sure we have a score table
        assert scores != null;

        setupScoreboard(numPlayers, scores);

        // set the title
        TextView titleText = (TextView) findViewById(R.id.current_player_text);
        titleText.setText(String.format(getResources().getString(R.string.player_turn), currentPlayer[0] + 1));

        // Make sure all three are hidden
        updateShotguns();

        // Find the three score indicators
        final TextView scoreThisTurnText = (TextView) findViewById(R.id.score_this_turn);
        TextView scoreInBankText = (TextView) findViewById(R.id.score_in_bank);
        final TextView scoreToWinText = (TextView) findViewById(R.id.score_to_win);

        // Set their values
        scoreThisTurnText.setText("0");
        scoreInBankText.setText(String.valueOf(scores[currentPlayer[0]]));
        scoreToWinText.setText(String.valueOf(13-scores[currentPlayer[0]]));

        // Set the functions for the +/- brain buttons
        final Button addBrainButton = (Button) findViewById(R.id.add_brain);
        addBrainButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScore++;
                        scoreThisTurnText.setText(String.valueOf(currentScore));
                        scoreToWinText.setText(String.valueOf(13-(currentScore+scores[currentPlayer[0]])));
                    }
                });

        final Button removeBrainButton = (Button) findViewById(R.id.remove_brain);
        removeBrainButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentScore>0) {
                            currentScore--;
                        } else {
                            Toast.makeText(PlayActivity.this, "Wait, what?!?", Toast.LENGTH_LONG).show();
                        }
                        scoreThisTurnText.setText(String.valueOf(currentScore));
                        scoreToWinText.setText(String.valueOf(13-(currentScore+scores[currentPlayer[0]])));
                    }
                });

        // same again for the shotgun buttons
        final Button addShotgunButton = (Button) findViewById(R.id.add_shotgun);
        addShotgunButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (numberShotguns == 3) {
                            Toast.makeText(PlayActivity.this, "You're already out!", Toast.LENGTH_LONG).show();
                        } else {
                            numberShotguns++;
                        }
                        updateShotguns();
                    }
                }
        );

        final Button removeShotgunButton = (Button) findViewById(R.id.remove_shotgun);
        removeShotgunButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (numberShotguns == 0) {
                            Toast.makeText(PlayActivity.this, "What are you on about?", Toast.LENGTH_SHORT).show();
                        } else {
                            numberShotguns--;
                        }
                        updateShotguns();
                    }
                }
        );

        // and finally, set up the end turn button
        final Button endTurnButton = (Button) findViewById(R.id.end_turn);
        endTurnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Check if the player has less than 3 shotguns
                        if (numberShotguns <3) {
                            // They do, so increase their 'banked' score
                            scores[currentPlayer[0]] += currentScore;
                        }

                        int nextPlayer = currentPlayer[0]+1;

                        if (nextPlayer == numPlayers) {
                            // back to the first player
                            nextPlayer = 0;
                        }

                        // Check if the current player is the last person who will be playing
                        if (winningPlayer[0] == nextPlayer) {

                            // It is, so let's go to the finishing screen
                            Intent intent = new Intent(PlayActivity.this, FinalScreenActivity.class);

                            // pack the scores
                            intent.putExtra(SCORES, scores);

                            // let's go
                            startActivity(intent);
                        }

                        // Check if the current player has enough brains to finish the game
                        if ((currentScore>=13) && (winningPlayer[0] == -1)) {
                            // They do!
                            // record them as the finishing player
                            winningPlayer[0] = currentPlayer[0];
                        }

                        // increment to the next player
                        currentPlayer[0] = nextPlayer;

                        // go to the play activity again
                        // prepare an intent to pass over some variables
                        Intent intent = new Intent(getBaseContext(), PlayActivity.class);

                        // pack the variables into the intent
                        intent.putExtra("SCORES", scores);
                        intent.putExtra(NUM_PLAYERS, numPlayers);
                        intent.putExtra(CURRENT_PLAYER, currentPlayer[0]);
                        intent.putExtra(WINNING_PLAYER, winningPlayer[0]);

                        // activate the intent
                        startActivity(intent);
                    }
                }
        );

        Log.d(TAG, "onCreate: Finished!");
    }

    private void setupScoreboard(int numPlayers, int[] scores) {
        // Set the scoreboard in the view
        Log.d(TAG, "onCreate: Creating scoreboard");

        String playerNameFormat = getResources().getString(R.string.player);

        // find the scoreboard in the view
        TableLayout scoreboard = (TableLayout) findViewById(R.id.scoreboard);

        // for each player entry in the scoreboard
        for (int i = 0; i < numPlayers; i++) {
            Log.d(TAG, "onCreate: adding scoreboard entry "+i);

            // create 2 TextViews
            TextView playerName = new TextView(PlayActivity.this);
            TextView playerScore = new TextView(PlayActivity.this);

            // Set the size of the TextViews
            playerName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            // set the first one to the player's name string resource
            playerName.setText(String.format(playerNameFormat, i + 1));
            // set the second one to the player's score resource, which takes the score twice (once for the number itself, and once to work out which plural is needed)
            playerScore.setText(getResources().getQuantityString(R.plurals.brains, scores[i], scores[i]));

            // Create a TableRow to put the score into
            TableRow scoreboardEntry = new TableRow(PlayActivity.this);
            // Set the size of the TableRow
            scoreboardEntry.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            // Add the TextViews to the TableRow
            scoreboardEntry.addView(playerName);
            scoreboardEntry.addView(playerScore);

            // Add the TableRow to the TableLayout
            scoreboard.addView(scoreboardEntry);
        }
    }

    private void updateShotguns() {
        // find the shotgun indicators
        ImageView shotgun1 = (ImageView) findViewById(R.id.shotgun1);
        ImageView shotgun2 = (ImageView) findViewById(R.id.shotgun2);
        ImageView shotgun3 = (ImageView) findViewById(R.id.shotgun3);

        if (numberShotguns ==0) {
            shotgun1.setVisibility(ImageView.INVISIBLE);
            shotgun2.setVisibility(ImageView.INVISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (numberShotguns ==1) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.INVISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (numberShotguns ==2) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.VISIBLE);
            shotgun3.setVisibility(ImageView.INVISIBLE);
        } else if (numberShotguns ==3) {
            shotgun1.setVisibility(ImageView.VISIBLE);
            shotgun2.setVisibility(ImageView.VISIBLE);
            shotgun3.setVisibility(ImageView.VISIBLE);
        }
    }
}

