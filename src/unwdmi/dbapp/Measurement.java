package unwdmi.dbapp;

import java.util.Date;
import java.util.EnumMap;

/**
 * Created by metoray on 23-9-15.
 */
public class Measurement {

    private Date date;
    private int stationID;

    public static enum Type{
        TEMP,
        DEWP,
        STP,
        SLP,
        VISIB,
        WDSP,
        PRCP,
        SNDP,
        CLDC,
        WINDDIR
    }

    private EnumMap<Type,Number> data;

    /**
     * Constructor for measurement
     * @param stationID  an integer representing the station the measurement came from.
     * @param date the date and time the measurement was made
     * @param data an array containing all measurements in the order described in types.
     */
    public Measurement(int stationID, Date date, EnumMap<Type,Number> data){
        this.data = data;
        this.date = date;
        this.stationID = stationID;
    }
}
