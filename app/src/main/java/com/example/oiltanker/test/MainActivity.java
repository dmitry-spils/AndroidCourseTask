package com.example.oiltanker.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ArrayList<ToDoItem> values;
    TodoAdapter TDadapter;
    private int currentTheme = 0;
    private boolean is_signedin = false;

    private ListView mListVeiw = null;

    private static final int RC_SIGN_IN = 123;
    private static boolean run = false;

    public static final void hideKeyboard(Activity act) {
        if (act != null && act.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void setTheme(@StyleRes int resid) {
        currentTheme = resid;
        super.setTheme(resid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!run) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            run = true;
        }
        super.onCreate(savedInstanceState);

        setTheme(ToDoDataManager.getTheme());
        setContentView(R.layout.activity_main);


        ToDoDataManager.giveContext(getApplicationContext());
        if (!ToDoDataManager.IsLoaded())
            ToDoDataManager.LoadAll();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            if (!ToDoDataManager.IsLoaded()) {
                TDadapter = new TodoAdapter(this);
            } else TDadapter = ToDoDataManager.getAdapter();

            // already signed in
            Toast.makeText(this, "User singed in", Toast.LENGTH_LONG)
                    .show();
            is_signedin = true;

        } else {
            // not signed in
            Toast.makeText(this, "User NOT singed in", Toast.LENGTH_LONG)
                    .show();
            is_signedin = false;
            TDadapter = new TodoAdapter(this);

            new AlertDialog.Builder(this)
                    .setTitle("Firebase")
                    .setMessage("Do you want to use Firebase for online storage?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(
                                    // Get an instance of AuthUI based on the default app
                                    AuthUI.getInstance().createSignInIntentBuilder()
                                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                            //.setTheme(getResources().getInteger(ToDoDataManager.getTheme()))
                                            .build(),
                                    RC_SIGN_IN);
                            //finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        finish();
                        }
                    })
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        mListVeiw = (ListView) findViewById(R.id.todo_list);
        //mListVeiw.setAdapter(adapter);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.addButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToDoDataManager.add(editText.getText().toString(), new Date(), false, "", null);
                //TDadapter.notifyDataSetChanged();
                editText.setText("");
                hideKeyboard(MainActivity.this);
            }
        });

        LinearLayout main = (LinearLayout) findViewById(R.id.task_act);
        main.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TDadapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (is_signedin) {
            ToDoDataManager.setWebTools(FirebaseAuth.getInstance().getCurrentUser().getUid()/*, FirebaseStorage.getInstance(), FirebaseDatabase.getInstance()*/);
            ToDoDataManager.AddListeners();
        }
        //if (!ToDoDataManager.IsLoaded() && is_signedin)
        //    ToDoDataManager.AddListeners();
        mListVeiw.setAdapter(TDadapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                //startActivity(SignedInActivity.createIntent(this, response));
                Toast.makeText(this, "All ok", Toast.LENGTH_LONG)
                        .show();
                is_signedin = true;

                ToDoDataManager.setWebTools(FirebaseAuth.getInstance().getCurrentUser().getUid()/*, FirebaseStorage.getInstance(), FirebaseDatabase.getInstance()*/);
                ToDoDataManager.AddListeners();
                //finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    //showSnackbar(R.string.sign_in_cancelled);
                    Toast.makeText(this, "I guess we going back", Toast.LENGTH_LONG)
                            .show();
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //showSnackbar(R.string.no_internet_connection);
                    Toast.makeText(this, "No Network", Toast.LENGTH_LONG)
                            .show();
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //showSnackbar(R.string.unknown_error);
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG)
                            .show();
                    finish();
                    return;
                }
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }

            //showSnackbar(R.string.unknown_sign_in_response);
            Toast.makeText(this, "What the hell man?", Toast.LENGTH_LONG)
                    .show();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //Log.d("LOL", "LOL\nLOL\nLOL\nLOL");
        //mMenu = menu;
        //lol += 1;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.firebase_signout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                Toast.makeText(MainActivity.this, "Signed out of Firebase", Toast.LENGTH_LONG)
                                        .show();
                                is_signedin = false;
                                MainActivity.this.invalidateOptionsMenu();
                                //startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Firebase")
                                        .setMessage("Do you want to use Firebase for online storage?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivityForResult(
                                                        // Get an instance of AuthUI based on the default app
                                                        AuthUI.getInstance().createSignInIntentBuilder()
                                                                .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                                                //.setTheme(getResources().getInteger(ToDoDataManager.getTheme()))
                                                                .build(),
                                                        RC_SIGN_IN);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        //.setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentTheme != ToDoDataManager.getTheme()) {
            recreate();
        }

        TDadapter.notifyDataSetChanged();
    }
}
