package unwdmi.dbapp;

import java.util.Date;

/**
 * Created by metoray on 23-9-15.
 */
public class Measurement {

    public final static String[] types = new String[]{"TEMP","DEWP","STP","SLP","VISIB","WDSP","PRCP","SNDP","CLDC","WNDDIR"};

    private Number[] data;
    private Date date;
    private int stationID;

    /**
     * Constructor for measurement
     * @param stationID  an integer representing the station the measurement came from.
     * @param date the date and time the measurement was made
     * @param data an array containing all measurements in the order described in types.
     */
    public Measurement(int stationID, Date date, Number[] data){
        if(data.length!=types.length){
            throw new IllegalArgumentException("Data array does not contain correct amount of data.");
        }
        this.data = data;
        this.date = date;
        this.stationID = stationID;
    }

}
