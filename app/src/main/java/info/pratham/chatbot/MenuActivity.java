package info.pratham.chatbot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @OnClick( R.id.btn_reading)
    public void StartReading(){
        startActivity(new Intent(MenuActivity.this, ReadingActivity.class));
    }

    @OnClick( R.id.btn_chat)
    public void StartChat(){
        startActivity(new Intent(MenuActivity.this, MainActivity.class));
    }
}
