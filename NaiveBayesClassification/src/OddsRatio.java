import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.tc33.jheatchart.HeatChart;


/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class OddsRatio {

	private double[][] confMatrix;
	private Map<Integer, double[][]> likeliHoodMap;
	public OddsRatio()
	{

	}

	public OddsRatio(double[][] confMatrix, Map<Integer, double[][]> likeliHoodMap)
	{
		this.confMatrix = confMatrix;
		this.setLikeliHoodMap(likeliHoodMap);
	}

	public double[][] getConfMatrix() {
		return confMatrix;
	}

	public void setConfMatrix(double[][] confMatrix) {
		this.confMatrix = confMatrix;
	}

	public Map<Integer, double[][]> getLikeliHoodMap() {
		return this.likeliHoodMap;
	}

	public void setLikeliHoodMap(Map<Integer, double[][]> likeliHoodMap) {
		this.likeliHoodMap = likeliHoodMap;
	}

	private void drawCharts(double[][] points, String heading, String pic) throws IOException 
	{
		HeatChart map = new HeatChart(points);

		map.setTitle(heading);
		map.setHighValueColour(Color.red);
		map.setLowValueColour(Color.yellow);
		map.setAxisValuesColour(Color.green);
		map.setBackgroundColour(Color.white);
		map.setXAxisLabel("Pixels (0-27)");
		map.setYAxisLabel("Pixels (0-27)");
		map.setAxisColour(Color.blue);

		map.saveToFile(new File(pic));
	}
	
	public static LinkedHashMap<String, Double> sortHashMapByValue(HashMap<String, Double> map) 
	{
		List<String> mapKeys = new ArrayList<String>(map.keySet());
		List<Double> mapValues = new ArrayList<Double>(map.values());
		Collections.sort(mapValues);
		Collections.reverse(mapValues);

		LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();

		Iterator<Double> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) 
		{
			Double val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) 
			{
				String key = keyIt.next();
				String comp1 = map.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) 
				{
					map.remove(key);
					mapKeys.remove(key);
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

	public void generateGraphics()
	{
		HashMap<String, Double> map = new HashMap<String, Double>();
		for(int i = 0; i < 10 ; i ++)
		{
			for(int j = 0; j < 10; j++)
			{
				Double val = confMatrix[i][j];
				String key = String.valueOf(i)+":"+String.valueOf(j);
				if(i!=j)
					map.put(key, val);
			}
		}
		LinkedHashMap<String, Double> sortedmap = sortHashMapByValue(map);
		int count = 0;
		System.out.println();
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("\t||||               Odds Ratio               ||||");
		System.out.println("\t||||  (Pair with Highest Confusion Matrix)  ||||");
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||");
		for(Entry<String, Double> entry : sortedmap.entrySet())
		{
			String[] coord = entry.getKey().split(":");
			String i = coord[0];
			String j = coord[1];
			//Double val = entry.getValue();
			System.out.println("\t||||||||| Digits i = " + i + " and Digit j = " + j+" |||||||||");
			try 
			{
				drawCharts(likeliHoodMap.get(Integer.valueOf(i)), "Heat Map " + i, i+".jpeg");
				drawCharts(likeliHoodMap.get(Integer.valueOf(j)), "Heat Map " + j, j+".jpeg");
				double[][] odds = fetchodds(likeliHoodMap.get(Integer.valueOf(i)), likeliHoodMap.get(Integer.valueOf(j)));
				drawCharts(odds, "Odds " + i + "/" + j, i + "-" + j + "OddsHeatMap.jpeg");
				
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			count++;
			if(count == 4)
				break;
		}
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||");
		
		// for tsting of graphics. delete later.
		/*double[][] odds = fetchodds(likeliHoodMap.get(1), likeliHoodMap.get(8));
		try {
			drawCharts(odds, "Odds 1"  + "/8", "1-8OddsHeatMap.jpeg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public double[][] fetchodds(double[][] p1, double[][] p2)
	{
		double[][] odds = new double[28][28];
		for(int i = 0; i < 28; i++ )
		{
			for(int j = 0; j < 28; j++ )
			{
				odds[i][j] = Math.log(p1[i][j]/p2[i][j]);
			}
		}
		return odds;
	}
}
