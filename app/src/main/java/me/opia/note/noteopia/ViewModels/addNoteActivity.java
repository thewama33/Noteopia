package me.opia.note.noteopia.ViewModels;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import me.opia.note.noteopia.MainActivity;
import me.opia.note.noteopia.Models.CommunityList;
import me.opia.note.noteopia.Models.NoteList;
import me.opia.note.noteopia.R;

public class addNoteActivity extends AppCompatActivity {

    private static final String TAG = "Error Handle";

    @BindView(R.id.txtTitle) EditText txtTitle;
    @BindView(R.id.txtNote) EditText txtNote;
    @BindView(R.id.btnDone) Button btnDone;
    FirebaseUser mAuth;
    DatabaseReference dbRef;
    Realm realm;
    Intent intent2;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        getExtras();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityNotes");
        dialog = new Dialog(this);




    }
    public int NextID(){
        try {
            return realm.where(NoteList.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return 0;
        }
    }

    @OnClick(R.id.btnDone) public void SaveNote(){
        CheckDecides();

    }
    public void CheckDecides(){

        dialog.setContentView(R.layout.dialog_chose_addnotes);
        dialog.setCancelable(false);

        final Button saveOnly,saveANDshare,shareOnly,cancel;

        saveOnly = dialog.findViewById(R.id.saveOnly);
        saveANDshare = dialog.findViewById(R.id.saveandShare);
        shareOnly = dialog.findViewById(R.id.shareOnly);
        cancel = dialog.findViewById(R.id.cancel);

        saveOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SaveNotesOffline();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: " + e.getMessage());
                    Toasty.error(addNoteActivity.this,"Error Check Log" , Toast.LENGTH_LONG).show();
                }
            }
        });
        saveANDshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SaveAndShare();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: " + e.getMessage());
                    Toasty.error(addNoteActivity.this,"Error Check Log" , Toast.LENGTH_LONG).show();

                }
            }
        });
        shareOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (mAuth != null) {
                        ShareOnline();
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: " + e.getMessage());
                    Toasty.error(addNoteActivity.this,"Error Check Log" , Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void SaveAndShare(){


        if (mAuth != null) {
            int id = NextID();
            String Title = txtTitle.getText().toString();
            String note = txtNote.getText().toString();
            String userName = mAuth.getDisplayName();
            String userProfilePic = String.valueOf(mAuth.getPhotoUrl());

            if (TextUtils.isEmpty(String.valueOf(id)) && TextUtils.isEmpty(Title) && TextUtils.isEmpty(note)) {

                Toasty.warning(this, "Check Empty Fields", Toast.LENGTH_LONG).show();

            } else {

                try {
                    final NoteList noteList = new NoteList();
                    noteList.setId(id);
                    noteList.setTitle(Title);
                    noteList.setNote(note);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(noteList);

                        }
                    });

                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                } catch (Exception e) {
                    Log.d(TAG, "SaveNote: " + e.getMessage());
                }
            }

        }else {
            Toasty.info(this,"Login First Please" , Toast.LENGTH_LONG).show();
        }
    }



    public void SaveNotesOffline(){

        int id = NextID();
        String Title = txtTitle.getText().toString();
        String note = txtNote.getText().toString();

        if (TextUtils.isEmpty(String.valueOf(id)) && TextUtils.isEmpty(Title) && TextUtils.isEmpty(note)){

            Toasty.warning(this,"Check Empty Fields", Toast.LENGTH_LONG).show();

        }else {

            try {
                final NoteList noteList = new NoteList();
                noteList.setId(id);
                noteList.setTitle(Title);
                noteList.setNote(note);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        realm.copyToRealmOrUpdate(noteList);

                    }
                });

                startActivity(new Intent(this, MainActivity.class));
                finish();

            } catch (Exception e) {
                Log.d(TAG, "SaveNote: " + e.getMessage());
            }
        }


    } // Done
    public void ShareOnline(){


        if (mAuth != null) {
            int id = NextID();
            String postID = dbRef.push().getKey();
            String title = txtTitle.getText().toString();
            String note = txtNote.getText().toString();
            String userName = mAuth.getDisplayName();
            String userProfilePic = String.valueOf(mAuth.getPhotoUrl());

            if (TextUtils.isEmpty(String.valueOf(id)) && TextUtils.isEmpty(title) && TextUtils.isEmpty(note)) {

                Toasty.warning(this, "Check Empty Fields", Toast.LENGTH_LONG).show();

            } else {
                try {
                    CommunityList communityList = new CommunityList();

                    communityList.setUserName(userName);
                    communityList.setUserProfilePic(userProfilePic);
                    communityList.setTitle(title);
                    communityList.setNote(note);
                    dbRef.child(postID).setValue(communityList);

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();

                    Log.d(TAG, "ShareOnline: " + e.getMessage());
                    Toasty.error(this, "Error Check Log", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Toasty.info(this,"Login First Please" , Toast.LENGTH_LONG).show();
        }
    } // Done
    public void getExtras(){

        try {

            String titleExtra = intent2.getStringExtra("title");
            String noteExtra = intent2.getStringExtra("note");

            if (titleExtra != null && noteExtra != null) {

             txtTitle.setText(titleExtra);
             txtNote.setText(noteExtra);

            }else {}
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getExtras: " + e.getMessage());
        }
    }

}
