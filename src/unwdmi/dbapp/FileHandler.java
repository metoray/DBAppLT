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

	public FileHandler(String path) {
		file = ensureFileExists(path);

		//FileHandler is in writing mode by default
		try{
			writeMode();
		}catch(Exception e){
			System.out.println("Error in initiating read/write request");
		}
	}
	public File ensureFileExists(String path){
		File newFile = new File(path);
		if(!newFile.isFile()){
			try{
				newFile.getParentFile().mkdirs();
				newFile.createNewFile();
				BufferedWriter fos = new BufferedWriter(new FileWriter(newFile));
				this.types = MeasurementType.values();
				fos.append("time");
				for(MeasurementType type: types) {
					fos.append(","+type.getColumnName());
				}
				fos.append('\n');
				fos.close();
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
			fileWriter.flush();
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
			String data;
			while((data = fileReader.readLine()) != null){
				String[] tokens = data.split(",");
				int typeToken = convertTypeToToken(type);
				if(requestedTime == tokens[TIME]){
					return tokens[typeToken];
				}
			}
			writeMode();
			semaphore.release();
		}
		catch(Exception e){
			System.out.println("Error in csv reader");
		}

		System.out.println("Error while trying to find requested measurement.");
		return null;
	}


	public String readLastMeasurement(MeasurementType type){
		try{
			semaphore.acquire();

			readMode();

			//read through file until the last line
			String data;
			String lastLine = "";
			while((data = fileReader.readLine()) != null){
				lastLine = data;
			}
			String[] tokens = lastLine.split(",");
			data = tokens[convertTypeToToken(type)];
			
			writeMode();
			semaphore.release();
			return data;
		}
		catch(Exception e){
			System.out.println("Error in csv reader");
		}

		System.out.println("Error while trying to find requested measurement.");
		return null;
	}
	
	public ArrayList<String> readAllFromType(MeasurementType type){
		try{
			semaphore.acquire();

			readMode();

			//read info from file line by line
			String data;
			ArrayList<String> returnData = new ArrayList<String>();
			int typeToken = convertTypeToToken(type);
			while((data = fileReader.readLine()) != null){
				String[] tokens = data.split(",");
				returnData.add(tokens[typeToken]);
				
			}
			
			writeMode();
			semaphore.release();
			return returnData;
		}
		catch(Exception e){
			System.out.println("Error in csv reader");
		}

		System.out.println("Error while trying to find requested measurement.");
		return null;
	}
	
	private int convertTypeToToken(MeasurementType type){
		int answer;
		switch(type.toString()){
		case "temperature": answer = TEMP;
		break;
		case "dewpoint": answer = DEWP;
		break;
		case "stationairpressure": answer = STP;
		break;
		case "seaairpressure": answer = SLP;
		break;
		case "visibility": answer = VISIB;
		break;
		case "windspeed": answer = WDSP;
		break;
		case "rainfall": answer = PRCP;
		break;
		case "snowdepth": answer = SNDP;
		break;
		case "cloudcoverage": answer = CLDC;
		break;
		case "winddirection": answer = WNDDIR;
		break;
		case "events": answer = FRSHTT;
		break;
		default: answer = TIME;
		}
	return answer;
	}



	public void readMode(){
		//closes writer, opens reader
		try{
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
			fileWriter.close();
		}catch(Exception e){
			System.out.println("error while closing");
			e.printStackTrace();
		}
	}
}
