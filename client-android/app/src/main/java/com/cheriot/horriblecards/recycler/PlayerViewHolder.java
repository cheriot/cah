package com.cheriot.horriblecards.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.models.Player;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by cheriot on 8/30/16.
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {

    private Player mPlayer;
    @BindView(R.id.player_display_name) TextView mDisplayNameText;

    public PlayerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setPlayer(Player player) {
        Timber.d("setPlayer %s", player.getDisplayName());
        mPlayer = player;
        mDisplayNameText.setText(mPlayer.getDisplayName());
        if(mPlayer.isConnected()) {
            mDisplayNameText.setTextColor(itemView.getResources().getColor(R.color.black_text));
        } else {
            mDisplayNameText.setTextColor(itemView.getResources().getColor(R.color.gray_text));
        }
    }
}
