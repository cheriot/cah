package com.cheriot.horriblecards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cheriot.horriblecards.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cheriot on 8/20/16.
 */
public class MessageView extends FrameLayout {

    static final String LOG_TAG = MessageView.class.getSimpleName();
    private DatabaseReference mFirebaseDatabaseReference;
    private TextView messageText;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_message, this, true);
        messageText = (TextView)findViewById(R.id.fullscreen_content);

        Log.v(LOG_TAG, "***MessageView()");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("message");
        //mFirebaseDatabaseReference.setValue("beep boop");
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = (String)dataSnapshot.getValue();
                Log.v(LOG_TAG,"*** onDataChanged " + message);
                messageText.setText(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Error reading database.", databaseError.toException());
            }
        });
    }
}
