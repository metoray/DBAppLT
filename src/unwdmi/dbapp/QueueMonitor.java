package unwdmi.dbapp;

import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 27-9-15.
 */
public class QueueMonitor extends Thread {

    private final BlockingQueue[] queues;

    public QueueMonitor(BlockingQueue[] queues){
        this.queues = queues;
        this.setName("Monitor");
        this.setDaemon(true);
    }

    public void run(){
        while(true) {
            for (int i = 0; i < queues.length; i++) {
                System.out.println(queues[i].size() + " measurements left on queue " + i);
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
