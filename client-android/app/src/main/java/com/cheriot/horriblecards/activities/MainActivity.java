package com.cheriot.horriblecards.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheriot.horriblecards.App;
import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.AuthStateListener;
import com.cheriot.horriblecards.models.GameService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cheriot on 8/22/16.
 */
public class MainActivity extends AppCompatActivity implements GameView {

    private GameService mGameService;
    @Inject AuthService mAuthService;
    AuthStateListener mAuthStateListener = new AuthStateListener() {
        @Override
        public void onSignedIn() {
            mNewGameButton.setEnabled(true);
        }

        @Override
        public void onSignedOut() {
            mNewGameButton.setEnabled(false);
        }
    };

    @BindView(R.id.create_game_button) Button mNewGameButton;
    @BindView(R.id.game_link) TextView mGameLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);
        ((App)getApplication()).getAppComponent().inject(this);

        mGameService = new GameService(this, null);
        // Initialize to a signed out state until we know the current state.
        mAuthStateListener.onSignedOut();
    }

    public void createGame(View view) {
        mGameService.createGame("fake-user-id");
    }

    @Override
    public void displayGameUrl(String gameUrl) {
        mGameLink.setText(gameUrl);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuthService.addAuthStateListener(mAuthStateListener);
        mAuthService.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuthService.stop();
        mAuthService.removeAuthStateListener(mAuthStateListener);
    }

}
