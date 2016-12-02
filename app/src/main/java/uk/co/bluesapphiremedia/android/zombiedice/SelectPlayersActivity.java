/*
 * Copyright (c) 2016. Jacob Mansfield. All rights reserved
 */

package uk.co.bluesapphiremedia.android.zombiedice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SelectPlayersActivity extends AppCompatActivity {

    TextView num_players_view;

    @Override
    protected void onStart() {
        super.onStart();

        FrameLayout num_players_frame = (FrameLayout) findViewById(R.id.num_players_frame);
        num_players_view = (TextView) findViewById(R.id.num_players);

        num_players_frame.setOnTouchListener(new OnSwipeTouchListener(SelectPlayersActivity.this) {
            public void onSwipeRight() {
                // Decrement number of players
                int num_players = Integer.parseInt(num_players_view.getText().toString());
                if (num_players > 2) {
                    num_players_view.setText(String.valueOf(num_players - 1));
                } else {
                    Toast.makeText(SelectPlayersActivity.this, "Cannot play with less than 2 players", Toast.LENGTH_LONG).show();
                }
            }

            public void onSwipeLeft() {
                // Increment number of players
                int num_players = Integer.parseInt(num_players_view.getText().toString());
                if (num_players == 0) {
                    Toast.makeText(SelectPlayersActivity.this, "What are you on about?", Toast.LENGTH_SHORT).show();
                } else {
                    num_players_view.setText(String.valueOf(num_players + 1));
                }

            }
        });
    }

    public void onContinueClick(View v) {
        int num_players = Integer.parseInt(num_players_view.getText().toString());
        int[] scores = new int[num_players];

        for (int i = 0; i<num_players; i++) {
            scores[i] = 0;
        }

        Intent intent = new Intent(getBaseContext(), PlayActivity.class);
        intent.putExtra("NUM_PLAYERS", num_players);
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
