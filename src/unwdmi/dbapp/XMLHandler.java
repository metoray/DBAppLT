package unwdmi.dbapp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.concurrent.BlockingQueue;

/**
 * Created by metoray on 23-9-15.
 * metoray was here!
 */
public class XMLHandler extends DefaultHandler {

    private StringBuffer acc = new StringBuffer();
    private EnumMap<MeasurementType, Number> properties;
    private String dateString;
    private String timeString;
    private int stationID;
    private BlockingQueue<Measurement> queue;

    public XMLHandler(BlockingQueue<Measurement> queue){
        this.queue = queue;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        acc.setLength(0); //remove all characters from acc to read text from element
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            properties = new EnumMap<>(MeasurementType.class);
            dateString = null;
            timeString = null;
            stationID = 0;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        acc.append(ch, start, length);
    }

    /**
     * Called when element ends, will set properties for the next measurement or will complete measurement reading.
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //Handle end of element using name
        switch (qName.toUpperCase()) {
            //collect data and put measurement in queue
            case "MEASUREMENT":
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date;
                try {
                    date = format.parse(this.dateString + " " + this.timeString);
                } catch (ParseException e) {
                    date = null;
                }
                Measurement measurement = new Measurement(stationID, date, properties);
                try {
                    queue.put(measurement);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "DATE":
                dateString = acc.toString();
                break;
            case "TIME":
                timeString = acc.toString();
                break;
            case "STN":
                try {
                    stationID = Integer.parseInt(acc.toString());
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                break;
            default:
                for (MeasurementType type : MeasurementType.values()) {
                    if (type.name().equalsIgnoreCase(qName)) {
                        this.properties.put(type, type.parse(acc.toString()));
                    }
                }
                break;
        }
    }
}
