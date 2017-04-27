package com.example.oiltanker.test;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest.permission;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.oiltanker.test.R.id.picture;


public class ToDoContentsActivity extends AppCompatActivity {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    Bitmap tmp = null;
    ImageView picture;
    Date tmp_date = null;
    private int currentTheme = 0;

    @Override
    public void setTheme(@StyleRes int resid) {
        currentTheme = resid;
        super.setTheme(resid);
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelable("picture", tmp);

        if (tmp_date != null) {
            outState.putString("date", dateFormat.format(tmp_date));
        } else
            outState.putString("date", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ToDoDataManager.getTheme());
        setContentView(R.layout.todo_contents);

        Intent intent = getIntent();
        final String uid = intent.getStringExtra("position");

        ToDoItem ftd = null;
        for (ToDoItem i : ToDoDataManager.values()) {
            if (i.id.equals(uid)) ftd = new ToDoItem(i);
        }
        final ToDoItem td = ftd;

        final EditText txt_name = (EditText) findViewById(R.id.text_name);
        final EditText txt_contents = (EditText) findViewById(R.id.text_contents);
        final Button btn_save = (Button) findViewById(R.id.button_save);
        final Button btn_share = (Button) findViewById(R.id.button_share);
        final Button btn_delete = (Button) findViewById(R.id.button_delete);
        picture = (ImageView) findViewById(R.id.image);
        final TextView date = (TextView) findViewById(R.id.text_date);

        if (savedInstanceState == null) {
            txt_name.setText(td.name);
            txt_contents.setText(td.contents);
            picture.setImageBitmap(td.picture);
            tmp = td.picture;
            date.setText(dateFormat.format(td.date));
            tmp_date = td.date;
        } else {
            tmp = savedInstanceState.getParcelable("picture");
            picture.setImageBitmap(tmp);

            String date_str = savedInstanceState.getString("date");
            if (date_str != null) {
                try {
                    date.setText(date_str);
                    tmp_date = dateFormat.parse(date_str);
                } catch (ParseException e) {
                    e.printStackTrace();
                    tmp_date = Calendar.getInstance().getTime();
                    date.setText(dateFormat.format(tmp_date));
                }
            } else {
                tmp_date = Calendar.getInstance().getTime();
                date.setText(dateFormat.format(tmp_date));
            }
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                td.name = txt_name.getText().toString();
                td.contents = txt_contents.getText().toString();
                td.picture = tmp;
                td.date = tmp_date;
                ToDoDataManager.notify(td);
                ToDoContentsActivity.this.finish();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_name.setText(td.name);
                txt_contents.setText("");

                picture.setImageBitmap(null);
                tmp = null;

                tmp_date = Calendar.getInstance().getTime();
                date.setText(dateFormat.format(tmp_date));
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cb;
                if (td.check) cb = "\u2611";
                else cb = "\u2610";

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "\t" + cb + "◄" + txt_name.getText().toString().toUpperCase() + "►\n\n" + txt_contents.getText().toString() + "\n----\n" + dateFormat.format(tmp_date);
                String shareSub = txt_name.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = new CharSequence[]{"Take a photo", "Select file"};

                AlertDialog dialog = new AlertDialog.Builder(ToDoContentsActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ActivityCompat.requestPermissions(
                                                ToDoContentsActivity.this,
                                                new String[]{permission.CAMERA, permission.READ_EXTERNAL_STORAGE},
                                                1
                                        );
                                        break;
                                    case 1:
                                        ActivityCompat.requestPermissions(
                                                ToDoContentsActivity.this,
                                                new String[]{permission.READ_EXTERNAL_STORAGE},
                                                0
                                        );
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).create();
                dialog.show();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog pickDate = new DatePickerDialog(
                        ToDoContentsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(year, month, dayOfMonth);
                                //td.date = c.getTime();
                                tmp_date = c.getTime();
                                date.setText(dateFormat.format(tmp_date));
                                //ToDoDataManager.notify(position);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                pickDate.setTitle("Pick a date");
                pickDate.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*Log.d("WHERE IS MY PERMISSIONS", "method is called");
        for (int i = 0; i < permissions.length; i++) {
            Log.d("ASKED PERMISSION LIST", "PERMISSION: " + permissions[i] + " -- " + ((grantResults[i] == PackageManager.PERMISSION_GRANTED) ? "GRANTED" : "NOT GRANTED"));
        }*/

        switch (requestCode) {

            case 0:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent pickPhoto = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                } else {
                    Toast.makeText(this, "Require storage permission", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case 1:
                if (grantResults.length > 1) {
                    boolean all_good = true;
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        all_good = false;
                        Toast.makeText(this, "Require camera permission", Toast.LENGTH_LONG)
                                .show();
                    }
                    if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                        all_good = false;
                        Toast.makeText(this, "Require storage permission", Toast.LENGTH_LONG)
                                .show();
                    }
                    if (all_good) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePicture.putExtra("return-data", true);
                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("RESULT", "Getting image result");
        switch (requestCode) {
            case 1:
                //Log.d("RESULT", "Getting image from device");
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();

                    ContentResolver cr = getContentResolver();
                    InputStream is;
                    try {
                        is = cr.openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        is = null;
                        e.printStackTrace();
                    }

                    if (is != null) {
                        picture.setImageBitmap(BitmapFactory.decodeStream(is));
                        tmp = ((BitmapDrawable) picture.getDrawable()).getBitmap();
                    }
                }
                break;

            case 0:
                //Log.d("RESULT", "Getting image from camera");
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap;
                    if (data.getExtras() != null)
                        bitmap = (Bitmap) data.getExtras().get("data");
                    else bitmap = null;

                    if (bitmap != null) {
                        tmp = bitmap;
                        picture.setImageBitmap(bitmap);
                    }
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
    }
}
