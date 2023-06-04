package com.omo.projectomo;

import static com.omo.projectomo.Login.idActive;
import static com.omo.projectomo.Login.nameActive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClaimStolen extends AppCompatActivity {
    private String idClaim;
    private String description;
    private StolenBikeDatabase dbManager;
    private DatabaseReference rDatabase;
    private StorageReference rStorage;
    private String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    public int flag = 0;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private ImageView toUpload;
    private String recordId;
    private Spinner slots;
    private String activeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_stolen);

        EditText serial = (EditText) findViewById(R.id.idToClaim);
        EditText descInput = (EditText) findViewById(R.id.bikeDesc);
        Button claim = (Button) findViewById(R.id.claimButton);
        Button upload = (Button) findViewById(R.id.uploadImage);
        Button choose = (Button) findViewById(R.id.imageChooseButton);
        toUpload = (ImageView) findViewById(R.id.imageToUpload);
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

        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idClaim = serial.getText().toString();
                description = descInput.getText().toString();

                rDatabase = FirebaseDatabase.getInstance().getReference().child("stolen_bikes").child(idClaim);

                newRecord(idClaim);

            }
        });
        choose.setOnClickListener(view -> {
            chooseImage();
        });
        upload.setOnClickListener(view -> {
            idClaim = serial.getText().toString();

            rStorage = FirebaseStorage.getInstance().getReference();

            uploadImage(idClaim);
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
                    bike = new StolenBike(serial, id, nameActive, Login.idActive, currentDate, description);
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
    private void uploadImage(String serial) {
        getId(serial);

        if(recordId == null)
            return;

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = rStorage.child("images/stolenBikeImages/"+recordId+"/"
                    + activeSlot);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ClaimStolen.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ClaimStolen.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            recordId = null;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                toUpload.setImageBitmap(bitmap);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }
    private void getId(String serial){

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
                    else if(!bike.getOwnerId().equals(idActive))
                        Toast.makeText(getApplicationContext(), "Not your record!", Toast.LENGTH_SHORT).show();
                    else
                        recordId = bike.getBikeId();
                }
            }
        });
    }


}