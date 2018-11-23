package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private Calendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.getDefault());
    private EditText mBirthDate;
    private final static String TAG = RegisterActivity.class.getSimpleName();
    private EditText mEmail, mPassword1, mPassword2, mUserName, mLastName;
    private Button mRegister;

    private long birthDate = 0;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        mBirthDate = (EditText) findViewById(R.id.input_birth_date);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword1 = (EditText) findViewById(R.id.input_password);
        mPassword2 = (EditText) findViewById(R.id.input_password_repeat);
        mRegister = (Button) findViewById(R.id.register_button);
        mUserName = (EditText) findViewById(R.id.input_user_name);

        // Stuff for the date time picker
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                birthDate = calendar.getTimeInMillis();

                DateFormat formatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
                mBirthDate.setText(formatter.format(new Timestamp(birthDate)));
            }
        };

        //Open the dateTimePickerDialog when the user click on the the text
        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validInput()) {
                    registerNewUser(mUserName.getText().toString(),
                            mEmail.getText().toString(),
                            mPassword1.getText().toString(),
                            birthDate);
                }
            }
        });
    }

    private boolean validInput() {
        boolean valid = true;

        String userName = mUserName.getText().toString();
        String password1 = mPassword1.getText().toString();
        String password2 = mPassword2.getText().toString();
        String email = mEmail.getText().toString();

        // check first name
        if (userName.isEmpty()) {
            mUserName.setError(getString(R.string.error_required));
            valid = false;
        }

        // check first password
        if (password1.isEmpty()) {
            mPassword1.setError(getString(R.string.error_required));
            valid = false;
        }

        // check for repeat first password
        if (password2.isEmpty() || password2.length() < 6 || password2.length() > 12) {
            mPassword2.setError(getString(R.string.error_password));
            valid = false;
        }

        if (!password1.equals(password2)) {
            mPassword2.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        // check email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(getString(R.string.error_email));
            valid = false;
        }

        // check birth date
        if (birthDate == 0) {
            mBirthDate.setError(getString(R.string.error_required));
            valid = false;
        }

        return valid;
    }

    private void registerNewUser(final String userName, final String email,
                                 final String password, final long birthDate) {
        final Intent intent = new Intent(this, LoginActivity.class);
        Log.d(TAG, "registerNewUser: username, password");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: success");
                            mAuth.signInWithEmailAndPassword(email, password);

                            mUser = mAuth.getCurrentUser();

                            // create a public profile
                            User publicProfile = new User(userName,
                                    System.currentTimeMillis(), birthDate);
                            mReference.child(User.CHILD).child(mUser.getUid()).setValue(publicProfile);
                            startActivity(intent);

                        } else {

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                RegisterActivity.this)
                .setTitle("Error:")
                .setMessage(R.string.error_profile_creation)
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // restart activity
                        RegisterActivity.this.startActivity(
                                new Intent(RegisterActivity.this,
                                        RegisterActivity.class));
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}
