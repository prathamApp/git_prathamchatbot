package info.pratham.chatbot.tts_classes;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

public class MyTTS {
    static TextToSpeech textToSpeech;
    static Context mContext;
    HashMap<String, String> map;

    public MyTTS(Context context, String Lang) {
        super();
        mContext = context;
        try {
            textToSpeech = new TextToSpeech(mContext, new ttsInitListener());
            textToSpeech.setLanguage(new Locale(Lang));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playTTS(final String toSpeak) {
        if (textToSpeech.isSpeaking())
            textToSpeech.stop();
        textToSpeech.setSpeechRate(0.8f);
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
    }

}