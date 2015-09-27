package unwdmi.dbapp;

import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 26-9-15.
 */
public class MeasurementPasser extends Thread {

    private BlockingQueue<Measurement> queue;

    public MeasurementPasser(BlockingQueue<Measurement> queue){
        this.setDaemon(true);
        this.setName("Passer-"+getId());
        this.setPriority(Thread.MAX_PRIORITY);
        this.queue = queue;
    }

    public void run(){
        while(true){
            try {
                Measurement m = queue.take();
                int sid = m.getStationID();
                Station station = Server.instance.getStation(sid);
                station.addMeasurement(m);
                Measurement correctedMeasurement = station.getLastMeasurement();
                // pass correctedMeasurement to database saver
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
