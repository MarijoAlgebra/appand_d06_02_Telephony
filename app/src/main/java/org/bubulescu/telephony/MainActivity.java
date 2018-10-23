package org.bubulescu.telephony;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TEL_PREFIX = "tel:";
    private static final int CALL_PHONE_REQUEST = 1;
    private static final int SEND_SMS_REQUEST = 2;
    private static final int RECEIVE_SMS_REQUEST = 3;
    private Button btnPrepareSMS;
    private Button btnSendSMS;
    private Button btnDial;
    private Button btnCall;
    private EditText etNumber;
    private EditText etSMStext;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        setupListeners();
        checkRcvSmsPermission();

    }

    private void initWidgets() {
        btnPrepareSMS = findViewById(R.id.btnPrepareSMS);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnDial = findViewById(R.id.btnDial);
        btnCall = findViewById(R.id.btnCall);
        etNumber = findViewById(R.id.etBroj);
        etSMStext = findViewById(R.id.etPoruka);
    }

    private void setupListeners() {

        btnPrepareSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numberEntered() && textEntered()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
                    intent.putExtra("sms_body", smsMessage);

                    startActivity(intent);
                }
            }
        });

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberEntered() && textEntered()) {


                    if (checkSendSmsPermission()) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(etNumber.getText().toString(), null, etSMStext.getText().toString(), null, null);
                        Toast.makeText(MainActivity.this, "SMS poslana", Toast.LENGTH_SHORT).show();
                        etSMStext.setText("");
                    }


                }
            }
        });

        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberEntered()) {
                    phoneNumber = TEL_PREFIX + etNumber.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                    startActivity(intent);
                }
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberEntered()) {
                    phoneNumber = TEL_PREFIX + etNumber.getText().toString();

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, 
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
                    }
                }
            }
        });
    }

    private boolean checkSendSmsPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST);
        }
        return false;
    }

    private void checkRcvSmsPermission () {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_REQUEST);
        }
    }

    private boolean numberEntered() {
        if (etNumber.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Unesite broj telefona!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean textEntered() {
        if (etSMStext.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "unesite SMS tekst!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
