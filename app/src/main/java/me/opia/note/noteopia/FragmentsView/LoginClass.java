package me.opia.note.noteopia.FragmentsView;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import me.opia.note.noteopia.R;

public class LoginClass extends AppCompatActivity {

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 999;







}