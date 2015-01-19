package damagination.com.habarihub.rss;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Newton Bujiku on 1/13/15.
 */
public class RSSReader {
    private String link;//a url to the
    private int total;

    public  RSSReader (String link){

        this.link = link;

    }

    public ArrayList<RSSItem> getRSSFeedItems(){


        SAXParserFactory saxParserFactory=null;
        SAXParser saxParser=null;

        //create a url object from the string url
        URL url = null;
        HttpURLConnection urlConnection = null;
        RSSHandler handler = null;

        try {
            //create an instance of the SAXParserFactory
            saxParserFactory= SAXParserFactory.newInstance();
            url  = new URL(link);

            //Open a HttpURLConnection from URL
            urlConnection=(HttpURLConnection) url.openConnection();

            //create a parser from the SAXParserFactory instance

            saxParser = saxParserFactory.newSAXParser();

            //instantiate the handler whose methods
            //will be invoked by the parser
            //during parsing
            handler = new RSSHandler();

            /*
            Start parsing.
            You can use this with pure Java by passing
              URL.openStream() instead of android's
              HttpURLConnection.getInputStream() method
             */
            saxParser.parse(urlConnection.getInputStream(),handler);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        //return all the items after parsing is done
        return handler.getFeedItems();

    }
}
