package com.example.smartlibrary.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlibrary.Book.IdCardActivity;
import com.example.smartlibrary.MainActivity;
import com.example.smartlibrary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDataActivity extends AppCompatActivity {

    private static String FLAG_BRANCH;
    private static String FLAG_SEM;
    private static String FLAG_COLLEGE;
    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.userNameEt)
    EditText textEt;
    @BindView(R.id.nextBt)
    Button nextBt;
    String name, city, rollNumber, text;
    String gender;
    DatabaseReference rootRef;
    String currentUserID;
    ProgressDialog loadingBar;

    @BindView(R.id.basicDetailRl)
    RelativeLayout basicDetailRl;
    @BindView(R.id.boyRl)
    RelativeLayout boyRl;
    @BindView(R.id.girlRl)
    RelativeLayout girlRl;
    @BindView(R.id.finishBt)
    Button finishBt;
    @BindView(R.id.detailRl)
    RelativeLayout detailRl;
    @BindView(R.id.boySrc)
    ImageView boySrc;
    @BindView(R.id.girlSrc)
    ImageView girlSrc;

    @BindView(R.id.branchSp)
    Spinner branchSp;
    @BindView(R.id.collegeSp)
    Spinner collegeSp;
    @BindView(R.id.semSp)
    Spinner semSp;
    @BindView(R.id.verifyRl)
    RelativeLayout verifyRl;
    @BindView(R.id.idcardImgBt)
    ImageView idcardImgBt;
    @BindView(R.id.saveBt)
    Button saveBt;

    private static Uri idUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        ButterKnife.bind(this);

        verifyRl.setVisibility(View.GONE);
        detailRl.setVisibility(View.GONE);
        basicDetailRl.setVisibility(View.VISIBLE);

        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        basicDetails();
        secondaryDetails();
        verifyDetail();

        setUpBranchSpinner();
        setUpCollegeSpinner();
        setUpSemesterSpinner();

    }

    private void secondaryDetails() {

        girlRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = "Female";

                girlSrc.setColorFilter(getResources().getColor(R.color.red));
                boySrc.setColorFilter(getResources().getColor(R.color.grey));

            }
        });

        boyRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";

                girlSrc.setColorFilter(getResources().getColor(R.color.grey));
                boySrc.setColorFilter(getResources().getColor(R.color.red));

            }
        });

        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(gender) && !TextUtils.isEmpty(FLAG_BRANCH) && !TextUtils.isEmpty(FLAG_COLLEGE) && !TextUtils.isEmpty(FLAG_SEM)) {
//                    if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {

//                        verifyRl.setVisibility(View.VISIBLE);
//                        detailRl.setVisibility(View.GONE);
                        loadingBar.setMessage("Please wait!");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("city", city);
                        map.put("gender", gender);
                        map.put("rollNumber", rollNumber);
                        map.put("branch", FLAG_BRANCH);
                        map.put("college", FLAG_COLLEGE);
                        map.put("semester", FLAG_SEM);
                        map.put("verified" ,"false");

                        rootRef.child("Users").child(currentUserID)
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            loadingBar.dismiss();
                                            Intent intent = new Intent(UserDataActivity.this, IdCardActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(UserDataActivity.this, "Your Details saved successfully!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(UserDataActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                        }
                                    }
                                });
//                    }
//                else {
//                        Toast.makeText(UserDataActivity.this, "Please grant the prmission!", Toast.LENGTH_SHORT).show();
//                    }
                } else{
                    Toast.makeText(UserDataActivity.this, "Please fill all details!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyDetail() {

        idcardImgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 32);

            }
        });


        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idUri != null) {
                    saveDetails();
                } else {
                    Toast.makeText(UserDataActivity.this, "Please upload image of your college ID Card!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void basicDetails() {

        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = textEt.getText().toString();

                if (!TextUtils.isEmpty(text)) {
                    if (name == null) {
                        name = text;
                        textEt.setText("");
                        text1.setText("Bad libraries build collections, good libraries build services, great libraries build communities.");
                        textEt.setHint("Enter City");

                    } else {
                        if (city == null) {

                            city = text;
                            textEt.setText("");
                            textEt.setHint("Enter your college roll number");
                            text1.setText("To build up a library is to create a life. It’s never just a random collection of books.");

                        } else {

                            rollNumber = text;

                            basicDetailRl.setVisibility(View.GONE);
                            detailRl.setVisibility(View.VISIBLE);

                        }
                    }

                } else {
                    Toast.makeText(UserDataActivity.this, "Please enter the required detail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDetails() {

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(rollNumber) && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(FLAG_BRANCH) && !TextUtils.isEmpty(FLAG_COLLEGE) && !TextUtils.isEmpty(FLAG_SEM)) {

            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Users").child("IDs").child(currentUserID);
            filePath.putFile(idUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("name", name);
                            map.put("city", city);
                            map.put("gender", gender);
                            map.put("rollNumber", rollNumber);
                            map.put("branch", FLAG_BRANCH);
                            map.put("college", FLAG_COLLEGE);
                            map.put("idImage", uri.toString());
                            map.put("semester", FLAG_SEM);

                            rootRef.child("Users").child(currentUserID)
                                    .updateChildren(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                loadingBar.dismiss();
                                                Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(UserDataActivity.this, "Your Details saved successfully!", Toast.LENGTH_SHORT).show();

                                            } else {
                                                String message = task.getException().toString();
                                                Toast.makeText(UserDataActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        } else {

            Toast.makeText(this, "Please select gender!", Toast.LENGTH_SHORT).show();

        }
    }

    private void setUpSemesterSpinner() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_semester, android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        semSp.setAdapter(categorySpinnerAdapter);
        semSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("1 Sem")) {
                        FLAG_SEM = "1 Sem";
                    } else if (selection.equals("2 Sem")) {
                        FLAG_SEM = "2 Sem";
                    } else if (selection.equals("3 Sem")) {
                        FLAG_SEM = "3 Sem";
                    } else if (selection.equals("4 Sem")) {
                        FLAG_SEM = "4 Sem";
                    } else if (selection.equals("5 Sem")) {
                        FLAG_SEM = "5 Sem";
                    } else if (selection.equals("6 Sem")) {
                        FLAG_SEM = "6 Sem";
                    } else if (selection.equals("7 Sem")) {
                        FLAG_SEM = "7 Sem";
                    } else if (selection.equals("8 Sem")) {
                        FLAG_SEM = "8 Sem";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setUpBranchSpinner() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_user_branch, android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        branchSp.setAdapter(categorySpinnerAdapter);
        branchSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    switch (selection) {
                        case "Computer Science":
                            FLAG_BRANCH = "Computer Science Engineering";
                            break;
                        case "Instrumentation and Control":
                            FLAG_BRANCH = "Instrumentation and Control";
                            break;
                        case "Electronics And Comm.":
                            FLAG_BRANCH = "Electronics And Comm.";
                            break;
                        case "Electrical":
                            FLAG_BRANCH = "Electrical Engineering";
                            break;
                        case "Mechanical":
                            FLAG_BRANCH = "Mechanical Engineering";
                            break;
                        case "Information Technology":
                            FLAG_BRANCH = "Information Technology";
                            break;
                        case "Chemical":
                            FLAG_BRANCH = "Chemical Engineering";
                            break;
                        case "Industrial And Production":
                            FLAG_BRANCH = "Industrial And Production";
                            break;
                        case "Civil":
                            FLAG_BRANCH = "Civil Engineering";
                            break;
                        case "Biotechnology":
                            FLAG_BRANCH = "Biotechnology";
                            break;
                        case "Textile":
                            FLAG_BRANCH = "Textile";
                            break;
                        case "Humanities":
                            FLAG_BRANCH = "Humanities";
                            break;
                        case "Chemistry":
                            FLAG_BRANCH = "Chemistry";
                            break;
                        case "Physics ":
                            FLAG_BRANCH = "Physics";
                            break;
                        case "Mathematics":
                            FLAG_BRANCH = "Mathematics";
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpCollegeSpinner() {
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_college, android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        collegeSp.setAdapter(categorySpinnerAdapter);
        collegeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    switch (selection) {
                        case "NIT Jalandhar":
                            FLAG_COLLEGE = "NIT Jalandhar";
                            break;
                        case "LPU":
                            FLAG_COLLEGE = "LPU";
                            break;
                        case "Thapar College":
                            FLAG_COLLEGE = "Thapar College";
                            break;
                        case "DAV College":
                            FLAG_COLLEGE = "DAV College";
                            break;
                        case "Other":
                            FLAG_COLLEGE = "Other";
                            break;

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}

