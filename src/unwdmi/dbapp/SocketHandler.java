package unwdmi.dbapp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by metoray on 25-9-15.
 */
public class SocketHandler extends Thread {

    private final SAXParser parser;
    private Socket sock;
    private ConcurrentLinkedQueue<Measurement> queue;

    public SocketHandler(Socket sock, SAXParser parser,ConcurrentLinkedQueue<Measurement> queue){
        this.sock = sock;
        this.parser = parser;
        this.queue = queue;
        this.setDaemon(true);
    }

    public void run(){
        try {
            InputSource src = new InputSource(new InputStreamReader(this.sock.getInputStream()));
            DefaultHandler handler = new XMLHandler(queue);
            this.parser.parse(src,handler);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

}
