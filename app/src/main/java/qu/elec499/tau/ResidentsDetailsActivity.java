package qu.elec499.tau;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import qu.elec499.tau.R;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import de.hdodenhof.circleimageview.CircleImageView;

import static qu.elec499.tau.Functions.BitmapToUri;
import static qu.elec499.tau.Functions.getResizedBitmap;

public class ResidentsDetailsActivity extends AppCompatActivity {

    DBhandler dBhandler;
    Resident resident;
    Button save, reset;
    int id;
    CircleImageView imageView;
    TextView Name, Phone, NP, Resume, Change;
    Spinner Status;
    String name, phone, position, status, R_uri;
    Uri photoURI, resumeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Resident Details");
        }

        Intent intent = getIntent();
        if (intent != null)
            id = intent.getIntExtra("employee_id", 0);

        dBhandler = new DBhandler(this, null, null, 1);

        Name = findViewById(R.id.nameDetail);
        Phone = findViewById(R.id.phoneDetail);
        NP = findViewById(R.id.NPDetail);
        Status = findViewById(R.id.statusDetail);
        imageView = findViewById(R.id.imageDetail);
        Resume = findViewById(R.id.resumeDetail);
        Change = findViewById(R.id.change);
        setView();
        setUpEdit();
    }

    private void setView() {
        resident = dBhandler.returnResident(id);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                getResources().getStringArray(R.array.statusSpinnner));
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Status.setAdapter(arrayAdapter);
        switch (resident.getStatus()) {
            case "Active":
                Status.setSelection(0);
                break;
            case "DELETE":
                Status.setSelection(1);
                break;
        }

        Name.setText(resident.getName());
        Phone.setText(resident.getPhone());
        NP.setText(resident.getNP());
        if (resident.getPhotoURI().length() > 0) {
            Picasso.with(this)
                    .load(resident.getPhotoURI())
                    .error(R.drawable.employee_tie)
                    .into(imageView);
            photoURI = Uri.parse(resident.getPhotoURI());
        }
        if (resident.getResumeURI().length() > 0) {
            Resume.setEnabled(true);
            Resume.setText("View");
            R_uri = resident.getResumeURI();
        } else {
            Resume.setEnabled(false);
            Resume.setText("N/A");
            R_uri = null;
        }
        openResume();
    }

    public void setUpEdit() {
        /*reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setView();
            }
        }); //For RESET BUTTON IN EDIT MENU */

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoURI = uploadPic();
            }
        });
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name.setEnabled(true);
            }
        });
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phone.setEnabled(true);
                Phone.setInputType(InputType.TYPE_CLASS_PHONE);
                Phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            }
        });
        changeResume();
        NP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NP.setEnabled(true);
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = Name.getText().toString();
                phone = Phone.getText().toString();
                position = NP.getText().toString();
                status = Status.getSelectedItem().toString();
                dBhandler.updateCandidate(id, name, phone, position, status, String.valueOf(photoURI), R_uri);
                Toast.makeText(ResidentsDetailsActivity.this, "Changes Saved.", Toast.LENGTH_SHORT).show();

                setView();
            }
        });

    }

    private Uri uploadPic() {
        PickSetup setup = new PickSetup()
                .setTitle("Select Image")
                .setSystemDialog(true);

        PickImageDialog.build(setup)
                .setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        Bitmap compressed = getResizedBitmap(r.getBitmap(), 1000);
                        photoURI = BitmapToUri(ResidentsDetailsActivity.this, compressed);
                        Picasso.with(ResidentsDetailsActivity.this)
                                .load(photoURI)
                                .error(R.drawable.employee_tie)
                                .into(imageView);
                    }
                })
                .setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        Toast.makeText(getApplicationContext(),
                                "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show(ResidentsDetailsActivity.this);
        return photoURI;
    }

    private void openResume() {
        String resumeUri = resident.getResumeURI();
        if (resumeUri.length() > 0) {
            Resume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(resident.getResumeURI()), "application/pdf");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 200);
                }
            });
        }
    }

    private void changeResume() {
        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            resumeUri = data.getData();
            if (resumeUri != null) {
                R_uri = resumeUri.toString();
                Change.setText("Uploaded to Cloud.");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
