package com.cheriot.horriblecards.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cheriot.horriblecards.App;
import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.models.AuthStateListener;
import com.cheriot.horriblecards.presenters.ChooseGamePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by cheriot on 8/22/16.
 */
public class MainActivity extends AppCompatActivity implements ChooseGameView, AuthStateListener {

    // This needs to be in a loader.
    @Inject ChooseGamePresenter mChooseGamePresenter;

    @BindView(R.id.game_link) TextView mGameLink;
    @BindView(R.id.create_game_button) Button mNewGameButton;
    @BindView(R.id.join_game_code) EditText mJoinGameCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);
        ((App) getApplication()).newActivityComponent(this).inject(this);

        mChooseGamePresenter.setChooseGameView(this);
        mJoinGameCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        mJoinGameCode.setImeActionLabel("Join", KeyEvent.KEYCODE_ENTER);
        mJoinGameCode.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mJoinGameCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    joinGame(mJoinGameCode);
                }
                return false; // false so the Keyboard will close
            }
        });

        // Initialize to a signed out state until we know the current state.
        onSignedOut();
    }

    public void createGame(View view) {
        mChooseGamePresenter.createGame();
    }

    public void joinGame(View view) {
        mChooseGamePresenter.joinGame(mJoinGameCode.getText().toString());
    }

    @Override
    public void onSignedIn() {
        Timber.d("Allow game creation.");
        mNewGameButton.setEnabled(true);
    }

    @Override
    public void onSignedOut() {
        mNewGameButton.setEnabled(false);
    }

    @Override
    public void displayError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG);
    }

    @Override
    public void startGame(String gameKey) {
        PlayGameActivity.startActivity(this, gameKey);
    }

    @Override
    public void onStart() {
        super.onStart();
        mChooseGamePresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChooseGamePresenter.onStop();
    }

}
