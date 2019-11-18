package edu.ramapo.bmulmi.fivecrowns.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Environment;
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
    private boolean lastTurn;
    private TextView textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastTurn = false;
        cardDiscarded = true;
        cardDrawn = false;
        selectedPile = "";
        selectedHandCard = -1;
        textBox = findViewById(R.id.hintView);

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
                    selectedPile = "";
                    cardDrawn = true;
                    cardDiscarded = false;
                    refreshLayout();
                    findViewById(R.id.arrangeHand).setEnabled(false);
                    findViewById(R.id.saveButton).setEnabled(false);
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
                    selectedHandCard = -1;
                    cardDiscarded = true;
                    cardDrawn = false;
                    changePlayer();
                    refreshLayout();
                    findViewById(R.id.arrangeHand).setEnabled(true);
                    findViewById(R.id.saveButton).setEnabled(true);
                }
            }
        });

        Button hintButton = findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardDiscarded) {
                    // ask for which card to draw
                    String hint = round.getPileHint();
                    if (hint.equals("draw")) {
                        int id = 31;
                        ImageView pile = findViewById(id);
                        pile.setBackgroundColor(Color.CYAN);
                    }
                    else {
                        int id = 41;
                        ImageView pile = findViewById(id);
                        pile.setBackgroundColor(Color.CYAN);
                    }
                    String text = "You should choose " + hint + " pile because it helps in making your score lower.\n";
                    textBox.setText(text);
                }
                else if (cardDrawn) {
                    // ask for which card to discard
                    int hint = round.getDiscardHint();
                    ImageView card = findViewById(hint);
                    card.setBackgroundColor(Color.CYAN);
                    String text = "You should discard " + round.getHumanHand().elementAt(hint).serializableString() +" because it helps in making your score lower.\n";
                    textBox.setText(text);
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
                if(round.getNextPlayer().equals("computer")) {
                    textBox.setText(round.playComputer());
                    changePlayer();
                    refreshLayout();
                }
            }
        });

        Button arrangeBtn = findViewById(R.id.arrangeHand);
        arrangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBox.setText(round.arrangeHand());
                refreshLayout();
            }
        });

        final Integer gameState = getIntent().getIntExtra("state",1);

        if (gameState == 1) {
        // start a new game
            game = new Game(1);
            round = game.generateNewRound();
            round.init();
            String turn = getIntent().getStringExtra("turn");
            String result = turn.equals("human") ? "You won the toss" : "You lost the toss";
            makeToast(result);
            round.setNextPlayer(turn);
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
    }

    private void changePlayer(){
        if (lastTurn) {
            // display message box about the score stats
            AlertDialog.Builder roundStat = new AlertDialog.Builder(this);
            roundStat.setTitle("*Round Ended*")
                    // this ends the round
                    .setMessage(round.endRound())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (game.gameEnded()) {
                                endGame();
                            }
                        }
                    })
                    .show();

            // stores the losing player
            String loser = round.getNextPlayer();
            // stores the player who went out
            String startingPlayer = loser.equals("human") ? "computer" : "human";

            // start a new round
            round = game.generateNewRound();
            round.init();
            round.setNextPlayer(startingPlayer);
            refreshLayout();

            lastTurn = false;
            return;
        }
        if (round.canCurrPlayerGoOut()) {
            TextView text = findViewById(R.id.hintView);
            String str = round.getNextPlayer() + " HAS GONE OUT!!";
            text.append(str);
            lastTurn = true;
            round.changePlayer();
        }
        else {
            round.changePlayer();
        }
    }

    private void endGame() {
        Intent intent = new Intent(MainActivity.this, EndActivity.class);
        intent.putExtra("computer", round.getComputerScore());
        intent.putExtra("human", round.getHumanScore());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finishAffinity();
        startActivity(intent);
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

        String nextPlayer = round.getNextPlayer();
        if(nextPlayer.equals("computer")) {
            disableHumanButtons();
            enableComputerButtons();
        }
        else {
            disableComputerButtons();
            enableHumanButtons();
        }
    }

    private void disableHumanButtons() {
        findViewById(R.id.discardButton).setEnabled(false);
        findViewById(R.id.drawButton).setEnabled(false);
        findViewById(R.id.hintButton).setEnabled(false);
        findViewById(R.id.arrangeHand).setEnabled(false);
    }

    private void disableComputerButtons(){
        findViewById(R.id.playButton).setEnabled(false);
    }

    private void enableHumanButtons() {
        findViewById(R.id.discardButton).setEnabled(true);
        findViewById(R.id.drawButton).setEnabled(true);
        findViewById(R.id.hintButton).setEnabled(true);
        findViewById(R.id.arrangeHand).setEnabled(true);
    }

    private void enableComputerButtons() {
        findViewById(R.id.playButton).setEnabled(true);
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
                int t_id = pileType.equals("draw") ? 31 : 41;
                cardView.setId(t_id);
                cardView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCardsBackground();
                        view.setBackgroundColor(Color.YELLOW);
                        selectedPile = pileType;
//                        makeToast();
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
                cardView.setId(i);
                cardView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearCardsBackground();
                        cardView.setBackgroundColor(Color.YELLOW);
                        selectedHandCard = cardView.getId();
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
