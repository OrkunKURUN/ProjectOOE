package com.omo.projectomo;

import static com.omo.projectomo.Login.idActive;
import static com.omo.projectomo.MessageBoard.msgListener;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyMessages extends AppCompatActivity {

    private int currId = 0;
    private DatabaseReference rDatabase;
    private ArrayList<String> arraylist = new ArrayList<>();
    private ArrayList<String> IDs = new ArrayList<>();
    private TextView messages;
    private EditText idInput;
    public ValueEventListener msgshowListener;
    public ValueEventListener msgdltListener;
    private boolean flag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_messages);

        messages = (TextView) findViewById(R.id.messages2);
        idInput = (EditText) findViewById(R.id.idDelete);
        Button del = (Button) findViewById(R.id.delMsg);
        Button inc = (Button) findViewById(R.id.incID);
        Button dec = (Button) findViewById(R.id.decID);

        rDatabase = FirebaseDatabase.getInstance().getReference().child("messages");
        System.out.println(rDatabase.getParent().toString());

        showMessages();

        del.setOnClickListener(view -> {
            String msgid = idInput.getText().toString();
            deleteMessage(msgid);
        });
        dec.setOnClickListener(view -> {
            if (currId > 0)
                currId -= 1;
            idInput.setText(IDs.get(currId));
        });
        inc.setOnClickListener(view -> {
            if (currId < IDs.size()-1)
                currId += 1;
            idInput.setText(IDs.get(currId));
        });

    }
    public void showMessages(){
        messages.setText(" ");
        arraylist.clear();

        msgshowListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String listToWrite = "";
                arraylist.clear();
                IDs.clear();
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot dateSnapshot : groupSnapshot.getChildren()){
                        for (DataSnapshot msgSnapshot : dateSnapshot.getChildren()){
                            Message msg = msgSnapshot.getValue(Message.class);

                            if (msg.getAuthorId().equals(idActive)){
                                arraylist.add("ID: ");
                                arraylist.add(msg.getMsgId());
                                arraylist.add(", Date:");
                                arraylist.add(msg.getDate());
                                if(!msg.isVisible())
                                    arraylist.add(" (Deleted)");
                                arraylist.add("\n");
                                arraylist.add(msg.getText());
                                arraylist.add("\n\n");
                                IDs.add(msg.getMsgId());
                            }
                        }
                    }
                }
                for(String x : arraylist) {
                    listToWrite = listToWrite + x;
                }
                messages.setText(listToWrite);

                if(currId >= IDs.size())
                    currId = IDs.size() - 1;
                idInput.setText(IDs.get(currId));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        rDatabase.addValueEventListener(msgshowListener);
    }
    public void deleteMessage(String msgid){
        flag = true;

        if (msgdltListener != null)
            rDatabase.removeEventListener(msgdltListener);

        msgdltListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groupSnapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot dateSnapshot : groupSnapshot.getChildren()){
                        for (DataSnapshot msgSnapshot : dateSnapshot.getChildren()){
                            Message msg = msgSnapshot.getValue(Message.class);

                            if ((msg.getAuthorId().equals(idActive))&&(msg.getMsgId().equals(msgid))&&flag){
                                msg.setVisible(!msg.isVisible());
                                rDatabase.child(msg.getGroup()).child(msg.getDate()).child(msg.getMsgId()).setValue(msg);
                                flag = false;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        rDatabase.addValueEventListener(msgdltListener);
    }
}
