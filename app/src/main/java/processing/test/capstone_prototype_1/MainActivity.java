package processing.test.capstone_prototype_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_gameplay;
    Button btn_createlvl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gameplay = (Button) findViewById(R.id.btn_gameplay);
        btn_createlvl = (Button) findViewById(R.id.btn_createlevel);

        btn_gameplay.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, GameplayActivity.class));
        });

        btn_createlvl.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SelectImageActivity.class));
        });
    }
}
