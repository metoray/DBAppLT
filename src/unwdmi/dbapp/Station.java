package unwdmi.dbapp;

import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 24-9-15.
 */
public class Station {

    private int id;
    private EnumMap<MeasurementType,LinkedList<Number>> measurements;
    private Date lastDate;
    private BlockingQueue<Measurement> outputQueue;

    public Station(int id, BlockingQueue<Measurement> outputQueue){
        this.id = id;
        this.measurements = new EnumMap<MeasurementType,LinkedList<Number>>(MeasurementType.class);
        this.outputQueue = outputQueue;
        for(MeasurementType type: MeasurementType.values()){
            this.measurements.put(type,new LinkedList<Number>());
        }
    }

    public synchronized void addMeasurement(Measurement measurement){
        lastDate = measurement.getDate();
        for (MeasurementType type: measurements.keySet()){
            LinkedList<Number> data = measurements.get(type);
            Number value;
            if(measurement.hasData(type)){
                value = measurement.getData(type);
                value = type.correctValue(data,value);
            }
            else {
                value = type.extrapolateValue(data);
            }
            data.add(value);

            if(data.size()>30){
                data.pop();
            }
        }
        try {
            outputQueue.put(getLastMeasurement());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized Measurement getLastMeasurement(){
        EnumMap<MeasurementType,Number> data = new EnumMap<MeasurementType, Number>(MeasurementType.class);
        for(Map.Entry<MeasurementType,LinkedList<Number>> entry: measurements.entrySet()){
            data.put(entry.getKey(),entry.getValue().peekLast());
        }
        return new Measurement(this.id,lastDate,data);
    }

}
