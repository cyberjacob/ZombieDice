/*
 * Copyright (c) 2016. Jacob Mansfield. All rights reserved
 */

package uk.co.bluesapphiremedia.android.zombiedice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SelectPlayersActivity extends AppCompatActivity {

    TextView numPlayersView;
    int numPlayers = 3;

    @Override
    protected void onStart() {
        super.onStart();

        FrameLayout numPlayersFrame = (FrameLayout) findViewById(R.id.num_players_frame);
        numPlayersView = (TextView) findViewById(R.id.num_players);

        numPlayersFrame.setOnTouchListener(new OnSwipeTouchListener(SelectPlayersActivity.this) {
            @Override
            public void onSwipeRight() {
                // Decrement number of players
                if (numPlayers > 2) {
                    numPlayersView.setText(String.valueOf(numPlayers - 1));
                } else {
                    Toast.makeText(SelectPlayersActivity.this, "Cannot play with less than 2 players", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onSwipeLeft() {
                // Increment number of players
                if (numPlayers == 0) {
                    Toast.makeText(SelectPlayersActivity.this, "What are you on about?", Toast.LENGTH_SHORT).show();
                } else {
                    numPlayersView.setText(String.valueOf(numPlayers + 1));
                }

            }
        });
    }

    public void onContinueClick() {
        int[] scores = new int[numPlayers];

        for (int i = 0; i<numPlayers; i++) {
            scores[i] = 0;
        }

        Intent intent = new Intent(getBaseContext(), PlayActivity.class);
        intent.putExtra("NUM_PLAYERS", numPlayers);
        intent.putExtra("SCORES", scores);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_players);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("8F213BB687E54CBF95E00CF902D55C0B").build();
        mAdView.loadAd(adRequest);
    }

}
