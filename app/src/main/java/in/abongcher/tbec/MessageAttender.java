package in.abongcher.tbec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageAttender extends BroadcastReceiver {

    DataBaseHandler dbh;

    private final String BALANCE_QUIRY = "bal";
    private final String MOBILE_UPDATE = "mob";

    public static final String CUSTOMER_OLD_MOBILE = "Old_Mobile_Number";
    public static final String CUSTOMER_QUIRY = "Quiry";
    public static final String CUSTOMER_NEW_NUMBER = "New_Mobile_Number";


    private final String SMS_RECEIVE_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public MessageAttender() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVE_ACTION) || intent.getAction().compareToIgnoreCase(SMS_RECEIVE_ACTION) == 0 && intent.getExtras() != null) {

            dbh = new DataBaseHandler(context);
            final ArrayList<Contributor> contrib = new ArrayList<>(dbh.getAllData());
            String sender = null;
            String quiry = null;

            Intent sendToRequestSender = new Intent(context, MessageHandler.class);

            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            final SmsMessage[] msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                String format = intent.getExtras().getString("format");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);

                    sender = msgs[i].getOriginatingAddress().substring(3);
                    for(Contributor conMob:contrib){
                        if(conMob.getMobileNo().equals(sender)){
                            quiry = msgs[i].getMessageBody().toLowerCase();
                            String[] quiryType = quiry.split("\\s+");
                            if(quiryType[0].compareToIgnoreCase(MOBILE_UPDATE) == 0 /*&& quiryType[1].length() == 10*/ ){
                                sendToRequestSender.putExtra(CUSTOMER_OLD_MOBILE, sender);
                                sendToRequestSender.putExtra(CUSTOMER_QUIRY, quiryType[0] /*quiry*/);
                                sendToRequestSender.putExtra(CUSTOMER_NEW_NUMBER, quiryType[1]);
                                context.startService(sendToRequestSender);
                            }
                            else if(quiryType[0].compareToIgnoreCase(BALANCE_QUIRY) == 0 && !(quiry.length() > 3 && quiry.contains(" "))){
                                sendToRequestSender.putExtra(CUSTOMER_OLD_MOBILE, sender);
                                sendToRequestSender.putExtra(CUSTOMER_QUIRY, quiryType[0] /*quiry*/);
//                                Toast.makeText(context, "length of text is :" + String.valueOf(quiry.length()), Toast.LENGTH_SHORT).show();
                                context.startService(sendToRequestSender);
                                }
                            }
                            else{
                                context.stopService(sendToRequestSender);
                            }
                        }
                    }

                else{
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    sender = msgs[i].getOriginatingAddress().substring(3);
                    for(Contributor conMob:contrib){
                        if(conMob.getMobileNo().equals(sender)){
                            quiry = msgs[i].getMessageBody().toLowerCase();
                            String[] quiryType = quiry.split("\\s+");
                            if(quiryType[0].compareToIgnoreCase(MOBILE_UPDATE) == 0 /*&& quiryType[1].length() == 10*/ ){
                                sendToRequestSender.putExtra(CUSTOMER_OLD_MOBILE, sender);
                                sendToRequestSender.putExtra(CUSTOMER_QUIRY, quiryType[0]);
                                sendToRequestSender.putExtra(CUSTOMER_NEW_NUMBER, quiryType[1]);
                                context.startService(sendToRequestSender);
                            }
                            else if(quiryType[0].compareToIgnoreCase(BALANCE_QUIRY) == 0 && !(quiry.length() > 3 && quiry.contains(" "))){
                                sendToRequestSender.putExtra(CUSTOMER_OLD_MOBILE, sender);
                                sendToRequestSender.putExtra(CUSTOMER_QUIRY, quiryType[0]);
                                context.startService(sendToRequestSender);
                            }
                            else{
                                context.stopService(sendToRequestSender);
                            }
                        }
                    }

                }
            }
            // Display the entire SMS Message
//            Toast.makeText(context, sender + "  :" + quiry + "Length    :" + String.valueOf(quiry.length()), Toast.LENGTH_LONG).show();
        }
    }
}

