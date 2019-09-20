package com.example.smartlibrary;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditDetailsActivity extends AppCompatActivity {

    private static String FLAG_BRANCH;
    private static String FLAG_SEM;
    private static String FLAG_COLLEGE;

    DatabaseReference rootRef;
    FirebaseAuth mAuth;
    String currentUserID;
    String gender;
    ProgressDialog loadingBar;
    private static Uri uri = null;

    @BindView(R.id.branchSp)
    Spinner branchSp;
    @BindView(R.id.collegeSp)
    Spinner collegeSp;
    @BindView(R.id.semSp)
    Spinner semSp;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.userPic)
    CircleImageView userPic;
    @BindView(R.id.userNameEt)
    EditText userNameEt;
    @BindView(R.id.cityEt)
    EditText cityEt;
    @BindView(R.id.boySrc)
    ImageView boySrc;
    @BindView(R.id.boyRl)
    RelativeLayout boyRl;
    @BindView(R.id.girlSrc)
    ImageView girlSrc;
    @BindView(R.id.girlRl)
    RelativeLayout girlRl;
    @BindView(R.id.rollNumberEt)
    EditText rollNoEt;
    @BindView(R.id.genderCv)
    CardView genderCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingBar = new ProgressDialog(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        setUpBranchSpinner();
        setUpCollegeSpinner();
        setUpSemesterSpinner();

        boyRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = "Male";
                girlSrc.setColorFilter(getResources().getColor(R.color.grey));
                boySrc.setColorFilter(getResources().getColor(R.color.red));

            }
        });


        girlRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = "Female";
                girlSrc.setColorFilter(getResources().getColor(R.color.red));
                boySrc.setColorFilter(getResources().getColor(R.color.grey));
            }
        });

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 32);
            }
        });


        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        userNameEt.setText(name);
                    }

                    if (dataSnapshot.hasChild("rollNumber")) {
                        String name = dataSnapshot.child("rollNumber").getValue().toString();
                        rollNoEt.setText(name);
                    }

                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(userPic);
                    }
                    if (dataSnapshot.hasChild("city")) {
                        String city = dataSnapshot.child("city").getValue().toString();
                        cityEt.setText(city);
                    }

                    if (dataSnapshot.hasChild("branch")) {
                        FLAG_BRANCH = dataSnapshot.child("branch").getValue().toString();
                        switch (FLAG_BRANCH) {

                            case "Computer Science":
                                branchSp.setSelection(1);
                                break;
                            case "Instrumentation and Control":
                                branchSp.setSelection(3);
                                break;
                            case "Electronics And Comm.":
                                branchSp.setSelection(2);
                                break;
                            case "Electrical":
                                branchSp.setSelection(4);
                                break;
                            case "Mechanical":
                                branchSp.setSelection(5);
                                break;
                            case "Information Technology":
                                branchSp.setSelection(6);
                                break;
                            case "Chemical":
                                branchSp.setSelection(7);
                                break;
                            case "Industrial And Production":
                                branchSp.setSelection(8);
                                break;
                            case "Civil":
                                branchSp.setSelection(9);
                                break;
                            case "Biotechnology":
                                branchSp.setSelection(10);
                                break;
                            case "Textile":
                                branchSp.setSelection(11);
                                break;
                            case "Humanities":
                                branchSp.setSelection(12);
                                break;
                            case "Physics ":
                                branchSp.setSelection(13);
                                break;
                            case "Chemistry":
                                branchSp.setSelection(14);
                                break;

                            case "Mathematics":
                                branchSp.setSelection(15);
                                break;
                        }
                    }

                        if (dataSnapshot.hasChild("college")) {
                            FLAG_COLLEGE = dataSnapshot.child("college").getValue().toString();
                            switch (FLAG_COLLEGE) {
                                case "NIT Jalandhar":
                                    collegeSp.setSelection(1);
                                    break;
                                case "LPU":
                                    collegeSp.setSelection(2);
                                    break;
                                case "Thapar College":
                                    collegeSp.setSelection(3);
                                    break;
                                case "DAV College":
                                    collegeSp.setSelection(4);
                                    break;
                                case "Other":
                                    collegeSp.setSelection(5);
                                    break;
                            }

                            if (dataSnapshot.hasChild("semester")) {
                                FLAG_SEM = dataSnapshot.child("semester").getValue().toString();
                                if (FLAG_SEM.equals("1 Sem")) {
                                    semSp.setSelection(1);
                                } else if (FLAG_SEM.equals("2 Sem")) {
                                    semSp.setSelection(2);
                                } else if (FLAG_SEM.equals("3 Sem")) {
                                    semSp.setSelection(3);
                                } else if (FLAG_SEM.equals("4 Sem")) {
                                    semSp.setSelection(4);
                                } else if (FLAG_SEM.equals("5 Sem")) {
                                    semSp.setSelection(5);
                                } else if (FLAG_SEM.equals("6 Sem")) {
                                    semSp.setSelection(6);
                                } else if (FLAG_SEM.equals("7 Sem")) {
                                    semSp.setSelection(7);
                                } else if (FLAG_SEM.equals("8 Sem")) {
                                    semSp.setSelection(8);
                                }
                            }

                            if (dataSnapshot.hasChild("gender")) {
                                String genderSt = dataSnapshot.child("gender").getValue().toString();
                                if (genderSt.equals("Male")) {
                                    girlSrc.setColorFilter(getResources().getColor(R.color.grey));
                                    boySrc.setColorFilter(getResources().getColor(R.color.red));
                                    gender = genderSt;
                                } else if (genderSt.equals("Female")) {

                                    gender = genderSt;

                                    girlSrc.setColorFilter(getResources().getColor(R.color.red));
                                    boySrc.setColorFilter(getResources().getColor(R.color.grey));

                                }
                            }

                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 32 && resultCode == RESULT_OK && data != null) {

            uri = data.getData();
            Picasso.get().load(uri.toString()).into(userPic);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_save) {
            saveDetails();
        }

        return true;

    }

    private void saveDetails() {

        String name = userNameEt.getText().toString();
        String city = cityEt.getText().toString();
        String rollNumber = rollNoEt.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(FLAG_BRANCH) && !TextUtils.isEmpty(FLAG_COLLEGE) && !TextUtils.isEmpty(FLAG_SEM) && !TextUtils.isEmpty(rollNumber)) {
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            if (uri != null) {

                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfilePic").child(currentUserID + ".jpg");
                filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("name", name);
                                    map.put("city", city);
                                    map.put("gender", gender);
                                    map.put("image", uri.toString());
                                    map.put("branch", FLAG_BRANCH);
                                    map.put("college", FLAG_COLLEGE);
                                    map.put("semester", FLAG_SEM);
                                    map.put("rollNumber",rollNumber);

                                    rootRef.child("Users").child(currentUserID)
                                            .updateChildren(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loadingBar.dismiss();

                                                        Intent intent = new Intent(EditDetailsActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();

                                                        Toast.makeText(EditDetailsActivity.this, "Your Details updated successfully!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        String message = task.getException().toString();
                                                        Toast.makeText(EditDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        } else {

                            String message = task.getException().toString();
                            Toast.makeText(EditDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    }
                });

            } else {

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("city", city);
                map.put("gender", gender);
                map.put("branch", FLAG_BRANCH);
                map.put("college", FLAG_COLLEGE);
                map.put("semester", FLAG_SEM);

                rootRef.child("Users").child(currentUserID)
                        .updateChildren(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(EditDetailsActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(EditDetailsActivity.this, "Your Details saved successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(EditDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(EditDetailsActivity.this, "Please fill all details!", Toast.LENGTH_SHORT).show();
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
