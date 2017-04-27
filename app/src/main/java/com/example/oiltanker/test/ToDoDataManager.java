package com.example.oiltanker.test;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class ToDoDataManager {
    private static Context mContext = null;

    private static boolean mIsLoaded = false;
    private static TodoAdapter mTodoAdapter = null;
    private static ArrayList<ToDoItem> values;
    private static ToDoOpenHelper dbHelper = null;
    private static StorageReference mStorageRef;

    private static int theme_id = R.style.AppTheme;
    private static String PREFS_NAME = "TestAppPrefs";
    private static SharedPreferences pref = null;
    private static SharedPreferences.Editor pref_editor = null;


    private static String mUserId;
    private static FirebaseStorage mWebStorage;
    //private static FirebaseDatabase mWebDB;

    private static DatabaseReference mUserRef;
    private static StorageReference mUserFiles;

    private static String pic_name = null;


    private static ByteArrayOutputStream getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            //Log.d("LOL", "no shit");
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream;
    }
    private static Bitmap getBitmapFromBytes(byte[] bytes, int size) {
        if (size == 0) return null;
        return BitmapFactory.decodeByteArray(bytes, 0, size);
    }

    public static String GenRandString(char[] alphabet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(alphabet.length);
            result[i] = alphabet[randomCharIndex];
        }
        return new String(result);
    }
    private static String GenImageName() {
        String pic_name;
        pic_name = GenRandString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray(), 4);
        for (int i = 0; i < 4; i++)
            pic_name += "-" + GenRandString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray(), 4);
        pic_name += ".png";
        return pic_name;
    }

    protected static class TDI {
        String TD_name;
        String TD_date;
        Boolean TD_check;
        String TD_contents;
        String TD_picture_id;

        TDI (String TD_name, String TD_date, Boolean TD_check,  String TD_contents, String TD_picture_id) {
            this.TD_name = TD_name;
            this.TD_date = TD_date;
            this.TD_check = TD_check;
            this.TD_contents = TD_contents;
            this.TD_picture_id = TD_picture_id;
        }

        TDI () {
            this.TD_name = null;
            this.TD_date = null;
            this.TD_check = null;
            this.TD_contents = null;
            this.TD_picture_id = null;
        }
    }


    private ToDoDataManager() {
        values = new ArrayList<>();
    }

    public static void giveContext(Context context) {
        mContext = context;

        //dbHelper = new ToDoOpenHelper(context);

        pref = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        pref_editor = pref.edit();
        theme_id = pref.getInt("theme_id", R.style.AppTheme);
        //theme_id = dbHelper.getThemeID();
    }

    public static void giveAdapter(TodoAdapter adapter) {
        mTodoAdapter = adapter;
    }
    public static TodoAdapter getAdapter() {
        return mTodoAdapter;
    }
    public static boolean IsLoaded() { return mIsLoaded; }

    public static void LoadAll() {
        /*try {
            values = dbHelper.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();*/
        values = new ArrayList<>();
    }

    //public static void add(ToDoItem td) {
    //    values.add(td);
    //}
    public static void add(String name, Date date, boolean check, String contents, Bitmap picture) {
        /*try {
            ToDoItem td = new ToDoItem(getNewID(), name, date, check, contents, picture);
            dbHelper.insertItem(td);
            values.add(td);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        DatabaseReference newRef = mUserRef.push();
        String id = newRef.getKey();
        //Log.d("NEW_ITEM_ID", id);

        if (picture != null) {
            pic_name = GenImageName();
            while (mUserFiles.child(pic_name).getDownloadUrl().isSuccessful())
                pic_name = GenImageName();
        } else
            pic_name = "none";

        DatabaseReference elemRef = mUserRef.child(id);
        elemRef.setValue(new TDI(name, ToDoDBContract.iso8601Format.format(date), check, contents, pic_name));
        //Log.d("DATE", "\n\n" + ToDoDBContract.iso8601Format.format(date) + "\n\n");

        if (picture != null) {
            StorageReference image = mUserFiles.child(pic_name);
            ByteArrayOutputStream bytes = getBytesFromBitmap(picture);
            image.putBytes(bytes.toByteArray());
        }
    }

    public static ToDoItem get(int index) {
        return values.get(index);
    }

    public static int size() {
        return values.size();
    }

    public static ArrayList<ToDoItem> values() {
        return values;
    }

    public static void remove(int index) {
        /*try {
            ToDoItem td = values.get(index);
            dbHelper.deleteItem(td.id);
            values.remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        DatabaseReference elemRef = mUserRef.child(values.get(index).id);
        if (!values.get(index).pic_name.equals("none"))
            mUserFiles.child(values.get(index).pic_name).delete();
        elemRef.removeValue();
    }

    public static void notify(final ToDoItem td) {

        //final ToDoItem v = values.get(index);
        //final TDI val = new TDI(v.name, v.date, v.check, v.contents, "LOL");

        final AsyncTask ATask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                /*try {
                    dbHelper.updateItem(values.get(index));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                if (!td.pic_name.equals("none"))
                        mUserFiles.child(td.pic_name).delete();

                if (td.picture != null) {
                    pic_name = GenImageName();
                    while (mUserFiles.child(pic_name).getDownloadUrl().isSuccessful())
                        pic_name = GenImageName();
                } else
                    pic_name = "none";

                if (td.picture != null) {
                    StorageReference image = mUserFiles.child(pic_name);
                    ByteArrayOutputStream bytes = getBytesFromBitmap(td.picture);
                    image.putBytes(bytes.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(mContext, "Image push success!", Toast.LENGTH_LONG).show();
                            DatabaseReference elemRef = mUserRef.child(td.id);
                            elemRef.setValue(new TDI(td.name, ToDoDBContract.iso8601Format.format(td.date), td.check, td.contents, pic_name));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Image push failure!", Toast.LENGTH_LONG).show();
                            //Log.d("Error", e.toString());
                        }
                    });
                } else {
                    DatabaseReference elemRef = mUserRef.child(td.id);
                    elemRef.setValue(new TDI(td.name, ToDoDBContract.iso8601Format.format(td.date), td.check, td.contents, pic_name));
                }

                return null;
            }
        };
        ATask.execute();
        /*try {
            dbHelper.updateItem(values.get(index));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
    public static void notify_check(final int index) {
        final AsyncTask ATask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                /*dbHelper.updateItem_check(values.get(index));*/
                Boolean check = values.get(index).check;
                String id = values.get(index).id;

                DatabaseReference checkRef = mUserRef.child(id).child("TD_check");
                checkRef.setValue(check);

                return null;
            }
        };
        ATask.execute();
    }

    public static int getTheme() {
        return theme_id;
    }

    public static void setTheme(int theme_id) {
        ToDoDataManager.theme_id = theme_id;
        //dbHelper.updateThemeID(theme_id);
        pref_editor.putInt("theme_id", theme_id);
        pref_editor.commit();
    }

    public static void setWebTools(String UserId/*, final FirebaseStorage storage, FirebaseDatabase database*/) {
        mUserId = UserId;
        mWebStorage = FirebaseStorage.getInstance();
        FirebaseDatabase mWebDB = FirebaseDatabase.getInstance();

        //mWebDB.setPersistenceEnabled(true);

        mUserFiles = mWebStorage.getReference().child("users").child(mUserId);
        mUserRef = mWebDB.getReference().child("users").child(mUserId);
        mUserRef.keepSynced(true);
    }

    public static void AddListeners() {
        mIsLoaded = true;

        mUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TDI newTDI = dataSnapshot.getValue(TDI.class);
                try {
                    //long size = picRef.getMetadata().getResult().getSizeBytes();

                    final String index = dataSnapshot.getKey();
                    //Log.d("SOME_KEY", ".\n.\n.\n.\n.\n" + index + "\n.\n.\n.\n." + dataSnapshot.toString());
                    for (ToDoItem td : values)
                        if (td.id.equals(index)) return;

                    ToDoItem tdi = new ToDoItem(
                            index,
                            newTDI.TD_name,
                            ToDoDBContract.iso8601Format.parse(newTDI.TD_date),
                            newTDI.TD_check,
                            newTDI.TD_contents,
                            null,//getBitmapFromBytes(pic_bytes.bytes, (int)size),
                            newTDI.TD_picture_id
                    );

                    values.add(tdi);
                    mTodoAdapter.notifyDataSetChanged();


                    final StorageReference picRef = mUserFiles.child(newTDI.TD_picture_id);

                    if (!newTDI.TD_picture_id.equals("none"))
                    picRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            /*StorageReference picRef = null;
                            for (ToDoItem td : values)
                                if (td.id.equals(index)) {
                                    picRef = mUserFiles.child(td.pic_name);
                                    break;
                                }*/
                            //Log.d("PIC_REF", picRef.getPath());

                            final long size = storageMetadata.getSizeBytes();
                            picRef.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    for (ToDoItem td : values)
                                        if (td.id.equals(index)) {
                                            td.picture = getBitmapFromBytes(bytes, (int)size);
                                            break;
                                        }
                                    mTodoAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                TDI newTDI = dataSnapshot.getValue(TDI.class);
                try {
                    //long size = picRef.getMetadata().getResult().getSizeBytes();

                    final String index = dataSnapshot.getKey();
                    int idx = -1;
                    for (int i = 0; i < values.size(); i++)
                        if (values.get(i).id.equals(index)) idx = i;


                    String name, contents, picture_id;
                    Boolean check;
                    Date date;

                    if (dataSnapshot.hasChild("TD_name")) name = newTDI.TD_name;
                    else name = values.get(idx).name;
                    if (dataSnapshot.hasChild("TD_date")) date = ToDoDBContract.iso8601Format.parse(newTDI.TD_date);
                    else date = values.get(idx).date;
                    if (dataSnapshot.hasChild("TD_check")) check = newTDI.TD_check;
                    else check = values.get(idx).check;
                    if (dataSnapshot.hasChild("TD_contents")) contents = newTDI.TD_contents;
                    else contents = values.get(idx).contents;
                    if (dataSnapshot.hasChild("TD_picture_id")) picture_id = newTDI.TD_picture_id;
                    else picture_id = values.get(idx).pic_name;

                    ToDoItem tdi = new ToDoItem(
                            index,
                            name,
                            date,
                            check,
                            contents,
                            values.get(idx).picture,//null,//getBitmapFromBytes(pic_bytes.bytes, (int)size),
                            picture_id
                    );

                    values.set(idx, tdi);
                    mTodoAdapter.notifyDataSetChanged();

                    final StorageReference picRef = mUserFiles.child(picture_id);

                    if (!picture_id.equals("none"))
                    picRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            final long size = storageMetadata.getSizeBytes();
                            picRef.getBytes(size).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    for (ToDoItem td : values)
                                        if (td.id.equals(index)) {
                                            td.picture = getBitmapFromBytes(bytes, (int) size);
                                            break;
                                        }
                                    mTodoAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mTodoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String index = dataSnapshot.getKey();
                //Toast.makeText(mContext, index, Toast.LENGTH_LONG).show();
                for (ToDoItem td : values)
                    if (td.id.equals(index))
                        values.remove(td);
                mTodoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
