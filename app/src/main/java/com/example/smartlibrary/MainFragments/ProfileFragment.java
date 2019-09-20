package com.example.smartlibrary.MainFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartlibrary.Book.MyBooksActivity;
import com.example.smartlibrary.Book.ScannedBarCodeActivity;
import com.example.smartlibrary.EditDetailsActivity;
import com.example.smartlibrary.Login.LoginActivity;
import com.example.smartlibrary.Model.Item;
import com.example.smartlibrary.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFragment extends Fragment {

    View v;
    ArrayList<Item> list = new ArrayList<>();
    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.nameTv)
    TextView nameTv;

    DatabaseReference userRef;
    String currentUserID;

    String loginID;
    @BindView(R.id.uniqueIDRl)
    RelativeLayout uniqueIDRl;
    @BindView(R.id.booksRl)
    RelativeLayout booksRl;
    @BindView(R.id.issueBookRl)
    RelativeLayout issueBookRl;
    @BindView(R.id.returnRl)
    RelativeLayout returnRl;
    @BindView(R.id.membershipTv)
    TextView membershipTv;
    @BindView(R.id.membershipRl)
    RelativeLayout membershipRl;
    @BindView(R.id.editView)
    ImageView editView;
    @BindView(R.id.logOutRl)
    RelativeLayout logOutRl;
    @BindView(R.id.uniqueIDTv)
    TextView uniqueIDTv;
    @BindView(R.id.verifyTv)
    TextView verifyTv;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        retrieveData();

        return v;
    }

    private void retrieveData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(userImage);
                    }

                    if (dataSnapshot.hasChild("name")) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        nameTv.setText(name);
                    }

                    if (dataSnapshot.hasChild("verify")){
                        String verify = dataSnapshot.child("verify").getValue().toString();

                        if (verify.equals("true")){
                            verifyTv.setText("Verified");
                        } else {
                            verifyTv.setText("Not verified");
                        }

                    }

                    loginID = dataSnapshot.child("userID").getValue().toString();
                    uniqueIDTv.setText(loginID);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.uniqueIDRl, R.id.booksRl, R.id.issueBookRl, R.id.returnRl, R.id.membershipRl, R.id.editView, R.id.logOutRl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.uniqueIDRl:
                break;
            case R.id.booksRl:
                Intent intent3 = new Intent(getActivity(), MyBooksActivity.class);
                startActivity(intent3);
                break;
            case R.id.issueBookRl:
                Intent intent = new Intent(getActivity(), ScannedBarCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.returnRl:
                returnDialog();
                break;
            case R.id.membershipRl:
                break;
            case R.id.logOutRl:
                logOut();
                break;
            case R.id.editView:
                Intent intent2 = new Intent(getActivity(), EditDetailsActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void returnDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.zlayout_return, null);
        dialog.setContentView(sheetView);

        TextView returnTv = dialog.findViewById(R.id.returnTv);

        userRef.child("Issued Books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long count = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String status = ds.child("status").getValue().toString();
                        if (status.equals("issued")) {
                            count++;
                        }
                    }

                    if (count == 0) {
                        returnTv.setText("You have no books to return currently!");
                    } else {
                        returnTv.setText("You have " + count + " book to return. Kindly return on time to avoid fine");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dialog.show();

    }

    private void logOut() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Logout").setMessage("Do you want to logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient;
                mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i2 = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i2);
                        getActivity().finish();
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}
