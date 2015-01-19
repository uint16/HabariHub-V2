package damagination.com.habarihub.radio;

/**
 * Created by Damas on 1/16/15.
 */
public class RadioList {

    public static final String STREAM_1_URL = "http://198.105.215.28:8000/bongo.mp3";
    public static final String STREAM_1_LABEL = "Bongo Flava Channel";

    public static final String STREAM_2_URL = "http://198.105.215.28:8000/taarab.mp3";
    public static final String STREAM_2_LABEL = "Taarab Miduara Channel";

    public static final String STREAM_3_URL = "http://198.105.215.28:8000/zilipendwa.mp3";
    public static final String STREAM_3_LABEL = "Zilipendwa Channel";

    public static final String STREAM_4_URL = "http://173.192.70.138:8270";
    public static final String STREAM_4_LABEL = "East Africa Radio";

    public static final String STREAM_5_URL = "http://173.192.70.138:8040";
    public static final String STREAM_5_LABEL = "Radio Mbao";

    public static final String STREAM_6_URL = "http://108.178.13.122:8085";
    public static final String STREAM_6_LABEL = "Swahili Gospel Radio";

    public static final String STREAM_7_URL = "http://41.216.220.75:8000/Timesfm";
    public static final String STREAM_7_LABEL = "Times FM";


    public static final StreamStation[] ALl_STATIONS = {
            new StreamStation(STREAM_4_LABEL, STREAM_4_URL),
            new StreamStation(STREAM_2_LABEL, STREAM_2_URL),
            new StreamStation(STREAM_3_LABEL, STREAM_3_URL),
            new StreamStation(STREAM_1_LABEL, STREAM_1_URL),
            new StreamStation(STREAM_5_LABEL, STREAM_5_URL),
            new StreamStation(STREAM_6_LABEL, STREAM_6_URL),
            new StreamStation(STREAM_7_LABEL, STREAM_7_URL)};
    public static final StreamStation DEFAULT_STREAM_STATION = ALl_STATIONS[0];
}
