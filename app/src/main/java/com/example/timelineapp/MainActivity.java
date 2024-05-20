package com.example.timelineapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.timelineapp.DataBase.XP;
import com.example.timelineapp.DataBase.XPDatabase;
import com.example.timelineapp.databinding.LayoutMainLandscapeBinding;
import com.example.timelineapp.databinding.CustomActionBarBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LayoutMainLandscapeBinding activityMainBinding;
    private CustomActionBarBinding customActionBarBinding;
    TextView selectedEventDescription;
    Button selectedYearBtn;

    String XP_TEXT_EXTRA = "XP_TEXT_EXTRA";

    public static final int MAX_EVENTS_SIZE = 4;
    Timeline chosenTimeline;
    GameViewModel gameViewModel;

    XPDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);

        } else {

            setContentView(R.layout.layout_main_landscape);
            gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

            Intent intent = getIntent();

            activityMainBinding = LayoutMainLandscapeBinding.inflate(getLayoutInflater());

            setContentView(activityMainBinding.getRoot());

            customActionBarBinding = CustomActionBarBinding.bind(activityMainBinding.getRoot().findViewById(R.id.customActionBar));

            db = XPDatabase.getInstance(getApplicationContext());

            if (db.xpDao().getUserXP(GameViewModel.USER_ID) == null){
                db.xpDao().insert(new XP(0));
            }

            setAllButtonsClickListener();
            setAllTimeLinesDescriptionClickListener();

            if (gameViewModel.getHearts() != null){
                customActionBarBinding.tvLivesLeft.setText(gameViewModel.getHearts().toString());
            }

            if (gameViewModel.getXp() != null){
                customActionBarBinding.xpText.setText(gameViewModel.getXp().toString() + " XP");
            }

            if (gameViewModel.getXp() == 0 && intent != null && intent.getExtras() != null && intent.getExtras().get(XP_TEXT_EXTRA) != null) {
                customActionBarBinding.xpText.setText(intent.getExtras().get(XP_TEXT_EXTRA).toString());
            }

            TimelineList timelineList = null;
            String json = loadJSONFromAsset("timelines.json");
            if (json != null) {
                Gson gson = new Gson();
                timelineList = gson.fromJson(json, TimelineList.class);
            }

            setTimeLinesDescriptionAndYearButtonsText(timelineList);

            activityMainBinding.btnStartAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                }
            });

            Log.e("TAG", "Error reading JSON from asset " + timelineList);
        }
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            json = sb.toString();
        } catch (IOException e) {
            Log.e("MEHDI-DEBUG", "Error reading JSON from asset", e);
        }
        return json;
    }


    private boolean checkForCorrectMatch() {

        if (selectedEventDescription != null && selectedYearBtn != null) {
            int chosenEventDescIndex = chosenTimeline.getTimeLineCorrectAnswers().indexOf(selectedEventDescription.getText().toString());
            int chosenYearIndex = chosenTimeline.getTimelinePoints().indexOf(Integer.parseInt(selectedYearBtn.getText().toString()));

            if (chosenYearIndex == chosenEventDescIndex) {
                gameViewModel.addCorrectAnsweredEventInList(chosenYearIndex);
                new AlertDialog.Builder(this).setMessage("Correct!  ✔️").create().show();
                selectedEventDescription.setVisibility(View.GONE);
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.correct);
                mediaPlayer.start();
                return true;
            } else {
                new AlertDialog.Builder(this).setMessage("Wrong!  ❌").create().show();
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.lost);
                mediaPlayer.start();
                return false;
            }
        }
        return false;
    }

    private void setButtonClickListener(Button btn) {
        btn.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DROP:
                        btn.performClick();
                        break;
                }
                return true; // return always true : https://stackoverflow.com/questions/21670807/ondrag-cannot-receive-dragevent-action-drop
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedYearBtn = (Button) view;
                checkForCorrectMatch();
                if (checkForCorrectMatch()) {
                    selectedYearBtn.setVisibility(View.GONE);
                    int totalXP = Integer.parseInt(customActionBarBinding.xpText.getText().toString().replace(" XP", ""));
                    totalXP += 10;
                    gameViewModel.setXp(totalXP);


                    XP userXP = db.xpDao().getUserXP(GameViewModel.USER_ID);
                    boolean newHighXpReached = userXP != null && userXP.experiencePoints < totalXP;
                    if (newHighXpReached){
                        db.xpDao().updateUserXP(GameViewModel.USER_ID, totalXP);
                    }


                    customActionBarBinding.xpText.setText(String.valueOf(totalXP + " XP"));

                    if (totalXP % 40 == 0) {
                        if (newHighXpReached){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()).setMessage(" ⭐⭐⭐ New Highest XP Reached! CONGRATULATION ! Highest Score :"+ userXP.getExperiencePoints());
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();

                                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                                    intent.putExtra(XP_TEXT_EXTRA, customActionBarBinding.xpText.getText().toString());
                                    startActivity(intent);
                                }
                            });
                            dialog.show();
                        }
                        else {


                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()).setMessage("CONGRATULATION! you won the round!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();

                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                intent.putExtra(XP_TEXT_EXTRA, customActionBarBinding.xpText.getText().toString());
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                    }
                } else {
                    int livesLeft = Integer.parseInt(customActionBarBinding.tvLivesLeft.getText().toString());
                    livesLeft -= 1;
                    gameViewModel.setHearts(livesLeft);
                    if (livesLeft == -1) {
                        new AlertDialog.Builder(view.getContext()).setMessage("Unfortunately! You Lost the Round!").create().show();
                        finish();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    customActionBarBinding.tvLivesLeft.setText(String.valueOf(livesLeft));
                }
            }
        });
    }

    private void setAllButtonsClickListener() {
        ArrayList<Button> listOfYearButtons = new ArrayList<>();
        listOfYearButtons.add(activityMainBinding.button1Timeline);
        listOfYearButtons.add(activityMainBinding.button2Timeline);
        listOfYearButtons.add(activityMainBinding.button3Timeline);
        listOfYearButtons.add(activityMainBinding.button4Timeline);


        for (int i = 0; i < MAX_EVENTS_SIZE; i++) {
            if (gameViewModel.isEventAlreadyAnsweredCorrectly(i)) {
                listOfYearButtons.get(i).setVisibility(View.GONE);
            } else {
                setButtonClickListener(listOfYearButtons.get(i));
            }

        }

    }

    private void setAllTimeLinesDescriptionClickListener() {
        ArrayList<TextView> listOfYearEventDescription = new ArrayList<>();
        listOfYearEventDescription.add(activityMainBinding.timelineDesc1);
        listOfYearEventDescription.add(activityMainBinding.timelineDesc2);
        listOfYearEventDescription.add(activityMainBinding.timelineDesc3);
        listOfYearEventDescription.add(activityMainBinding.timelineDesc4);


        for (int i = 0; i < MAX_EVENTS_SIZE; i++) {
            if (gameViewModel.isEventAlreadyAnsweredCorrectly(i)) {
                listOfYearEventDescription.get(i).setVisibility(View.GONE);
            } else {
                setTimeLineDescriptionClickListener(listOfYearEventDescription.get(i));
            }
        }
    }

    private void setTimeLineDescriptionClickListener(TextView tv) {
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipData.Item item = new ClipData.Item(tv.getText());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(tv.getText(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(tv);

                selectedEventDescription = tv;

                tv.startDragAndDrop(data, shadowBuilder, tv, 0);
                return true;
            }
        });
    }

    private void setTimeLinesDescriptionAndYearButtonsText(TimelineList timelineList) {
        Random random = new Random();

        int randomNumber = random.nextInt(timelineList.getTimelines().size());

        if (gameViewModel.chosenTimeline != null && !(gameViewModel.getXp() % 40 == 0)) {
            chosenTimeline = gameViewModel.chosenTimeline;
        } else {
            chosenTimeline = timelineList.getTimelines().get(randomNumber); //chosen timeline to show
            gameViewModel.setChosenTimeline(chosenTimeline);
        }

        customActionBarBinding.timelineTitle.setText(chosenTimeline.getTitle());// set timeline title

        shuffleAndSetTimelineDescriptionsTextsV2(chosenTimeline);

        activityMainBinding.button1Timeline.setText(chosenTimeline.getTimelinePoints().get(0).toString());
        activityMainBinding.button2Timeline.setText(chosenTimeline.getTimelinePoints().get(1).toString());
        activityMainBinding.button3Timeline.setText(chosenTimeline.getTimelinePoints().get(2).toString());
        activityMainBinding.button4Timeline.setText(chosenTimeline.getTimelinePoints().get(3).toString());
    }

    private void shuffleAndSetTimelineDescriptionsTextsV2(Timeline chosenTimeline) {
        ArrayList<Integer> alreadySetTimelineDescriptionIndices = new ArrayList<>(gameViewModel.correctAnsweredEvents);
        ArrayList<TextView> listOfYearEventDescription = new ArrayList<>();
        if (!alreadySetTimelineDescriptionIndices.contains(0)){
            listOfYearEventDescription.add(activityMainBinding.timelineDesc1);
        }
        if (!alreadySetTimelineDescriptionIndices.contains(1)){
            listOfYearEventDescription.add(activityMainBinding.timelineDesc2);
        }
        if (!alreadySetTimelineDescriptionIndices.contains(2)){
            listOfYearEventDescription.add(activityMainBinding.timelineDesc3);
        }
        if (!alreadySetTimelineDescriptionIndices.contains(3)){
            listOfYearEventDescription.add(activityMainBinding.timelineDesc4);
        }

        Random random = new Random();
        for (TextView timeLineDescriptionTv : listOfYearEventDescription) {
            int randomNumber;
            do {
                randomNumber = random.nextInt(MAX_EVENTS_SIZE);
            } while (alreadySetTimelineDescriptionIndices.contains(randomNumber) && alreadySetTimelineDescriptionIndices.size() < MAX_EVENTS_SIZE);

            if (alreadySetTimelineDescriptionIndices.size() < MAX_EVENTS_SIZE) {

                alreadySetTimelineDescriptionIndices.add(randomNumber);
                timeLineDescriptionTv.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));
            }
        }

    }

    private void shuffleAndSetTimelineDescriptionsTexts(Timeline chosenTimeline) {

        ArrayList<Integer> alreadySetTimelineDescriptionIndices = new ArrayList<>(gameViewModel.correctAnsweredEvents);
        while (alreadySetTimelineDescriptionIndices.size() != MAX_EVENTS_SIZE) {
            Random random = new Random();

            int randomNumber = random.nextInt(MAX_EVENTS_SIZE);
            if (!alreadySetTimelineDescriptionIndices.contains(randomNumber)) {
                alreadySetTimelineDescriptionIndices.add(randomNumber);

                if (alreadySetTimelineDescriptionIndices.size() == 1) {
                    activityMainBinding.timelineDesc1.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));
                }

                if (alreadySetTimelineDescriptionIndices.size() == 2) {
                    activityMainBinding.timelineDesc2.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));
                }

                if (alreadySetTimelineDescriptionIndices.size() == 3) {
                    activityMainBinding.timelineDesc3.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));
                }

                if (alreadySetTimelineDescriptionIndices.size() == 4) {
                    activityMainBinding.timelineDesc4.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));
                }
            }
        }
    }
}