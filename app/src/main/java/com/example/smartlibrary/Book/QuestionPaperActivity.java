package com.example.smartlibrary.Book;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartlibrary.Model.Book;
import com.example.smartlibrary.Model.Papers;
import com.example.smartlibrary.R;
import com.example.smartlibrary.SearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.SearchAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionPaperActivity extends AppCompatActivity {

    @BindView(R.id.paperRv)
    RecyclerView papersRv;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.searchPb)
    ProgressBar searchPb;
    String branch;
    DatabaseReference papersRef;
    ArrayList<Papers> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Question Papers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        branch = getIntent().getStringExtra("branch");

        papersRv.setLayoutManager(new LinearLayoutManager(this));
        searchView.showSearch(false);

        searchPb.setVisibility(View.VISIBLE);
        papersRv.setVisibility(View.GONE);

        papersRef = FirebaseDatabase.getInstance().getReference().child("Papers").child(branch);
        papersRv.setLayoutManager(new LinearLayoutManager(this));

        searches();

    }

    private void searches() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    private void search(String newText) {
        ArrayList<Papers> myList = new ArrayList<>();

        for (Papers object : list) {
            String search = object.getSubject() + object.getSemester() + object.getExam();
            if (search.toLowerCase().contains(newText.toLowerCase())) {
                myList.add(object);
            }

            PaperAdapter adapter = new PaperAdapter(myList);
            papersRv.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        papersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       list.add(ds.getValue(Papers.class));
                    }

                    searchPb.setVisibility(View.GONE);
                    papersRv.setVisibility(View.VISIBLE);

                    PaperAdapter adapter = new PaperAdapter(list);
                    papersRv.setAdapter(adapter);

                } else{
                    searchPb.setVisibility(View.GONE);
                    Toast.makeText(QuestionPaperActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.PaperHolder> {
        ArrayList<Papers> list;

        public PaperAdapter(ArrayList<Papers> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public PaperHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_papers, parent, false);
            return new PaperHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PaperHolder holder, int position) {
            Papers paper = list.get(position);

            holder.subjectTv.setText(paper.getSubject());
            holder.seMinTv.setText(paper.getSemester() + " Semester " + " " + paper.getExam());

            holder.downloadBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(QuestionPaperActivity.this, "Download Paper", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class PaperHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.subjectTv)
            TextView subjectTv;
            @BindView(R.id.semMinTv)
            TextView seMinTv;
            @BindView(R.id.downloadBt)
            ImageView downloadBt;

            public PaperHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
