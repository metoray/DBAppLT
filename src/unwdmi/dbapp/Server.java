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

    public Server(){
        stations = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        instance.run();
    }

    public void run() throws IOException {
        ServerSocket sock = new ServerSocket(7789);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        while(true) {
            try {
                Socket s = sock.accept();
                SAXParser parser = factory.newSAXParser();
                BlockingQueue<Measurement> queue = new LinkedTransferQueue<>();
                SocketHandler sh = new SocketHandler(s, parser, queue);
                MeasurementPasser mp = new MeasurementPasser(queue);
                mp.start();
                sh.start();
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Station getStation(int id){
        if(!this.stations.containsKey(id)){
            this.stations.put(id,new Station(id));
        }
        return this.stations.get(id);
    }
}
