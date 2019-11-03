package edu.ramapo.bmulmi.fivecrowns.view;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Deque;

import edu.ramapo.bmulmi.fivecrowns.R;
import edu.ramapo.bmulmi.fivecrowns.model.Card;
import edu.ramapo.bmulmi.fivecrowns.model.Deck;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Integer gameState = getIntent().getIntExtra("state",1);


        if (gameState == 1) {
        // start a new game

        }
        else {
        // load the game
            String fileName = (String) getIntent().getSerializableExtra("file");
            try {
                if(isExternalStorageReadable()) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savedGames/"+fileName;
                    InputStream is = new FileInputStream(path);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Deck tDeck = Deck.getInstanceOfDeck(2);
        Deque<Card> tDraw = tDeck.getDrawPile();

        TextView temp = findViewById(R.id.temp);
        temp.setText("Game State: " + gameState);
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //checks if the ExternalStorage is Readable or not
    //returns boolean values
    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

}
