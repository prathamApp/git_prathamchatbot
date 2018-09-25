package info.pratham.chatbot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.rg_Language)
    RadioGroup rgLanguage;
    RadioButton radioButton;

    public static String sysLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @OnClick( R.id.btn_reading)
    public void StartReading(){
        int selectedId = rgLanguage.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        String selectedLang = (String) radioButton.getText();

        if(selectedLang.equalsIgnoreCase("english"))
            sysLang="en-IN";
        else
            sysLang="hi-IN";

        Intent intent = new Intent(MenuActivity.this, ReadingActivity.class);
        intent.putExtra("selectedLang", selectedLang);
        startActivity(intent);
    }

    @OnClick( R.id.btn_chat)
    public void StartChat(){

        int selectedId = rgLanguage.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        String selectedLang = (String) radioButton.getText();

        if(selectedLang.equalsIgnoreCase("english"))
            sysLang="en-IN";
        else
            sysLang="hi-IN";

        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("selectedLang", selectedLang);
        startActivity(intent);
    }

    @OnClick( R.id.chatTemp)
    public void StartChatTemp(){
    }
}
