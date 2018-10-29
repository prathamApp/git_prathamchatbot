package info.pratham.chatbot.menuDisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import info.pratham.chatbot.R;


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

    private Context mContext;
    private List<ContentView> contentViewList;
    ContentClicked contentClicked;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public LinearLayout content_card_view;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.content_title);
            thumbnail = (ImageView) view.findViewById(R.id.content_thumbnail);
            content_card_view = (LinearLayout) view.findViewById(R.id.content_card_view);
        }
    }

    public ContentAdapter(Context mContext, List<ContentView> contentViewList, ContentClicked contentClicked) {
        this.mContext = mContext;
        this.contentViewList = contentViewList;
        this.contentClicked = contentClicked;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ContentView contentList = contentViewList.get(position);

        holder.title.setText(contentList.contentName);
        holder.content_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentClicked.onStoryClicked(position, contentList.contentId, contentList.contentData, contentList.contentId, contentList.contentName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contentViewList.size();
    }
}