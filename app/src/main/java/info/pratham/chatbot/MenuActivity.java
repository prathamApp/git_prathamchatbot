package info.pratham.chatbot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;
import info.pratham.chatbot.menuDisplay.ContentDisplay;

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

/*
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
*/
                sysLang = "en-IN";
                Intent intent = new Intent(MenuActivity.this, ReadingActivity.class);
                intent.putExtra("selectedLang", "English");
                startActivity(intent);
/*            }
        });

        btn_hin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sysLang = "hi-IN";
                Intent intent = new Intent(MenuActivity.this, ReadingActivity.class);
                intent.putExtra("selectedLang", "Hindi");
                startActivity(intent);
            }
        });*/
    }


    @OnClick(R.id.btn_reading)
    public void StartReading() {
        showLangDialog();
    }

    @OnClick(R.id.btn_chat)
    public void StartChat() {
        startActivity(new Intent(MenuActivity.this, ContentDisplay.class));
    }
}
