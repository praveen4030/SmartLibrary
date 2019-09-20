package com.example.smartlibrary.MainFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartlibrary.Book.BooksActivity;
import com.example.smartlibrary.Model.Book;
import com.example.smartlibrary.Model.Search;
import com.example.smartlibrary.R;
import com.example.smartlibrary.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */

public class SearchFragment extends Fragment {

    @BindView(R.id.raw1)
    TextView raw1;
    @BindView(R.id.searchImage)
    ImageView searchImage;
    @BindView(R.id.searchRl)
    RelativeLayout searchRl;
    @BindView(R.id.searchRv)
    RecyclerView searchRv;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this,v);

        searchRv.setLayoutManager(new GridLayoutManager(getActivity(),2));

        searchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("Books").orderByKey();

        FirebaseRecyclerOptions<Search> options = new FirebaseRecyclerOptions.Builder<Search>()
                .setQuery(query,Search.class)
                .build();

        FirebaseRecyclerAdapter<Search,SearchHolder> adapter = new FirebaseRecyclerAdapter<Search, SearchHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchHolder holder, int i, @NonNull Search search) {
                String key = getRef(i).getKey();
                holder.itemNameTv.setText(key);

//                Picasso.get().load(search.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), BooksActivity.class);
                        intent.putExtra("category",key);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_search_item,parent,false);
                return  new SearchHolder(v);
            }
        };

        searchRv.setAdapter(adapter);
        adapter.startListening();
    }

    public class SearchHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemNameTv)
        TextView itemNameTv;
        @BindView(R.id.itemImage)
        ImageView imageView;
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
