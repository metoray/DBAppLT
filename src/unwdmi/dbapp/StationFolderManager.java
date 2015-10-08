package unwdmi.dbapp;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by metoray on 8-10-15.
 */
public class StationFolderManager {

    public static String DATA_FOLDER = "data";

    public static String getMeasurementFolder(Measurement m) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return DATA_FOLDER + File.separator + m.getStationID() + File.separator + format.format(m.getDate()) + File.separator;
    }

}
