package com.omo.projectomo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckStolen extends AppCompatActivity {
    private String idCheck;
    private StolenBikeDatabase dbManager;
    private DatabaseReference rDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_stolen);

        EditText id = (EditText) findViewById(R.id.idToCheck);
        Button check = (Button) findViewById(R.id.checkButton);
        TextView result = (TextView) findViewById(R.id.checkResult);

        dbManager = new StolenBikeDatabase(this);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheck = id.getText().toString();
                rDatabase = FirebaseDatabase.getInstance().getReference().child("stolen_bikes").child(idCheck);

                /*if(checkId(idCheck) == true){
                    result.setText("Record with this ID found. The bike you're looking for may be stolen.");
                }
                else{
                    result.setText("Record with this ID not found. The bike you're looking for may not be stolen.");
                }*/

                rDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            StolenBike bike = task.getResult().getValue(StolenBike.class);
                            if (bike == null)
                                result.setText("Record with this ID not found. The bike you're looking for may not be stolen.");
                            else
                                result.setText("Record with this ID found. The bike you're looking for may be stolen.");
                        }
                    }
                });
            }
        });
    }
    /*private boolean checkId(String id){
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = "SELECT * FROM stolenBikes WHERE bike_id = '"+id+"'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0){
            return false;
        }
        return true;
    }*/
}
