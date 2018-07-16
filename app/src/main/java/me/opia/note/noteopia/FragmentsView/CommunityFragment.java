package me.opia.note.noteopia.FragmentsView;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.opia.note.noteopia.Models.CommunityAdapter;
import me.opia.note.noteopia.Models.CommunityList;
import me.opia.note.noteopia.Models.NoteList;
import me.opia.note.noteopia.R;

import static android.support.constraint.Constraints.TAG;

public class CommunityFragment extends Fragment {


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()){
            realm.close();
        }
    }

    @BindView(R.id.rcCommunity)
    RecyclerView rcCommunity;
    @BindView(R.id.swipRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.backTextCommunity)
    TextView txtBack;

    CommunityAdapter adapter;
    RealmResults<CommunityList> lists;
    Realm realm;


    DatabaseReference dbref;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_community, container, false);
        ButterKnife.bind(this,view);
        realm = Realm.getDefaultInstance();


        try {
            parseData();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: " + e.getMessage());
        }


        return view;
    }

    public void parseData(){
        lists = realm.where(CommunityList.class).findAllAsync().sort("id", Sort.DESCENDING);
        lists.size();

            if (!lists.isEmpty()) {

                LinearLayoutManager linearLayoutManager =
                        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rcCommunity.setLayoutManager(linearLayoutManager);
                rcCommunity.setItemAnimator(new DefaultItemAnimator());
                rcCommunity.smoothScrollToPosition(0);
                rcCommunity.setHasFixedSize(true);

                dbref = FirebaseDatabase.getInstance().getReference("CommunityNotes");

                try {
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dt : dataSnapshot.getChildren()) {

                                if (!dt.hasChildren()){

                                    txtBack.setVisibility(View.VISIBLE);
                                }
                                else {
                                    txtBack.setVisibility(View.GONE);

                                    final CommunityList communityModel = dt.getValue(CommunityList.class);

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {

                                            communityModel.setId(NextID());
                                            realm.copyToRealmOrUpdate(communityModel);
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toasty.error(getActivity(), "Error" + databaseError.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(getActivity(),"Error please Check Log", Toast.LENGTH_LONG).show();
                }
            } else {

                txtBack.setVisibility(View.VISIBLE);
            }

        adapter = new CommunityAdapter(lists,getActivity());
        rcCommunity.setAdapter(adapter);

    }

    public int NextID(){
        try {
            return realm.where(NoteList.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return 0;
        }
    }
}
