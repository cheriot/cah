package com.cheriot.horriblecards.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cheriot.horriblecards.App;
import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.presenters.PlayGamePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cheriot on 8/28/16.
 */
public class PlayGameActivity extends AppCompatActivity implements PlayGameView {

    @Inject PlayGamePresenter mPlayGamePresenter;
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

        mPlayGamePresenter.setPlayGameView(this);

        Intent intent = getIntent();
        String gameKey = intent.getStringExtra(GAME_KEY_PARAM);
        mJoinGameCode.setText(gameKey);
        mPlayGamePresenter.findGame(gameKey);
    }

    @Override
    public void displayGame(String gameCode) {
        mJoinGameCode.setText(gameCode);
    }

    @Override
    public void displayError(String msg) {
        Snackbar.make(mJoinGameCode, msg, Snackbar.LENGTH_LONG).show();
    }
}
