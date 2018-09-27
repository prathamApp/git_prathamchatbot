package info.pratham.chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.pratham.chatbot.tts_classes.MyTTS;

public class ReadingActivity extends AppCompatActivity implements RecognitionListener{

    @BindView(R.id.myflowlayout2)
    FlowLayout quesFlowLayout;
    @BindView(R.id.btnHear)
    ImageButton btnHear;
    @BindView(R.id.btnNext)
    ImageButton btnNextSentence;
    @BindView(R.id.btnMic)
    ImageButton btnSpeak;
    @BindView(R.id.tv_mic)
    TextView tv_mic;


    String selectedLanguage, systemLang="", mySentence = "",finalData="";
    Intent intent;
    JSONArray actualReadingData;
    String splitQues[];
    boolean voiceStart = false;
//    boolean correctArr[];
    public MyTTS ttspeech;
    private SpeechRecognizer speech = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int btnWidth = btnSpeak.getWidth();

        Toast.makeText(this, ""+btnWidth, Toast.LENGTH_SHORT).show();

        btnSpeak.getLayoutParams().height = 1;


        selectedLanguage = getIntent().getStringExtra("selectedLang");
        if(selectedLanguage.equalsIgnoreCase("english"))
            systemLang="en-IN";
        else
            systemLang="hi-IN";

        ttspeech = new MyTTS(this,systemLang);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        startSTTIntent();
    }


    public void startSTTIntent() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, systemLang);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        getReadingData();
    }

    public void getReadingData() {
        actualReadingData = languageData();
//        Collections.shuffle(Collections.singletonList(actualReadingData));
        getNextSentence();
    }

    public JSONArray languageData(){
        try {
            JSONArray readingData = getJsonData();
            for (int i = 0; i < readingData.length(); i++) {
                String lang = readingData.getJSONObject(i).getString("language");
                if (lang.equalsIgnoreCase(selectedLanguage)) {
                    actualReadingData = readingData.getJSONObject(i).getJSONArray("sentences");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return actualReadingData;
    }

    public JSONArray getJsonData() {
        JSONArray returnStoryNavigate = null;
        try {
            InputStream is = getAssets().open("ReadingData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject jsonObj = new JSONObject(new String(buffer));
            returnStoryNavigate = jsonObj.getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStoryNavigate;
    }

    @OnClick(R.id.btnHear)
    public void playQues() {
        ttspeech.playTTS(mySentence);
    }

    @OnClick(R.id.btnMic)
    public void micActive() {
        if (!voiceStart) {
            voiceStart = true;
            finalData="";
            tv_mic.setText("Stop");
            btnSpeak.setImageResource(R.drawable.stop);
            startSpeechInput();
        } else {
            stopSpeechInput();
            tv_mic.setText("Speak");
            btnSpeak.setImageResource(R.drawable.mic);
            voiceStart = false;
            stopSpeechInput();
        }
    }

    private void stopSpeechInput() {
        speech.stopListening();
    }

    private void startSpeechInput() {
        speech.startListening(intent);
    }


    @OnClick(R.id.btnNext)
    public void getNextSentence() {
        quesFlowLayout.removeAllViewsInLayout();
        try {
            if (voiceStart) {
                stopSpeechInput();
                tv_mic.setText("Speak");
                btnSpeak.setImageResource(R.drawable.mic);
                voiceStart = false;
                stopSpeechInput();
            }
            int randomNum = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
            mySentence = actualReadingData.getJSONObject(randomNum).getString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        splitQues = mySentence.split(" ");
//        correctArr = new boolean[splitQues.length];

        for (int i = 0; i < splitQues.length; i++) {
            final TextView myTextView = new TextView(this);
            myTextView.setText(splitQues[i]);
            myTextView.setTextSize(30);
            myTextView.setTextColor(Color.YELLOW);
            quesFlowLayout.addView(myTextView);
        }

    }


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        voiceStart = false;
        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.mic);
    }

    @Override
    public void onResults(Bundle results) {
        voiceStart = false;
        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.mic);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        System.out.println("LogTag"+ " onResults");
        ArrayList<String> matches = partialResults
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String sttResult = matches.get(0);
        String sttQuestion = mySentence;

        Log.d("STT-Res", "sttResult: " + sttResult + "             sttQuestion: " + sttQuestion);

        String splitRes[] = sttResult.split(" ");

        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitQues.length; i++) {
                if(splitRes[j].equalsIgnoreCase(splitQues[i])/* && !correctArr[i]*/) {
                    ((TextView)quesFlowLayout.getChildAt(i)).setTextColor(Color.GREEN);
//                    correctArr[i]=true;
//                    break;
                }
            }
        }    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}