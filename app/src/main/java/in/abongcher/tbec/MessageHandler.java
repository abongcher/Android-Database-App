package in.abongcher.tbec;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageHandler extends Service {

    DataBaseHandler dbh;
    MessageAttender ma;
    logHandler lHandler;

    private String sms, Gender,contribName, updatedNumber, title, name;
    private String mobileNumber, balance;

    private SimpleDateFormat sdf;
    private Calendar cal;

    private String _INQUIRY = "";

    SmsManager smsManager;

    private String value;

    public MessageHandler() {
    }

    private DataBaseHandler getDataBaseHandler(){
        dbh = new DataBaseHandler(getApplicationContext());
        return dbh;
    }

    private MessageAttender getMessageAttender(){
        ma = new MessageAttender();
        return ma;
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        lHandler = new logHandler(getApplicationContext());

        //initialise contributor name for ready use
        mobileNumber = ContributorNumber(intent);

        //initialise balance for ready use
        balance = getDataBaseHandler().getQuiryBalance(mobileNumber);

        //intitialise contributor name for ready use
        contribName = getDataBaseHandler().getQuiryName(mobileNumber);

        //initialise title  for ready use
        title = getDataBaseHandler().getQuiryGender(mobileNumber);

        //initialise name for ready use
        name = getDataBaseHandler().getQuiryName(mobileNumber);

        //get both title and name for use now
        String titleAndName = showGender(title) + ' ' + name;


        /*Toast.makeText(this, mobileNumber + balance, Toast.LENGTH_LONG).show();
        sendMessage(mobileNumber, showGender(mobileNumber));*/

        sdf = new SimpleDateFormat("yyyy-MM-dd'/'hh:mm:ss'/'");
        cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());


        switch (ContributorQuiry(intent)){
            case "bal":
                sendBalanceStatus(mobileNumber, titleAndName, balance);
                _INQUIRY += " enquired balance.";
                break;

            case "mob":
                if(ContributorNewNumber(intent).length() < 10 && ContributorNewNumber(intent).length() != 10 && isMobileNumber(intent)){
                    shortMobileNo(mobileNumber, titleAndName);
                    _INQUIRY += " tried update with short number.";
                }
                else if(ContributorNewNumber(intent).length() > 10 && ContributorNewNumber(intent).length() != 10 && isMobileNumber(intent)){
                    longMobileNo(mobileNumber, titleAndName);
                    _INQUIRY += " tried update with more than 10 digits number.";
                }
                else if(ContributorNewNumber(intent).length() == 10 && isMobileNumber(intent)) {
                    updateMobileNo(mobileNumber, titleAndName, ContributorNewNumber(intent), contribName);
                    _INQUIRY += " updated mobile number.";
                }
                break;
            default:
        }

        //add quiry of customer to the log file
        value = date+(contribName+_INQUIRY);
        if(-1 != lHandler.AddLog(new ContributorLogHolder(value))){
//            Toast.makeText(getApplicationContext(), "Log added", Toast.LENGTH_SHORT).show();
            _INQUIRY = "";
        }

        return START_NOT_STICKY;
    }





    private String ContributorNumber(Intent intent){
        return intent.getExtras().getString(getMessageAttender().CUSTOMER_OLD_MOBILE);
    }

    private String ContributorQuiry(Intent intent){
        return intent.getExtras().getString(getMessageAttender().CUSTOMER_QUIRY);
    }

    private String ContributorNewNumber(Intent intent){
        return intent.getExtras().getString(getMessageAttender().CUSTOMER_NEW_NUMBER);
    }

    //show gender
    private String showGender(String title){
        if(title.equals("Male")) {
            Gender = "Mr.";
        }
        else if(title.equals("Female")){
            Gender = "Mrs/Miss";
        }
        else{
            Gender = "Dear";
        }
        return Gender;
    }


    //send balance to customer
    private void sendBalanceStatus(String Mobile_No, String titleandname, String balance) {
        sms = titleandname + ", your total contribution to TBEC is Rs." + balance + " only.";
        sendMessage(Mobile_No, sms);
    }



    //send sms on successfull mobile update
    private void updateMobileNo(String custMob, String titleAndName, String newMob, String contribname){
        if (getDataBaseHandler().isMobileUpdated(custMob, newMob) != -1) {
            //initialized new updated mobile number
            updatedNumber = getDataBaseHandler().getNewMobileNumber(contribname);
            sms = titleAndName + ", your request updated successfully. Your new number is " + updatedNumber;
            sendMessage(custMob, sms);
        } else {
            return;
        }
    }


    //send sms
    private void sendMessage(String Mobile_No, String message) {
        smsManager = SmsManager.getDefault();
        synchronized (message){
            smsManager.sendTextMessage(Mobile_No, null, message, null, null);
        }
    }

    private void shortMobileNo(String Mobile_No, String titleandname){
        sms = titleandname + ", your mobile number is too short. Please provide us with 10 digit mobile number.";
        sendMessage(Mobile_No, sms);
    }

    private void longMobileNo(String Mobile_No, String titleandname){
        sms = titleandname + ", your mobile number is too long. Please provide us with 10 digit mobile number.";
        sendMessage(Mobile_No, sms);
    }

    private boolean isMobileNumber(Intent intent){
        String number = ContributorNewNumber(intent);
        char [] stringSplitter = number.toCharArray();
        for(char c:stringSplitter){
            return Character.isDigit(c);
        }
        return true;
    }

}
