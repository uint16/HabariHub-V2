package damagination.com.habarihub.rss;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by Damas on 1/13/15.
 */
public class RSSHandler extends DefaultHandler {

    private RSSItem rssFeedItem;//A representation of a single item
    private ArrayList<RSSItem> rssFeedItems;//A collection of all RSS items
    private StringBuilder builder;//a string variable to hold the content between two XML tags


    public RSSHandler() {

        rssFeedItems = new ArrayList<RSSItem>();
        //rssFeedItem = new RSSItem();


    }

    /*
     *This method will be invoked each time the parser encounters a
      * starting tag
      * <tag>
     */

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        //instantiate a builder since we are at the beginning of the tag
        builder = new StringBuilder();
        if (qName.equalsIgnoreCase(Constant.ITEM) || qName.equalsIgnoreCase(Constant.ENTRY)) {
            //The first tag is item;create a new RSSFeedItem object
            //to hold the children of this tag
           rssFeedItem = new RSSItem();
        } else if ((qName.equalsIgnoreCase(Constant.MEDIA_CONTENT)) ||
                (qName.equalsIgnoreCase(Constant.MEDIA_THUMBNAIL)) ||
                (qName.equalsIgnoreCase(Constant.IMAGE))) {
            //if any of the above tags is encountered
            //what we have to look for is the url of the image
            //in the post

            if (attributes.getValue(Constant.URL) != null) {
                //if the url attribute is not equal to null
                //get that url
               rssFeedItem.setImageUrl(attributes.getValue(Constant.URL));
            }

        }



    }


    /**
     *This method will be invoked each and every single time
     * the parser meets a closing tag
     * </tag>
     *
     */

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        try {

            if (qName.equalsIgnoreCase(Constant.ITEM) || qName.equalsIgnoreCase(Constant.ENTRY)) {
                //It is the end of the item tag
                //add the item to the list of the rss feed items
                //set the rss feed item to null
                rssFeedItems.add(rssFeedItem);
                rssFeedItem = null;

            } else if (qName.equalsIgnoreCase(Constant.PUBLICATION_DATE)) {
                //it is the end of the pubDate tag
                //set the content of the builder to pubDate
                //of the feed item
                rssFeedItem.setPubDate(builder.toString());

            } else if (qName.equalsIgnoreCase(Constant.TITLE)) {
                //The end of the title tag

                rssFeedItem.setTitle(builder.toString());

            } else if (qName.equalsIgnoreCase(Constant.DESCRIPTION)) {

                rssFeedItem.setDescription(builder.toString());

            } else if (qName.equalsIgnoreCase(Constant.POST_URL)) {

                rssFeedItem.setPostUrl(builder.toString());

            } else if (qName.equalsIgnoreCase(Constant.AUTHOR)) {

                rssFeedItem.setAuthor(builder.toString());

            }

        }catch (Exception e){

            e.getMessage();

        }
    }


    /**
     *This method will be called after the startElement() method
     * until the closing tag is met
     *
     * What is done here is appending the value in between the tag
     * to create one String.
     *
     * We are using a StringBuilder (also a StringBuffer object could be
     * used) because we want to collect together the content since there
     * is no guarantee that at one call this method could return all that is
     * between the tags.
     *
     * It could be called multiple times before the closing tag is met.
     *
     * A use of the String object could produce unexpected results
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        //append the value returned
        builder.append(ch,start,length);

    }

    public ArrayList<RSSItem> getFeedItems(){

        //return a collection of the feed items
        return rssFeedItems;
    }
}
