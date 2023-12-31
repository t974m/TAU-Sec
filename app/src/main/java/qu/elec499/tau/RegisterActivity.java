package qu.elec499.tau;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import qu.elec499.tau.R;

public class RegisterActivity extends AppCompatActivity {

    TextView btn;
    private EditText inputUsername, inputPassword, inputEmail, inputSerial;
    Button registerBtn;

    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn=findViewById(R.id.alreadyHaveAccount);
        inputUsername=findViewById(R.id.inputUsername);
        inputPassword=findViewById(R.id.inputPassword);
        inputEmail=findViewById(R.id.inputEmail);
        inputSerial=findViewById(R.id.inputSerialNumber);
        registerBtn = findViewById(R.id.btnRegister);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(RegisterActivity.this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void validate() {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String serial = inputSerial.getText().toString();

        if(username.isEmpty() || username.length()<5){
            showError(inputUsername, "Username must be above 5 characters.");
        }
        else if(email.isEmpty() && !email.contains("@") && !email.contains(".")){
            showError(inputEmail, "Invalid E-mail format!");
        }
        else if (password.isEmpty() || password.length()<8){
            showError(inputPassword, "Invalid Password! Must be minimum 8 characters.");
        }
        else if (serial.isEmpty() || !serial.contains("29905000063")){
            showError(inputSerial, "Invalid Serial Number! Please contact support team.");
        }
        else{
            mLoadingBar.setTitle("User Registration");
            mLoadingBar.setMessage("Please wait, registering...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Registration Completed Successfully. Please proceed to sign-in.", Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}
