package com.joevirn.joyce.melanie.ibm2105_group3_code;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private HistoryListOpenHelper mDb;
    private HistoryListAdapter mAdapter;
    Button mBtnStart;
    Button mBtnHit;
    Button mBtnStand;
    ImageView mPlayerCard1;
    ImageView mPlayerCard2;
    ImageView mPlayerCard3;
    ImageView mPlayerCard4;
    ImageView mPlayerCard5;
    ImageView mPlayerCard6;
    ImageView mPlayerCard7;
    ImageView mPlayerCard8;
    ImageView mDealerCard1;
    ImageView mDealerCard2;
    ImageView mDealerCard3;
    ImageView mDealerCard4;
    ImageView mDealerCard5;
    ImageView mDealerCard6;
    ImageView mDealerCard7;
    ImageView mDealerCard8;
    TextView mTvRound;
    //strings that will help the assignCard function determine who the card should be given to
    String PLAYER = "player";
    String DEALER = "dealer";
    public static final int HISTORY_EDIT = 1;
    public static final int HISTORY_ADD = -1;
    //variable for saving the maximum number of card the player and dealer can have
    int limit;
    boolean recordHistory;
    //variables for tracking the number of cards dealt
    int numOfTotalCards;
    int numOfPlayerCards;
    int numOfDealerCards;
    //arrays for saving the cards dealt
    int allAssignedCards[] = new int[16];
    int playerCards[] = new int[8];
    int dealerCards[] = new int[8];
    //variables for saving the total value of cards held by the player and dealer
    int playerValue;
    int dealerValue;
    //game session statistics
    int roundsPlayed;
    int roundsWon;
    String date;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = new HistoryListOpenHelper(this);
        SQLiteDatabase d = mDb.getReadableDatabase();

        //get the max card and record history settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        limit = Integer.valueOf(sharedPref.getString("list_maxCards", "8"));
        recordHistory = sharedPref.getBoolean("switch_history", true);
        //inform player about the current settings
        Toast.makeText(this, "Maximum Number of Cards: " + limit +
                "\nRecord History: " + (recordHistory ? "On" : "Off"), Toast.LENGTH_SHORT).show();

        //initialise game session variables
        roundsPlayed = 0;
        roundsWon = 0;
        date = "";
        time = "";

        mBtnHit = findViewById(R.id.btnHit);
        mBtnStand = findViewById(R.id.btnStand);
        mBtnStart = findViewById(R.id.btnStart);
        mPlayerCard1 = findViewById(R.id.player_card1);
        mPlayerCard2 = findViewById(R.id.player_card2);
        mPlayerCard3 = findViewById(R.id.player_card3);
        mPlayerCard4 = findViewById(R.id.player_card4);
        mPlayerCard5 = findViewById(R.id.player_card5);
        mPlayerCard6 = findViewById(R.id.player_card6);
        mPlayerCard7 = findViewById(R.id.player_card7);
        mPlayerCard8 = findViewById(R.id.player_card8);
        mDealerCard1 = findViewById(R.id.dealer_card1);
        mDealerCard2 = findViewById(R.id.dealer_card2);
        mDealerCard3 = findViewById(R.id.dealer_card3);
        mDealerCard4 = findViewById(R.id.dealer_card4);
        mDealerCard5 = findViewById(R.id.dealer_card5);
        mDealerCard6 = findViewById(R.id.dealer_card6);
        mDealerCard7 = findViewById(R.id.dealer_card7);
        mDealerCard8 = findViewById(R.id.dealer_card8);
        mTvRound = findViewById(R.id.tvRound);

        //set current round number
        mTvRound.setText("Round " + (roundsPlayed + 1));
    }

    //inflate the option menu on the activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    //response to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_history :
                //open history menu
                Intent intent_history = new Intent(this, History.class);
                startActivity(intent_history);
                break;
            case R.id.menu_settings :
                //if user has already begun playing, inform them that their current game session records will be reset
                if (roundsPlayed > 0 || numOfTotalCards > 0) {
                    Toast.makeText(this, "Current Game Session Records Discarded", Toast.LENGTH_SHORT).show();
                }
                //open settings menu
                Intent intent_settings = new Intent(this, SettingActivity.class);
                startActivity(intent_settings);
                break;
            case R.id.menu_logout :
                //send player back to login page
                Toast.makeText(this, "You are now logged out!", Toast.LENGTH_SHORT).show();
                Intent intent_login = new Intent(this, Login.class);
                startActivity(intent_login);
                break;
        }

        return true;
    }

    public void btnStart_clicked(View view) {
        //if this is the player's first round, get the game session's start date and time
        if (roundsPlayed == 0) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            int seconds = c.get(Calendar.SECOND);

            date = day + "/" + (month + 1) + "/" + year;
            time = hours + ":" + minutes + ":" + seconds;
        }

        //reset the player's and dealer's hand
        reset();

        //assign 2 cards each to the player and dealer
        for (int i = 0; i < 2; i++) {
            assignCard(PLAYER);
            assignCard(DEALER);
        }

        //calculate both the total value of the player's and dealer's cards
        playerValue = calculateHandValue(playerCards, numOfPlayerCards);
        dealerValue = calculateHandValue(dealerCards, numOfDealerCards);

        //show the player their cards
        displayCard(mPlayerCard1, playerCards[0]);
        displayCard(mPlayerCard2, playerCards[1]);

        //check whether player or dealer won with a blackjack
        if (playerValue == 21 && dealerValue < 21) {
            revealDealerHand();
            displayResults(true, "Player got a blackjack");
        }
        else if (playerValue < 21 && dealerValue == 21) {
            revealDealerHand();
            displayResults(false, "Dealer got a blackjack");
        }
        else if (playerValue == 21 && dealerValue == 21){
            revealDealerHand();
            displayResults(true, "Player and Dealer got a blackjack");
        }
        else {
            //if there is no blackjack, enable the hit and stand button, disable the start button
            mBtnHit.setVisibility(View.VISIBLE);
            mBtnStand.setVisibility(View.VISIBLE);
            mBtnStart.setVisibility(View.INVISIBLE);
        }
    }

    public void btnHit_clicked(View view) {
        //deal a card to the player
        assignCard(PLAYER);

        //display the appropriate card
        switch(numOfPlayerCards) {
            case 3:
            {
                mPlayerCard3.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard3, playerCards[numOfPlayerCards - 1]);
            }
            break;
            case 4:
            {
                mPlayerCard4.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard4, playerCards[numOfPlayerCards - 1]);
            }
            break;
            case 5:
            {
                mPlayerCard5.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard5, playerCards[numOfPlayerCards - 1]);
            }
            break;
            case 6:
            {
                mPlayerCard6.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard6, playerCards[numOfPlayerCards - 1]);
            }
            break;
            case 7:
            {
                mPlayerCard7.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard7, playerCards[numOfPlayerCards - 1]);
            }
            break;
            case 8:
            {
                mPlayerCard8.setVisibility(View.VISIBLE);
                displayCard(mPlayerCard8, playerCards[numOfPlayerCards - 1]);
            }
            break;
        }

        //if the maximum number of cards have been reached
        if (numOfPlayerCards >= limit) {
            //calculate the total value of the player's cards
            playerValue = calculateHandValue(playerCards, numOfPlayerCards);

            //player automatically wins if it's less than 17
            if (playerValue < 17) {
                revealDealerHand();

                mBtnHit.setVisibility(View.INVISIBLE);
                mBtnStand.setVisibility(View.INVISIBLE);
                mBtnStart.setVisibility(View.VISIBLE);

                displayResults(true, "Player has reached card limit and total card value is below 17");
            }
            //player automatically loses if it's more than 21
            else if (playerValue > 21) {
                revealDealerHand();

                mBtnHit.setVisibility(View.INVISIBLE);
                mBtnStand.setVisibility(View.INVISIBLE);
                mBtnStart.setVisibility(View.VISIBLE);

                displayResults(false, "Player has reached card limit and total card value is above 21");
            }
            //disable the hit button if it's between 17 and 21
            else {
                mBtnHit.setEnabled(false);
            }
        }
    }

    public void btnStand_clicked(View view) {
        //disable the hit and stand button, enable the start button
        mBtnHit.setVisibility(View.INVISIBLE);
        mBtnStand.setVisibility(View.INVISIBLE);
        mBtnStart.setVisibility(View.VISIBLE);

        //allow dealer to 'hit' and 'stand'
        dealerAction();
    }

    public void reset() {
        //set current round number
        mTvRound.setText("Round " + (roundsPlayed + 1));

        //reset all cards assigned
        for (int i = 0; i < 16; i++) {
            allAssignedCards[i] = 0;
        }

        //reset all cards assigned to player
        for (int i = 0; i < 8; i++) {
            playerCards[i] = 0;
        }

        //reset all cards assigned to dealer
        for (int i = 0; i < 8; i++) {
            dealerCards[i] = 0;
        }

        //reset total card count
        numOfTotalCards = 0;
        numOfPlayerCards = 0;
        numOfDealerCards = 0;

        //reset the player's hand appearance
        mPlayerCard1.setImageResource(R.drawable.backofcard);
        mPlayerCard2.setImageResource(R.drawable.backofcard);
        mPlayerCard3.setVisibility(View.INVISIBLE);
        mPlayerCard4.setVisibility(View.INVISIBLE);
        mPlayerCard5.setVisibility(View.INVISIBLE);
        mPlayerCard6.setVisibility(View.INVISIBLE);
        mPlayerCard7.setVisibility(View.INVISIBLE);
        mPlayerCard8.setVisibility(View.INVISIBLE);

        //reset the dealer's hand appearance
        mDealerCard1.setImageResource(R.drawable.backofcard);
        mDealerCard2.setImageResource(R.drawable.backofcard);
        mDealerCard3.setVisibility(View.INVISIBLE);
        mDealerCard4.setVisibility(View.INVISIBLE);
        mDealerCard5.setVisibility(View.INVISIBLE);
        mDealerCard6.setVisibility(View.INVISIBLE);
        mDealerCard7.setVisibility(View.INVISIBLE);
        mDealerCard8.setVisibility(View.INVISIBLE);

        //enable the hit button (in case it was disabled in the previous round)
        mBtnHit.setEnabled(true);
    }

    public void displayResults(boolean playerWin, String reason) {
        //increase the number of rounds played
        roundsPlayed++;
        //increase the number of rounds won if the player won
        if (playerWin) {
            roundsWon++;
        }
        //calculate the player's winning rate
        double winningRate = Double.valueOf(roundsWon) / roundsPlayed * 100;

        //indicate round winner with the title
        String title = "Round " + roundsPlayed + " - " + (playerWin ? "Player Won" : "Player Lost");

        //create alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);

        //display current game session statistics
        String alertMessage = String.format("%s\n\nTotal Round(s) Played: %d\nTotal Round(s) Won: %d\nWinning Rate(%%): %.2f", reason, roundsPlayed, roundsWon, winningRate);
        alert.setMessage(alertMessage);
        //if user chooses to proceed,
        alert.setPositiveButton("Next Round", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //inform the user using a Toast message
                Toast.makeText(MainActivity.this, "Starting Next Round!", Toast.LENGTH_SHORT).show();
                //reset the player's and dealer's hand
                reset();
            }
        });
        //if user chooses to end the current session,
        alert.setNegativeButton("End Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //inform the user using a Toast message
                Toast.makeText(MainActivity.this, "Game Session Ended!", Toast.LENGTH_SHORT).show();
                //reset the game session variables
                roundsPlayed = 0;
                roundsWon = 0;
                date = "";
                time = "";
                //reset the player's and dealer's hand
                reset();
            }
        });

        //display the alert dialog
        alert.create().show();
    }

    public void dealerAction() {
        //variable to indicate whether dealer has the maximum number of cards allowed
        boolean limitReached = false;

        //repeat as long as the total value of dealer's hand is less than 17
        while (dealerValue < 17) {
            //assign a card to the dealer
            assignCard(DEALER);

            //show a new downward facing card
            switch(numOfDealerCards) {
                case 3:
                {
                    mDealerCard3.setVisibility(View.VISIBLE);
                    mDealerCard3.setImageResource(R.drawable.backofcard);
                }
                break;
                case 4:
                {
                    mDealerCard4.setVisibility(View.VISIBLE);
                    mDealerCard4.setImageResource(R.drawable.backofcard);
                }
                break;
                case 5:
                {
                    mDealerCard5.setVisibility(View.VISIBLE);
                    mDealerCard5.setImageResource(R.drawable.backofcard);
                }
                break;
                case 6:
                {
                    mDealerCard6.setVisibility(View.VISIBLE);
                    mDealerCard6.setImageResource(R.drawable.backofcard);
                }
                break;
                case 7:
                {
                    mDealerCard7.setVisibility(View.VISIBLE);
                    mDealerCard7.setImageResource(R.drawable.backofcard);
                }
                break;
                case 8:
                {
                    mDealerCard8.setVisibility(View.VISIBLE);
                    mDealerCard8.setImageResource(R.drawable.backofcard);
                }
                break;
            }

            //calculate the dealer's card value
            dealerValue = calculateHandValue(dealerCards, numOfDealerCards);

            //check if the dealer has the maximum number of cards allowed
            if (numOfDealerCards >= limit) {
                //if so, stop the loop
                limitReached = true;
                break;
            }
        }

        //display the dealer's cards
        revealDealerHand();

        //dealer automatically wins if they have the maximum number of cards, but total card value is less than 17
        if (limitReached && dealerValue < 17) {
            displayResults(false, "Dealer has reached card limit and total card value is below 17");
        }
        //dealer automatically loses if they have the maximum number of cards, but total card value is more than 21
        else if (limitReached && dealerValue > 21) {
            displayResults(true, "Dealer has reached card limit and total card value is above 21");
        }
        else {
            //calculate the player's total card value
            playerValue = calculateHandValue(playerCards, numOfPlayerCards);

            //if both the player and dealer have card values between 17 and 21
            if (playerValue >= 17 && playerValue <= 21 && dealerValue >= 17 && dealerValue <= 21) {
                //player will win if they have the higher card value
                if (playerValue > dealerValue) {
                    displayResults(true, "Player has the higher card value");
                }
                //dealer will win if they have the higher card value
                else if (playerValue < dealerValue) {
                    displayResults(false, "Dealer has the higher card value");
                }
                //player will win in the event of a tie
                else {
                    displayResults(true, "Player and Dealer have the same card value");
                }
            }
            else {
                //player wins if their card value is between 17 and 21, but dealer's card value is out of range
                if ((playerValue >= 17 && playerValue <= 21) && (dealerValue < 17 || dealerValue > 21)) {
                    displayResults(true, "Dealer failed to obtain a card value between 17 and 21");
                }
                //dealer wins if their card value is between 17 and 21, but player's card value is out of range
                else if ((playerValue < 17 || playerValue > 21) && (dealerValue >= 17 && dealerValue <= 21)) {
                    displayResults(false, "Player failed to obtain a card value between 17 and 21");
                }
                //player wins if both player's and dealer's card value is out of range
                else {
                    displayResults(true, "Player and Dealer failed to obtain a card value between 17 and 21");
                }
            }
        }
    }

    public void revealDealerHand() {
        //loop until all cards have been shown
        for (int i = 0; i < numOfDealerCards; i++) {
            switch(i) {
                case 0:
                {
                    displayCard(mDealerCard1, dealerCards[i]);
                }
                break;
                case 1:
                {
                    displayCard(mDealerCard2, dealerCards[i]);
                }
                break;
                case 2:
                {
                    displayCard(mDealerCard3, dealerCards[i]);
                }
                break;
                case 3:
                {
                    displayCard(mDealerCard4, dealerCards[i]);
                }
                break;
                case 4:
                {
                    displayCard(mDealerCard5, dealerCards[i]);
                }
                break;
                case 5:
                {
                    displayCard(mDealerCard6, dealerCards[i]);
                }
                break;
                case 6:
                {
                    displayCard(mDealerCard7, dealerCards[i]);
                }
                break;
                case 7:
                {
                    displayCard(mDealerCard8, dealerCards[i]);
                }
                break;
            }
        }
    }

    public void assignCard(String target) {
        Random rand = new Random();
        int number = 0;
        boolean duplicate = false;

        do {
            duplicate = false;
            //generate a number between 1 and 52
            number = rand.nextInt(52) + 1;

            //check whether this number has already been taken
            for(int i = 0; i < numOfTotalCards; i++) {
                if (number == allAssignedCards[i]) {
                    duplicate = true;
                    break;
                }
            }
        } while(duplicate);

        //add the card to the all assigned cards array
        allAssignedCards[numOfTotalCards] = number;
        numOfTotalCards++;

        if (target.equals(PLAYER)) {
            //add the card to the player's hand
            playerCards[numOfPlayerCards] = number;
            numOfPlayerCards++;
        }
        else if (target.equals(DEALER)) {
            //add the card to the dealer's hand
            dealerCards[numOfDealerCards] = number;
            numOfDealerCards++;
        }
    }

    public int calculateHandValue(int hand[], int numOfCards) {
        int temp = 0;
        int totalValue = 0;
        boolean haveAce = false;
        int copy[] = new int[numOfCards];

        //create a copy of the hand (so that the original hand will not be affected)
        for (int i = 0; i < numOfCards; i++) {
            copy[i] = hand[i];
        }

        //sort the copy by descending order
        for (int i = 0; i < numOfCards; i++) {
            for (int j = i + 1; j < numOfCards; j++) {
                if (copy[j] > copy[i]) {
                    temp = copy[i];
                    copy[i] = copy[j];
                    copy[j] = temp;
                }
            }
        }

        //check whether the hand has any cards of ace suit
        for (int i = 0; i < numOfCards; i++) {
            if (copy[i] >= 1 && copy[i] <= 4) {
                haveAce = true;
                break;
            }
        }

        //if there is at least 1 ace
        if (haveAce) {
            //calculate the total value of non-ace cards
            for (int i = 0; i < (numOfCards - 1); i++) {
                totalValue += assignValue(copy[i]);
            }

            //determine the best value of the ace for the hand
            if (totalValue >= 1 && totalValue <= 10) {
                totalValue += 11;
            }
            else if (totalValue >= 11){
                totalValue += 1;
            }
        }
        else {
            //calculate the total value of all cards
            for (int i = 0; i < numOfCards; i++) {
                totalValue += assignValue(copy[i]);
            }
        }

        return totalValue;
    }

    public int assignValue(int number) {
        int value = 0;

        //assign the appropriate value to the card
        if (number >= 1 && number <= 4) {
            value = 1;
        }
        else if (number <= 8) {
            value = 2;
        }
        else if (number <= 12) {
            value = 3;
        }
        else if (number <= 16) {
            value = 4;
        }
        else if (number <= 20) {
            value = 5;
        }
        else if (number <= 24) {
            value = 6;
        }
        else if (number <= 28) {
            value = 7;
        }
        else if (number <= 32) {
            value = 8;
        }
        else if (number <= 36) {
            value = 9;
        }
        else if (number <= 52) {
            value = 10;
        }

        return value;
    }

    public void displayCard(ImageView card, int number) {
        //display the appropriate card
        switch(number) {
            case 1:
            {
                card.setImageResource(R.drawable.ace_of_clubs);
            }
            break;
            case 2:
            {
                card.setImageResource(R.drawable.ace_of_diamonds);
            }
            break;
            case 3:
            {
                card.setImageResource(R.drawable.ace_of_hearts);
            }
            break;
            case 4:
            {
                card.setImageResource(R.drawable.ace_of_spades);
            }
            break;
            case 5:
            {
                card.setImageResource(R.drawable.two_of_clubs);
            }
            break;
            case 6:
            {
                card.setImageResource(R.drawable.two_of_diamonds);
            }
            break;
            case 7:
            {
                card.setImageResource(R.drawable.two_of_hearts);
            }
            break;
            case 8:
            {
                card.setImageResource(R.drawable.two_of_spades);
            }
            break;
            case 9:
            {
                card.setImageResource(R.drawable.three_of_clubs);
            }
            break;
            case 10:
            {
                card.setImageResource(R.drawable.three_of_diamonds);
            }
            break;
            case 11:
            {
                card.setImageResource(R.drawable.three_of_hearts);
            }
            break;
            case 12:
            {
                card.setImageResource(R.drawable.three_of_spades);
            }
            break;
            case 13:
            {
                card.setImageResource(R.drawable.four_of_clubs);
            }
            break;
            case 14:
            {
                card.setImageResource(R.drawable.four_of_diamonds);
            }
            break;
            case 15:
            {
                card.setImageResource(R.drawable.four_of_hearts);
            }
            break;
            case 16:
            {
                card.setImageResource(R.drawable.four_of_spades);
            }
            break;
            case 17:
            {
                card.setImageResource(R.drawable.five_of_clubs);
            }
            break;
            case 18:
            {
                card.setImageResource(R.drawable.five_of_diamonds);
            }
            break;
            case 19:
            {
                card.setImageResource(R.drawable.five_of_hearts);
            }
            break;
            case 20:
            {
                card.setImageResource(R.drawable.five_of_spades);
            }
            break;
            case 21:
            {
                card.setImageResource(R.drawable.six_of_clubs);
            }
            break;
            case 22:
            {
                card.setImageResource(R.drawable.six_of_diamonds);
            }
            break;
            case 23:
            {
                card.setImageResource(R.drawable.six_of_hearts);
            }
            break;
            case 24:
            {
                card.setImageResource(R.drawable.six_of_spades);
            }
            break;
            case 25:
            {
                card.setImageResource(R.drawable.seven_of_clubs);
            }
            break;
            case 26:
            {
                card.setImageResource(R.drawable.seven_of_diamonds);
            }
            break;
            case 27:
            {
                card.setImageResource(R.drawable.seven_of_hearts);
            }
            break;
            case 28:
            {
                card.setImageResource(R.drawable.seven_of_spades);
            }
            break;
            case 29:
            {
                card.setImageResource(R.drawable.eight_of_clubs);
            }
            break;
            case 30:
            {
                card.setImageResource(R.drawable.eight_of_diamonds);
            }
            break;
            case 31:
            {
                card.setImageResource(R.drawable.eight_of_hearts);
            }
            break;
            case 32:
            {
                card.setImageResource(R.drawable.eight_of_spades);
            }
            break;
            case 33:
            {
                card.setImageResource(R.drawable.nine_of_clubs);
            }
            break;
            case 34:
            {
                card.setImageResource(R.drawable.nine_of_diamonds);
            }
            break;
            case 35:
            {
                card.setImageResource(R.drawable.nine_of_hearts);
            }
            break;
            case 36:
            {
                card.setImageResource(R.drawable.nine_of_spades);
            }
            break;
            case 37:
            {
                card.setImageResource(R.drawable.ten_of_clubs);
            }
            break;
            case 38:
            {
                card.setImageResource(R.drawable.ten_of_diamonds);
            }
            break;
            case 39:
            {
                card.setImageResource(R.drawable.ten_of_hearts);
            }
            break;
            case 40:
            {
                card.setImageResource(R.drawable.ten_of_spades);
            }
            break;
            case 41:
            {
                card.setImageResource(R.drawable.jack_of_clubs);
            }
            break;
            case 42:
            {
                card.setImageResource(R.drawable.jack_of_diamonds);
            }
            break;
            case 43:
            {
                card.setImageResource(R.drawable.jack_of_hearts);
            }
            break;
            case 44:
            {
                card.setImageResource(R.drawable.jack_of_spades);
            }
            break;
            case 45:
            {
                card.setImageResource(R.drawable.queen_of_clubs);
            }
            break;
            case 46:
            {
                card.setImageResource(R.drawable.queen_of_diamonds);
            }
            break;
            case 47:
            {
                card.setImageResource(R.drawable.queen_of_hearts);
            }
            break;
            case 48:
            {
                card.setImageResource(R.drawable.queen_of_spades);
            }
            break;
            case 49:
            {
                card.setImageResource(R.drawable.king_of_clubs);
            }
            break;
            case 50:
            {
                card.setImageResource(R.drawable.king_of_diamonds);
            }
            break;
            case 51:
            {
                card.setImageResource(R.drawable.king_of_hearts);
            }
            break;
            case 52:
            {
                card.setImageResource(R.drawable.king_of_spades);
            }
            break;
        }
    }
}