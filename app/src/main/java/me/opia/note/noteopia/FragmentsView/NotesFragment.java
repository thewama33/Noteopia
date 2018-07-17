package me.opia.note.noteopia.FragmentsView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.opia.note.noteopia.Models.NoteAdapter;
import me.opia.note.noteopia.Models.NoteList;
import me.opia.note.noteopia.R;
import me.opia.note.noteopia.Utils.Utils.TinyDB;
import me.opia.note.noteopia.ViewModels.addNoteActivity;

import static android.support.constraint.Constraints.TAG;

public class NotesFragment extends Fragment implements View.OnClickListener {
    public NotesFragment() {
        // Required empty public constructor
    }

    FloatingActionButton btnAddNote;
    FloatingActionButton btnChangeView;
    FloatingActionButton del;
    RecyclerView rcNotes;
    TextView backText;

    Realm realm;
    TinyDB tinyDB;
    NoteAdapter adapter;
    RealmResults<NoteList> list;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        realm = Realm.getDefaultInstance();
        tinyDB = new TinyDB(getActivity());
        Binds(view);
        parseData();



        return view;
    }


    public void parseData(){

        list = realm.where(NoteList.class).findAll().sort("id",Sort.DESCENDING);
        list.size();

        if (!list.isEmpty()) {
            backText.setVisibility(View.GONE);

          //  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);

            rcNotes.setLayoutManager(staggeredGridLayoutManager);
            rcNotes.setHasFixedSize(true);
            rcNotes.smoothScrollToPosition(0);
            rcNotes.setItemAnimator(new DefaultItemAnimator());

            adapter = new NoteAdapter(getActivity(), list);
            rcNotes.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    public void Binds(View view){
        btnAddNote = view.findViewById(R.id.addNote);
        btnAddNote.setOnClickListener(this);
        btnChangeView = view.findViewById(R.id.changeForm);
        btnChangeView.setOnClickListener(this);
        del = view.findViewById(R.id.deleteAll);
        del.setOnClickListener(this);

        rcNotes = view.findViewById(R.id.recyclerNotes);
        backText = view.findViewById(R.id.backText);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addNote:

                startActivity(new Intent(getContext(),addNoteActivity.class));

            break;

            case R.id.changeForm:
//                String rcView = tinyDB.getString("rcView");
//
//
//                if (rcView == "LinearLayoutManager") {
//
//                    rcNotes.setLayoutManager(linearLayoutManager);
//                } else if (rcView == "staggeredGridLayoutManager"){
//
//                    rcNotes.setLayoutManager(staggeredGridLayoutManager);
//                }
                break;

            case R.id.deleteAll:
                try {
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();

                    adapter.notifyDataSetChanged();

                    backText.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "deleteRealmData: " + e.getMessage());
                }
                break;


        }
    }
}
