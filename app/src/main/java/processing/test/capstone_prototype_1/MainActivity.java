package processing.test.capstone_prototype_1;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_gameplay;
    Button btn_createlvl;
    Button btn_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gameplay = (Button) findViewById(R.id.btn_gameplay);
        btn_createlvl = (Button) findViewById(R.id.btn_createlevel);
        btn_help = (Button) findViewById(R.id.btn_help);

        btn_gameplay.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, GameplayActivity.class));
        });

        btn_createlvl.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SelectImageActivity.class));
        });

        btn_help.setOnClickListener(v->{
            showHelpDialog();
        });
    }


    private void showHelpDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.help_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }
}
