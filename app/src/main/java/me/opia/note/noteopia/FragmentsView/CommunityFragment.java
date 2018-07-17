package me.opia.note.noteopia.FragmentsView;


import android.content.Intent;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.opia.note.noteopia.Models.CommunityAdapter;
import me.opia.note.noteopia.Models.CommunityList;
import me.opia.note.noteopia.Models.NoteList;
import me.opia.note.noteopia.R;

public class CommunityFragment extends Fragment {


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_community, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        try {
            parseData();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: " + e.getMessage());
        }

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
});
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }

    }

    public void parseData() {
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

                            if (!dt.hasChildren()) {

                                txtBack.setVisibility(View.VISIBLE);
                            } else {
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
                Toasty.error(getActivity(), "Error please Check Log", Toast.LENGTH_LONG).show();
            }
        } else {

            txtBack.setVisibility(View.VISIBLE);
        }

        adapter = new CommunityAdapter(lists, getActivity());
        rcCommunity.setAdapter(adapter);

    }

    public int NextID() {
        try {
            return realm.where(NoteList.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return 0;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toasty.error(getActivity(),"Authentication Failed." , Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            signInButton.setVisibility(View.GONE);
            rcCommunity.setVisibility(View.VISIBLE);
            txtBack.setVisibility(View.VISIBLE);

        } else {

            signInButton.setVisibility(View.VISIBLE);
            rcCommunity.setVisibility(View.GONE);
            txtBack.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.sign_in_button)
    public void GoogleSignInButton(){

        signIn();

}

}
