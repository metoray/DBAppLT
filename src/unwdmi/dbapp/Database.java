package unwdmi.dbapp;
import java.sql.*;
import java.util.EnumMap;
import java.util.LinkedList;

public class Database {
	
	private Connection conn;
	
	public Database(){
		try{
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/unwdmi";
			Class.forName(myDriver);
			this.conn = DriverManager.getConnection(myUrl, "root", "pass");
		}catch(Exception ex)
		{
			System.out.println(ex);
		}
		
	}
	public void insertMeasurement(Measurement measurement) {

	try{
		MeasurementType[] types = MeasurementType.values();
        String names = "(stn, date";
        for(MeasurementType type: types){
            names += ","+type.getColumnName();
        }
        names += ")";
        //MAKE A QUERY USING THE NAMES VARIABLE HERE
        String query = "INSERT INTO Measurements "+names+" VALUES(";
        for(MeasurementType type: types){
        	query +="?,";
        }
        query = query.substring(0, query.length()-1);
        query += ")"; 
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1,measurement.getStationID());
        preparedStmt.setTimestamp(2, new java.sql.Timestamp(measurement.getDate().getTime()));
        int i=2;
        for(MeasurementType type: types){
    	   i++;
    	   Number value = measurement.getData(type);
           if(value instanceof Integer){
        	   preparedStmt.setInt(i,value.intValue());
           }
           else{
        	   preparedStmt.setDouble(i,value.doubleValue());
        	   }
           }
        }
		catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	public static void selectStatement(String colum){
		
		try{
			//prepare connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/test";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "pass");
			//Select query
			String query ="select "+colum+(" from measurment");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			//loop trough resultlist
			while(rs.next()){
				
				System.out.println(rs.getDouble(colum));
			}
			
	      }
		catch(Exception ex){
			System.out.println(ex);
		}
		
	}
}
