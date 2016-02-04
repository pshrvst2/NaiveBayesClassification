import java.text.DecimalFormat;
import java.util.Map;

/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class Testing {
	
	private int[][] confMatrix = new int[10][10];

	public int[][] getConfMatrix() {
		return confMatrix;
	}

	public void setConfMatrix(int[][] confMatrix) {
		this.confMatrix = confMatrix;
	}
	
	public void addToConfusionMatrix(int i, int j) 
	{
        this.confMatrix[i][j]++;
    }
	
	public void displayConfusionMatrix() 
	{
        DecimalFormat format = new DecimalFormat("0.000");
        format.setMaximumFractionDigits(3);
        double[][] CM = fetchConfusionMatrix();
        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 10; j++)
            {
                System.out.print("\t"+format.format(CM[i][j]));
            }
            System.out.println("\n");
        }
    }
	
	public double[][] fetchConfusionMatrix() 
	{
        // Normalization of the conf matrix.
        int[] rowSum = new int[10];
        double[][] CM = new double[10][10];
        
        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 10; j++) 
            {
                rowSum[i] = rowSum[i] + confMatrix[i][j];
            }
        }

        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 10; j++) 
            {
                CM[i][j] = 1.00 * confMatrix[i][j] / rowSum[i];
            }
        }
        return CM;
    }
	
	
    public double[] fetchClassificationRates() 
    {
    	// Diagonal of the confusion Matrix is the 
    	// classification rate.
    	// First getting the confusion matrix. And 
    	// then returning the i,i part.
        //double[][] cm = fetchConfusionMatrix();

        double[] classificationRates = new double[10];
        
        for (int digit = 0; digit < 10; digit++) 
        {
        	classificationRates[digit] = fetchConfusionMatrix()[digit][digit];
        }

        return classificationRates;
    }
    
    public void oddsRatio( Map<Integer, double[][]> likeliHoodMap)
	{
		OddsRatio oddsRatio = new OddsRatio();
		oddsRatio.setConfMatrix(fetchConfusionMatrix());
		
		oddsRatio.setLikeliHoodMap(likeliHoodMap);
		oddsRatio.generateGraphics();
	}


}
