package com.example.allan.appalpharead;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.allan.appalpharead.Adapters.RecyclerViewAdapter;
import com.example.allan.appalpharead.provas.Prova;
import com.example.allan.appalpharead.provas.QuestionFour;
import com.example.allan.appalpharead.provas.QuestionOne;
import com.example.allan.appalpharead.provas.QuestionThree;
import com.example.allan.appalpharead.provas.QuestionTwo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.SimpleTimeZone;

public class SearchExam extends Activity {

    private FirebaseAuth mAuth;
    private String Uid;

    private ArrayList<String> titles, points, uid;
    private ArrayList<Prova> prova;
    private String myScore;
    private ProgressBar prog;

    private ImageView img;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_exam);

        msg = findViewById(R.id.msg);
        img = findViewById(R.id.back);

        titles = new ArrayList<>();
        points = new ArrayList<>();
        uid = new ArrayList<>();
        prova = new ArrayList<>();
    //    myScore = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        Uid = mAuth.getUid();
        prog = findViewById(R.id.progressAllProvas);
        prog.setVisibility(View.VISIBLE);

        initList();
    }

    private void initList(){


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refProva = database.getReference("Users/");

        refProva.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals(Uid)) {
                        myScore = "";
                        for (DataSnapshot uSnapShot : ds.child("UserProfile").getChildren()) {
                            myScore = uSnapShot.child("score").getValue().toString();
                        }
                    }

                    for (DataSnapshot pSnapShot : ds.child("Provas").getChildren()) {
                        titles.add(pSnapShot.child("questionTitle").getValue().toString());
                        points.add(pSnapShot.child("score").getValue().toString());
                        uid.add(pSnapShot.getKey());

                        QuestionOne q1 = new QuestionOne(pSnapShot.child("_q1").child("w1").getValue().toString(),
                                pSnapShot.child("_q1").child("w2").getValue().toString(),
                                pSnapShot.child("_q1").child("w3").getValue().toString());

                        QuestionTwo q2 = new QuestionTwo(pSnapShot.child("_q2").child("word").getValue().toString());


                        QuestionThree q3 = new QuestionThree(pSnapShot.child("_q3").child("name").getValue().toString(), pSnapShot.child("_q3").child("image").getValue().toString());

                        QuestionFour q4 = new QuestionFour(pSnapShot.child("_q4").child("frase").getValue().toString());

                        Prova p = new Prova(q1, q2, q3, q4, pSnapShot.child("questionTitle").getValue().toString(), pSnapShot.child("userUid").getValue().toString());
                        p.setScore(Integer.valueOf(pSnapShot.child("score").getValue().toString()));
                        prova.add(p);
                    }
                }
                if (prova.isEmpty()){
                    img.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.books));
                    msg.setText("Não existe nenhuma atividade registrada até o momento.\n\nCrie uma atividade agora para que outras pessoas possam aprender com você!");
                }
                else initRecyclerView();

                prog.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

   private void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(myScore, prova, uid, titles, points, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

}
