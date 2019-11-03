package edu.ramapo.bmulmi.fivecrowns.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.ramapo.bmulmi.fivecrowns.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);
        startButton.setText("New Game");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("state",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setText("Load Game");
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder prompt = new AlertDialog.Builder(StartActivity.this);
                prompt.setTitle("Load Game");

                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/savedGames/";
                File directory = new File(fileDir);
                final File[] files = directory.listFiles();
                List<String> nameList = new ArrayList<>();
                for (File file : files ){
                    String name = file.getName();
                    if (name.endsWith(".txt")){
                        nameList.add(name);
                    }
                }

                String [] items = new String[nameList.size()];
                items = nameList.toArray(items);

                prompt.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int these) {
                        ListView select = ((AlertDialog)dialog).getListView();
                        String fileName = (String) select.getAdapter().getItem(these);
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra("state", 2);
                        intent.putExtra("file", fileName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                prompt.show();            }
        });
    }
}
