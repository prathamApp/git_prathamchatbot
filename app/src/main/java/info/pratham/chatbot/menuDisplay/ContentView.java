package info.pratham.chatbot.menuDisplay;

class ContentView {

    public String contentId;
    public String contentName;
    public String contentThumbnail;
    public String contentData;

    public ContentView(String contentName, String contentId, String contentThumbnail, String contentData) {
        this.contentName = contentName;
        this.contentId = contentId;
        this.contentThumbnail = contentThumbnail;
        this.contentData = contentData;
    }


    public ContentView() {

    }
}
