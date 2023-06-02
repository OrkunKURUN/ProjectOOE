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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClaimFound extends AppCompatActivity {
    private String idClaim;
    private StolenBikeDatabase dbManager;
    private DatabaseReference rDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_found);

        EditText id = (EditText) findViewById(R.id.idToDelete);
        Button claim = (Button) findViewById(R.id.deleteButton);
        //dbManager = new StolenBikeDatabase(this);

        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idClaim = id.getText().toString();
                rDatabase = FirebaseDatabase.getInstance().getReference().child("stolen_bikes").child(idClaim);
                deleteRecord(idClaim);
            }
        });

    }

    public void deleteRecord(String id){
        /*SQLiteDatabase db = dbManager.getReadableDatabase();
        db.execSQL("DELETE FROM stolenBikes WHERE bike_id = '"+id+"'");
        Toast.makeText(getApplicationContext(),"Record deleted!",Toast.LENGTH_LONG).show();*/

        rDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }else {
                    StolenBike bike = task.getResult().getValue(StolenBike.class);
                    if (bike.getOwnerName().equals(Login.nameActive))
                        rDatabase.removeValue();
                }
            }
        });
        Toast.makeText(getApplicationContext(),"Record deleted!",Toast.LENGTH_LONG).show();
    }
}
