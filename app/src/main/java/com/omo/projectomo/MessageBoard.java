package com.omo.projectomo;

import static com.omo.projectomo.Login.idActive;
import static com.omo.projectomo.Login.nameActive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MessageBoard extends AppCompatActivity{

    private String activeGroup = null;
    private DatabaseReference rDatabase;
    private String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    private ArrayList<String> arraylist = new ArrayList<>();
    private TextView messages;
    private EditText date;
    public static ValueEventListener msgListener;
    public ValueEventListener msgshowListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_board);

        Spinner clubs = (Spinner) findViewById(R.id.clubs);
        EditText new_msg = (EditText) findViewById(R.id.newMsg);
        Button send = (Button) findViewById(R.id.msgSend);
        TextView title = (TextView) findViewById(R.id.group_title);
        messages = (TextView) findViewById(R.id.messages);
        date = (EditText) findViewById(R.id.dateSelect);
        Button show = (Button) findViewById(R.id.slctDate);
        Button mymsg = (Button) findViewById(R.id.my_messages);

        rDatabase = FirebaseDatabase.getInstance().getReference().child("messages");

        date.setText(currentDate);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.clubs_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        clubs.setAdapter(adapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                activeGroup = (String) adapterView.getItemAtPosition(i);
                title.setText(activeGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        clubs.setOnItemSelectedListener(listener);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessages();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = new_msg.getText().toString();
                sendMessage(text);
            }
        });

        mymsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MyMessages.class);
                startActivity(i);
                //rDatabase.removeEventListener(msgListener);
            }
        });

    }
    public void sendMessage(String text){
        String id = rDatabase.push().getKey();//getting unique id

        msgListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message msg = new Message(id, nameActive, activeGroup, idActive, text, currentDate, true);
                rDatabase.child(activeGroup).child(currentDate).child(id).setValue(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        };
        rDatabase.addValueEventListener(msgListener);
        //showMessages();
    }
    public void showMessages(){
        messages.setText("");
        arraylist.clear();

        msgshowListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String listToWrite = "";
                arraylist.clear();
                for(DataSnapshot cSnapshot : dataSnapshot.getChildren()){
                    Message msg = cSnapshot.getValue(Message.class);
                    if(msg.isVisible()){
                        arraylist.add(msg.getAuthor());
                        arraylist.add(":\n");
                        arraylist.add(msg.getText());
                        arraylist.add("\n\n");
                    }
                }
                for(String x : arraylist) {
                    listToWrite = listToWrite + x;
                }
                messages.setText(listToWrite);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        rDatabase.child(activeGroup).child(date.getText().toString()).addValueEventListener(msgshowListener);

    }
}

