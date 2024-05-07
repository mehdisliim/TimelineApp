package com.example.timelineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView timelineDesc4, timelineDesc3, timelineDesc2, timelineDesc1, tv_lives_left, tv_total_xp, timeLineTitle;
    Button year1Btn, year2Btn, year3Btn, year4Btn, startAgainBtn;

    TextView selectedEventDescription;
    Button selectedYearBtn;

    String XP_TEXT_EXTRA = "XP_TEXT_EXTRA";

    private final int MAX_EVENTS_SIZE = 4;

    LinearLayout timelinesDescContainer;
    Timeline chosenTimeline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        year1Btn = findViewById(R.id.button1Timeline);
        year2Btn = findViewById(R.id.button2Timeline);
        year3Btn = findViewById(R.id.button3Timeline);
        year4Btn = findViewById(R.id.button4Timeline);
        startAgainBtn = findViewById(R.id.btn_start_again);

        tv_total_xp = findViewById(R.id.xp_text);
        tv_lives_left = findViewById(R.id.tv_lives_left);


        timelineDesc4 = findViewById(R.id.timelineDesc4);
        timelineDesc3 = findViewById(R.id.timelineDesc3);
        timelineDesc2 = findViewById(R.id.timelineDesc2);
        timelineDesc1 = findViewById(R.id.timelineDesc1);
        timelinesDescContainer = findViewById(R.id.timelines_desc_view_holder);

        timeLineTitle = findViewById(R.id.timeline_title);

        setAllButtonsClickListener();
        setAllTimeLinesDescriptionClickListener();

        if (intent != null && intent.getExtras() != null && intent.getExtras().get(XP_TEXT_EXTRA) != null){
            tv_total_xp.setText(intent.getExtras().get(XP_TEXT_EXTRA).toString());
        }


        TimelineList timelineList = null;
        String json = loadJSONFromAsset("timelines.json");
        if (json != null) {
            Gson gson = new Gson();
            timelineList = gson.fromJson(json, TimelineList.class);
        }

        setTimeLinesDescriptionAndYearButtonsText(timelineList);

        startAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        Log.e("TAG", "Error reading JSON from asset "+timelineList);
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


    private void applyBorder(TextView textView) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(5, Color.BLACK);
        borderDrawable.setColor(Color.TRANSPARENT);
        textView.setBackground(borderDrawable);

        textView.setTag("border_applied");

        selectedEventDescription = textView;

        removeCurSelectedDescBorder(textView);
    }

    private void removeCurSelectedDescBorder(TextView textView) {

        for(int i = 0; i < timelinesDescContainer.getChildCount(); i++){
            TextView tv = (TextView) timelinesDescContainer.getChildAt(i);
            Log.e("MEHDI-DEBUG", "removeCurSelectedDescBorder: "+tv.getTag()  );
            if (tv != null && tv.getTag() != null && tv.getTag().toString().equals("border_applied") && !tv.equals(textView) ){
                tv.setTag("");
                tv.setBackground(null);
            }
        }

    }

    private boolean checkForCorrectMatch() {

        if (selectedEventDescription != null && selectedYearBtn != null) {
            int chosenEventDescIndex = chosenTimeline.getTimeLineCorrectAnswers().indexOf(selectedEventDescription.getText().toString());
            int chosenYearIndex = chosenTimeline.getTimelinePoints().indexOf(Integer.parseInt(selectedYearBtn.getText().toString()));
            if (chosenYearIndex == chosenEventDescIndex) {
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

    private void setButtonClickListener(Button btn){
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
                if (checkForCorrectMatch()){
                    selectedYearBtn.setVisibility(View.GONE);
                    int totalXP = Integer.parseInt(tv_total_xp.getText().toString().replace(" XP", ""));
                    totalXP += 10;
                    tv_total_xp.setText(String.valueOf(totalXP + " XP"));

                    if (totalXP % 40 == 0){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()).setMessage("CONGRATULATION! you won the round!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();

                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                intent.putExtra(XP_TEXT_EXTRA, tv_total_xp.getText().toString());
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                }else {
                    int livesLeft = Integer.parseInt(tv_lives_left.getText().toString());
                    livesLeft -=1;
                    if(livesLeft == -1){
                        new AlertDialog.Builder(view.getContext()).setMessage("Unfortunately! You Lost the Round!").create().show();
                        finish();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    tv_lives_left.setText(String.valueOf(livesLeft));


                }
            }
        });
    }

    private void setAllButtonsClickListener() {
        setButtonClickListener(year1Btn);
        setButtonClickListener(year2Btn);
        setButtonClickListener(year3Btn);
        setButtonClickListener(year4Btn);
    }

    private void setAllTimeLinesDescriptionClickListener() {
        setTimeLineDescriptionClickListener(timelineDesc1);
        setTimeLineDescriptionClickListener(timelineDesc2);
        setTimeLineDescriptionClickListener(timelineDesc3);
        setTimeLineDescriptionClickListener(timelineDesc4);
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

    private void setTimeLinesDescriptionAndYearButtonsText(TimelineList timelineList){
        Random random = new Random();

        int randomNumber = random.nextInt(timelineList.getTimelines().size());

        chosenTimeline = timelineList.getTimelines().get(randomNumber); //choosen timeline to show

        timeLineTitle.setText(chosenTimeline.getTitle());// set timeline title

        shuffleAndSetTimelineDescriptionsTexts(chosenTimeline);


        year1Btn.setText(chosenTimeline.getTimelinePoints().get(0).toString());
        year2Btn.setText(chosenTimeline.getTimelinePoints().get(1).toString());
        year3Btn.setText(chosenTimeline.getTimelinePoints().get(2).toString());
        year4Btn.setText(chosenTimeline.getTimelinePoints().get(3).toString());
    }

    private void shuffleAndSetTimelineDescriptionsTexts(Timeline chosenTimeline){

        ArrayList<Integer> alreadySetTimelineDescriptionIndices = new ArrayList<Integer>();



       while(alreadySetTimelineDescriptionIndices.size() != MAX_EVENTS_SIZE){
            Random random = new Random();

            int randomNumber = random.nextInt(MAX_EVENTS_SIZE);
            if(!alreadySetTimelineDescriptionIndices.contains(randomNumber)){
                alreadySetTimelineDescriptionIndices.add(randomNumber);

                if (alreadySetTimelineDescriptionIndices.size() == 1){
                    timelineDesc1.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));

                }

                if (alreadySetTimelineDescriptionIndices.size() == 2){
                    timelineDesc2.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));

                }

                if (alreadySetTimelineDescriptionIndices.size() == 3){
                    timelineDesc3.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));

                }

                if (alreadySetTimelineDescriptionIndices.size() == 4){
                    timelineDesc4.setText(chosenTimeline.getTimeLineCorrectAnswers().get(randomNumber));

                }
            }
        }
    }
}