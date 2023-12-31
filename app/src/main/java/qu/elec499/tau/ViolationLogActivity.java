package qu.elec499.tau;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import qu.elec499.tau.R;
import com.squareup.picasso.Picasso;

public class ViolationLogActivity extends AppCompatActivity {

    EditText violationSearch;
    RecyclerView violationRecyclerView;
    FirebaseRecyclerOptions<Violation> options;
    FirebaseRecyclerAdapter<Violation, ViolationViewHolder> adapter;
    DatabaseReference dataRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_log);

        dataRef = FirebaseDatabase.getInstance().getReference().child("violationDetails");
        violationSearch = findViewById(R.id.search_violation);
        violationRecyclerView = findViewById(R.id.violationRecyclerView);
        violationRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        violationRecyclerView.setHasFixedSize(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("History of Violations");
        }

        loadData("");

        violationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString()!=null){
                    loadData(editable.toString());
                }
                else {
                    loadData("");
                }

            }
        });
    }

    private void loadData(String data) {
        Query query = dataRef.orderByChild("np").startAt(data).endAt(data+"\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Violation>().setQuery(query,Violation.class).build();
        adapter = new FirebaseRecyclerAdapter<Violation, ViolationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViolationViewHolder violationViewHolder, @SuppressLint("RecyclerView") final int i, @NonNull Violation violation) {
                violationViewHolder.npHolder.setText(violation.getNp());
                Picasso.with(ViolationLogActivity.this).load(violation.getImageURL()).into(violationViewHolder.imageView);
                violationViewHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViolationLogActivity.this, ViewViolationDetails.class);
                        intent.putExtra("violationKey", getRef(i).getKey());
                        startActivity(intent);


                    }
                });
            }

            @NonNull
            @Override
            public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.violation_single_adapter,viewGroup,false);
                return new ViolationViewHolder(view);
            }
        };
        adapter.startListening();
        violationRecyclerView.setAdapter(adapter);
    }
}
