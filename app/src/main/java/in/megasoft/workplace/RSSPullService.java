package in.megasoft.workplace;

import android.app.IntentService;
import android.content.Intent;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class RSSPullService extends IntentService {

    public RSSPullService() {
        super("RSSPullService");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Extract data from the incoming Intent
        String dataString = workIntent.getStringExtra("data_key");

        // Pass the data to the Worker
        Data inputData = new Data.Builder()
            .putString("data_key", dataString)
            .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RSSPullWorker.class)
            .setInputData(inputData)
            .build();

        // Enqueue the Worker
        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
