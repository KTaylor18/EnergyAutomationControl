package com.ntu.energyautomationcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Objects;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}
