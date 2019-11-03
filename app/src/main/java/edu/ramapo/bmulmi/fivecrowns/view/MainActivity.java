package edu.ramapo.bmulmi.fivecrowns.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Deque;
import java.util.Vector;

import edu.ramapo.bmulmi.fivecrowns.R;
import edu.ramapo.bmulmi.fivecrowns.model.Card;
import edu.ramapo.bmulmi.fivecrowns.model.Deck;
import edu.ramapo.bmulmi.fivecrowns.model.Game;
import edu.ramapo.bmulmi.fivecrowns.model.Round;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private Round round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Integer gameState = getIntent().getIntExtra("state",1);


        if (gameState == 1) {
        // start a new game
            game = new Game(1);
            round = game.generateNewRound();
            round.init();
            refreshLayout();
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
//        Deck tDeck = Deck.getInstanceOfDeck(2);
//        String tDraw = tDeck.toString(tDeck.getDiscardPile());
//        TextView t = findViewById(R.id.hintView);
//        t.setText(tDraw);
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
        addCardsToTable(drawPileView, drawPile);
        addCardsToTable(discardPileView, discardPile);

        // add cards to hand
        addCardsToTable(humanHandView, humanHand);
        addCardsToTable(computerHandView, computerHand);
    }

    private void addCardsToTable(LinearLayout layout, Collection<Card> pile) {
        for (Card card : pile) {
            ImageView cardView = new ImageView(this);
            Context context = layout.getContext();

            int id = context.getResources().getIdentifier(card.toString(), "drawable", context.getPackageName());

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), id, null);
            cardView.setBackground(drawable);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 250);

            params.setMargins(15, 0, 15, 0);
            cardView.setLayoutParams(params);

            layout.addView(cardView);
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

}
