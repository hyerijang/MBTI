package kr.hongik.mbti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class MatchingActivity extends AppCompatActivity {

    private Button btn_matchingOption;
    private static final String TAG = "MatchingActivity";
    String mp_nickname, mp_gender, mp_age, mp_mbti, mp_address, mp_stateMessage, mp_gender2;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        usersRef.document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("gender")=="남자"){
                    mp_gender2 = "여자";
                }
                else mp_gender2 = "남자";
            }
        });

        btn_matchingOption = findViewById(R.id.btn_matchingOption);

        btn_matchingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matchingAlgorithm();
            }
        });
    }

    private void matchingAlgorithm (){

        String m_mbti = ((EditText)findViewById(R.id.m_mbti)).getText().toString();
        String m_minage = ((EditText)findViewById(R.id.m_minage)).getText().toString();
        String m_maxage = ((EditText)findViewById(R.id.m_maxage)).getText().toString();

        if(m_mbti.length()>0 && m_minage.length()>0 && m_maxage.length()>0){
            usersRef.whereEqualTo("gender", mp_gender2)
                    .whereEqualTo("mbti", m_mbti)
                    .whereGreaterThanOrEqualTo("age", m_minage)
                    .whereLessThanOrEqualTo("age", m_maxage)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                        mp_nickname=document.getString("nickname");
                                        mp_gender=document.getString("gender");
                                        mp_age=document.getString("age");
                                        mp_mbti=document.getString("mbti");
                                        mp_address=document.getString("address");
                                        mp_stateMessage=document.getString("stateMessage");

                                            MemberInfo m = new MemberInfo(mp_nickname, mp_gender, mp_age, mp_mbti, mp_address, mp_stateMessage);

                                            Intent intent = new Intent(MatchingActivity.this,MatchingPersonActivity.class);
                                            intent.putExtra("MemberInfo", m);
                                            startActivity(intent);
                                        }
                                }
                             else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        else {
            startToast("옵션을 전부 입력해주세요");
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }

    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}