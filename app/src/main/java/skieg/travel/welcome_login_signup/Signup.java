package skieg.travel.welcome_login_signup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import skieg.travel.InputValidation;
import skieg.travel.MainActivity;
import skieg.travel.R;
import skieg.travel.WeatherActivity;
import skieg.travel.user.User;

public class Signup extends AppCompatActivity {

//    private FirebaseAuth authentication;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private boolean validCity = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this::backBtnClicked);
//        authentication = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        Button signupButton = findViewById(R.id.signupBtn);
        signupButton.setOnClickListener(view -> {
            signUp();
        });

    }


    public void backBtnClicked(View view) {
        Intent mainIntent = new Intent(this, Welcome.class);
        startActivity(mainIntent);
    }


    public void signUp(){

        EditText firstNameInput = findViewById(R.id.firstName);
        EditText lastNameInput = findViewById(R.id.lastName);
        EditText usernameInput = findViewById(R.id.username);
        EditText emailInput = findViewById(R.id.userEmail);
        EditText passwordInput = findViewById(R.id.userPassword);

        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        EditText cityInput = findViewById(R.id.city);
        String city = cityInput.getText().toString();

        if (InputValidation.invalidStringInput(firstName) || InputValidation.invalidStringInput(lastName) || InputValidation.invalidStringInput(username)
            || InputValidation.invalidStringInput(email) || InputValidation.invalidStringInput(password) || InputValidation.invalidStringInput(city)) {
            Toast.makeText(Signup.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = "https://api.openweathermap.org/data/2.5/weather";
        String appid = "fa211ad253385ab5e5f303af6dfebb44";
        String tempUrl = url + "?q=" + city + "&appid=" + appid;

        Signup.AsyncTaskRunner runner = new Signup.AsyncTaskRunner();
        runner.execute(tempUrl);


        if (!validCity) {
            Toast.makeText(Signup.this, "Invalid city entered.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(databaseReference.push().getKey(), firstName, lastName, city, username, email, password);
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);

        // create a task to set the value of the node as the new user
        Task setValueTask = databaseReference.child("Users").child(user.getId()).setValue(user);

        // add a success listener to the task
        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(Signup.this, "Account created", Toast.LENGTH_LONG).show();

                redirectMainPage(user.getId(), user.getUsername(), user.getPassword());
            }
        });
    }


    public void redirectMainPage(String id, String username, String password) {
        Intent intent = new Intent(Signup.this, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("username", username);
        bundle.putString("password", password);
        intent.putExtras(bundle);

        startActivity(intent);
    }


    class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(Signup.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        response.getJSONObject("main");
                        validCity = true;
                        System.out.println("VALID CITY = " + validCity);
//                        Log.e("MESSAGE", jsonObjectMain.toString());
                    } catch (JSONException e) {
                        validCity = false;
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    validCity = false;
                    System.out.println("VALID CITY = " + validCity);
                }
            });
            queue.add(request);
            return null;
        }
    }
}

