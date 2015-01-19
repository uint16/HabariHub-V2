package damagination.com.habarihub.rss;

/**
 * Created by Newton Bujiku on 1/13/15.
 */
public class RSSItem {



    private String title;//title of the post
    private String description;//a description contained in the post
    private String postUrl;//a url pointing to the post
    private String pubDate;//a date of publication
    private String author;//author of the post
    private String imageUrl;//url of the image within the post


    public RSSItem(){

    }

    //getters and setters for all fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
