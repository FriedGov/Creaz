package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.ehi2vsd5.hboict.creazapp.R;

public class LoginActivity extends AppCompatActivity {


    private EditText mLoginEmail, mLoginPassword;
    private Button mLogin;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView mRegister;
    private final static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mLoginEmail = (EditText) findViewById(R.id.login_email);
        mLoginPassword = (EditText) findViewById(R.id.login_password);
        mRegister = (TextView) findViewById(R.id.login_register);

        mLogin = (Button) findViewById(R.id.login_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: loginClick");
                if (mLoginEmail.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter e-mail", Toast.LENGTH_SHORT).show();
                } else if (mLoginPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else {
                    String username = mLoginEmail.getText().toString();
                    String password = mLoginPassword.getText().toString();
                    signIn(username, password);
                }


            }
        });
        final Intent registerIntent = new Intent(this, RegisterActivity.class);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        // stop the user from closing app
        moveTaskToBack(true);
    }

    /**
     * Clean the string of spaces values
     *
     * @param string
     * @return
     */
    private String cleanString(String string) {
        string = string.replace(" ", "");
        return string;
    }


    /**
     * Try logging in with the given values
     *
     * @param username
     * @param password
     */
    private void signIn(String username, String password) {
        Log.d(TAG, "signIn: ");
        final Intent loginIntent = new Intent(this, MainActivity.class);
        username = cleanString(username);
        password = cleanString(password);
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail: success");
                            mUser = mAuth.getCurrentUser();
                            startActivity(loginIntent);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
