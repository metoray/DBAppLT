package unwdmi.dbapp;

/**
 * Created by metoray on 24-9-15.
 */
public enum MeasurementType {
    TEMP,
    DEWP,
    STP,
    SLP,
    VISIB,
    WDSP,
    PRCP,
    SNDP,
    CLDC,
    WNDDIR;

    public Number parse(String s){
        try {
            switch (this) {
                case WNDDIR:
                    return Integer.parseInt(s);
                default:
                    return Float.parseFloat(s);
            }
        }
        catch (NumberFormatException ex){
            return null;
        }
    }
}
