package info.pratham.chatbot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.speakButton)
    RelativeLayout speakButton;
    @BindView(R.id.fab_img)
    ImageView fab_img;
    @BindView(R.id.displayText)
    TextView displayText;

    JSONArray conversation;
    Intent intent;
    String selectedLanguage;
    String replyText;
    int currentQueNo = 0;
    private SpeechRecognizer speech = null;
    public RecognitionListener listener;
    Bitmap send, mic;
    boolean flagSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        initialiseAdapter(linearLayoutManager);
        selectedLanguage = "english";
        send = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
        mic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        initialiseListeners();
        speech.setRecognitionListener(listener);
        startSTTIntent();
        conversation = getRandomConversation(getConversations());
        try {
            displayText.setText(conversation.getJSONObject(currentQueNo).getString("Que"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initialiseAdapter(final LinearLayoutManager linearLayoutManager) {

        /*adapter = new Adapter<ChatMessage, chat_rec>(ChatMessage.class,R.layout.msglist,chat_rec.class,ref.child("chat")) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {

                if (model.getMsgUser()!=null && model.getMsgUser().equals("user")) {


                    viewHolder.rightText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                }
                else {
                    viewHolder.leftText.setText(model.getMsgText());

                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);

                }

            }
        });

        recyclerView.setAdapter(adapter);*/
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
                displayText.setText(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
                ImageViewAnimatedChange(MainActivity.this, fab_img, send);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                /*System.out.println("LogTag" + " onResults");
                ArrayList<String> matches = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String sttResult = matches.get(0);
                String sttQuestion = mySentence;

                String splitQues[] = sttQuestion.split(" ");
                String splitRes[] = sttResult.split(" ");
                String splitPrevRes[] = finalData.split(" ");


                for (int i = 0; i < splitQues.length; i++) {
                    final TextView myView = (TextView) quesFlowLayout.getChildAt(i);
                    String resString = ""+myView.getText();
                    for (int j = 0; j < splitRes.length; j++) {
                        if(splitRes[j].equalsIgnoreCase(resString)) {
                            myView.setTextColor(Color.GREEN);
                        }
                    }
                }*/
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        };
    }

    @OnClick(R.id.clearButton)
    public void clearText() {
        displayText.setText("");
        flagSend = false;
        ImageViewAnimatedChange(MainActivity.this, fab_img, mic);
    }

    @OnClick(R.id.speakButton)
    public void startRecognition() {
        if (flagSend) {
            flagSend = false;
            // send to chat
            checkAnswer(displayText.getText().toString());
            setReplyResultForNextQuestion();
//            displayText.setText("");
            ImageViewAnimatedChange(MainActivity.this, fab_img, mic);
        } else {
            speech.startListening(intent);
        }
    }

    private void checkAnswer(String userAnswer) {
        try {
            String expectedAnswer = conversation.getJSONObject(currentQueNo).getString("Ans");
            int percent = getSuccessPercent(userAnswer, expectedAnswer);
            if (percent < 60) {
                replyText = "I was expecting: " + expectedAnswer;
                setReplyResultForCorrection();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setReplyResultForCorrection() {
        displayText.setText(replyText);
    }

    private void setReplyResultForNextQuestion() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    currentQueNo += 1;
                    if (currentQueNo == conversation.length()) {
                        conversation = getRandomConversation(getConversations());
                        currentQueNo = 0;
                        displayText.setText(conversation.getJSONObject(currentQueNo).getString("Que"));
                    }
                    else
                        displayText.setText(conversation.getJSONObject(currentQueNo).getString("Que"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
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

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
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
                v.setImageBitmap(new_image);
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
