package unwdmi.dbapp;

import java.util.Iterator;
import java.util.List;

/**
 * Created by metoray on 24-9-15.
 */
public enum MeasurementType {
    TEMP("temperature"),
    DEWP("dewpoint"),
    STP("stationairpressure"),
    SLP("seaairpressure"),
    VISIB("visibility"),
    WDSP("windspeed"),
    PRCP("rainfall"),
    SNDP("snowdepth"),
    CLDC("cloudcoverage"),
    WNDDIR("winddirection"),
    FRSHTT("???");

    private String dbName;

    MeasurementType(String dbName){
        this.dbName = dbName;
    }

    public String getColumnName(){
        return this.dbName;
    }

    public Number parse(String s){
        try {
            switch (this) {
                case WNDDIR:
                    return Integer.parseInt(s);
                case FRSHTT:
                    return Integer.parseInt(s,2);
                default:
                    return Double.parseDouble(s);
            }
        }
        catch (NumberFormatException ex){
            return null;
        }
    }

    /**
     * Extrapolates a missing value based on data list
     * @param data previous values to extrapolate from
     * @return extrapolated value
     */
    public double extrapolate(List<Number> data){
        if(data.size()==0) return 0.0;
        double totalChange = 0;
        double lastValue = (Double)data.get(0);
        Iterator<Number> i = data.iterator();

        while(i.hasNext()){
            double d = (Double)i.next();
            totalChange += d- lastValue;
            lastValue = d;
        }
        double avgChange = totalChange / data.size();
        return lastValue + avgChange;
    }

    /**
     * Extrapolates new temperature, then calculates if measured temperature is within +-20% bounds
     * 	@param data previous values to extrapolate from
     * 	@param temperature new value to be tested
     * 	@return the temperature to be stored
     */
    public double extrapolateTemperature(List<Number> data, double temperature){
        if(data.size()==0) return temperature;
        double extrapolatedValue = extrapolate(data);
        if(temperature > extrapolatedValue*1.2){
            return extrapolatedValue * 1.2;
        }
        if(temperature < extrapolatedValue*0.8){
            return extrapolatedValue * 0.8;
        }
        return temperature;
    }

    /**
     * Extrapolates a new wind direction based on previous value.
     * This method was written to account for the fact that directions are circular values
     * @param data previous values to extrapolate from
     * @return the extrapolated value
     */
    public int extrapolateWindDir(List<Number> data){
        int totalChange = 0;
        int last = (int)data.get(0);
        Iterator<Number> it = data.iterator();

        while(it.hasNext()){
            int d = (int) it.next();
            int change = d - last;
            last = d;
            if(change > 180)
                change -= 180;
            if(change < -180)
                change += 180;
            totalChange += change;
        }
        int avgChange = totalChange / data.size();
        return ((last + avgChange) % 360 + 360) % 360;
    }

    /**
     * Dethod for extrapolating new value for any type
     * @param data data to extrapolate from
     * @return new value
     */
    public Number extrapolateValue(List<Number> data) {
        if(this==WNDDIR) {
            return extrapolateWindDir(data);
        }
        if(this==FRSHTT){
            return new Integer(0);
        }
        return extrapolate(data);
    }

    /**
     * Correct value if type is TEMP
     * @param data data to extrapolate from
     * @param value value to correct
     * @return corrected value
     */
    public Number correctValue(List<Number> data, Number value) {
        if(this==TEMP) {
            return extrapolateTemperature(data,(double)value);
        }
        else if(this==FRSHTT){
            return value;
        }
        return value;
    }
}
