package in.abongcher.tbec;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SentMessageState extends BroadcastReceiver {

    boolean isSent;

    public SentMessageState(){}


    public SentMessageState(boolean bool) {
        this.isSent = bool;
    }

    public void setBool(boolean boo){
        this.isSent = boo;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()){
            case Activity.RESULT_OK:
                setBool(true);
                break;
            default:
                setBool(false);
        }
//        Toast.makeText(context, "Message sent:"+ String.valueOf(getBool()), Toast.LENGTH_SHORT).show();
    }

    public boolean getBool(){
        return this.isSent;
    }
}

