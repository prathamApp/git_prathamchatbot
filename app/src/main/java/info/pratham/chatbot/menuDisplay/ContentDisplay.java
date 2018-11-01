package info.pratham.chatbot.menuDisplay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import info.pratham.chatbot.R;
import info.pratham.chatbot.ReadChatbot;

public class  ContentDisplay extends AppCompatActivity implements ContentClicked {

    boolean flag = false;
    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    private List<ContentView> contentViewList;
    String sdCardPathString, studentID,storyTitle,convoMode;
    public static String storiesDispLang,readType;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_display);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        readType = "story";
        convoMode = getIntent().getStringExtra("convoMode");
        setPath();

//        studentID = getIntent().getStringExtra("StudentID");

        recyclerView = (RecyclerView) findViewById(R.id.attendnce_recycler_view);

        contentViewList = new ArrayList<>();
        contentAdapter = new ContentAdapter(this, contentViewList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(15), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contentAdapter);

/*        String appLang = getLanguage();

        if ( appLang.equalsIgnoreCase("Punjabi")) {
            storiesDispLang = "pun";
        } else if (appLang.equalsIgnoreCase("Odiya")) {
            storiesDispLang = "odiya";
        } else if (appLang.equalsIgnoreCase("Gujarati")) {
            storiesDispLang = "guj";
        }else
            storiesDispLang = "NA";*/


        prepareStories();

    }

    public void setPath() {
        try {
//            sdCardPathString = appDatabase.getStatusDao().getValue("SdCardPath");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareStories() {
        JSONArray storiesJA = fetchConversations();
        try {
            for (int i = 0; i < storiesJA.length(); i++) {
                ContentView contentView = new ContentView();
                String sName, sId, sThumbnail, contentData;
                sName = contentView.contentName = storiesJA.getJSONObject(i).getString("convoTitle");
/*                sId = contentView.contentId= storiesJA.getJSONObject(i).getString("resourceId");
                sThumbnail = contentView.contentThumbnail= sdCardPathString + "StoryData/" + storiesJA.getJSONObject(i).getString("storyImage");*/
                contentData = storiesJA.getJSONObject(i).getString("convoList");
                ContentView contentListView = new ContentView("" + sName, "" , "", contentData);
                contentViewList.add(contentListView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray fetchConversations() {
        JSONArray returnStoryNavigate = null;
        try {
            InputStream is = getAssets().open("ConversationData.json");;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            returnStoryNavigate = jsonObj.getJSONArray("conversations");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStoryNavigate;
    }


    @Override
    public void onStoryClicked(int position, String id, String contentData, String contentId, String contentName) {
//        this.finish();
        Intent mainNew = new Intent(ContentDisplay.this, ReadChatbot.class);
        mainNew.putExtra("contentData", contentData);
        mainNew.putExtra("contentId", contentId);
        mainNew.putExtra("studentID", studentID);
        mainNew.putExtra("contentName", contentName);
        mainNew.putExtra("convoMode", convoMode);
        startActivity(mainNew);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + avatar) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + avatar) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}
