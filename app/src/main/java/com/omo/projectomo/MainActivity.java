package com.omo.projectomo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button check = (Button) findViewById(R.id.buttonCheckStolen);
        Button claim = (Button) findViewById(R.id.buttonClaimStolen);
        Button delete = (Button) findViewById(R.id.buttonClaimFound);
        Button logout = (Button) findViewById(R.id.buttonLogout);
        TextView name = (TextView) findViewById(R.id.nameplace);

        name.setText("User: "+Login.nameActive);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),CheckStolen.class);
                startActivity(i);
            }
        });
        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ClaimStolen.class);
                startActivity(i);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ClaimFound.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Login.class);

                Login.idActive = null;
                Login.nameActive = null;
                Login.passwordActive = null;
                startActivity(i);
            }
        });
    }
}