package info.pratham.chatbot;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class chat_rec extends RecyclerView.ViewHolder  {

    @BindView(R.id.leftText)    TextView leftText;
    @BindView(R.id.rightText)    TextView rightText;

    public chat_rec(View itemView){
        super(itemView);
        ButterKnife.bind(itemView);
    }
}
