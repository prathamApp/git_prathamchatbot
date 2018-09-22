package info.pratham.chatbot.tts_classes;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

import info.pratham.chatbot.MenuActivity;

import static info.pratham.chatbot.tts_classes.MyTTS.textToSpeech;

class ttsInitListener implements TextToSpeech.OnInitListener {
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(new Locale(MenuActivity.sysLang));
            textToSpeech.setSpeechRate((float)0.4);
            textToSpeech.setPitch((float) 1);
        } else {
            textToSpeech = null;
            Toast.makeText(MyTTS.mContext, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
