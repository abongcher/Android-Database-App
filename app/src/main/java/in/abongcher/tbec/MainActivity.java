package in.abongcher.tbec;


import android.app.AlertDialog;

import android.app.PendingIntent;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.support.design.widget.FloatingActionButton;;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity{

    DataBaseHandler dbh;
    logHandler logContainer;
    dataAdapter data;
    ListView lvItems;
    Contributor con;

    private final String filename = "TBEC_Contributors.txt";
    File fileToReadAndWrite;

    SmsManager mgr;
    private final String sentTo = "Your number";
    private String myMessage;
    private final String SEND_SMS_ACTION = "abongcher.in.TBEC.DELIVERED_SMS_ACTION";
    private Intent sentIntent;
    private PendingIntent sentPI;

    private Handler handler;
    private int oneMobileNo = 1;
    private String Gender;

    SentMessageState sms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvItems = (ListView) findViewById(R.id.listViewTBEC);
        dbh = new DataBaseHandler(getApplicationContext());

        this.handler = new Handler(getMainLooper());

        ShowRecords();
        actionOnItemClick();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addData = new Intent(MainActivity.this, Add_Contributor.class);
                startActivity(addData);
            }
        });

        logContainer = new logHandler(getApplicationContext());

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        sms = new SentMessageState();
        sms.setBool(false);

        //Registration of broadcast receiver for delivery status
        registerReceiver(sms, new IntentFilter(SEND_SMS_ACTION));
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void showMessage(String title, String mesg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(mesg);
        builder.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_view_log:
                Intent intentLog = new Intent(MainActivity.this, LogViewer.class);
                startActivity(intentLog);
                return true;

            case R.id.action_clear_log:
                logContainer.clear();
                return true;

            case R.id.action_delete_all_data:
                dbh.deleteTBECData();
                onHandle(runner);
                return true;
            case R.id.action_save_all_data:
                if(isExternalStorageWritable()) {
                    if(getDocumentStoragePath().exists() && getDocumentStoragePath().isDirectory()){
                        if(isFileWritable(getDocumentStoragePath())){
                            writeToFile(getFilename());
                        }else{
                            Toast.makeText(MainActivity.this, "Not Writable dir", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No directory", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Storage not writable.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_send_quiry:
                do{
                    sendMessage();
                    this.handler.post(new sentJob());
                }while (oneMobileNo <= maxMobNo());
                return true;
            case R.id.action_send_balance:
                do{
                    sendIndividualBalance();
                    this.handler.post(new sentBal());
                }while (oneMobileNo <= maxMobNo());
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowRecords(){
        data = new dataAdapter(getApplicationContext(), getContributors());
        lvItems.setAdapter(data);
    }

    private void actionOnItemClick(){

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                con = getContributors().get(position);
                showInfo(con);

//                Toast.makeText(getApplicationContext(), String.valueOf(con.getName()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    Runnable runner = new Runnable() {
        @Override
        public void run() {
            ShowRecords();
        }
    };

    protected void onHandle(Runnable runner) {
        this.handler.post(runner);
    }

    @Override
    protected void onDestroy() {
//        super.onDestroy();
        this.handler.removeCallbacks(runner);
        this.handler.removeCallbacks(new sentJob());
//        getHandler().removeCallbacks(new sentJob());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onHandle(runner);
    }

    private void showInfo(final Contributor mCon){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View mView = layoutInflater.inflate(R.layout.displayinfo, null, false);
        ImageView image = (ImageView) mView.findViewById(R.id.custPhoto);
        TextView name = (TextView) mView.findViewById(R.id.custNam);
        TextView gender = (TextView) mView.findViewById(R.id.custGender);
        TextView mobile = (TextView) mView.findViewById(R.id.custMobile);
        final EditText balance  = (EditText) mView.findViewById(R.id.custBal);

        Button updater = (Button) mView.findViewById(R.id.update);
        Button erase = (Button) mView.findViewById(R.id.delete);

        image.setImageBitmap(Utility.convertToBitmap(mCon.getImage()));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        name.setText(mCon.getName());
        gender.setText(mCon.getGender());
        mobile.setText(mCon.getMobileNo());
        balance.setText(String.valueOf(mCon.getAmount()));

        mBuilder.setView(mView);
        final AlertDialog mAlert = mBuilder.create();
        updater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sum = 0;
                synchronized (this){
                    sum = mCon.getAmount() + Integer.valueOf(balance.getText().toString());
                }
                if(sum != 0){
                    if (dbh.updateBalance(new Contributor(sum), mCon.getName()) != -1) {
                        Toast.makeText(getApplicationContext(), "Balance updated with Rs." + Integer.valueOf(sum), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Balance not updated", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error in addition", Toast.LENGTH_SHORT).show();
                }
                mAlert.dismiss();
                onHandle(runner);
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbh.deleteByName(mCon.getName());
                mAlert.dismiss();
                onHandle(runner);
            }
        });
        mAlert.show();
    }

    private ArrayList<Contributor> getContributors(){
        final ArrayList<Contributor> contacts = new ArrayList<>(dbh.getAllData());
        return contacts;
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    data.filter("");
                    lvItems.clearTextFilter();
                } else {
                    data.filter(newText);
                }
                return true;
            }
        });

        return true;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    public File getDocumentStoragePath() {
        // Get the directory for the user's public documents directory.
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!path.mkdirs()) {
            path.mkdirs();
        }
        return path;
    }



    public File getFilename(){
        fileToReadAndWrite = new File(getDocumentStoragePath(), filename);
        return fileToReadAndWrite;
    }

    public boolean isFileWritable(File file){
        if(!file.canWrite()){
            return file.setWritable(true);
        }
        return file.canWrite();
    }




    public void writeToFile(File textfile){

        String TBEC_Data = dbh.get_TBEC_Data();

        try {

            FileOutputStream fos = new FileOutputStream(textfile);
            if(TBEC_Data != null){
                fos.write(TBEC_Data.getBytes());
            }
            fos.close();
            Toast.makeText(MainActivity.this, "Finish writing...", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendMessage(){
        for(String mobile:getArrayOfNumbers()) {
            mgr = SmsManager.getDefault();
            myMessage = "Dear Member,\n\tFor balance inquiry sms <bal> and send to 8575512690.\n For change of mobile number sms <mob ten_digit_no> and send to 8575512690";
            mgr.sendTextMessage(mobile, null, myMessage, getPI(), null);
            oneMobileNo++;
        }
    }

    public void sendIndividualBalance(){
        for(String mobile:getArrayOfNumbers()){

            String title = dbh.getQuiryGender(mobile);
            String name = dbh.getQuiryName(mobile);
            String balance = dbh.getQuiryBalance(mobile);
            mgr = SmsManager.getDefault();
            myMessage = showGender(title) +' '+name+", your balance with us is Rs."+balance+".\n Regard,\nTBEC.";
            mgr.sendTextMessage(mobile, null, myMessage, getPI(), null);
            oneMobileNo++;
        }
    }

    public PendingIntent getPI(){
        sentIntent = new Intent(SEND_SMS_ACTION);
        sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return sentPI;
    }



    public class sentJob implements Runnable {
        @Override
        public void run(){

            if (oneMobileNo == maxMobNo()) {
                return;
            } else if (sms.getBool()) {
                sendMessage();
            }
        }
    }

    public class sentBal implements Runnable {
        @Override
        public void run(){

            if (oneMobileNo == maxMobNo()) {
                return;
            } else if (sms.getBool()) {
                sendIndividualBalance();
            }
        }
    }




    //Get array of mobile numbers
    public ArrayList<String> getArrayOfNumbers(){
        ArrayList<String> noList = (ArrayList<String>) dbh.getAllMobileNo();
        return noList;
    }

    //Get total length of mobile numbers
    public int maxMobNo(){
        return getArrayOfNumbers().size();
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
