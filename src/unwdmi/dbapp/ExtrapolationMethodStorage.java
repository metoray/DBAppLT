
package unwdmi.dbapp;
import java.util.*;

//extrapolate methods. temporarily saved in a separate class
public class ExtrapolationMethodStorage {

	// Double ArrayList<Double> data
	// Extrapolates and returns the new value based on the dataset.
	// 
	public double extrapolate(List<Number> data){
		double totalChange = 0;
		double lastValue = (Double)data.get(0);
		Iterator<Number> i = data.iterator();

		while(i.hasNext()){
			double d = (Double)i.next();
			totalChange += d- lastValue;
			lastValue = d;
		}
		double avgChange = totalChange / data.size();
		return lastValue + avgChange;
	}
	/*	double temperature
	 * 	ArrayList<Double> data
	 *	Extrapolates new temperature, then calculates if measured temperature is within +-20% bounds
	 */
	public double extrapolateTemperature(List<Number> data, double temperature){
		double extrapolatedValue = extrapolate(data);
		if(temperature > extrapolatedValue*1.2){
			return extrapolatedValue * 1.2;
		}
		if(temperature < extrapolatedValue*0.8){
			return extrapolatedValue * 0.8;
		}
		return temperature;
	}

	/**
	 * Extrapolates a new wind direction based on previous value.
	 * This method was written to account for the fact that directions are circular values
	 * @param data previous values to extrapolate from
	 * @return the extrapolated value
	 */
	public int extrapolateWindDir(List<Number> data){
		int totalChange = 0;
		int last = (int)data.get(0);
		Iterator<Number> it = data.iterator();

		while(it.hasNext()){
			int d = (int) it.next();
			int change = d - last;
			last = d;
			if(change > 180)
				change -= 180;
			if(change < -180)
				change += 180;
			totalChange += change;
		}
		int avgChange = totalChange / data.size();
		return ((last + avgChange) % 360 + 360) % 360;
	}
}