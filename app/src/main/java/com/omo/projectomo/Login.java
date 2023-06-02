package com.omo.projectomo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private String name;
    private String password;
    private String e_mail;
    public static String nameActive;
    public static String passwordActive;
    public static String idActive;
    private DatabaseReference rDatabase;

    public int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        EditText nameInput = (EditText) findViewById(R.id.userNameInput);
        EditText nameInput2 = (EditText) findViewById(R.id.userNameInput2);
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        EditText passwordInput2 = (EditText) findViewById(R.id.passwordInput2);
        EditText emailInput = (EditText) findViewById(R.id.emailInput);
        EditText emailInput2 = (EditText) findViewById(R.id.emailInput);
        Button login = (Button) findViewById(R.id.loginButton);
        Button signup = (Button) findViewById(R.id.signupButton);
        Button forgot = (Button) findViewById(R.id.forgotButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameInput2.getText().toString();
                rDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(name);
                password = passwordInput2.getText().toString();
                e_mail = emailInput.getText().toString();
                addAccount(name, password);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);

                name = nameInput.getText().toString();
                password = passwordInput.getText().toString();

                rDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(name);

                rDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            User user = task.getResult().getValue(User.class);

                            if(user == null)
                                Toast.makeText(getApplicationContext(),"User not found!",Toast.LENGTH_SHORT).show();
                            else if(!password.equals(user.getPassword()))
                                Toast.makeText(getApplicationContext(),"Wrong password!",Toast.LENGTH_SHORT).show();
                            else{
                                setNameActive(name);
                                setPasswordActive(password);
                                setIdActive(user.getUserId());
                                startActivity(i);
                            }
                        }
                    }
                });
            }
        });
    }

    public void addAccount(String name, String password) {
        String id = rDatabase.push().getKey();//getting unique id
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                User u2 = snapshot.getValue(User.class);
                if (u2 == null) {
                    System.out.println("NULL");
                    user = new User(id, name, password, e_mail);
                    rDatabase.setValue(user);
                    Toast.makeText(getApplicationContext(), name + ", Welcome!", Toast.LENGTH_LONG).show();
                    flag = 1;
                } else if (flag == 0){
                    System.out.println("Username: " + u2.getName());//test line
                    Toast.makeText(getApplicationContext(), "Name already registered!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }

        };

        rDatabase.addValueEventListener(userListener);
    }

    public String getNameActive() {
        return nameActive;
    }

    public void setNameActive(String nameActive) {
        this.nameActive = nameActive;
    }

    public String getPasswordActive() {
        return passwordActive;
    }

    public void setPasswordActive(String passwordActive) {
        this.passwordActive = passwordActive;
    }

    public String getIdActive() {
        return idActive;
    }

    public void setIdActive(String idActive) {
        this.idActive = idActive;
    }
}
