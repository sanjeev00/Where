package com.barebrains.where;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.ListActivity;

import com.bumptech.glide.Glide;
import com.barebrains.where.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriends extends ListActivity {
    private DatabaseReference mDatabase;
    private ChildEventListener mChild;
    public ArrayList<Users> list;
    public List<String> kl;
    public List<String> ll;
    public Users user;
    public String uid;

    Map<String,String> ld ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser us = mAuth.getCurrentUser();
        uid = " ";
        if (us != null)
            uid = us.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new Users();
        list = new ArrayList<>();
        ll  = new ArrayList<>();
        kl = new ArrayList<>();
        mDatabase.child("avatars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ld =(HashMap<String,String>)dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                kl.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().equalsIgnoreCase(uid)) {


                        user = new Users((String) ds.child("Name").getValue(),(String)ds.child("Email").getValue());

                        String k = ds.getKey();

                        Log.e(user.getName(), user.getEmail());
                        kl.add(k);
                        if(ld.containsKey(ds.getKey()))
                        {
                            ll.add(ld.get(ds.getKey()));
                        }
                        else
                            ll.add("https://firebasestorage.googleapis.com/v0/b/where-2f226.appspot.com/o/avatars%2Favatar.png?alt=media&token=625007a5-b676-4254-8f57-6c4511b0fd09");

                        list.add(user);
                    }

                }
                setListAdapter(new MyAdapter());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addFriend(String key) {
        FirebaseDatabase.getInstance().getReference().child("FrReq").child(key).child(uid).setValue(uid);
    }

    protected void onStart() {
        super.onStart();


    }

    class MyAdapter extends BaseAdapter {

        // override other abstract methods here
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            Log.e("print ", list.get(position).getName());
            return list.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getName().hashCode();
        }

        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.friend_add, container, false);
            }

            ((TextView)convertView.findViewById(R.id.text1)).setText(getItem(position));


            ImageView dp = convertView.findViewById(R.id.av);
            Glide.with(AddFriends.this).load(ll.get(position)).circleCrop().into(dp);
            ImageView add = convertView.findViewById(R.id.button);
            final TextView t = convertView.findViewById(R.id.pos);
            t.setText(String.valueOf(position));
            final ImageView check = convertView.findViewById(R.id.check);
            add.setTag(position);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.setVisibility(View.GONE);
                    int pos = (Integer) v.getTag();
                    t.setText(String.valueOf(pos));
                    String f = kl.get(pos);
                    Log.d("id", f);
                    addFriend(f);
                    ((RelativeLayout)v.getParent()).getChildAt(1).setVisibility(View.VISIBLE);

                }
            });
            return convertView;

        }
    }
}




