package unwdmi.dbapp;

import java.io.*;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileHandler extends Thread {

    private MeasurementType[] types;
    private File file;
    private Lock lock;
    private BufferedWriter writer;
    private boolean readMode;
    private int id;
    private BlockingQueue<Measurement> queue;

    public FileHandler(File f, boolean writeMode, int id) {
        this.file = f;
        this.lock = new ReentrantLock();
        this.readMode = !writeMode;
        if(!this.readMode){
            enableWrite();
        }
    }

    private  void enableWrite() {
        this.writer = openWriter();
        this.queue = new LinkedBlockingQueue<>();
        this.start();
    }

    private void initFile() throws IOException {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
            writer.append("time");
            types = MeasurementType.values();
            for(MeasurementType type: types){
                writer.append(","+type.getColumnName());
            }
            writer.newLine();
            writer.close();
        }
        else {
            openReader().close();
        }
    }

    private BufferedReader openReader() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));

            String[] columnNames = reader.readLine().split(",");
            this.types = new MeasurementType[columnNames.length-1];
            for (int i=0; i<types.length; i++) {
                this.types[i] = MeasurementType.fromDBName(columnNames[i+1]);
            }

            return reader;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedWriter openWriter() {
        try {
            initFile();
            return new BufferedWriter(new FileWriter(this.file, true));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void writeMeasurement(Measurement m) {
        lock.lock();
        try {
            writer.append(Long.toString(m.getDate().getTime()));

            for(MeasurementType type: this.types) {
                writer.append(","+m.getData(type));
            }
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public List<Measurement> getMeasurements(Date from, Date till) {
        LinkedList<Measurement> results = new LinkedList<>();
        long fromTime = from.getTime();
        long tillTime = till.getTime();
        try {
            lock.lock();
            if (!readMode) {
                writer.close();
            }

            BufferedReader reader = this.openReader();
            if(reader==null) return results;

            String line;
            while ((line = reader.readLine())!=null) {
                String[] columns = line.split(",");
                long date = Long.parseLong(columns[0]);
                if(date>=fromTime && date<=tillTime) {
                    EnumMap<MeasurementType,Number> props = new EnumMap<>(MeasurementType.class);
                    for(int i=1; i<columns.length; i++){
                        MeasurementType type = types[i-1];
                        props.put(type,type.parse(columns[i]));
                    }
                    Date d = new Date();
                    d.setTime(date);
                    Measurement m = new Measurement(this.id, d, props);
                    results.add(m);
                }
            }

            if(!readMode) {
                writer = this.openWriter();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return results;
    }

    public BlockingQueue<Measurement> getQueue() {
        if(this.readMode){
            this.lock.lock();
            this.readMode = false;
            this.enableWrite();
            this.lock.unlock();
        }
        return this.queue;
    }

    public void run(){
        try {
            while (true) {
                Measurement m = this.queue.poll(5, TimeUnit.SECONDS);
                if(m==null) break;

                writeMeasurement(m);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("<<<EOT>>>");
    }
}
