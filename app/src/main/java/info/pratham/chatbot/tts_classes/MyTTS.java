package info.pratham.chatbot.tts_classes;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

public class MyTTS {
    static TextToSpeech textToSpeech;
    static Context mContext;
    HashMap<String, String> map;

    public MyTTS(Context context) {
        super();
        mContext = context;
        try {
            textToSpeech = new TextToSpeech(mContext, new ttsInitListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playTTS(final String toSpeak, String Lang, float pitchTts) {
        textToSpeech.setLanguage(new Locale(Lang));
        textToSpeech.setSpeechRate(0.8f);
        textToSpeech.setPitch(pitchTts);
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
    }

}