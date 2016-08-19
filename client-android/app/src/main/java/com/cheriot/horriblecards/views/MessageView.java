package com.cheriot.horriblecards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.cheriot.horriblecards.R;

/**
 * Created by cheriot on 8/20/16.
 */
public class MessageView extends FrameLayout {

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_message, this, true);
    }
}
