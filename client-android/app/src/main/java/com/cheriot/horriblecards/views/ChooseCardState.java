package com.cheriot.horriblecards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheriot.horriblecards.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cheriot on 9/3/16.
 */
public class ChooseCardState extends LinearLayout {
    @BindView(R.id.choose_card_text) TextView textView;

    public ChooseCardState(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_state_choose_card, this, true);
        ButterKnife.bind(this);
    }

    public void startRound(int i) {
        textView.setText("Playing round " + i + ".");
    }
}
