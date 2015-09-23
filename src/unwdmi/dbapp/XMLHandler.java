package unwdmi.dbapp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;

/**
 * Created by metoray on 23-9-15.
 */
public class XMLHandler extends DefaultHandler {

    private StringBuffer acc = new StringBuffer();
    private EnumMap<Measurement.Type, Number> properties;
    private String dateString;
    private String timeString;
    private int stationID;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        System.out.println(qName); //TODO Remove
        acc.setLength(0); //remove all characters from acc to read text from element
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            properties = new EnumMap<>(Measurement.Type.class);
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
        System.out.println("Trying to set " + qName + " to " + acc.toString());
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = format.parse(this.dateString + " " + this.timeString);
            } catch (ParseException e) {
                date = null;
            }
            Measurement measurement = new Measurement(stationID, date, properties);
            System.out.println(measurement.toString()); //TODO Handle measurement object
        } else if (qName.equalsIgnoreCase("DATE")) {
            dateString = acc.toString();
        } else if (qName.equalsIgnoreCase("TIME")) {
            timeString = acc.toString();
        } else if (qName.equalsIgnoreCase("STN")) {
            try {
                stationID = Integer.parseInt(acc.toString());
            }
            catch (NumberFormatException ex){}
        }
        for (Measurement.Type type : Measurement.Type.values()) {
            if (type.name().equalsIgnoreCase(qName)) {
                this.setProperty(type, acc.toString());
            }
        }
    }

    private void setProperty(Measurement.Type type, String str) {
        Number number;
        try {
            number = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            try {
                number = Double.parseDouble(str);
            } catch (NumberFormatException ex) {
                number = Double.NaN;
            }
        }
        this.properties.put(type, number);
    }
}
