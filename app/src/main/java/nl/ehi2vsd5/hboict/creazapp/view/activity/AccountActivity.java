package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.User;

public class AccountActivity extends AppCompatActivity {

    private final static String TAG = AccountActivity.class.getSimpleName();

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private ImageView profileImage,folder;
    private EditText username,oldPassword,newPassword,newPasswordRe;
    private Button save;
    private Intent intent;

    private static final int REQUEST_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        profileImage = (ImageView) findViewById(R.id.account_profile_image);
        folder = (ImageView) findViewById(R.id.account_folder);
        username = (EditText) findViewById(R.id.account_username);
        oldPassword = (EditText) findViewById(R.id.account_old_password);
        newPassword = (EditText) findViewById(R.id.account_password);
        newPasswordRe = (EditText) findViewById(R.id.account_password_re);
        save = (Button) findViewById(R.id.account_save);
        intent = new Intent(this,LoginActivity.class);

        folder.setEnabled(false);
        username.setEnabled(false);
        newPassword.setEnabled(false);
        newPasswordRe.setEnabled(false);
        save.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        //If the user is not null(if logged in)
        if(mUser != null){
            folder.setEnabled(true);
            username.setEnabled(true);
            newPassword.setEnabled(true);
            newPasswordRe.setEnabled(true);
            save.setEnabled(true);



            final String photoUrl = String.valueOf(mUser.getPhotoUrl());

            if (photoUrl != null && !photoUrl.equals("null")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(photoUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            Glide.with(profileImage.getContext()).load(downloadUrl)
                                    .thumbnail(0.5f)
                                    .into(profileImage);
                        } else {
                            Log.e(TAG, "onComplete: Getting download url was not successfull.", task.getException());
                        }
                    }
                });
            }
        }

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().isEmpty()){
                    Toast.makeText(AccountActivity.this, "Gebruiksnaam mag niet leeg zijn", Toast.LENGTH_SHORT).show();
                }else if(oldPassword.getText().toString().isEmpty() && !newPassword.getText().toString().isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Vul huidige wachtwoord in om uw nieuwe wachtwoord op te slaan", Toast.LENGTH_SHORT).show();
                }else if(newPassword.getText().toString().isEmpty() && !oldPassword.getText().toString().isEmpty()){
                    Toast.makeText(AccountActivity.this, "Vul uw nieuw wachtwoord in", Toast.LENGTH_SHORT).show();
                }else if(!newPassword.getText().toString().isEmpty() && newPasswordRe.getText().toString().isEmpty()){
                    Toast.makeText(AccountActivity.this, "Vul uw nieuw wachtwoord nogmaals in", Toast.LENGTH_SHORT).show();
                }else if(!oldPassword.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty() && !newPasswordRe.getText().toString().isEmpty()){
                    Toast.makeText(AccountActivity.this, "Vul eerst uw nieuwe wachtwoord in voordat u verder gaat", Toast.LENGTH_SHORT).show();
                }else if(oldPassword.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty() && !newPasswordRe.getText().toString().isEmpty()) {
                    Toast.makeText(AccountActivity.this, "Vul eerst uw huidige en nieuwe wachtwoord in voordat u de wachtwoord herhaalt", Toast.LENGTH_SHORT).show();
                }else if(!username.getText().toString().isEmpty() && oldPassword.getText().toString().isEmpty() && newPassword.getText().toString().isEmpty() && newPasswordRe.getText().toString().isEmpty()) {
                    updateUsername(username.getText().toString());
                }else {
                    updateUsername(username.getText().toString());
                    updatePassword(oldPassword.getText().toString(),newPassword.getText().toString(),newPasswordRe.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {

            if (mUser != null) {
                final Uri uri = data.getData();
                Log.d(TAG, "URI: " + uri.toString());

                String key = mDatabaseReference.getKey();

                DatabaseReference userRef = mDatabaseReference.child(User.CHILD).push();


                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference()
                        .child(User.CHILD)
                        .child(mUser.getUid())
                        .child(uri.getLastPathSegment());
                uploadImage(storageReference,key, uri);
            } else {
                Toast.makeText(this, "User is not signed in!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadImage(StorageReference storageReference, String key, final Uri uri) {
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadImage: success");
                    Log.d(TAG, "onComplete: URL " + task.getResult().getMetadata().getDownloadUrl());
                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(task.getResult().getMetadata().getDownloadUrl())
                            .build();
                    mUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.signOut();
                                Log.d(TAG, "ChangeRequest success! Photo URL = " + mUser.getPhotoUrl() + ", displayName = " + mUser.getDisplayName());
                            } else {
                                Log.d(TAG, "Change request failed");
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "uploadImage: failed");
                }
            }
        });
    }

    void updateUsername(String username){
        mUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: username = " + mUser.getDisplayName());
                            Log.d(TAG, "re-authenticated. onComplete: displayname = " + mUser.getDisplayName() + ", photo-url" + mUser.getPhotoUrl());
                            mAuth.signOut();
                            startActivity(intent);


                        } else {
                            Log.w(TAG, "Failed to change displayname", task.getException());
                        }
                    }
                });
    }

    void updatePassword(final String oldPassword, final String password, String passwordRepeat){
        if(password.equals(passwordRepeat)){

            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(),oldPassword);
            mUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Nieuwe wachtwoord opgeslagen");
                                            mAuth.signOut();
                                            startActivity(intent);
                                        } else {
                                            Log.d(TAG, "Fout met nieuwe wachtwoord opslaan");
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "Fout met authenticatie");
                            }
                        }
                    });
        }
    }
}
