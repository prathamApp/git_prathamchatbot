package info.pratham.chatbot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class chatTemp extends AppCompatActivity {

    @BindView(R.id.btnLeft)
    Button btnLeft;
    @BindView(R.id.btnRight)
    Button btnRight;
    @BindView(R.id.chatFlow)
    FlowLayout chatFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_temp);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        chatFlow.removeAllViewsInLayout();

    }

    @OnClick({R.id.btnLeft})
    public void addLeftString() {

        final LinearLayout myLL = new LinearLayout(this);
        myLL.setGravity(View.TEXT_ALIGNMENT_GRAVITY);

        final TextView myTextView = new TextView(this);
        myTextView.setText("Left String added");
        myTextView.setTextSize(30);
        myTextView.setTextColor(Color.YELLOW);
        myLL.addView(myTextView);
        chatFlow.addView(myLL);

    }

    @OnClick({R.id.btnRight})
    public void addRigthString() {

        final LinearLayout myLL = new LinearLayout(this);
        final TextView myTextView = new TextView(this);
        myTextView.setText("Left String added");
        myTextView.setTextSize(30);
        myTextView.setGravity(2);
        myTextView.setTextColor(Color.GREEN);
        myLL.addView(myTextView);
        chatFlow.addView(myLL);

    }
}