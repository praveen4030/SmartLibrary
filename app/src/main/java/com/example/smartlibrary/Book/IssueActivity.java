package com.example.smartlibrary.Book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlibrary.MainActivity;
import com.example.smartlibrary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IssueActivity extends AppCompatActivity {

    private static String CANCEL = "start";
    String key;
    @BindView(R.id.bookNameTv)
    TextView bookNameTv;
    @BindView(R.id.publicationTv)
    TextView publicationTv;
    @BindView(R.id.authorTv)
    TextView authorTv;
    @BindView(R.id.statusTv)
    TextView statusTv;
    @BindView(R.id.issueBt)
    Button issueBt;
    @BindView(R.id.cancelBt)
    Button cancelBt;
    @BindView(R.id.timerTv)
    TextView timerTv;
    @BindView(R.id.codeTv)
    TextView codeTv;
    @BindView(R.id.infoTv)
    TextView infoTv;

    DatabaseReference issueRef;
    CountDownTimer yourCountDownTimer;
    int time, timeRunning;

    DatabaseReference bookRef, usersRef;
    String status;
    String saveCurrentDate, currentUserID, returnDate;
    @BindView(R.id.infoRl)
    RelativeLayout infoRl;
    @BindView(R.id.bookRl)
    RelativeLayout bookRl;
    @BindView(R.id.locationTv)
    TextView locationTv;
    String name,location,verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        ButterKnife.bind(this);

        key = getIntent().getStringExtra("key");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM , yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        reverseTimer();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 14);
        returnDate = currentDate.format(cal.getTime());
        Log.d("Issue", "Date : " + returnDate);

        issueRef = FirebaseDatabase.getInstance().getReference().child("Issue");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        bookRef = FirebaseDatabase.getInstance().getReference().child("AllBooks").child(key);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("verify")){
                        verify = dataSnapshot.child("verify").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String author = dataSnapshot.child("author").getValue().toString();
                    name = dataSnapshot.child("name").getValue().toString();
                    String publication = dataSnapshot.child("publication").getValue().toString();
                    status = dataSnapshot.child("status").getValue().toString();
                    location = dataSnapshot.child("location").getValue().toString();

                    locationTv.setText("Location: " + location);

                    authorTv.setText("Author: " + author);
                    bookNameTv.setText(name);
                    publicationTv.setText("Publication: " + publication);

                    if (status.equals("issued")) {

                        statusTv.setText("This book is issued!");
                        issueBt.setEnabled(false);
                        infoRl.setVisibility(View.GONE);
                        timerTv.setVisibility(View.GONE);
                        codeTv.setVisibility(View.GONE);
                        infoTv.setVisibility(View.GONE);
                        issueBt.setVisibility(View.GONE);
                        cancelBt.setVisibility(View.GONE);

                        yourCountDownTimer.cancel();

                    } else {
                        statusTv.setText("Check your book details and click on issue button to start the procedure.");
                    }

                } else {
                    timerTv.setVisibility(View.GONE);
                    codeTv.setVisibility(View.GONE);
                    infoTv.setVisibility(View.GONE);
                    issueBt.setVisibility(View.GONE);
                    cancelBt.setVisibility(View.GONE);
                    statusTv.setText("Sorry, this book is not available in database of our library! " + key);
//                    Toast.makeText(IssueActivity.this, "Sorry, this is not available in database of our library!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.issueBt, R.id.cancelBt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.issueBt:
                issue();
                break;
            case R.id.cancelBt:
                cancel();
                break;
        }
    }

    private void cancel() {
        if (CANCEL.equals("start")) {

            yourCountDownTimer.cancel();
            Intent intent = new Intent(IssueActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (CANCEL.equals("issue")) {
            issueRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        yourCountDownTimer.cancel();
                        Intent intent = new Intent(IssueActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        }
    }

    private void issue() {
        if (verify!=null){

            if (verify.equals("true")){

                if (status != null) {
                    if (status.equals("available")) {
                        yourCountDownTimer.start();

                        CANCEL = "issue";
                        Random rand = new Random();

                        int n = rand.nextInt(1000);
                        n += 1;

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("code", String.valueOf(n));
                        map.put("by", currentUserID);
                        map.put("book",name);
                        map.put("shell",location);

                        Toast.makeText(this, "Book is available!", Toast.LENGTH_SHORT).show();

                        int finalN = n;

                        usersRef.child("Issued Books").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long count = 0;

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String status = ds.child("status").getValue().toString();
                                    if (status.equals("issued")) {
                                        count++;
                                    }
                                }

                                if (count > 2) {
                                    statusTv.setText("Sorry,you have reached your maximum limit and cant issue more than 2 books.");
                                } else {
                                    issueRef.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(IssueActivity.this, "Now scan your book on scanner!", Toast.LENGTH_SHORT).show();
                                                codeTv.setText(String.valueOf(finalN));

                                                codeTv.setVisibility(View.VISIBLE);
                                                infoTv.setVisibility(View.VISIBLE);
                                                timerTv.setVisibility(View.VISIBLE);
                                                infoRl.setVisibility(View.VISIBLE);
                                                issueBt.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Unknown book!", Toast.LENGTH_SHORT).show();
                }

            } else{
                Toast.makeText(this, "You are not a verified user! Please try again after you are verified!", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(this, "Verification status not found!", Toast.LENGTH_SHORT).show();
        }

    }

    public void reverseTimer() {

        yourCountDownTimer = new CountDownTimer(180 * 1000 + 1000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                timeRunning = time - (hours * 60 + minutes);
                timerTv.setText(String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                issueRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(IssueActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                            Toast.makeText(IssueActivity.this, "Sorry,your time is up ! Try again to issue your book!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        };
    }
}
