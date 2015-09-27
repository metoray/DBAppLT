package unwdmi.dbapp;
import java.sql.*;

public class Database {

	public static void main(String[] args){
		insertMeasurement(1,2,3,4,5,6,7,8,9,10);
	}
	
	public static void insertMeasurement(Measurement measurement) {

		try{
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/test";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "pass");
			
			String query ="insert into measurement (TEMP, DEWP, STP, SLP, VISIB, WDSP, PRCP, SNDP,"
					+ " CLDC, WINDIR) Values (?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
   	      	
			
			preparedStmt.setDouble (1, TEMP);
   	      	preparedStmt.setDouble (2, dewp);
   	      	preparedStmt.setDouble (3, stp);
   	     	preparedStmt.setDouble (4, slp);
   	  		preparedStmt.setDouble (5, visib);
   			preparedStmt.setDouble (6, wdsp);
   			preparedStmt.setDouble (7, prcp);
   			preparedStmt.setDouble (8, sndp);
   			preparedStmt.setDouble (9, cldc);
   			preparedStmt.setDouble (10, windir);
   			
   			System.out.println("shits done");
	      }
		catch(Exception ex){
			System.out.println(ex);
		}
	}
	
	public static void selectStatement(String colum){
		
		try{
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/test";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "pass");
			
			String query ="select "+colum+(" from measurment");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				
				System.out.println(rs.getDouble(colum));
			}
			
	      }
		catch(Exception ex){
			System.out.println(ex);
		}
		
	}
}
