package com.cheriot.horriblecards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.cheriot.horriblecards.R;

/**
 * Created by cheriot on 9/3/16.
 */
public class UnstartedState extends FrameLayout {

    public UnstartedState(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_state_unstarted, this, true);
    }

}
