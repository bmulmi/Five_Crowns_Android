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
import java.util.Random;

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
                AlertDialog.Builder guess = new AlertDialog.Builder(StartActivity.this);
                guess.setTitle("Toss: Heads or Tails");

                final String[] choice = {"Heads", "Tails"};
                final String result;

                // display the toss dialogue and start the main activity
                guess.setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ListView select = ((AlertDialog)dialogInterface).getListView();
                        String selected = (String) select.getAdapter().getItem(i);
                        final Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra("state",1);

                        // generate a random integer
                        Random rand = new Random();
                        int temp = rand.nextInt(15189);
                        int tossResult = temp;

                        if ((selected.equals("Heads") && tossResult == 0) || (selected.equals("Tails") && tossResult == 1)) {
                            intent.putExtra("turn", "human");
                        }
                        else {
                            intent.putExtra("turn","computer");
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                guess.show();

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

                // show the load file list and start the main activity
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
                prompt.show();
            }
        });
    }
}
