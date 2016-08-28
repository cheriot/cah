package com.cheriot.horriblecards.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cheriot.horriblecards.App;
import com.cheriot.horriblecards.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cheriot on 8/28/16.
 */
public class PlayGameActivity extends AppCompatActivity {

    @BindView(R.id.join_game_code) TextView mJoinGameCode;

    public static final String GAME_KEY_PARAM = "GAME_KEY_PARAM";
    public static void startActivity(Activity source, String gameKey) {
        Intent intent = new Intent(source.getApplicationContext(), PlayGameActivity.class);
        intent.putExtra(GAME_KEY_PARAM, gameKey);
        source.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game_activity);

        ButterKnife.bind(this);
        ((App) getApplication()).newActivityComponent(this).inject(this);

        Intent intent = getIntent();
        String gameKey = intent.getStringExtra(GAME_KEY_PARAM);
        mJoinGameCode.setText(gameKey);
    }
}
