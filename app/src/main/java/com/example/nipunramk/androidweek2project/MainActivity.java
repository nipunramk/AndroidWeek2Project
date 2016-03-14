package com.example.nipunramk.androidweek2project;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String TEXTKEY = "textkey";

    Button button;
    EditText editText;
    TextView textView;
    boolean closingActivity;

    SharedPreferences sharedPreferences;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button1);
        editText = (EditText) findViewById(R.id.edit_text);
        textView = (TextView) findViewById(R.id.textView);
        ;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedText = editText.getText().toString();
                if (typedText.length() != 0) {

                    manageSharedPreferences(typedText);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Error: there is no input text")
                            .setTitle("Error")
                            .setNegativeButton("OK", null)
                            .show();
                }
                }

        });


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                button = (Button) v;
//                button.setVisibility(View.INVISIBLE);
//            }

    }

    private void reload() {
        String displayText = sharedPreferences.getString(TEXTKEY, null);
        if(displayText != null) {
            textView.setText(displayText);
        }
        else {
            textView.setTypeface(null);
            editText.setTypeface(null);
        }
    }

    public void manageSharedPreferences(String text) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (text != null) {
            editor.putString(TEXTKEY, text);
            editor.apply();

            editText.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            reload();
        } else {
            editor.remove(TEXTKEY);
            editor.apply();
            button.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            textView.setText(text);
            button.setText(text);
            textView.setVisibility(View.GONE);


        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                // the method onStop() will be called but the app itself is not closing.
                closingActivity = false;
                return true;
            case R.id.reset:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reset values")
                        .setMessage("Are you sure you want to reset values?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manageSharedPreferences(null);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String txt = sharedPreferences.getString(TEXTKEY, null);
        manageSharedPreferences(txt);
        closingActivity = true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(closingActivity) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(android.R.drawable.ic_notification_overlay)
                            .setContentText(sharedPreferences.getString(TEXTKEY, ""))
                            .setAutoCancel(true);
            Intent resultIntent = new Intent(this, MainActivity.class);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }
}
