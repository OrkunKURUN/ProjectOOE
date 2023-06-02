package com.omo.projectomo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClaimStolen extends AppCompatActivity {
    private String idClaim;
    private StolenBikeDatabase dbManager;
    private DatabaseReference rDatabase;

    public int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_stolen);

        EditText serial = (EditText) findViewById(R.id.idToClaim);
        Button claim = (Button) findViewById(R.id.claimButton);
        dbManager = new StolenBikeDatabase(this);


        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idClaim = serial.getText().toString();

                rDatabase = FirebaseDatabase.getInstance().getReference().child("stolen_bikes").child(idClaim);

                newRecord(idClaim);

            }
        });
    }
    public void newRecord(String serial){
        //SQLiteDatabase db = dbManager.getReadableDatabase();
        //db.execSQL("INSERT INTO stolenBikes VALUES('"+serial+"')");
        //Toast.makeText(getApplicationContext(),"Record added!",Toast.LENGTH_LONG).show();

        String id = rDatabase.push().getKey();//getting unique id
        ValueEventListener bikeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StolenBike bike = null;
                StolenBike b2 = snapshot.getValue(StolenBike.class);

                if ((b2 == null) && (flag == 0)) {
                    System.out.println("NULL");
                    bike = new StolenBike(serial, id, Login.nameActive, Login.idActive);
                    rDatabase.setValue(bike);
                    Toast.makeText(getApplicationContext(),"Record added!", Toast.LENGTH_LONG).show();
                    flag = 1;
                } else if (flag == 0){
                    Toast.makeText(getApplicationContext(), "Bike already registered!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        };

        rDatabase.addValueEventListener(bikeListener);
        if (flag == 1)
            rDatabase.removeEventListener(bikeListener);
    }

}