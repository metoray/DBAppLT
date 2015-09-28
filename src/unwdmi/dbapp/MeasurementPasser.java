package unwdmi.dbapp;

import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 26-9-15.
 */
public class MeasurementPasser extends Thread {

    private BlockingQueue<Measurement> queue;

    public MeasurementPasser(BlockingQueue<Measurement> queue, String function){
        this.setDaemon(true);
        this.setName("Passer("+function+")-"+getId());
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
