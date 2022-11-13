package skieg.travel.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import skieg.travel.MainActivity;
import skieg.travel.R;
import skieg.travel.user.User;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class PostPage extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private User user;
    private EditText infoBlock;
    private Button postButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page_activity);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://skieg-364814-default-rtdb.firebaseio.com/").getReference();
        postButton = findViewById(R.id.post);


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(view);
            }
        });
    }


    private void postToDatabase(View view, Post post){

         String postID = databaseReference.push().getKey();

         Task setValueTask = databaseReference.child("Forum").child("posts").child(postID).setValue(post);

         setValueTask.addOnSuccessListener(new OnSuccessListener(){

             @Override
             public void onSuccess(Object o) {

                System.out.println("Succeeded");
                 infoBlock.setText("");
             }
         });

//        Intent intent = new Intent(PostPage.this, PostActivity.class);
//        startActivity(intent);
    }

    public void post(View view){

        infoBlock = findViewById(R.id.postContent);
        String info = infoBlock.getText().toString();
        String date = CurrentDateTime.date();
        System.out.println("DATE: " + date);
        Post post = new Post(MainActivity.USER, date, info);
        postToDatabase(view,post);
    }


    private static class CurrentDateTime{
        public static String date() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            return dtf.format(LocalDateTime.now());
        }
    }

}
