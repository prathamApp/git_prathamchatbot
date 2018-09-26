package info.pratham.chatbot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.speakButton)
    RelativeLayout speakButton;
    @BindView(R.id.fab_img)
    ImageButton fab_img;
    @BindView(R.id.displayText)
    TextView displayText;

    JSONArray conversation;
    int currentQueNo = 0;
    private SpeechRecognizer speech = null;
    public RecognitionListener listener;
    Intent intent;
    String selectedLanguage;
    String replyText;
    int send, mic;
    boolean flagSend;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();
    public static MyTTS ttspeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        selectedLanguage = "en-IN";
        ttspeech = new MyTTS(this, selectedLanguage);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(mAdapter);
        send = R.drawable.send;
        mic = R.drawable.mic;
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        initialiseListeners();
        speech.setRecognitionListener(listener);
        startSTTIntent();
        try {
            conversation = getRandomConversation(getConversations());
            if (conversation != null && conversation.length() > 0) {
                messageList.add(new Message(conversation.getJSONObject(currentQueNo).getString("Que"), "bot"));
            }
            else Toast.makeText(this, "Problem in getting conversation!!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
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
                Toast.makeText(MainActivity.this, "Can't hear you", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                flagSend = true;
                ImageViewAnimatedChange(MainActivity.this, fab_img, send);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

                ArrayList<String> matches = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                displayText.setText(matches.get(0));
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
    }

    @OnClick(R.id.clearImage)
    public void clearText() {
        displayText.setText("");
        flagSend = false;
        ImageViewAnimatedChange(MainActivity.this, fab_img, mic);
    }

    @OnClick(R.id.fab_img)
    public void startRecognition() {
        if (flagSend) {
            flagSend = false;
            // send to chat
            checkAnswer(displayText.getText().toString());
            displayText.setText("");
            mAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setReplyResultForNextQuestion();
                    mAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }, 1000);
            ImageViewAnimatedChange(MainActivity.this, fab_img, mic);
        } else {
            speech.startListening(intent);
        }
    }

    public static void playChat(String chatText) {
        ttspeech.playTTS(chatText);
    }

    private void checkAnswer(String userAnswer) {
        try {
            messageList.add(new Message(userAnswer, "user"));
            String expectedAnswer = conversation.getJSONObject(currentQueNo).getString("Ans");
            int percent = getSuccessPercent(userAnswer, expectedAnswer);
            if (percent < 60) {
                replyText = "Oops! I was expecting: " + expectedAnswer;
                setReplyResultForCorrection();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setReplyResultForCorrection() {
        messageList.add(new Message(replyText, "bot"));
    }

    private void setReplyResultForNextQuestion() {
        try {
            currentQueNo += 1;
            if (currentQueNo == conversation.length()) {
                conversation = getRandomConversation(getConversations());
                currentQueNo = 0;
                messageList.add(new Message(conversation.getJSONObject(currentQueNo).getString("Que"), "bot"));
            } else
                messageList.add(new Message(conversation.getJSONObject(currentQueNo).getString("Que"), "bot"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getSuccessPercent(String userAnswer, String expectedAnswer) {
        int correctCount = 0;
        String userAnsArray[] = userAnswer.split(" ");
        String expectedAnsArray[] = expectedAnswer.split(" ");
        for (int userIndex = 0; userIndex < userAnsArray.length; userIndex++)
            for (int expectedIndex = 0; expectedIndex < expectedAnsArray.length; expectedIndex++) {
                if (userAnsArray[userIndex].equalsIgnoreCase(expectedAnsArray[expectedIndex])) {
                    correctCount += 1;
                    break;
                }
            }
        if (correctCount == 0) return 0;
        return ((expectedAnsArray.length / correctCount) * 100);
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

    public void ImageViewAnimatedChange(Context c, final ImageButton v, final int new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageResource(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
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
