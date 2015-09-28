package unwdmi.dbapp;
import java.sql.*;
import java.util.EnumMap;
import java.util.LinkedList;

public class Database {
	
	private Connection conn;
	
	public Database(){
		try{
			String dbDriver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/unwdmi";
			Class.forName(dbDriver);
			this.conn = DriverManager.getConnection(url, "unwdmi", ":(");
		}catch(Exception ex)	//gotta catch 'em all!
		{
			ex.printStackTrace();
		}
		
	}

	public void insertMeasurement(Measurement measurement) {
		try {
			MeasurementType[] types = MeasurementType.values();
			String names = "(stn, time";
			for (MeasurementType type : types) {
				names += "," + type.getColumnName();
			}
			names += ")";
			String query = "INSERT INTO Measurements "+names+" VALUES(?,?,";
			for (int i=0; i<types.length; i++) {
				query += "?"+((i==types.length-1)?"":",");
			}
			query += ")";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, measurement.getStationID());
			preparedStmt.setTimestamp(2, new java.sql.Timestamp(measurement.getDate().getTime()));
			int i = 2;
			for (MeasurementType type : types) {
				i++;
				Number value = measurement.getData(type);
				if (value instanceof Integer) {
					preparedStmt.setInt(i, value.intValue());
				} else {
					preparedStmt.setDouble(i, value.doubleValue());
				}
			}
			preparedStmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void selectStatement(int stationID, Date date){
		
		try{
			
			//Select query
			String query ="select * from measurment WHERE stn = "+stationID+" AND time ="+date.getTime();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			//loop trough resultlist
			while(rs.next()){
				System.out.println("ID: "+rs.getInt("ID"));
				System.out.println("stn: "+rs.getInt("stn"));
				System.out.println("time: "+rs.getTimestamp("time"));
				System.out.println("temprature: "+rs.getDouble("temprature"));
				System.out.println("dewpoint: "+rs.getDouble("dewpoint"));
				System.out.println("stationairpressure: "+rs.getDouble("stationairpressure"));
				System.out.println("seaairpressure: "+rs.getDouble("seaairpressure"));
				System.out.println("visibility: "+rs.getDouble("visibility"));
				System.out.println("windspeed: "+rs.getDouble("windspeed"));
				System.out.println("rainfall: "+rs.getDouble("rainfall"));
				System.out.println("snowdepth: "+rs.getDouble("snowdepth"));
				System.out.println("events: "+rs.getInt("events"));
				System.out.println("cloudcoverage: "+rs.getDouble("cloudcoverage"));
				System.out.println("winddirection: "+rs.getInt("winddirection"));
			}
			
	      }
		catch(Exception ex){	//gotta catch 'em all!
			ex.printStackTrace();
		}
		
	}
}
