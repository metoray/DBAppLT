package unwdmi.dbapp;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by metoray on 23-9-15.
 */
public class Server {

    public static void main(String[] args){
        String xml =
                "<?xml version=\"1.0\"?>\n" +
                "<!-- Het weatherdata-element bevat meerdere measurement-elementen -->\n" +
                "<WEATHERDATA>\n" +
                "\t<!-- Het measurement-element bevat de meetgegevens van een weerstation -->\n" +
                "\t<MEASUREMENT>\n" +
                "\t\t<!-- Het station waarvan deze gegevens zijn -->\n" +
                "\t\t<STN>123456</STN>\n" +
                "\t\t\n" +
                "\t\t<!-- Datum van versturen van deze gegevens, formaat: yyyy-mm-dd -->\n" +
                "\t\t<DATE>2009-09-13</DATE>\n" +
                "\t\t\n" +
                "\t\t<!-- Tijd van versturen van deze gegevens, formaat: hh:mm:ss -->\n" +
                "\t\t<TIME>15:59:46</TIME>\n" +
                "\t\t\n" +
                "\t\t<!-- Temperatuur in graden Celsius, geldige waardes van -9999.9 t/m 9999.9 met 1 decimaal -->\n" +
                "\t\t<TEMP>-60.1</TEMP>\n" +
                "\t\t\n" +
                "\t\t<!-- Dauwpunt in graden Celsius, geldige waardes van -9999.9 t/m 9999.9 met 1 decimaal -->\n" +
                "\t\t<DEWP>-58.1</DEWP>\n" +
                "\t\t\n" +
                "\t\t<!-- Luchtdruk op stationsniveau in millibar, geldige waardes van 0.0 t/m 9999.9 met 1 decimaal -->\n" +
                "\t\t<STP>1034.5</STP>\n" +
                "\t\t\n" +
                "\t\t<!-- Luchtdruk op zeeniveau in millibar, geldige waardes van 0.0 t/m 9999.9 met 1 decimaal -->\n" +
                "\t\t<SLP>1007.6</SLP>\n" +
                "\t\t\n" +
                "\t\t<!-- Zichtbaarheid in kilometers, geldige waardes van 0.0 t/m 999.9 met 1 decimaal -->\n" +
                "\t\t<VISIB>123.7</VISIB>\n" +
                "\t\t\n" +
                "\t\t<!-- Windsnelheid in kilometers per uur, geldige waardes van 0.0 t/m 999.9 met 1 decimaal -->\n" +
                "\t\t<WDSP>10.8</WDSP>\n" +
                "\t\t\n" +
                "\t\t<!-- Neerslag in centimeters, geldige waardes van 0.00 t/m 999.99 met 2 decimalen -->\n" +
                "\t\t<PRCP>11.28</PRCP>\n" +
                "\t\t\n" +
                "\t\t<!-- Gevallen sneeuw in centimeters, geldige waardes van -9999.9 t/m 9999.9 met 1 decimaal -->\n" +
                "\t\t<SNDP>11.1</SNDP>\n" +
                "\t\t\n" +
                "\t\t<!--\n" +
                "\t\tGebeurtenissen op deze dag, cummulatief, binair uitgedrukt.\n" +
                "\t\tOpeenvolgend, van meest- naar minst significant:\n" +
                "\t\tVriezen, geeft aan of het gevroren heeft\n" +
                "\t\tRegen, geeft aan of het geregend heeft.\n" +
                "\t\tSneeuw, geeft aan of het gesneeuwd heeft.\n" +
                "\t\tHagel, geeft aan of het gehageld heeft.\n" +
                "\t\tOnweer, geeft aan of er onweer is geweest.\n" +
                "\t\tTornado/windhoos, geeft aan of er een tornado of windhoos geweest is.\n" +
                "\t\t-->\n" +
                "\t\t<FRSHTT>010101</FRSHTT>\n" +
                "\t\t\n" +
                "\t\t<!-- Bewolking in procenten, geldige waardes van 0.0 t/m 99.9 met 1 decimaal -->\n" +
                "\t\t<CLDC>87.4</CLDC>\n" +
                "\t\t\n" +
                "\t\t<!-- Windrichting in graden, geldige waardes van 0 t/m 359 alleen gehele getallen -->\n" +
                "\t\t<WNDDIR>342</WNDDIR>\n" +
                "\t</MEASUREMENT>\n" +
                "</WEATHERDATA>\n";
        //TODO Remove test data
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
            DefaultHandler handler = new XMLHandler();
            parser.parse(new InputSource(new StringReader(xml)),handler);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
