package com.sakshi.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView name, countDown, points,time, finalScore;
    ImageView man, questionMark, celebImage;
    Button play, o1,o2,o3,o4;
    ConstraintLayout startPage, gamePage, waitPage, resultPage;
    ArrayList<String> imageURLs= new ArrayList<String>();
    ArrayList<String> names= new ArrayList<String>();
    ArrayList<String> options;
    int correctLocation, p=0, total=0, chosenCeleb;
    MediaPlayer mp;

    public void play(View view)
    {
        resultPage.setVisibility(View.INVISIBLE);
        startPage.setVisibility(View.INVISIBLE);
        waitPage.setVisibility(View.VISIBLE);
        p=0;
        total=0;

        CountDownTimer cdm = new CountDownTimer(3500,1000) {
            @Override
            public void onTick(long l) {
                if(l/1000 == 0)
                {
                    countDown.setText("Guess!!");
                }
                else {
                    countDown.setText(String.valueOf(l / 1000));
                    countDown.setScaleX(0.2f);
                    countDown.setScaleY(0.2f);
                    countDown.animate().scaleX(2f).scaleY(2f).setDuration(1000);
                }
            }

            @Override
            public void onFinish() {
                waitPage.setVisibility(View.INVISIBLE);
                gamePage.setVisibility(View.VISIBLE);
                gameSetup();
                CountDownTimer timer= new CountDownTimer(30000,1000) {
                    @Override
                    public void onTick(long l) {
                        time.setText(Long.toString(l/1000)+"s");
                    }

                    @Override
                    public void onFinish() {
                        displayResult();
                    }
                }.start();
            }
        }.start();
    }

    public void gameSetup()
    {
        o1.setBackgroundColor(getResources().getColor(R.color.black));
        o2.setBackgroundColor(getResources().getColor(R.color.black));
        o3.setBackgroundColor(getResources().getColor(R.color.black));
        o4.setBackgroundColor(getResources().getColor(R.color.black));

        Random rand= new Random();
        chosenCeleb= rand.nextInt(imageURLs.size());

        downloadImage(imageURLs.get(chosenCeleb));

        correctLocation= rand.nextInt(4);
        options= new ArrayList<String>();

        for(int i=0;i<4;i++)
        {
            if(i==correctLocation)
            {
                options.add(names.get(chosenCeleb));
            }
            else
            {
                int incorrectOption= rand.nextInt(imageURLs.size());

                while(incorrectOption==chosenCeleb)
                {
                    incorrectOption= rand.nextInt(imageURLs.size());
                }

                options.add(names.get(incorrectOption));
            }
        }

        o1.setText(options.get(0));
        o2.setText(options.get(1));
        o3.setText(options.get(2));
        o4.setText(options.get(3));
    }

    public void check(View view)
    {
        int tag= Integer.parseInt(view.getTag().toString());
        total+=1;

        if(tag==correctLocation)
        {
            view.setBackgroundColor(getResources().getColor(R.color.green));
            mp= MediaPlayer.create(getApplicationContext(), R.raw.correct);
            mp.start();
            p+=1;
        }
        else
        {
            view.setBackgroundColor(getResources().getColor(R.color.red));
            if(Integer.parseInt(o1.getTag().toString())== correctLocation)
            {
                o1.setBackgroundColor(getResources().getColor(R.color.green));
            }
            else if(Integer.parseInt(o2.getTag().toString())== correctLocation)
            {
                o2.setBackgroundColor(getResources().getColor(R.color.green));
            }
            else if(Integer.parseInt(o3.getTag().toString())== correctLocation)
            {
                o3.setBackgroundColor(getResources().getColor(R.color.green));
            }
            else if(Integer.parseInt(o4.getTag().toString())== correctLocation)
            {
                o4.setBackgroundColor(getResources().getColor(R.color.green));
            }
            mp= MediaPlayer.create(getApplicationContext(), R.raw.wrong);
            mp.start();
        }

        Log.i("points:", Integer.toString(p)+"/"+Integer.toString(total));

        points.setText(Integer.toString(p)+"/"+Integer.toString(total));

        new CountDownTimer(1100,1000) {
            @Override
            public void onTick(long l) {
                //Do Nothing
            }

            @Override
            public void onFinish() {
                gameSetup();
            }
        }.start();
    }

    public void displayResult()
    {
        gamePage.setVisibility(View.INVISIBLE);
        resultPage.setVisibility(View.VISIBLE);
        finalScore.setText(Integer.toString(p)+"/"+Integer.toString(total));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData();

        startPage= (ConstraintLayout) findViewById(R.id.startPage);
        startPage.setVisibility(View.VISIBLE);

        gamePage= (ConstraintLayout) findViewById(R.id.gamePage);
        gamePage.setVisibility(View.GONE);

        waitPage= (ConstraintLayout) findViewById(R.id.waitPage);
        waitPage.setVisibility(View.GONE);

        resultPage= (ConstraintLayout) findViewById(R.id.resultPage);
        resultPage.setVisibility(View.GONE);

        name= (TextView) findViewById(R.id.gameName);
        man= (ImageView) findViewById(R.id.manImage);
        questionMark= (ImageView) findViewById(R.id.questionMark);
        play= (Button) findViewById(R.id.play);
        countDown= (TextView) findViewById(R.id.countDown);
        celebImage= (ImageView) findViewById(R.id.celebImage);
        o1= (Button) findViewById(R.id.option1);
        o2= (Button) findViewById(R.id.option2);
        o3= (Button) findViewById(R.id.option3);
        o4= (Button) findViewById(R.id.option4);
        points= (TextView) findViewById(R.id.Score);
        time= (TextView) findViewById(R.id.timer);
        finalScore=(TextView) findViewById(R.id.finalScore);

        name.animate().scaleXBy(1f).scaleYBy(1f).setDuration(1000);
        man.animate().alpha(1f).setDuration(2000);
        questionMark.setScaleX(10f);
        questionMark.setScaleY(10f);
        questionMark.setAlpha(0f);

        questionMark.animate().scaleX(1f).scaleY(1f).rotation(3600f).alpha(1f).setDuration(2000);
        play.animate().alpha(1f).setDuration(3000);

    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url= new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void downloadImage(String imageURL)
    {
        DownloadImage task = new DownloadImage();
        Bitmap myImage= null;

        try {
            myImage = task.execute(imageURL).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        celebImage.setImageBitmap(myImage);
    }

    public void getData()
    {
        DownloadTask task= new DownloadTask();
        String result= null;
        try {
            result= task.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] splitResult= result.split("<div class=\"lister-list\">");
            String[] finalResult= splitResult[1].split("<div class=\"row text-center lister-working hidden\">");

            Pattern p= Pattern.compile("src=\"(.*?)\"");
            Matcher m= p.matcher(finalResult[0]);

            while(m.find())
            {
                imageURLs.add(m.group(1));
            }

            p= Pattern.compile("img alt=\"(.*?)\"");
            m= p.matcher(finalResult[0]);

            while(m.find())
            {
                names.add(m.group(1));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("Exception","Interrupted Exception occurred");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("Exception","Execution Exception occurred");
        }

        //Log.i("Contents",result);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url= new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data= reader.read();

                while(data!=-1)
                {
                    char current= (char) data;
                    result+=current;
                    data= reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
}