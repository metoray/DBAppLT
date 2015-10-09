package unwdmi.dbapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

//TODO: Replace 'Thread' with actual filehandler objects
public class StationFolderManager {

    private static ConcurrentHashMap<String,Thread> fileHandlers = new ConcurrentHashMap<>();

    public static String DATA_FOLDER = "data";

    public static String getMeasurementFolder(Measurement m) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return DATA_FOLDER + File.separator + m.getStationID() + File.separator + format.format(m.getDate()) + File.separator;
    }

    public static String getFileName(Measurement m){
        Calendar c = Calendar.getInstance();
        c.setTime(m.getDate());
        return c.get(Calendar.HOUR_OF_DAY)+".csv";
    }

    public static Thread getFileHandler(Measurement m){
        return getFileHandler(getMeasurementFolder(m)+getFileName(m));
    }

    public static Thread getFileHandler(String path) {
        if(!fileHandlers.containsValue(path)){
            return fileHandlers.get(path);
        }
        else {
            Thread t = new Thread();
            t.start();
            fileHandlers.put(path,t);
            return t;
        }
    }

}
