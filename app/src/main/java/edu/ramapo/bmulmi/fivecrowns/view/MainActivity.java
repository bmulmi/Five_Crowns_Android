package edu.ramapo.bmulmi.fivecrowns.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Deque;
import java.util.Vector;

import edu.ramapo.bmulmi.fivecrowns.R;
import edu.ramapo.bmulmi.fivecrowns.model.Card;
import edu.ramapo.bmulmi.fivecrowns.model.Computer;
import edu.ramapo.bmulmi.fivecrowns.model.Deck;
import edu.ramapo.bmulmi.fivecrowns.model.Game;
import edu.ramapo.bmulmi.fivecrowns.model.Human;
import edu.ramapo.bmulmi.fivecrowns.model.Round;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private Round round;
    private String selectedPile;
    private int selectedHandCard;
    private boolean cardDrawn;
    private boolean cardDiscarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardDiscarded = true;
        cardDrawn = false;

        Button drawButton = findViewById(R.id.drawButton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPile.isEmpty()) {
                    makeToast("You have not selected a pile yet.");
                }
                else if (cardDrawn) {
                    makeToast("You have already drawn a card.");
                }
                else {
                    round.draw(selectedPile);
                    refreshLayout();
                    selectedPile = "";
                    cardDrawn = true;
                    cardDiscarded = false;
                }
            }
        });

        Button discardButton = findViewById(R.id.discardButton);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedHandCard == -1) {
                    makeToast("You have not selected a card yet.");
                }
                else if (cardDiscarded) {
                    makeToast("You have already discarded a card.");
                }
                else {
                    round.discard(selectedHandCard);
                    refreshLayout();
                    selectedHandCard = -1;
                    cardDiscarded = true;
                    cardDrawn = false;
                }
            }
        });

        Button hintButton = findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardDiscarded) {
                    // ask for which card to draw
                }
                else if (cardDrawn) {
                    // ask for which card to discard
                }
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndQuit();
            }
        });

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(round.getNextPlayer().getClass() == Computer.class) {
                    round.playComputer();
                }
            }
        });

        final Integer gameState = getIntent().getIntExtra("state",1);

        if (gameState == 1) {
        // start a new game
            game = new Game(1);
            round = game.generateNewRound();
            round.init();
            refreshLayout();
            toss();
        }
        else {
        // load the game
            String fileName = (String) getIntent().getSerializableExtra("file");
            try {
                if(isExternalStorageReadable()) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savedGames/"+fileName;
                    InputStream is = new FileInputStream(path);
                    game = new Game ();
                    round = game.load(is);
                    refreshLayout();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void toss() {
        AlertDialog.Builder guess = new AlertDialog.Builder(this);
        guess.setTitle("Toss: Heads or Tails");
        final int tossResult = game.toss();
        final String[] choice = {"Heads", "Tails"};
        guess.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView select = ((AlertDialog)dialogInterface).getListView();
                String selected = (String) select.getAdapter().getItem(i);
                if ((selected.equals("Heads") && tossResult == 0) || (selected.equals("Tails") && tossResult == 1)) {
                    round.setNextPlayer(Human.class);
                    makeToast("You Won the Toss! You play first.");
                }
                else {
                    round.setNextPlayer(Computer.class);
                    makeToast("You Lost the Toss. Computer plays first");
                }
            }
        });
        guess.show();
    }

    public void makeToast(String tst) {
        Toast.makeText(getApplicationContext(), tst, Toast.LENGTH_SHORT).show();
    }

    private void refreshLayout() {
        int roundNumber = game.getRoundNumber();
        Deck deck = Deck.getInstanceOfDeck(2);
        Deque<Card> drawPile = deck.getDrawPile();
        Deque<Card> discardPile = deck.getDiscardPile();
        Vector<Card> humanHand = round.getHumanHand();
        Vector<Card> computerHand = round.getComputerHand();
        int humanScore = round.getHumanScore();
        int computerScore = round.getComputerScore();

        TextView roundNumberView = findViewById(R.id.roundNumber);
        TextView computerScoreView = findViewById(R.id.compScore);
        TextView humanScoreView = findViewById(R.id.humanScore);
        LinearLayout humanHandView = findViewById(R.id.humanHand);
        LinearLayout computerHandView = findViewById(R.id.computerHand);
        LinearLayout drawPileView = findViewById(R.id.drawPile);
        LinearLayout discardPileView = findViewById(R.id.discardPile);

        // clear all card views
        humanHandView.removeAllViews();
        computerHandView.removeAllViews();
        drawPileView.removeAllViews();
        discardPileView.removeAllViews();

        // set the round number view
        String roundNum = "Round Number: " + roundNumber;
        roundNumberView.setText(roundNum);

        // add score to score view
        humanScoreView.setText(Integer.toString(humanScore));
        computerScoreView.setText(Integer.toString(computerScore));

        // add cards to table
        addCardsToTable(drawPileView, drawPile, "draw");
        addCardsToTable(discardPileView, discardPile, "discard");

        // add cards to hand
        addCardsToHand(humanHandView, humanHand, true);
        addCardsToHand(computerHandView, computerHand, false);
    }

    private void addCardsToTable(LinearLayout layout, Collection<Card> pile, final String pileType) {
        boolean first = true;
        for (Card card : pile) {
            ImageView cardView = new ImageView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 250);
            params.setMargins(15, 0, 15, 0);
            cardView.setLayoutParams(params);

            Context context = layout.getContext();
            int id = context.getResources().getIdentifier(card.toString(), "drawable", context.getPackageName());
            cardView.setImageResource(id);

            if (first) {
                cardView.setTag(pileType);
                cardView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCardsBackground();
                        view.setBackgroundColor(Color.YELLOW);
                        selectedPile = pileType;
                        makeToast(selectedPile);
                    }
                });
                first = false;
            }
            layout.addView(cardView);
        }
    }

    private void addCardsToHand(LinearLayout layout, Collection<Card> pile, boolean human) {
        int i = 0;
        for (Card card : pile) {
            final ImageView cardView = new ImageView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 250);
            params.setMargins(15, 0, 15, 0);
            cardView.setLayoutParams(params);

            Context context = layout.getContext();
            int id = context.getResources().getIdentifier(card.toString(), "drawable", context.getPackageName());
            cardView.setImageResource(id);

            if (human) {
                cardView.setTag(i);
                cardView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCardsBackground();
                        cardView.setBackgroundColor(Color.YELLOW);
                        selectedHandCard = Integer.parseInt(cardView.getTag().toString());
                        makeToast(selectedHandCard + "");
                    }
                });
            }
            layout.addView(cardView);
            i++;
        }
    }

    private void clearCardsBackground() {
        selectedHandCard = -1;
        selectedPile = "";
        LinearLayout humanHand = findViewById(R.id.humanHand);
        int size = round.getHumanHand().size();
        for (int i = 0; i < size; i++) {
            ImageView each = (ImageView) humanHand.getChildAt(i);
            each.setBackgroundColor(Color.TRANSPARENT);
        }

        LinearLayout discard = findViewById(R.id.discardPile);
        try {
            discard.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout draw = findViewById(R.id.drawPile);
        try {
            draw.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //checks if the ExternalStorage is Readable or not
    //returns boolean values
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    private void saveAndQuit() {
        String info = round.serialize();
        try {
            if(isExternalStorageWritable()) {
                File file = getFile();
                OutputStream os = new FileOutputStream(file);
                os.write(info.getBytes());
                os.close();
                makeToast("Game Saved as: " + file.getName());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
    private File getFile(){
        int i = 1;
        while (true){
            String fileName = "save"+i+".txt";
            String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/savedGames/"+fileName;
            File file = new File(fileDir);
            if(!file.exists()){
                return file;
            }
            i++;
        }
    }
}
