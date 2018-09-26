package info.pratham.chatbot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    public static String sysLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void showLangDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_language);
        dialog.setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        Button btn_eng = (Button) dialog.findViewById(R.id.dialog_btn_eng);
        Button btn_hin = (Button) dialog.findViewById(R.id.dialog_btn_hin);
        dialog.show();

        btn_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sysLang="en-IN";
                Intent intent = new Intent(MenuActivity.this, ReadingActivity.class);
                intent.putExtra("selectedLang", "English");
                startActivity(intent);
            }
        });

        btn_hin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sysLang="hi-IN";
                Intent intent = new Intent(MenuActivity.this, ReadingActivity.class);
                intent.putExtra("selectedLang", "Hindi");
                startActivity(intent);            }
        });
    }

    public void showChatDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_chat);
        dialog.setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        Button btn_chat = (Button) dialog.findViewById(R.id.dialog_btn_chat);
        Button btn_convo = (Button) dialog.findViewById(R.id.dialog_btn_convo);
        dialog.show();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sysLang="en-IN";
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("selectedLang", "English");
                startActivity(intent);
            }
        });

        btn_convo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sysLang="en-IN";
                Intent intent = new Intent(MenuActivity.this, ReadChatbot.class);
                intent.putExtra("selectedLang", "English");
                startActivity(intent);            }
        });
    }

    @OnClick( R.id.btn_reading)
    public void StartReading(){
        showLangDialog();
    }

    @OnClick( R.id.btn_chat)
    public void StartChat(){
        showChatDialog();
    }
}
