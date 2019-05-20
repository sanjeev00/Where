package com.barebrains.where;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.barebrains.where.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener{

        public ImageView dp;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars");
    String clubkey;
    TextView avname;
    List<String> l;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout,null);
        avname = view.findViewById(R.id.avatar_name);
        dp = view.findViewById(R.id.avatar);
        dp.setOnClickListener(this);
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("avatars");
                   l = new ArrayList<>();
                mDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren())
                        {

                                if(ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                    clubkey= (String)ds.getValue();
                                    l.add(clubkey);
                                    Log.e("ds",clubkey);
                                }

                        }
                        if(clubkey!=null)
                        Glide.with(ProfileFragment.this).load(clubkey).centerCrop()
                                .transition(new DrawableTransitionOptions().crossFade()).circleCrop().into(dp);
                        else
                            Glide.with(ProfileFragment.this).load(R.drawable.avatar).circleCrop().into(dp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference("Users").
                        addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Users u = new Users();
                                for(DataSnapshot ds:dataSnapshot.getChildren())
                                {
                                   if(ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    u = ds.getValue(Users.class);
                                }
                                avname.setText(u.getName());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
        storageReference =storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".png");



        return view;


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.avatar)
        {
            Intent upload  = new Intent(v.getContext(), UploadAvatar.class);
            startActivity(upload);
        }
    }
}
