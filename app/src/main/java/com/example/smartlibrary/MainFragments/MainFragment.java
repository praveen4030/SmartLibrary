package com.example.smartlibrary.MainFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartlibrary.Book.ArticleListActivity;
import com.example.smartlibrary.Book.BooksActivity;
import com.example.smartlibrary.Book.QuestionPaperActivity;
import com.example.smartlibrary.Extra.WebViewActivity;
import com.example.smartlibrary.Home.VideoListActivity;
import com.example.smartlibrary.Login.UserDataActivity;
import com.example.smartlibrary.Model.Item;
import com.example.smartlibrary.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {

    View v;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.raw2)
    TextView raw2;
    @BindView(R.id.coursesRv)
    RecyclerView coursesRv;

    ArrayList<Item> list1 = new ArrayList<>();

    @BindView(R.id.itemCv1)
    CardView itemCv1;
    @BindView(R.id.itemCv2)
    CardView itemCv2;
    @BindView(R.id.itemCv3)
    CardView itemCv3;
    @BindView(R.id.itemCv4)
    CardView itemCv4;

    BottomSheetDialog dialog;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, v);

        coursesRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        addData();

        RecyclerAdapter adapter = new RecyclerAdapter(list1);

        coursesRv.setAdapter(adapter);
        return v;

    }

    private void addData() {

        Uri uri1 = Uri.parse("android.resource://com.example.smartlibrary/drawable/gate");
        Uri uri2 = Uri.parse("android.resource://com.example.smartlibrary/drawable/jee");
        Uri uri3 = Uri.parse("android.resource://com.example.smartlibrary/drawable/nptel");
        Uri uri4 = Uri.parse("android.resource://com.example.smartlibrary/drawable/unacademy");
        Uri uri5 = Uri.parse("android.resource://com.example.smartlibrary/drawable/ncert");

        Item item1 = new Item("GATE", uri1.toString(),"http://thegateacademy.com/");
        Item item2 = new Item("Joint Admission Board of IITs", uri2.toString(),"https://www.toppr.com/bytes/jee-coaching-classes/");
        Item item3 = new Item("NPTEL", uri3.toString(),"https://nptel.ac.in/");
        Item item4 = new Item("Unacademy", uri4.toString(),"https://unacademy.com/");
        Item item5 = new Item("NCERT", uri5.toString(),"http://ncert.nic.in/");

        list1.add(item1);
        list1.add(item2);
        list1.add(item3);
        list1.add(item4);
        list1.add(item5);

    }

    @OnClick({R.id.itemCv1, R.id.itemCv2, R.id.itemCv3, R.id.itemCv4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.itemCv1:
                chooseCategory("book");
                break;
            case R.id.itemCv2:
                chooseCategory("video");
                break;
            case R.id.itemCv3:
                chooseCategory("paper");
                break;
            case R.id.itemCv4:
                Intent intent = new Intent(getActivity(), ArticleListActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void chooseCategory(String type) {

        dialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.zlayout_branch,null);
        dialog.setContentView(sheetView);

        RelativeLayout cseRl = dialog.findViewById(R.id.cseRl);
        RelativeLayout eceRl = dialog.findViewById(R.id.eceRl);
        RelativeLayout iceRl = dialog.findViewById(R.id.iceRl);
        RelativeLayout mechRl = dialog.findViewById(R.id.mechRl);
        RelativeLayout chemRl = dialog.findViewById(R.id.chemRl);
        RelativeLayout civilRl = dialog.findViewById(R.id.civilRl);

        if (type.equals("book")){
            branchClick(cseRl,"Computer Science");
            branchClick(eceRl,"Electronics and Communication engineering");
            branchClick(iceRl,"Instrumentation");
            branchClick(mechRl,"Mechanical");
            branchClick(chemRl,"Chemical");
            branchClick(civilRl,"Civil");

        }else if (type.equals("video")){
            branchClickVideo(cseRl,"Computer Science");
            branchClickVideo(eceRl,"Electronics and Communication engineering");
            branchClickVideo(iceRl,"Instrumentation");
            branchClickVideo(mechRl,"Mechanical");
            branchClickVideo(chemRl,"Chemical");
            branchClickVideo(civilRl,"Civil");
        } else if (type.equals("paper")){

            branchClickPaper(cseRl,"Computer Science");
            branchClickPaper(eceRl,"Electronics and Communication engineering");
            branchClickPaper(iceRl,"Instrumentation");
            branchClickPaper(mechRl,"Mechanical");
            branchClickPaper(chemRl,"Chemical");
            branchClickPaper(civilRl,"Civil");

        }

        dialog.show();

    }

    private void branchClick(RelativeLayout rl,String intentSt){
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent intent = new Intent(getActivity(), BooksActivity.class);
                intent.putExtra("category",intentSt);
                startActivity(intent);

            }
        });

    }

    private void branchClickVideo(RelativeLayout rl,String intentSt){
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(),VideoListActivity.class);
                intent.putExtra("branch",intentSt);
                startActivity(intent);
            }
        });
    }

    private void branchClickPaper(RelativeLayout rl,String intentSt){
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), QuestionPaperActivity.class);
                intent.putExtra("branch",intentSt);
                startActivity(intent);
            }
        });
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemHolder> {

        ArrayList<Item> list;

        public RecyclerAdapter(ArrayList<Item> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zunit_item, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            Item item = list.get(position);

            holder.itemNameTv.setText(item.getName());
            Picasso.get().load(item.getImage()).into(holder.itemImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("webUrl",item.getUrl());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.itemImage)
            ImageView itemImage;
            @BindView(R.id.itemNameTv)
            TextView itemNameTv;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
