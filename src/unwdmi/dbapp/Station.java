package unwdmi.dbapp;

import java.util.EnumMap;
import java.util.LinkedList;

/**
 * Created by metoray on 24-9-15.
 */
public class Station {

    private int id;
    private EnumMap<MeasurementType,LinkedList<Number>> measurements;

    public Station(int id){
        this.id = id;
        this.measurements = new EnumMap<MeasurementType,LinkedList<Number>>(MeasurementType.class);
        for(MeasurementType type: MeasurementType.values()){
            this.measurements.put(type,new LinkedList<Number>());
        }
    }

    public void addMeasurement(Measurement measurement){
        System.out.println(String.format("Station %d receiving measurement!",id));
        System.out.println(measurement.toString());
        for (MeasurementType type: measurements.keySet()){
            LinkedList<Number> data = measurements.get(type);
            Number value;
            if(measurement.hasData(type)){
                value = measurement.getData(type);
                value = type.correctValue(data,value);
            }
            else {
                value = type.extrapolateValue(data);
                data.push(value);
            }
            data.push(value);

            if(data.size()>30){
                data.pop();
            }
        }
    }

}
