package info.pratham.chatbot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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


public class ReadChatbot extends AppCompatActivity {

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
    int currentQueNo = 0;
    private SpeechRecognizer speech = null;
    public RecognitionListener listener;
    Intent intent;
    String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_chatbot);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(mAdapter);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        initialiseListeners();
        speech.setRecognitionListener(listener);
        startSTTIntent();

        try {
            conversation = getRandomConversation(getConversations());
            if (conversation != null && conversation.length() > 0) {
                question = conversation.getJSONObject(currentQueNo).getString("Que");
                answer = conversation.getJSONObject(currentQueNo).getString("Ans");
                addItemInConvo(question, false);
                setAnswerText(answer);
            } else
                Toast.makeText(this, "Problem in getting conversation!!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_reading)
    public void startRecognition() {
        speech.stopListening();
        speech.startListening(intent);
    }

    @OnClick(R.id.btn_imgsend)
    public void sendMessage() {
        addItemInConvo(answer,true);
        displayNextQuestion();
    }

    private void displayNextQuestion() {
        try {
            currentQueNo += 1;
            question = conversation.getJSONObject(currentQueNo).getString("Que");
            answer = conversation.getJSONObject(currentQueNo).getString("Ans");
            addItemInConvo(question, false);
            setAnswerText(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                btn_reading.setImageResource(R.drawable.mic);
            }

            @Override
            public void onResults(Bundle results) {
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

    public JSONArray getConversations() {
        JSONArray conversationArray = null;
        try {
            InputStream is = getAssets().open("ConversationData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONObject jsonObj = new JSONObject(new String(buffer));
            conversationArray = jsonObj.getJSONArray("conversations");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conversationArray;
    }

    private JSONArray getRandomConversation(JSONArray conversations) {
        try {
            int randomNumber = new Random().nextInt(conversations.length());
            return conversations.getJSONArray(randomNumber);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

    public void startSTTIntent() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.domain.app");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }


}
