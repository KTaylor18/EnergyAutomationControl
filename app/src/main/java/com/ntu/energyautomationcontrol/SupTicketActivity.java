package com.ntu.energyautomationcontrol;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Objects;

public class SupTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sup_ticket);

        //https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    //help for the form came from https://code.tutsplus.com/tutorials/android-essentials-creating-simple-user-forms--mobile-1758
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submitFeedback (View button){
        final EditText fnamefield = (EditText) findViewById(R.id.FirstName);
        String fname = fnamefield.getText().toString();

        final EditText lnamefield = (EditText) findViewById(R.id.LastName);
        String lname = lnamefield.getText().toString();

        final EditText emailfield = (EditText) findViewById(R.id.Email);
        String email = emailfield.getText().toString();

        final Spinner spinnerinput = (Spinner) findViewById(R.id.spinner);
        String spinner = spinnerinput.getSelectedItem().toString();

        final EditText feedbackinput = (EditText) findViewById(R.id.feedbacktext);
        String feedback = feedbackinput.getText().toString();

        //https://stackoverflow.com/questions/7833689/java-string-new-line
        String message = "Message" +
                System.lineSeparator() +
                "From: " + fname + lname +
                System.lineSeparator() +
                "Email address: " + email +
                System.lineSeparator() +
                "Subject: " + spinner +
                System.lineSeparator() +
                "Message: " + feedback;

        //https://stackoverflow.com/questions/21202069/sending-form-data-to-email
        Intent sendemail = new Intent(Intent.ACTION_SEND);
        sendemail.putExtra(Intent.EXTRA_EMAIL, new String[]{"N0741061@my.ntu.ac.uk"});
        sendemail.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        sendemail.putExtra(Intent.EXTRA_TEXT, message);
        sendemail.setType("message/rfc822");
        startActivity(Intent.createChooser(sendemail, "Choose an email client to send message"));

    }
}
