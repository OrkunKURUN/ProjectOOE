package com.omo.projectomo;

import static com.omo.projectomo.Login.idActive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CheckStolen extends AppCompatActivity {
    private String idCheck;
    private StolenBikeDatabase dbManager;
    private DatabaseReference rDatabase;
    private Spinner slots;
    private String activeSlot;
    private ImageView resultImgShow;
    private String recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_stolen);

        EditText id = (EditText) findViewById(R.id.idToCheck);
        Button check = (Button) findViewById(R.id.checkButton);
        TextView result = (TextView) findViewById(R.id.checkResult);
        Button view = (Button) findViewById(R.id.viewImage);
        resultImgShow = (ImageView) findViewById(R.id.imageToShow);

        dbManager = new StolenBikeDatabase(this);

        slots = (Spinner) findViewById(R.id.imageSlots);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.image_slots, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        slots.setAdapter(adapter);
        AdapterView.OnItemSelectedListener slotListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                activeSlot = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        slots.setOnItemSelectedListener(slotListener);

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
                            else{
                                String alert = "Record with this ID found. The bike you're looking for may be stolen.\nOwner:\t";
                                alert = alert + bike.getOwnerName() + "\nRecord date:\t" + bike.getDate() + "\nDescription: " + bike.getDescription() + "\n\n";

                                result.setText(alert);
                            }
                        }
                    }
                });
                showResImg(idCheck);
            }
        });
        view.setOnClickListener(view1 -> {
            String serialNo = id.getText().toString();
            showResImg(serialNo);
        });
    }
    private void showResImg(String serial){
        StorageReference rStorage = FirebaseStorage.getInstance().getReference();
        rDatabase = FirebaseDatabase.getInstance().getReference().child("stolen_bikes").child(serial);
        rDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    StolenBike bike = task.getResult().getValue(StolenBike.class);
                    if(bike == null)
                        Toast.makeText(getApplicationContext(), "No record!", Toast.LENGTH_SHORT).show();
                    else
                        recordId = bike.getBikeId();
                }
            }
        });
        StorageReference resImgRef = rStorage.child("images/stolenBikeImages/"+recordId+"/"+activeSlot);
        final long TWO_MEGABYTE = 2 * 1024 * 1024;
        resImgRef.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                resultImgShow.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "No such path or file!", Toast.LENGTH_SHORT).show();
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
