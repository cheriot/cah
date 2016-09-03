package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.activities.PlayGameView;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.GameContainer;
import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.Player;
import com.cheriot.horriblecards.models.TaskResultListener;
import com.cheriot.horriblecards.models.firebase.FirebasePlayers;
import com.cheriot.horriblecards.recycler.PlayerViewHolder;
import com.cheriot.horriblecards.recycler.PlayersRecyclerAdapter;

import javax.inject.Inject;

/**
 * Created by cheriot on 8/29/16.
 */
public class PlayGamePresenter {

    private PlayGameView mPlayGameView;
    private GameContainer mGameContainer;
    @Inject FirebaseGame mGame;
    @Inject FirebasePlayers mGamePlayers;
    @Inject DealerService mDealerService;

    public PlayGamePresenter(GameContainer gameContainer) {
        mGameContainer = gameContainer;
    }

    public void initGame(String gameKey) {
        mGameContainer.playGame(gameKey).inject(this);
        mGamePlayers.setConnected();

        getPlayGameView().displayPlayers(playersAdapter());

        mGame.findInviteCode(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String inviteCode) {
                getPlayGameView().displayInviteCode(inviteCode);
            }

            @Override
            public void onError(Throwable t) {
                getPlayGameView().displayError("Error accessing game.");
            }
        });
    }

    public void startPlaying() {
        mDealerService.startGame(mGameContainer.getGameKey(), new TaskResultListener() {
            @Override
            public void onSuccess(Object result) {
                getPlayGameView().displayStarted();
            }

            @Override
            public void onError(Throwable e) {
                getPlayGameView().displayStartError();
            }
        });
    }

    private PlayersRecyclerAdapter playersAdapter() {
        return new PlayersRecyclerAdapter(
                Player.class,
                R.layout.list_item_player,
                PlayerViewHolder.class,
                mGamePlayers.getPlayersRef());
    }

    private PlayGameView getPlayGameView() {
        return mPlayGameView;
    }

    public void setPlayGameView(PlayGameView playGameView) {
        mPlayGameView = playGameView;
    }
}
