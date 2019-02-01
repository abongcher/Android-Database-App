package in.abongcher.tbec;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;


public class LogViewer extends AppCompatActivity {
    logHandler logData;

    private Handler Refresh;
    private ListView listLog;
    private logAdapter log_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_viewer_list);

        logData = new logHandler(getApplicationContext());
        this.Refresh = new Handler(getMainLooper());

        listLog = (ListView) findViewById(R.id.listViewLog);

        ShowLogList();
    }

    private ArrayList<ContributorLogHolder> getContributorlog(){
        final ArrayList<ContributorLogHolder> logarraylist = new ArrayList<>(logData.getAllLog());
        return logarraylist;
    }

    private void ShowLogList(){
        log_adapter = new logAdapter(getApplicationContext(), R.layout.logview, getContributorlog());
        listLog.setAdapter(log_adapter);
    }



    Runnable viewLog = new Runnable() {
        @Override
        public void run() {
            ShowLogList();
        }
    };

    protected void logGenerator(Runnable log){
        this.Refresh.post(log);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.Refresh.removeCallbacks(viewLog);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.Refresh.removeCallbacks(viewLog);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logGenerator(viewLog);

    }
}
