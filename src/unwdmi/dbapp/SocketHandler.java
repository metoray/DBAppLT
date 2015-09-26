package unwdmi.dbapp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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
            //Create instance of handler to put measurements on the queue
            DefaultHandler handler = new XMLHandler(queue);
            //Make readers and buffers to read and process socket inputstream
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                //Process stringbuilder buffer when XML header is found
                if (line.startsWith("<?xml")&&sb.length()!=0) {
                    this.parser.parse(new InputSource(new StringReader(sb.toString())), handler);
                    sb = new StringBuilder();
                }
                sb.append(line+"\n");
            }

        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

}
