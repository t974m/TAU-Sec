package qu.elec499.tau;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import qu.elec499.tau.R;
import com.squareup.picasso.Picasso;

public class ViewViolationDetails extends AppCompatActivity {
    private ImageView imageView;
    TextView textView1, textView2;
    Button deleteButton;

    DatabaseReference dataRef, dataRefDEL;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_violation_details);

        imageView = findViewById(R.id.image_single_view_details);
        textView1 = findViewById(R.id.violation_NP_details);
        textView2 = findViewById(R.id.violation_speed_details);
        deleteButton = findViewById(R.id.delete_btn_violation);
        dataRef = FirebaseDatabase.getInstance().getReference().child("violationDetails");

        String violationKey = getIntent().getStringExtra("violationKey");
        dataRefDEL = FirebaseDatabase.getInstance().getReference().child("violationDetails").child(violationKey);
        storageRef = FirebaseStorage.getInstance().getReference().child("violation_images").child(violationKey+".jpg");

        dataRef.child(violationKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String np = dataSnapshot.child("np").getValue().toString();
                    String speed = dataSnapshot.child("speed").getValue().toString();
                    String imageURL = dataSnapshot.child("imageURL").getValue().toString();

                    Picasso.with(ViewViolationDetails.this).load(imageURL).into(imageView);
                    textView1.setText(np);
                    textView2.setText(speed);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataRefDEL.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                startActivity(new Intent(getApplicationContext(),ViolationLogActivity.class));
                                Toast.makeText(ViewViolationDetails.this,
                                        "Data Deleted Successfully.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
        });
    }
}