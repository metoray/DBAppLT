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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by metoray on 23-9-15.
 */
public class Server {

    public static void p(Object o){
        System.out.println(o);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket sock = new ServerSocket(7789);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        while(true) {
            try {
                Socket s = sock.accept();
                SAXParser parser = factory.newSAXParser();
                ConcurrentLinkedQueue<Measurement> queue = new ConcurrentLinkedQueue<>();
                SocketHandler sh = new SocketHandler(s, parser, queue);
                sh.start();
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
