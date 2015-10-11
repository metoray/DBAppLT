package unwdmi.dbapp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;

public class FileHandler extends Thread {

	private File file;
	private BufferedWriter fileWriter = null;
	private BufferedReader fileReader = null;
	private MeasurementType[] types;
	private final Semaphore semaphore = new Semaphore(1);

	private static final int TIME = 0;

	public FileHandler(String path) {
		file = ensureFileExists(path);

		//FileHandler is in writing mode by default
		writeMode();
	}

	public File ensureFileExists(String path) {
		File newFile = new File(path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(newFile));
			String[] columns = br.readLine().split(",");
			this.types = new MeasurementType[columns.length - 1];
			for (int i = 0; i < this.types.length; i++) {
				this.types[i] = MeasurementType.fromDBName(columns[i + 1]);
			}
			br.close();

		} catch (FileNotFoundException ex) {
			try {
				newFile.getParentFile().mkdirs();
				newFile.createNewFile();
				BufferedWriter fos = new BufferedWriter(new FileWriter(newFile));
				this.types = MeasurementType.values();
				fos.append("time");
				for (MeasurementType type : types) {
					fos.append("," + type.getColumnName());
				}
				fos.append('\n');
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFile;
	}

	public void addMeasurement(Measurement m){
		try{
			semaphore.acquire();
		}catch(InterruptedException ie){
			System.out.println("Acquiring semaphore interrupted while adding measurement.");
		}
		//send measurement time MM:SS
		SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
		String time = timeFormat.format(m.getDate());
		try{
			fileWriter.append(time);
			//send data
			for(MeasurementType type: types){
				fileWriter.append(",");
				fileWriter.append(m.getData(type).toString());

			}
			fileWriter.append("\n");
			fileWriter.flush();
			semaphore.release();
		}catch(IOException ie){
			System.out.println("IOException while adding measurement.");
		}
	}


	public String readMeasurement(String requestedTime, MeasurementType type) {
		try{
			semaphore.acquire();
		}catch(InterruptedException ie){
			System.out.println("Acquiring semaphore interrupted during read.");
		}

		readMode();

		try{
			//read info from file line by line
			String data;
			while ((data = fileReader.readLine()) != null) {
				String[] tokens = data.split(",");
				int typeToken = getTypeIndex(type);
				if (requestedTime == tokens[TIME]) {
					return tokens[typeToken];
				}
			}
			writeMode();
			semaphore.release();
		}
		catch(IOException e){
			System.out.println("IOEXception while reading measurement");
		}

		System.out.println("Could not find requested measurement.");
		return null;
	}


	public String readLastMeasurement(MeasurementType type) {
		try{
			semaphore.acquire();
		}catch(InterruptedException ie){
			System.out.println("Acquiring semaphore interrupted during read.");
		}
		
		readMode();

		try{
			//read through file until the last line
			String data;
			String lastLine = "";
			while ((data = fileReader.readLine()) != null) {
				lastLine = data;
			}
			String[] tokens = lastLine.split(",");
			data = tokens[getTypeIndex(type)];

			writeMode();
			semaphore.release();
			return data;
		}
		catch(IOException e){
			System.out.println("IOEXception while reading measurement");
		}

		System.out.println("Could not find requested measurement.");
		return null;
	}

	public ArrayList<String> readAllFromType(MeasurementType type) {
		try{
			semaphore.acquire();
		}catch(InterruptedException ie){
			System.out.println("Acquiring semaphore interrupted during read.");
		}
		readMode();

		try{
			//read info from file line by line
			String data;
			ArrayList<String> returnData = new ArrayList<String>();
			int typeIndex = getTypeIndex(type);
			while ((data = fileReader.readLine()) != null) {
				String[] tokens = data.split(",");
				returnData.add(tokens[typeIndex]);

			}

			writeMode();
			semaphore.release();
			return returnData;
		}
		catch(IOException e){
			System.out.println("IOEXception while reading measurement");
		}

		System.out.println("Could not find requested measurement.");
		return null;
	}

	/**
	 * Returns index of type in array from file
	 *
	 * @param type requested type index
	 * @return index of type in input array
	 */
	private int getTypeIndex(MeasurementType type) {
		for (int idx = 0; idx < this.types.length; idx++) {
			if (this.types[idx] == type) {
				return idx + 1;
			}
		}
		return -1;
	}


	public void readMode() {
		//closes writer, opens reader
		try {
			fileWriter.close();
			fileReader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			System.out.println("Error while switching FileHandler to read mode");
		}

	}

	public void writeMode() {
		try {
			//closes reader, reopens writer
			fileReader.close();
			fileWriter = new BufferedWriter(new FileWriter(file));
		} catch (Exception e) {
			System.out.println("Error while switching FileHandler to write mode");
		}
	}

	public void close() {
		try {
			//closes writer
			fileWriter.close();
		} catch (Exception e) {
			System.out.println("error while closing");
			e.printStackTrace();
		}
	}
}
