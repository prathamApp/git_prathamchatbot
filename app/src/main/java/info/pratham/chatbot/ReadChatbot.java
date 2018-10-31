package info.pratham.chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.pratham.chatbot.tts_classes.MyTTS;


public class ReadChatbot extends AppCompatActivity implements RecognitionListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.readChatFlow)
    FlowLayout readChatFlow;
    @BindView(R.id.btn_reading)
    ImageButton btn_reading;

    JSONArray conversation;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();
    String question, answer;
    int currentQueNo = 0, randomNumA, randomNumB;
    boolean voiceStart = false;
    private SpeechRecognizer speech = null;
    public RecognitionListener listener;
    public MyTTS ttspeech;
    String selectedLanguage,contentData,contentId,studentID,contentName;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private AudioManager audioManager;
    boolean correctArr[];

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
        setContentView(R.layout.read_chatbot);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        selectedLanguage = "english";
        contentData = getIntent().getStringExtra("contentData");
        contentId = getIntent().getStringExtra("contentId");
        studentID = getIntent().getStringExtra("studentID");
        contentName = getIntent().getStringExtra("contentName");

        ttspeech = new MyTTS(this, "en-IN");
        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        resetSpeechRecognizer();

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(mAdapter);
        //initialiseListeners();


        try {
            conversation = new JSONArray(contentData);
            randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
            randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
            question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
            answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
            addItemInConvo(question, false);
            setAnswerText(answer);
            //Toast.makeText(this, "Problem in getting conversation!!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_reading)
    public void startRecognition() {
        if (!voiceStart) {
            voiceStart = true;
            btn_reading.setImageResource(R.drawable.stop);
            startSpeechInput();
        } else {
            voiceStart = false;
            btn_reading.setImageResource(R.drawable.mic);
            stopSpeechInput();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        }
    }

    private void stopSpeechInput() {
        speech.stopListening();
    }
    private void startSpeechInput() {
        setRecogniserIntent();
        speech.startListening(recognizerIntent);
    }

    @OnClick(R.id.btn_imgsend)
    public void sendMessage() {
        if (voiceStart) {
            btn_reading.setImageResource(R.drawable.mic);
            voiceStart = false;
            speech.stopListening();
        }
        addItemInConvo(answer, true);
        displayNextQuestion();
    }

    private void displayNextQuestion() {
        try {
            if (currentQueNo < conversation.length()-1) {
                currentQueNo += 1;
                randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                addItemInConvo(question, false);
                setAnswerText(answer);
            }
            else{
                currentQueNo=0;
                randomNumA = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").length());
                randomNumB = getRandomNum(conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").length());
                question = conversation.getJSONObject(currentQueNo).getJSONArray("PersonA").getJSONObject(randomNumA).getString("data");
                answer = conversation.getJSONObject(currentQueNo).getJSONArray("PersonB").getJSONObject(randomNumB).getString("data");
                addItemInConvo(question, false);
                setAnswerText(answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    private void initialiseListeners() {
        listener = new RecognitionListener() {
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
                btn_reading.setImageResource(R.drawable.mic);
            }

            @Override
            public void onResults(Bundle results) {
                voiceStart = false;
                btn_reading.setImageResource(R.drawable.mic);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                System.out.println("LogTag" + " onResults");
                ArrayList<String> matches = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String sttResult = matches.get(0);
                String sttQuestion = answer;

                String splitQues[] = sttQuestion.split(" ");
                String splitRes[] = sttResult.split(" ");


                for (int i = 0; i < splitQues.length; i++) {
                    final TextView myView = (TextView) readChatFlow.getChildAt(i);
                    String resString = "" + myView.getText();
                    for (int j = 0; j < splitRes.length; j++) {
                        if (splitRes[j].equalsIgnoreCase(resString)) {
                            myView.setTextColor(Color.GREEN);
                        }
                    }
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
    }
*/


    private int getRandomNum(int max) {
        try {
            int randomNumber = new Random().nextInt(max);
            return randomNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void addItemInConvo(String text, boolean user) {
        if (user)
            messageList.add(new Message(text, "user"));
        else
            messageList.add(new Message(text, "bot"));
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void setAnswerText(String answerText) {
        readChatFlow.removeAllViews();
        String[] splittedAnswer = answerText.split(" ");
        for (String word : splittedAnswer) {
            final TextView myTextView = new TextView(this);
            myTextView.setText(word);
            myTextView.setTextSize(20);
            myTextView.setTextColor(Color.YELLOW);
            readChatFlow.addView(myTextView);
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
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
/*        voiceStart = false;
        btn_reading.setImageResource(R.drawable.mic);*/
    }

    @Override
    public void onResults(Bundle results) {
/*        voiceStart = false;
        btn_reading.setImageResource(R.drawable.mic);*/

        System.out.println("LogTag" + " onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String sttResult = matches.get(0);
        String sttQuestion = answer;

        String splitQues[] = sttQuestion.split(" ");
        correctArr = new boolean[splitQues.length];
        String splitRes[] = sttResult.split(" ");

        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitQues.length; i++) {
                if (splitRes[j].equalsIgnoreCase(splitQues[i]) && !correctArr[i]) {
                    ((TextView) readChatFlow.getChildAt(i)).setTextColor(Color.GREEN);
                    correctArr[i] = true;
                    break;
                }
            }
        }

        if (!voiceStart)
            resetSpeechRecognizer();
        else
            speech.startListening(recognizerIntent);


        /*        for (int i = 0; i < splitQues.length; i++) {
            final TextView myView = (TextView) readChatFlow.getChildAt(i);
            String resString = "" + myView.getText();
            for (int j = 0; j < splitRes.length; j++) {
                if (splitRes[j].equalsIgnoreCase(resString)) {
                    myView.setTextColor(Color.GREEN);
                }
            }
        }*/

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
