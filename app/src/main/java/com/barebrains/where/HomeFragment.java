package com.barebrains.where;
import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.barebrains.where.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager;

    private FirebaseAuth mAuth;
    private TextView w_text;
    private DatabaseReference mDatabase;
    private String  t;
    private List<Users> d;
    private List<String> Fl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,null);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        d = new ArrayList<>();
        Fl = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user.getUid());

        view.findViewById(R.id.fr).setOnClickListener(this);
        recyclerView = view.findViewById(R.id.rl);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    t = ds.getKey();
                    Fl.add(t);


                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!Fl.isEmpty())
                {
                    d.clear();
                    view.findViewById(R.id.add_fr).setVisibility(View.GONE);
                    for(String x:Fl)
                    {
                        d.add(dataSnapshot.child(x).getValue(Users.class));
                    }
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);
                    mAdapter = new rAdapter(d, Fl);

                    recyclerView.setAdapter(mAdapter);
                }
                else
                    view.findViewById(R.id.rl).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void onClick(View v)
    {
        int i = v.getId();
        if(i==R.id.fr)
        {
            Intent ni = new Intent(v.getContext(), AddFriends.class);
            startActivity(ni,ActivityOptions.makeScaleUpAnimation(v,315,419,20,20).toBundle());
        }

    }
    class rAdapter extends RecyclerView.Adapter<rAdapter.MyViewHolder> {
        private List<Users> dataset;
        private List<String> L;

         class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView nam,location;
            ImageView avt;
            MyViewHolder(View c)

            {
                super(c);
                avt = c.findViewById(R.id.fr_avatar);
                nam = c.findViewById(R.id.fr_name);
                location = c.findViewById(R.id.fr_location);


            }
        }
        rAdapter(List<Users> d,List<String> fl)
        {
            L = fl;
            dataset = d;
        }



        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            ImageView avt= holder.avt;
            if(dataset.get(position).getAvatar()!=null)
                Glide.with(HomeFragment.this).load(dataset.get(position).getAvatar()).circleCrop().into(avt);
            else
                Glide.with(HomeFragment.this).load(R.drawable.avatar).circleCrop().into(avt);
            TextView name = holder.nam;
            name.setText(dataset.get(position).getName());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                TextView loc = holder.location;

                String l = dataset.get(position).getLocation();
                if(l!=null) {
                    l = "Is at "+l;
                    loc.setText(l);
                }
            }
            else
            {
                TextView loc = holder.location;

                String l = dataset.get(position).getLocation();
                if(l!=null) {
                    l = "Is at "+l;
                    loc.setText(l);
                }
            }

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View locationview = inflater.inflate(R.layout.friendlocard,viewGroup,false);
            MyViewHolder viewHolder = new MyViewHolder(locationview);
            return viewHolder;

        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}

