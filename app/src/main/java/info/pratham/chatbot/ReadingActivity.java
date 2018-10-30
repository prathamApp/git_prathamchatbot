package info.pratham.chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.pratham.chatbot.tts_classes.MyTTS;

public class ReadingActivity extends AppCompatActivity implements RecognitionListener {

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


    String selectedLanguage, systemLang = "", mySentence = "", finalData = "";
    Intent intent;
    JSONArray actualReadingData;
    String splitQues[];
    boolean voiceStart = false, stopFlg = false;
    boolean correctArr[];
    public MyTTS ttspeech;
    private SpeechRecognizer speech = null;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Intent recognizerIntent;
    int original_volume_level;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private AudioManager audioManager;

    private void resetSpeechRecognizer() {

        if (speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if (SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        selectedLanguage = "english";
/*        selectedLanguage = getIntent().getStringExtra("selectedLang");
        if (selectedLanguage.equalsIgnoreCase("english"))
            systemLang = "en-IN";
        else
            systemLang = "hi-IN";*/

        ttspeech = new MyTTS(this, "en-IN");

        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);

        int btnWidth = btnSpeak.getWidth();

        btnSpeak.getLayoutParams().height = 1;

        //speech.setRecognitionListener(this);
        getReadingData();
        // start speech recogniser
        resetSpeechRecognizer();
    }

    public void getReadingData() {
        actualReadingData = languageData();
//        Collections.shuffle(Collections.singletonList(actualReadingData));
        getNextSentence();
    }

    public JSONArray languageData() {
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
        if (voiceStart) {
            micActive();
        }
        ttspeech.playTTS(mySentence);
    }

    @OnClick(R.id.btnMic)
    public void micActive() {
        if (!voiceStart) {
            voiceStart = true;
            finalData = "";
            tv_mic.setText("Stop");
            btnSpeak.setImageResource(R.drawable.stop);
            startSpeechInput();
        } else {
            voiceStart = false;
            tv_mic.setText("Speak");
            btnSpeak.setImageResource(R.drawable.mic);
            //audioManager.setMicrophoneMute(false);
            stopSpeechInput();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        }
    }

    private void stopSpeechInput() {
        speech.stopListening();
    }

    private void startSpeechInput() {
        //speech.startListening(intent);
        setRecogniserIntent();
        speech.startListening(recognizerIntent);
    }


    @OnClick(R.id.btnNext)
    public void getNextSentence() {
        quesFlowLayout.removeAllViewsInLayout();
        try {
            if (voiceStart) {
                micActive();
                tv_mic.setText("Speak");
                btnSpeak.setImageResource(R.drawable.mic);
                voiceStart = false;
                stopSpeechInput();
                resetSpeechRecognizer();
            }
            int randomNum = ThreadLocalRandom.current().nextInt(0, actualReadingData.length());
            mySentence = actualReadingData.getJSONObject(randomNum).getString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        splitQues = mySentence.split(" ");
        correctArr = new boolean[splitQues.length];

        for (int i = 0; i < splitQues.length; i++) {
            final TextView myTextView = new TextView(this);
            myTextView.setText(splitQues[i]);
            myTextView.setTextSize(30);
            myTextView.setTextColor(Color.YELLOW);
            quesFlowLayout.addView(myTextView);
        }
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
//        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        /*voiceStart = false;
        tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.mic);*/
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
//        btnSpeak.setImageResource(R.drawable.stop);
    }

    @Override
    public void onResults(Bundle results) {
        //voiceStart = false;
        /*tv_mic.setText("Speak");
        btnSpeak.setImageResource(R.drawable.mic);*/

        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String sttResult = matches.get(0);
        String sttQuestion = mySentence;

        Log.d("STT-Res", "sttResult: " + sttResult + "             sttQuestion: " + sttQuestion);
        Log.d("STT-Res", "\n");

        String splitRes[] = sttResult.split(" ");

        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitQues.length; i++) {
                if (splitRes[j].equalsIgnoreCase(splitQues[i]) && !correctArr[i]) {
                    ((TextView) quesFlowLayout.getChildAt(i)).setTextColor(Color.GREEN);
                    correctArr[i] = true;
                    break;
                }
            }
        }
        if (!voiceStart)
            resetSpeechRecognizer();
        else
            speech.startListening(recognizerIntent);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        System.out.println("LogTag" + " onResults");
/*        ArrayList<String> matches = partialResults
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String sttResult = matches.get(0);
        String sttQuestion = mySentence;

        Log.d("STT-Res", "sttResult: " + sttResult + "             sttQuestion: " + sttQuestion);

        String splitRes[] = sttResult.split(" ");

        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitQues.length; i++) {
                if (splitRes[j].equalsIgnoreCase(splitQues[i])*//* && !correctArr[i]*//*) {
                    ((TextView) quesFlowLayout.getChildAt(i)).setTextColor(Color.GREEN);
//                    correctArr[i]=true;
//                    break;
                }
            }
        }*/
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}


/*
<ScrollView
                    android:id="@+id/myScrollView2"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:layout_gravity="center">

<com.nex3z.flowlayout.FlowLayout xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/myflowlayout2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginVertical="5dp"
        app:flChildSpacing="7dp"
        app:flChildSpacingForLastRow="align"
        app:flRowSpacing="5dp"></com.nex3z.flowlayout.FlowLayout>
</ScrollView>
*/
