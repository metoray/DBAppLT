package unwdmi.dbapp;

import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 28-9-15.
 */
public class DatabaseConsumer extends MeasurementPasser {

    private Database db;

    public DatabaseConsumer(BlockingQueue<Measurement> queue) {
        super(queue,"DB");
        db = new Database();
    }

    public void handleMeasurement(Measurement m){
        db.insertMeasurement(m);
    }

}
