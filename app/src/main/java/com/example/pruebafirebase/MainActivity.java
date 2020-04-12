package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pruebafirebase.Model.UserModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etLastName, etEmail, etPass;
    private ListView listViewUserList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<UserModel> userModelList = new ArrayList<>();
    private ArrayAdapter<UserModel> userModelArrayAdapter;
    private UserModel userModelSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configViews();
        configFirebase();
        configEventsFirebase();
        configEvents();

    }

    // Configs
    private void configViews() {
        etName = findViewById(R.id.editTextName);
        etLastName = findViewById(R.id.editTextLastName);
        etEmail = findViewById(R.id.editTextEmail);
        etPass = findViewById(R.id.editTextPass);
        listViewUserList = findViewById(R.id.UserList);
    }

    private void configFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void configEventsFirebase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                    UserModel userModel = snapShot.getValue(UserModel.class);
                    userModelList.add(userModel);

                    userModelArrayAdapter = new ArrayAdapter<UserModel>(MainActivity.this, android.R.layout.simple_list_item_1, userModelList);
                    listViewUserList.setAdapter(userModelArrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configEvents() {
        listViewUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userModelSelected = ( UserModel ) parent.getItemAtPosition(position);
                etName.setText(userModelSelected.getName());
                etLastName.setText(userModelSelected.getLastname());
                etEmail.setText(userModelSelected.getEmail());
                etPass.setText(userModelSelected.getPassword());
            }
        });
    }

    //Others

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.icon_add: {
                if(validateFields()){

                    String uuid = UUID.randomUUID().toString();
                    UserModel userModel = new UserModel(
                            uuid,
                            etName.getText().toString(),
                            etLastName.getText().toString(),
                            etEmail.getText().toString(),
                            etPass.getText().toString()
                    );
                    databaseReference.child("User").child(uuid).setValue(userModel);

                    Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                    emptyFields();
                }else{
                    Toast.makeText(this, "Todos los campos deben de estar llenos para poder continuar", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.icon_save: {

                UserModel userModel = new UserModel(
                        userModelSelected.getId(),
                        etName.getText().toString().trim(),
                        etLastName.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etPass.getText().toString().trim()
                );
                databaseReference.child("User").child(userModel.getId()).setValue(userModel);

                Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
                emptyFields();
                break;
            }
            case R.id.icon_delete: {
                UserModel userModel = new UserModel();
                userModel.setId(userModelSelected.getId());

                databaseReference.child("User").child(userModel.getId()).removeValue();

                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                emptyFields();
                break;
            }
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields(){
        boolean isSuccess = false;
        String name = etName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !pass.isEmpty()){
            isSuccess = true;
        }

        return isSuccess;
    }

    private void emptyFields(){
        etName.setText("");
        etEmail.setText("");
        etLastName.setText("");
        etPass.setText("");
    }
}
