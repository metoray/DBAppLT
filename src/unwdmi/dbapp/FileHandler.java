package unwdmi.dbapp;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;
import java.io.File;

public class FileHandler {

	private String fileName;
	private String folderPath;
	private File file;
	private BufferedWriter fileWriter = null;
	private BufferedReader fileReader = null;
	private MeasurementType[] types = MeasurementType.values();
	private final Semaphore semaphore = new Semaphore(1);

	private static final int TIME = 0;
	private static final int TEMP = 1;
	private static final int DEWP = 2;
	private static final int STP = 3;
	private static final int SLP = 4;
	private static final int VISIB = 5;
	private static final int WDSP = 6;
	private static final int PRCP = 7;
	private static final int SNDP = 8;
	private static final int CLDC = 9;
	private static final int WNDDIR = 10;
	private static final int FRSHTT = 11;

	/*
	 * check if file exists, else make it
	 * write data to file: open in write mode, then write in csv format
	 * read data from file: close file, open in read mode, read, close, open in write mode
	 * 
	 */

	public FileHandler(String fileName, String folderPath){
		this.folderPath = folderPath;
		this.fileName = fileName;
		file = ensureFileExists();

		//FileHandler is in writing mode by default
		try{
			writeMode();
		}catch(Exception e){
			System.out.println("Error in initiating read/write request");
		}
	}
	public File ensureFileExists(){
		File newFile = new File(folderPath + fileName);
		if(!newFile.isFile()){
			try{
				newFile.createNewFile();
			}catch(Exception e){
				System.out.println("Error in creating new file");
				e.printStackTrace();
			}
		}
		return newFile;
	}

	public void addMeasurement(Measurement m){
		try{
			semaphore.acquire();

			//send measurement time MM:SS
			SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
			String time = timeFormat.format(m.getDate());
			fileWriter.append(time);
			//send data
			for(MeasurementType type: types){
				fileWriter.append(",");
				fileWriter.append(m.getData(type).toString());

			}
			fileWriter.append("\n");
			semaphore.release();
		}
		catch(Exception e){
			System.out.println("Error in csv writer.");
			e.printStackTrace();
		}
	}

	public String readMeasurement(String requestedTime, MeasurementType type){
		try{
			semaphore.acquire();

			readMode();

			//read info from file line by line
			String info;
			while((info = fileReader.readLine()) != null){
				String[] tokens = info.split(",");
				String tokenTime = tokens[TIME];
				if(requestedTime == tokenTime){
					for(String token: tokens){
						if(token == type.toString()){
							return token;
						}
					}
				}
			}
			writeMode();
			semaphore.release();
		}
		catch(Exception e){
			System.out.println("Error in csv reader");
		}

		System.out.println("Requested measurement could not be found.");
		return null;
	}


	public String readLastMeasurement(MeasurementType type){
		try{
			semaphore.acquire();

			readMode();

			//read through file until the last line
			String info;
			String lastLine = "";
			while((info = fileReader.readLine()) != null){
				lastLine = info;
			}
			String[] tokens = lastLine.split(",");
			for(String token: tokens){
				if(token == type.toString()){
					return token;
				}
			}

			writeMode();
			semaphore.release();
		}
		catch(Exception e){
			System.out.println("Error in csv reader");
		}

		System.out.println("Requested measurement could not be found.");
		return null;
	}



	public void readMode(){
		//closes writer, opens reader
		try{
			fileWriter.flush();
			fileWriter.close();
			fileReader = new BufferedReader( new FileReader(file));
		}
		catch(Exception e){
			System.out.println("Error while switching FileHandler to read mode");
		}

	}

	public void writeMode(){
		try{
			//closes reader, reopens writer
			fileReader.close();
			fileWriter = new BufferedWriter(new FileWriter(file));
		}
		catch(Exception e){
			System.out.println("Error while switching FileHandler to write mode");
		}	
	}
	
	public void close(){
		try{
			//closes writer
			fileWriter.flush();
			fileWriter.close();
		}catch(Exception e){
			System.out.println("error while closing");
			e.printStackTrace();
		}
	}
}
