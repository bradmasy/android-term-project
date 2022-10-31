package skieg.travel.user;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    private String username;
    private String password;
    private String id;

    public User() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        this.id  = databaseReference.push().getKey();
        Task setValueTask = databaseReference.child("User").child(id).setValue(this);

        setValueTask.addOnSuccessListener(new OnSuccessListener(){

            @Override
            public void onSuccess(Object o) {
                Log.d("Success: ", "user: " + id + " has been created.");
            }
        });

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId(){
        return this.id;
    }
}