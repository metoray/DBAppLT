
package unwdmi.dbapp;
import java.util.*;

//extrapolate methods. temporarily saved in a separate class
public class ExtrapolationMethodStorage {

	// Double ArrayList<Double> data
	// Extrapolates and returns the new value based on the dataset.
	// 
	public double extrapolate(ArrayList<Double> data){
		double avgChange;
		double totalChange = 0;
		double lastValue = data.get(0);
		Iterator<Double> i = data.iterator();

		while(i.hasNext()){
			double d = i.next();
			totalChange += d- lastValue;
			lastValue = d;
		}
		avgChange = totalChange / data.size();
		return lastValue + avgChange;
	}
	/*	double temperature
	 * 	ArrayList<Double> data
	 *	Extrapolates new temperature, then calculates if measured temperature is within +-20% bounds
	 */
	public double extrapolateTemerature(ArrayList<Double> data, double temperature){
		double extrapolatedValue = extrapolate(data);
		if(temperature > extrapolatedValue*1.2){
			return extrapolatedValue * 1.2;
		}
		if(temperature < extrapolatedValue*0.8){
			return extrapolatedValue * 0.8;
		}
		return temperature;
	}
}