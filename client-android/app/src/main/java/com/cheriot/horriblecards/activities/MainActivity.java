package com.cheriot.horriblecards.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.models.AuthenticationService;
import com.cheriot.horriblecards.models.GameService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cheriot on 8/22/16.
 */
public class MainActivity extends AppCompatActivity implements GameView {
    static final String LOG_TAG = MainActivity.class.getSimpleName();
    private GameService mGameService;
    private AuthenticationService mAuthenticationService;

    @BindView(R.id.create_game_button) Button mNewGameButton;
    @BindView(R.id.game_link) TextView mGameLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        mGameService = new GameService(this, null);
        mAuthenticationService = new AuthenticationService(null);
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
        mAuthenticationService.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuthenticationService.stop();
    }

}
