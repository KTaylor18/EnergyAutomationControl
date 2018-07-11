package com.ntu.energyautomationcontrol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TempActivity extends AppCompatActivity {

    int targettemp = 0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();




    private FirebaseAuth mAuth;

    //help for voice commands came from http://stacktips.com/tutorials/android/speech-to-text-in-android
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private String spokenText;
    private ImageButton voiceBut;

    //https://www.sitepoint.com/using-android-text-to-speech-to-create-a-smart-assistant/
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        voiceBut = (ImageButton)findViewById(R.id.voiceButton);
        voiceBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stVoiceRec();
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();
        final String UID = mAuth.getUid();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int ctemp = dataSnapshot.child("users").child(UID).child("ct").getValue(Integer.class);
                int ttemp = dataSnapshot.child("users").child(UID).child("tt").getValue(Integer.class);

                TextView cutemp = (TextView) findViewById(R.id.curtempt);
                //https://stackoverflow.com/questions/4105331/how-do-i-convert-from-int-to-string
                cutemp.setText(String.valueOf(ctemp));
                EditText tatemp = (EditText) findViewById(R.id.targtemp);
                tatemp.setText(String.valueOf(ttemp));

                //https://developer.android.com/reference/android/widget/CheckBox
                if((ctemp > ttemp) || (ctemp == ttemp)){
                    CheckBox tempbox = (CheckBox) findViewById(R.id.HeatOnOff);
                    tempbox.setChecked(false);
                }
                if(ctemp < ttemp){
                    CheckBox tempbox = (CheckBox) findViewById(R.id.HeatOnOff);
                    tempbox.setChecked(true);
                }
            }

                @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TempActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.contact){
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.supporttick){
            Intent intent = new Intent(this, SupTicketActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.about){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        //https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase
        if(item.getItemId()==R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(TempActivity.this, "Sign Out Successful",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    spokenText = result.get(0);
                }
                break;
            }
        }

        //https://stackoverflow.com/questions/17134773/to-check-if-string-contains-particular-word
        if(spokenText.contains("heating")) {
            if (spokenText.contains("up")) {
                EditText ttemp = (EditText) findViewById(R.id.targtemp);
                TextView cctemp = (TextView) findViewById(R.id.curtempt);
                targettemp = Integer.parseInt(ttemp.getText().toString());
                int currenttemp = Integer.parseInt(cctemp.getText().toString());
                targettemp = currenttemp + 5;
                ttemp.setText(String.valueOf(targettemp));
                mAuth = FirebaseAuth.getInstance();
                final String UID = mAuth.getUid();
                myRef.child("users").child(UID).child("tt").setValue(targettemp);
                Toast.makeText(TempActivity.this, "Heating turned up", Toast.LENGTH_SHORT).show();

                tts.speak("The heating has been turned up", TextToSpeech.QUEUE_FLUSH,null);
            }
            if (spokenText.contains("down")) {
                EditText ttemp = (EditText) findViewById(R.id.targtemp);
                TextView cctemp = (TextView) findViewById(R.id.curtempt);
                targettemp = Integer.parseInt(ttemp.getText().toString());
                int currenttemp = Integer.parseInt(cctemp.getText().toString());
                targettemp = currenttemp - 5;
                ttemp.setText(String.valueOf(targettemp));
                mAuth = FirebaseAuth.getInstance();
                final String UID = mAuth.getUid();
                myRef.child("users").child(UID).child("tt").setValue(targettemp);
                Toast.makeText(TempActivity.this, "Heating turned down", Toast.LENGTH_SHORT).show();

                tts.speak("The heating has been turned down", TextToSpeech.QUEUE_FLUSH,null);
            }
        }
        else{
            Toast.makeText(TempActivity.this, "I cannot do that", Toast.LENGTH_SHORT).show();

            tts.speak("I cannot do that", TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void stVoiceRec(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {
            Toast.makeText(TempActivity.this, "I cannot do that",
                    Toast.LENGTH_SHORT).show();
        }
    }



    //https://stackoverflow.com/questions/17958887/make-a-button-change-value-of-a-textview
    public void addone(View view) {
        //https://stackoverflow.com/questions/2709253/converting-a-string-to-an-integer-on-android
        EditText text = (EditText) findViewById(R.id.targtemp);
        targettemp = Integer.parseInt(text.getText().toString());
        switch (view.getId()) {
            case R.id.imageButton2:
                targettemp += 1;
                //https://stackoverflow.com/questions/4105331/how-do-i-convert-from-int-to-string
                text.setText(String.valueOf(targettemp));
                break;
        }
        mAuth = FirebaseAuth.getInstance();
        final String UID = mAuth.getUid();
        myRef.child("users").child(UID).child("tt").setValue(targettemp);
    }

    public void takeone(View view) {
        EditText text = (EditText) findViewById(R.id.targtemp);
        targettemp = Integer.parseInt(text.getText().toString());
        switch (view.getId()) {
            case R.id.imageButton:
                targettemp = targettemp - 1;
                text.setText(String.valueOf(targettemp));
                break;
        }
        mAuth = FirebaseAuth.getInstance();
        final String UID = mAuth.getUid();
        myRef.child("users").child(UID).child("tt").setValue(targettemp);

    }
}

