package qu.elec499.tau;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.storage.StorageReference;
import qu.elec499.tau.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AllResidentsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    DBhandler dBhandler;
    Button sortBy, push, retrieve, refresh, speed;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ListView residentListView;
    ArrayList<Resident> arrayList;
    ResidentAdapter residentAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    ProgressBar progressBar, progressBar2;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_residents);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Data Management");
        }
        setUpView();
    }

    private void setUpView() {
        dBhandler = new DBhandler(this, null, null, 1);
        residentListView = findViewById(R.id.residentList);
        getValues();

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dBhandler.deleteSelected();
                //residentAdapter.clear();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        sortBy = findViewById(R.id.sortbybutton);
        sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpSortBy();
            }
        });

        push = findViewById(R.id.push);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToRTDB();
            }
        });

        retrieve = findViewById(R.id.retrieve);
        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childEventListener = null;
                retrieveFromRTDB();
            }
        }
        );

        speed = findViewById(R.id.speed);
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSpeedLimit();
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPage();
            }
        });

        searchView = findViewById(R.id.search);
        onSearch();
    }

    private void refreshPage() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void getValues() {
        arrayList = dBhandler.returnResidents(0);
        residentAdapter = new ResidentAdapter(
                AllResidentsActivity.this, R.layout.resident_tile, arrayList);
        residentListView.setAdapter(residentAdapter);
        residentListView.setOnItemClickListener(this);
    }

    private void retrieveFromRTDB() {
        // Delete local database
        dBhandler.deleteAll();

        // Retrieve data from cloud
        final View pullDialog = getLayoutInflater().inflate(R.layout.request_key, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AllResidentsActivity.this);
        builder.setView(pullDialog);

        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        progressBar = pullDialog.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        final Button key_proceed = pullDialog.findViewById(R.id.key_proceed);
        key_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key_proceed.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                //TextInputEditText keyTIET = pullDialog.findViewById(R.id.Key);
                //final String key = keyTIET.getText().toString();
                final String key = "residents";
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(key)) {
                            key_proceed.setClickable(true);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AllResidentsActivity.this,
                                    "No information available on cloud", Toast.LENGTH_LONG).show();
                        } else {
                            if (childEventListener == null) {
                                childEventListener = new ChildEventListener() {
                                    private static final String TAG = "Upload error";
                                    StorageReference images;

                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        final Resident resident = dataSnapshot.getValue(Resident.class);

                                        images = FirebaseStorage.getInstance()
                                                .getReferenceFromUrl("gs://tau-ac002.appspot.com/Images/" + key + "/" + resident.getPhotoURI());
                                        final long ONE_MEGABYTE = 1024 * 1024;

                                        //download file as a byte array
                                        if (resident.getPhotoURI().equals("null"))
                                            dBhandler.addResident(resident);
                                        else {
                                            images.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    resident.setPhotoURI(String.valueOf(Functions
                                                            .BitmapToUri(AllResidentsActivity.this, bitmap)));
                                                    Toast.makeText(AllResidentsActivity.this, "Retrieval successful!", Toast.LENGTH_LONG).show();
                                                    dBhandler.addResident(resident);
                                                    getValues();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AllResidentsActivity.this, "Retrieval failed!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                        dialog.dismiss();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                };
                            }
                            databaseReference.child(key).addChildEventListener(childEventListener);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(AllResidentsActivity.this,
                        "Successfully imported from cloud!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                getValues();
            }
        });
        progressBar.setVisibility(View.GONE);
        key_proceed.setClickable(true);
    }

    private void pushToRTDB() {
        final View pushDialog = getLayoutInflater().inflate(R.layout.request_key, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AllResidentsActivity.this);
        builder.setView(pushDialog);

        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        progressBar = pushDialog.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        final Button key_proceed = pushDialog.findViewById(R.id.key_proceed);
        key_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                key_proceed.setClickable(false);
                //TextInputEditText keyTIET = pushDialog.findViewById(R.id.Key);
                //String key = keyTIET.getText().toString();
                String key = "residents";
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child(key);
                if (childEventListener != null)
                    databaseReference.removeEventListener(childEventListener);

                StorageReference imageReference;
                StorageReference resumeReference;

                Map<String, Resident> sample = new HashMap<>();
                for (Resident entry : arrayList) {
                    if (!entry.getPhotoURI().equals("null")) {
                        imageReference = FirebaseStorage.
                                getInstance().getReference()
                                .child("Images/" + key + "/" + entry.getId());
                        imageReference.putFile(Uri.parse(entry.getPhotoURI()));
                        entry.setPhotoURI(String.valueOf(entry.getId()));
                    }
                    if (!entry.getResumeURI().equals("null")) {
                        resumeReference = FirebaseStorage.
                                getInstance().getReference()
                                .child("Resume/" + key + "/" + entry.getId());
                        resumeReference.putFile(Uri.parse(entry.getResumeURI()));
                        entry.setResumeURI(String.valueOf(entry.getId()));
                    }
                    sample.put(String.valueOf(entry.getId()), entry);
                }
                databaseReference.setValue(sample);
                Toast.makeText(AllResidentsActivity.this,
                        "Successfully uploaded to cloud!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                getValues();
            }
        });
        progressBar.setVisibility(View.GONE);
        key_proceed.setClickable(true);
    }

    private void setSpeedLimit() {
        final View pushDialog = getLayoutInflater().inflate(R.layout.request_speed, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AllResidentsActivity.this);
        builder.setView(pushDialog);

        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        progressBar2 = pushDialog.findViewById(R.id.progress2);
        progressBar2.setVisibility(View.GONE);
        final Button speed_proceed = pushDialog.findViewById(R.id.speed_proceed);
        speed_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                speed_proceed.setClickable(false);
                TextInputEditText speed = pushDialog.findViewById(R.id.speedInput);
                String speedLimit = speed.getText().toString();

                if (!speedLimit.isEmpty()) {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("speedLimit");
                    if (childEventListener != null)
                        databaseReference.removeEventListener(childEventListener);

                    databaseReference.setValue(speedLimit);

                    Toast.makeText(AllResidentsActivity.this,
                            "Successfully updated on cloud!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    getValues();
                } else {
                    progressBar2.setVisibility(View.GONE);
                    speed_proceed.setClickable(true);
                    Toast.makeText(AllResidentsActivity.this,
                            "Speed Limit cannot be empty!", Toast.LENGTH_LONG).show();
                }
            }
        });
        progressBar2.setVisibility(View.GONE);
        speed_proceed.setClickable(true);
    }

    private void setUpSortBy() {
        final View sortDialog = getLayoutInflater().inflate(R.layout.sortby, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(AllResidentsActivity.this);
        builder.setView(sortDialog);

        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        radioGroup = sortDialog.findViewById(R.id.sortByRG);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = sortDialog.findViewById(checkedId);
                Toast.makeText(AllResidentsActivity.this,
                        "Sorting by " + radioButton.getText().toString(),
                        Toast.LENGTH_LONG).show();

                residentListView = findViewById(R.id.residentList);
                switch (radioButton.getText().toString()) {
                    case "Status":
                        arrayList = dBhandler.returnResidents(100);
                        break;
                    case "Alphabetically":
                        arrayList = dBhandler.returnResidents(200);
                        break;
                    case "Car Number Plate":
                        arrayList = dBhandler.returnResidents(300);
                        break;
                    case "Time":
                    default:
                        arrayList = dBhandler.returnResidents(0);
                        break;

                }
                residentAdapter = new ResidentAdapter(
                        AllResidentsActivity.this, R.layout.resident_tile, arrayList);
                residentListView.setAdapter(residentAdapter);

                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(AllResidentsActivity.this, ResidentsDetailsActivity.class);
        intent.putExtra("employee_id", arrayList.get(position).getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpView();
    }

    protected void onSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayList = dBhandler.returnQuery(query.toLowerCase());
                if (!arrayList.isEmpty()) {
                    residentAdapter = new ResidentAdapter(
                            AllResidentsActivity.this, R.layout.resident_tile, arrayList);
                    residentListView.setAdapter(residentAdapter);
                } else
                    Toast.makeText(AllResidentsActivity.this, "Name does not exist!", Toast.LENGTH_LONG).show();
                return arrayList != null;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                arrayList = dBhandler.returnQuery(query.toLowerCase());
                if (!arrayList.isEmpty()) {
                    residentAdapter = new ResidentAdapter(
                            AllResidentsActivity.this, R.layout.resident_tile, arrayList);
                    residentListView.setAdapter(residentAdapter);
                } else
                    Toast.makeText(AllResidentsActivity.this, "Name does not exist!", Toast.LENGTH_LONG).show();
                return arrayList != null;
            }
        });
    }
}
