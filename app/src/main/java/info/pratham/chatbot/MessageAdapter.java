package info.pratham.chatbot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static info.pratham.chatbot.ReadChatbot.playChat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List messageList;
    @BindView(R.id.arrow)
    LinearLayout rightSide;
    @BindView(R.id.arrow1)
    LinearLayout leftSide;

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    public MessageAdapter(List messages) {
        messageList = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text) TextView mTextView;

        public ViewHolder(RelativeLayout v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_purple, parent, false);
            ViewHolder vh = new ViewHolder((RelativeLayout) v);
/*            if(ReadChatbot.convoMode.equals("A")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.android_rd_small));
            }
            else if(ReadChatbot.convoMode.equals("B")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.android_rd_small));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
            }
            else if(ReadChatbot.convoMode.equals("C")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
            }*/
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_green, parent, false);
            ViewHolder vh = new ViewHolder((RelativeLayout) v);
/*            if(ReadChatbot.convoMode.equals("A")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.android_rd_small));
            }
            else if(ReadChatbot.convoMode.equals("B")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.android_rd_small));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
            }
            else if(ReadChatbot.convoMode.equals("C")){
                rightSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
                leftSide.setBackground(parent.getContext().getResources().getDrawable(R.drawable.studentsmall));
            }*/
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(((Message)messageList.get(holder.getAdapterPosition())).getMessage());
        holder.mTextView.setMaxWidth(500);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Play TTS
                playChat(""+holder.mTextView.getText());
            }
        });
    }

    public void remove(int pos) {
        int position = pos;
        messageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messageList.size());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messageList.get(position);

        if (message.getSenderName().equals("user")) {
            return SENDER;
        } else {
            return RECEIVER;
        }

    }

}