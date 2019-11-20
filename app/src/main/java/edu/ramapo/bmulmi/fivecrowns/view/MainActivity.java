/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
package edu.ramapo.bmulmi.fivecrowns.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    // stores the index of the selected human hand card
    private int selectedHandCard;
    // stores boolean value as true when human draws a card, else false
    private boolean cardDrawn;
    // stores boolean value as true when human discards a card, else false
    private boolean cardDiscarded;
    // stores boolean value as true when a player goes out first
    private boolean lastTurn;
    // stores the Hint Box of the view
    private TextView textBox;
    // stores boolean value as true when a round ends
    private boolean roundEnded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the default values for the variables
        lastTurn = false;
        cardDiscarded = true;
        cardDrawn = false;
        roundEnded = false;
        selectedPile = "";
        selectedHandCard = -1;
        textBox = findViewById(R.id.hintView);
        textBox.setMovementMethod(new ScrollingMovementMethod());

        // --------------------------- Button OnClick Listeners -----------------------------------
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
                    String text = "You should choose " + hint + " pile because it helps in making runs or books.\n";
                    textBox.append("\n");
                    textBox.append(text);
                }
                else if (cardDrawn) {
                    // ask for which card to discard
                    int hint = round.getDiscardHint();
                    ImageView card = findViewById(hint);
                    card.setBackgroundColor(Color.CYAN);
                    String text = "You should discard " + round.getHumanHand().elementAt(hint).serializableString() +" because it helps in making your score lower.\n";
                    textBox.append("\n");
                    textBox.append(text);
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
                    textBox.append("\n");
                    textBox.append(round.playComputer());
                    changePlayer();
                    refreshLayout();
                }
            }
        });

        Button arrangeBtn = findViewById(R.id.arrangeHand);
        arrangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textBox.append("\n");
                textBox.append(round.arrangeHand("human"));
                refreshLayout();
            }
        });

        Button nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.gameEnded()) {
                    endGame();
                }
                // stores the losing player
                String loser = round.getNextPlayer();
                // stores the player who went out
                String startingPlayer = loser.equals("human") ? "computer" : "human";

                // start a new round
                round = game.generateNewRound();
                round.init();
                round.setNextPlayer(startingPlayer);
                roundEnded = false;

                refreshLayout();
            }
        });
        // --------------------------- Game State -----------------------------------
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

    /**
     * handles altering players in the game
     * handles end of round and game states
     */
    private void changePlayer(){
        if (lastTurn) {
            endRound();
            return;
        }
        if (round.canCurrPlayerGoOut()) {
            AlertDialog.Builder roundStat = new AlertDialog.Builder(this);
            String body = round.getNextPlayer() + " has gone out.";
            roundStat.setTitle("*Last Turn*").setMessage(body)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    })
                    .show();

            lastTurn = true;
            // arrange the hand of the going out player
            StringBuilder txt = new StringBuilder();
            txt.append(round.arrangeHand(round.getNextPlayer())).append("\n");
            round.changePlayer();
            textBox.append("\n");
            textBox.append(txt);
        }
        else {
            round.changePlayer();
        }
    }

    /**
     * ends the round and generates a new one
     * also checks for the end of the game and
     * changes the activity accordingly
     */
    private void endRound() {
        // display message box about the score stats
        AlertDialog.Builder roundStat = new AlertDialog.Builder(this);
        roundStat.setTitle("*Round Ended*")
                // this ends the round
                .setMessage(round.endRound())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
        // disable all buttons
        disableHumanButtons();
        disableComputerButtons();

        // enable the next round button
        findViewById(R.id.next).setEnabled(true);

        StringBuilder txt = new StringBuilder();
        // display the hand of last player
        txt.append(round.arrangeHand(round.getNextPlayer()));
        textBox.append("\n");
        textBox.append(txt);
        lastTurn = false;
        roundEnded = true;
    }

    /**
     * starts the EndActivity and ends the game
     */
    private void endGame() {
        Intent intent = new Intent(MainActivity.this, EndActivity.class);
        intent.putExtra("computer", round.getComputerScore());
        intent.putExtra("human", round.getHumanScore());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finishAffinity();
        startActivity(intent);
    }

    /**
     * handles the toast
     * @param tst String value, will be used in making the toast
     */
    public void makeToast(String tst) {
        Toast.makeText(getApplicationContext(), tst, Toast.LENGTH_SHORT).show();
    }

    /**
     * refreshes the entire layout of the Main Activity
     */
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
        if(roundEnded) {
            disableHumanButtons();
            disableComputerButtons();
            findViewById(R.id.next).setEnabled(true);
        }
        else {
            findViewById(R.id.next).setEnabled(false);
            if(nextPlayer.equals("computer")) {
                disableHumanButtons();
                enableComputerButtons();
            }
            else {
                disableComputerButtons();
                enableHumanButtons();
            }
        }
    }

    /**
     * disables all the buttons used by human
     */
    private void disableHumanButtons() {
        findViewById(R.id.discardButton).setEnabled(false);
        findViewById(R.id.drawButton).setEnabled(false);
        findViewById(R.id.hintButton).setEnabled(false);
        findViewById(R.id.arrangeHand).setEnabled(false);
    }

    /**
     * disables the "play" button
     */
    private void disableComputerButtons(){
        findViewById(R.id.playButton).setEnabled(false);
    }

    /**
     * enables all the buttons used by human
     */
    private void enableHumanButtons() {
        findViewById(R.id.discardButton).setEnabled(true);
        findViewById(R.id.drawButton).setEnabled(true);
        findViewById(R.id.hintButton).setEnabled(true);
        findViewById(R.id.arrangeHand).setEnabled(true);
    }

    /**
     * enables the "play" button
     */
    private void enableComputerButtons() {
        findViewById(R.id.playButton).setEnabled(true);
    }

    /**
     * Adds card image views to the layout passed in as param
     * @param layout the Linear Layout where cards are to be added
     * @param pile the Collection of Card objects that is matched to its corresponding drawable and displayed
     * @param pileType String value that holds "draw" or "discard" pile as values
     */
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

    /**
     * Adds card image views to the hand layout passed in as param
     * @param layout the Linear Layout where the hand cards to be added
     * @param pile the Collection of Card objects that is matched to its drawable and displayed
     * @param human boolean value that holds true if the layout is of human, must set onClick listeners for human
     */
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
                        // makeToast(selectedHandCard + "");
                    }
                });
            }
            layout.addView(cardView);
            i++;
        }
    }

    /**
     * Clears the background of all the clickable cards
     */
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

    /**
     * checks if the device is writable for loading serialized files
     * @return boolean value, that holds true if the device is writable
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * checks if the device is readable for loading serialized files
     * @return boolean value, holds true if the device is readable
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     * gets serialized string from model  and saves it to a filename
     * in the storage of the device
     */
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

    /**
     * generates a filename not yet used by the program to save
     * the serialized file
     * @return File object, where the game is saved later
     */
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
