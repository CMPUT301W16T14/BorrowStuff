package t14.com.GameRentals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Allows users to bid on a game.
 */
public class BidOnGameActivity extends Activity {
    private Game game;
    private ArrayList<Game> returnedGames;
    private double rate;
    private User currentUser;
    private final String FILENAME = "searchedResults.sav";

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.bid_on_game);
        Button okButton = (Button)findViewById(R.id.OKButton);
        loadFromFile();
        currentUser = UserController.getCurrentUser();
        int gamePosition = getIntent().getExtras().getInt("gamePosition");
        game = returnedGames.get(gamePosition);
        final EditText bidMoney = (EditText) findViewById(R.id.bidMoney);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(BidOnGameActivity.this);
                adb.setTitle("Bidding");
                adb.setMessage("Are you sure to bid it?");
                adb.setCancelable(true);
                adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //////////TODO:GET RATE VALUE
                        if (currentUser.getUserName().equals(game.getOwner())){
                            Toast.makeText(BidOnGameActivity.this,"can't bid your own game",Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            rate = Double.parseDouble(bidMoney.getText().toString());
                            game.getBidList().AddBid(currentUser, rate);
                            game.setStatus(1);
                            currentUser.getBiddedItems().addGame(game.getGameID());
                            ElasticSearchUsersController.EditUserTask ese = new ElasticSearchUsersController.EditUserTask();
                            ese.execute(currentUser);
                            ElasticsearchGameController.EditGameTask editGameTask = new ElasticsearchGameController.EditGameTask();
                            editGameTask.execute(game);
                            finish();
                        }
                    }
                });

                adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                adb.show();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        loadFromFile();
    }

    private void loadFromFile(){
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            // Took from https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html Jan-21-2016
            Type listType = new TypeToken<ArrayList<Game>>() {}.getType();
            returnedGames = gson.fromJson(in, listType);
        } catch(IOException e){
            throw new RuntimeException();
        }
    }

}
