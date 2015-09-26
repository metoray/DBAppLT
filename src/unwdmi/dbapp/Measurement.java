package unwdmi.dbapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

/**
 * Created by metoray on 23-9-15.
 */
public class Measurement {

    private Date date;
    private int stationID;

    private EnumMap<MeasurementType,Number> data;

    /**
     * Constructor for measurement
     * @param stationID  an integer representing the station the measurement came from.
     * @param date the date and time the measurement was made
     * @param data an array containing all measurements in the order described in types.
     */
    public Measurement(int stationID, Date date, EnumMap<MeasurementType,Number> data){
        this.data = data;
        this.date = date;
        this.stationID = stationID;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString;
        if(date!=null){
            dateString = format.format(date);
        }
        else {
            dateString = "MISSING";
        }
        sb.append("MEASUREMENT\nStation: ").append(stationID).append("\nDate: ").append(dateString).append("\n");
        for(MeasurementType type: MeasurementType.values()){
            if(data.containsKey(type)) {
                sb.append(String.format("%s: %s%n", type, data.get(type)));
            } else {
                sb.append(String.format("%s: MISSING%n",type));
            }
        }
        return sb.toString();
    }

    public boolean hasData(MeasurementType type){
        return data.containsKey(type);
    }

    public Number getData(MeasurementType type){
        return data.get(type);
    }
}
