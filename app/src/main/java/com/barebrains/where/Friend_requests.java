package com.barebrains.where;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.barebrains.where.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friend_requests extends AppCompatActivity {
    private DatabaseReference mDatabase;
    public ArrayList<Users> list;
    public List<String> kl;
    public List<String> ll;
    public Map<String,String> ld;
    public Map<String, String> td;
    public FirebaseAuth mAuth;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        listView= findViewById(R.id.list);
        list = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        kl = new ArrayList<>();
        ll = new ArrayList<>();
        FirebaseUser us = mAuth.getCurrentUser();
        String uid = us.getUid();
        getSupportActionBar().setTitle("Friend Requests");
        if(us!=null)
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("avatars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ld =(HashMap<String,String>)dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.child("FrReq").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                td = (HashMap<String,String>) dataSnapshot.getValue();

                List<String> values = new ArrayList<String>();;
                      if(!values.isEmpty())
                      values.addAll(td.values());

                for(int x=0; x<values.size(); x++)
                {
                    Log.d("friend",values.get(x));
                }
                //notifyDataSetChanged();
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
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {

                 if(td!=null && td.containsKey(ds.getKey()))
                    {
                        list.add(new Users((String) ds.child("Name").getValue(),(String)ds.child("Email").getValue()));
                        kl.add(ds.getKey());
                        if(ld.containsKey(ds.getKey()))
                        {
                            ll.add(ld.get(ds.getKey()));
                        }
                        else
                            ll.add("https://firebasestorage.googleapis.com/v0/b/where-2f226.appspot.com/o/avatars%2Favatar.png?alt=media&token=625007a5-b676-4254-8f57-6c4511b0fd09");

                    }

                }


                if(!list.isEmpty())
                listView.setAdapter(new MyAdapter());
                else
                {
                    String[] val = {"You have no friend requests"};
                    ArrayAdapter<String>  adapter = new ArrayAdapter<String>(Friend_requests.this,R.layout.list_text,R.id.text3,val);
                    listView.setAdapter(adapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    class MyAdapter extends BaseAdapter {

        // override other abstract methods here
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            Log.e("print ",list.get(position).getName());
            return list.get(position).getName();
        }
        @Override
        public long  getItemId(int position)
        {
            return list.get(position).getName().hashCode();
        }

        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.friendcard, container, false);
            }

            ((TextView) convertView.findViewById(R.id.friend_name))
                    .setText(getItem(position));
            final TextView hid_id=(TextView)convertView.findViewById(R.id.id);
                    hid_id.setText(kl.get(position));
            final ImageView accept= convertView.findViewById(R.id.accept);
            final ImageView reject =convertView.findViewById(R.id.reject);
            accept.setTag(position);
            reject.setTag(position);
            ImageView cdp =  convertView.findViewById(R.id.card_avatar);
            Glide.with(Friend_requests.this).load(ll.get(position)).into(cdp);
            class click implements View.OnClickListener
            {
                @Override
                public void onClick(View v) {

                    if(v.getId() ==R.id.accept)
                    {
                        int pos = (Integer) accept.getTag();
                        reject.setVisibility(View.GONE);
                        String uid =mAuth.getCurrentUser().getUid();
                        mDatabase.child("FrReq").child(uid).child(hid_id.getText().toString()).removeValue();
                        mDatabase.child("Friends").child(uid).child(hid_id.getText().toString()).setValue(hid_id.getText().toString());
                        mDatabase.child("Friends").child(hid_id.getText().toString()).child(uid).setValue(uid);
                    }
                    if(v.getId() == R.id.reject)
                    {
                        int pos = (Integer) reject.getTag();
                        accept.setVisibility(View.GONE);
                        mDatabase.child("FrReq").child(mAuth.getCurrentUser().getUid()).child(hid_id.getText().toString()).removeValue();

                    }
                }

            }
            accept.setOnClickListener(new click());
            reject.setOnClickListener(new click());

            return convertView;

        }
    }
}






