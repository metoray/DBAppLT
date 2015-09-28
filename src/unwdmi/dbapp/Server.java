package unwdmi.dbapp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by metoray on 23-9-15.
 */
public class Server {

    private HashMap<Integer,Station> stations;
    public static final Server instance = new Server();

    private static final int PASSER_THREADS = 8;
    private BlockingQueue<Measurement>[] queues;

    private static final int DB_PASSER_THREADS = 4;
    private BlockingQueue<Measurement>[] dbQueues;
    private int dbqIndex = 0;

    public Server(){
        stations = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        instance.run();
    }

    public void run() throws IOException {
        ServerSocket sock = new ServerSocket(7789);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        queues = new BlockingQueue[PASSER_THREADS];
        dbQueues = new BlockingQueue[DB_PASSER_THREADS];
        for(int i=0; i<PASSER_THREADS; i++){
            BlockingQueue<Measurement> queue = new LinkedTransferQueue<>();
            MeasurementPasser mp = new MeasurementPasser(queue,"STN");
            mp.start();
            queues[i] = queue;
        }
        for(int i=0; i<DB_PASSER_THREADS; i++){
            BlockingQueue<Measurement> queue = new LinkedTransferQueue<>();
            MeasurementPasser mp = new MeasurementPasser(queue,"DB");
            mp.start();
            dbQueues[i] = queue;
        }
        QueueMonitor qm = new QueueMonitor(queues);
        qm.start();
        QueueMonitor dbqm = new QueueMonitor(dbQueues);
        dbqm.start();
        int queueIndex = 0;
        while(true) {
            try {
                Socket s = sock.accept();
                SAXParser parser = factory.newSAXParser();
                SocketHandler sh = new SocketHandler(s, parser, queues[queueIndex]);
                sh.start();
                queueIndex++;
                queueIndex%=queues.length;
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Station getStation(int id){
        if(!this.stations.containsKey(id)){
            this.stations.put(id,new Station(id,getDBPasser()));
        }
        return this.stations.get(id);
    }

    public BlockingQueue<Measurement> getDBPasser(){
        dbqIndex++;
        dbqIndex%=dbQueues.length;
        return dbQueues[dbqIndex];
    }
}
