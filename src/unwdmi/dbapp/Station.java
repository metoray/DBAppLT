package unwdmi.dbapp;

/**
 * Created by metoray on 24-9-15.
 */
public class Station {

    private int id;
    private Number[][] measurements;

    public Station(int id){
        this.id = id;
        this.measurements = new Number[MeasurementType.values().length][];
        for (Number[] arr: this.measurements){
            arr = new Number[30];
        }
    }

}
