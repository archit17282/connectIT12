package com.example.architpanwar.connected;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/*
START AUTHENIC BY SETTING UP AUTH STATE LISTNER
HAVE TWO STATES
==SIGN IN  AND SIGN OUT
OFF STATE ATCHED TO THE THE OFFF STATE OBJECT

AUTH STATE LISTNER SHOWS KI USER KO AKHIR DIKHANA KYA CHAHTE HO
ATTACH LISTNER ON THE ON RESUME AND WILL REMOVE IT ON THE ON PAUSE WHEN THE APP IS NO LONGER IN USE
 */


/*
firebase ui
===
https://github.com/firebase/FirebaseUI-Android
link to add login and to study about it
 */

/*
FIREBASE AUTHENTICATION RULES

==
DATABASE SECURITY RULES
3 n=main type of rules ie
READ=
WRITE=
VALIDATE=



 */
/*
SEE CONCEPT OF EXTENDING ARRAY ADAPTER   AS USME SIDHA KAISE KAAM HO JATA HAI BAS ADD LIKHK DO TO;
 */
// "auth != null"
// "auth != null

/*
FIRE BASE DATABASE==
WHY USE THIS TYPE OF DATABASE==
BECAUSE IT SYNCHRONYSES THE DATA ON THE SPOT
RESPONSIVE WHEN USER IS OFFLINE AS THE FIREBASE DATABASE STORES IT AS CACHE
 */
/*
STRUCTURE OF FIREBASE DATABASE==
PUSH IDS==IMP TO DIFFERENTIATE BETWEEN USERS STUDY THIS
 */
public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;
    public static final int RC_PHOTO = 2;

    private ListView mMessageListView;
    private chatadapter mMessageAdapter;
    // private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;

    private FirebaseDatabase mfireDatabase;           //what are thse two things ????doubt
    static private DatabaseReference mMessageDatabaseReference;//it refernces a specific portion of the database
    private ChildEventListener mchild;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authlistner;
    private FirebaseStorage mstore;
    private StorageReference mrefstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mUsername = ANONYMOUS;

        mfireDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        mstore = FirebaseStorage.getInstance();
        mMessageDatabaseReference = mfireDatabase.getReference().child("messages");
        mrefstore = mstore.getReference().child("Chat Photos");
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);


        List<freindlychat> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new chatadapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);


        // mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                FirebaseUser user = auth.getCurrentUser();
                Onsignedini(user.getDisplayName());
                if (charSequence.toString().trim().length() > 0 && user.getUid().equals("A1L8v5bNmqRmnRaiRKGn2QhEw5y2") || user.getUid().equals("rcqdEQn7z7MZoj7VC8dxsjOJpyn2")) {
                    mSendButton.setEnabled(true);

                } else if (charSequence.toString().trim().length() > 0) {

                    Toast.makeText(MainActivity.this, "you are not allowed to write here", Toast.LENGTH_SHORT).show();
                    mMessageEditText.setText(" ");
                    mSendButton.setEnabled(false);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                freindlychat friendly = new freindlychat(mMessageEditText.getText().toString(), mUsername, null);

                mMessageDatabaseReference.push().setValue(friendly);

                mMessageEditText.setText("");
            }
        });


        //mmessage refrence just tells us that what are we listning to and child event listner will trigger the data when it happens

        authlistner = new FirebaseAuth.AuthStateListener() {
            /*
            isme jo fire base auth hai wo ye batayga ki user sign in hai ki nahi
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "You are signed in.Welcome to CONNECTED", Toast.LENGTH_SHORT).show();
                    Onsignedini(user.getDisplayName());
                } else {
                    //yaha pe jaha user sign out hai yaha pe firebase ui kaam me aati hai
                    Onsignedoutclean();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)//yekya hota hai pata lagao==ye inf stores of user as lock which is not needed here so isko false rakha hai
                                    .setProviders(AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER).setTheme(R.style.AppTheme)
                                    .build(),
                            RC_SIGN_IN);
                    //RC_SIGN_IN==YE JUST EK REQUEST CODE HAI INTENT KE LIYE
                }

            }
        };


        //understand the whole photo picking process
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                Onsignedini(user.getDisplayName());


                if (user.getUid().equals("A1L8v5bNmqRmnRaiRKGn2QhEw5y2") || user.getUid().equals("rcqdEQn7z7MZoj7VC8dxsjOJpyn2")) {
                    Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
                    pick.setType("image/jpeg");
                    pick.putExtra(Intent.EXTRA_LOCAL_ONLY, true);//inko samajhna aakhir inme ho kya raha hai aakhir
                    startActivityForResult(Intent.createChooser(pick, "Complete"), RC_PHOTO);
                } else {
                    Toast.makeText(MainActivity.this, "You are not allowed to share photos", Toast.LENGTH_SHORT);
                }

                //Adding intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); will allow for local files only. It will exclude picasa images
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO && resultCode == RESULT_OK) {
            Uri selectimage = data.getData();
            StorageReference photoref = mrefstore.child(selectimage.getLastPathSegment());
            //now we have the image and reference where we need to store image just need to send the image at that jagah
            //upload the file to firebase storage
//
            photoref.putFile(selectimage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri sent = taskSnapshot.getDownloadUrl();
                    freindlychat friendly = new freindlychat(null, mUsername, sent.toString());
                    mMessageDatabaseReference.push().setValue(friendly);
                }
            });


            //TaskSnapshot==key to the url of the file that had been just send to the storage


        }
    }

    private void Onsignedini(String displayName) {


        mUsername = displayName;
        if (mchild == null) {
            mchild = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    freindlychat chat = dataSnapshot.getValue(freindlychat.class);//desiarilized to friendly message object
                    mMessageAdapter.add(chat);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMessageDatabaseReference.addChildEventListener(mchild);
        }


    }


    private void Onsignedoutclean() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseListner();
    }


    //dethach the radble power of database when signing out from the app
    private void detachDatabaseListner() {
        if (mchild != null) {
            mMessageDatabaseReference.removeEventListener(mchild);
            mchild = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authlistner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        auth.removeAuthStateListener(authlistner);
        detachDatabaseListner();
        mMessageAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_signout:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }


    }

    public static int remove(freindlychat message) {
        Query applesQuery = mMessageDatabaseReference.orderByChild("text").equalTo(message.getText().toString());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }


            public void onCancelled(DatabaseError data) {
                Log.e(TAG, "onCancelled", data.toException());
            }

                                                   }
        );

        return 1;

    }
}

