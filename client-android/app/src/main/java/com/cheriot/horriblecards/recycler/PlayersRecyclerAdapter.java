package com.cheriot.horriblecards.recycler;

import com.cheriot.horriblecards.models.Player;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by cheriot on 8/30/16.
 */
public class PlayersRecyclerAdapter extends FirebaseRecyclerAdapter<Player, PlayerViewHolder> {

    public PlayersRecyclerAdapter(Class<Player> modelClass, int modelLayout, Class<PlayerViewHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(PlayerViewHolder viewHolder, Player model, int position) {
        viewHolder.setPlayer(model);
    }
}
