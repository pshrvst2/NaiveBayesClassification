import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class Training {

	/**
	 * @param args
	 */
	
	public static String testImageFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/testimages";
	public static String trainImageFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/trainingimages";
	public static String testLabelFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/testlabels";
	public static String trainLabelFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/traininglabels";
	public static List<Digit> testDigitList = new ArrayList<Digit>();
	public static List<Digit> trainDigitList = new ArrayList<Digit>();
	public static Map<Integer, List<Digit>> testDigitMap = new HashMap<Integer, List<Digit>>();
	public static Map<Integer, List<Digit>> trainDigitMap = new HashMap<Integer, List<Digit>>();
	public static Map<Integer, double[][]> trainLikeliHoodMap = new HashMap<Integer, double[][]>();
	public static Map<Integer, Float> classProbMap = new HashMap<Integer, Float>();
	
	public static void main(String[] args) 
	{
		readTrainingFiles();
		readTestingFiles();
		trainFeature();
		testFeature();
		likeliHoodCalculationTrain();
		
		//Now proceed to testing
		testingPhase();
	}
	
	public static void readTrainingFiles()
	{
		try 
		{
			//List<Digit> digitList = new ArrayList<Digit>();
			Scanner scannerImage = new Scanner(new File(trainImageFilePath));
			Scanner scannerLabel = new Scanner(new File(trainLabelFilePath));
			String labelLine = null;
			String imageLine = null;
			
			while(scannerLabel.hasNextLine())
			{
				Digit digit = new Digit();
				labelLine = scannerLabel.nextLine();
				digit.setValue(Integer.valueOf(labelLine));
				
				int i = 0; int j = 0;
				do
				{
					imageLine = scannerImage.nextLine();
					for(j = 0; j < 28 ; j ++)
					{
						if(imageLine.charAt(j) == ' ')
							digit.matrix[i][j] = 0;
						else
							digit.matrix[i][j] = 1;
					}
					i++;
				}while(scannerImage.hasNextLine() & i%28 != 0);
				trainDigitList.add(digit);
				
			}
			scannerImage.close();
			scannerLabel.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readTestingFiles()
	{
		try 
		{
			//List<Digit> digitList = new ArrayList<Digit>();
			Scanner scannerImage = new Scanner(new File(testImageFilePath));
			Scanner scannerLabel = new Scanner(new File(testLabelFilePath));
			String labelLine = null;
			String imageLine = null;
			
			while(scannerLabel.hasNextLine())
			{
				Digit digit = new Digit();
				labelLine = scannerLabel.nextLine();
				digit.setValue(Integer.valueOf(labelLine));
				
				int i = 0; int j = 0;
				do
				{
					imageLine = scannerImage.nextLine();
					for(j = 0; j < 28 ; j ++)
					{
						if(imageLine.charAt(j) == ' ')
							digit.matrix[i][j] = 0;
						else
							digit.matrix[i][j] = 1;
					}
					i++;
				}while(scannerImage.hasNextLine() & i%28 != 0);
				testDigitList.add(digit);
				
			}
			scannerImage.close();
			scannerLabel.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testFeature()
	{
		for(Digit digit : testDigitList)
		{
			Integer value = digit.getValue();
			if(testDigitMap.get(value) == null)
			{
				List<Digit> list = new ArrayList<Digit>();
				list.add(digit);
				testDigitMap.put(value, list);
			}
			else
			{
				List<Digit> list = testDigitMap.get(value);
				list.add(digit);
			}
		}
	}
	
	public static void trainFeature()
	{
		for(Digit digit : trainDigitList)
		{
			Integer value = digit.getValue();
			if(trainDigitMap.get(value) == null)
			{
				List<Digit> list = new ArrayList<Digit>();
				list.add(digit);
				trainDigitMap.put(value, list);
			}
			else
			{
				List<Digit> list = trainDigitMap.get(value);
				list.add(digit);
			}
		}
	}
	
	public static void likeliHoodCalculationTrain()
	{
		for(int i = 0; i < 10; i++ )
		{
			trainLikeliHoodMap.put(i, new double[28][28]);
		}
		
		float likelihood;
		int sum = 0;
		float k = 50.00f; // test with different values of k
		int v = 2; // pixel is either 0 or 1.
		
		for(int s = 0; s < trainDigitMap.size(); s++)
		{
			List<Digit> digitList = trainDigitMap.get(Integer.valueOf(s));
			//for every pixel i,j
			for(int i = 0; i < 28; i++ )
			{
				for(int j = 0; j < 28; j++ )
				{
					sum = 0;
					for(Digit digit : digitList)
					{
						sum = sum + digit.matrix[i][j];
					}
					likelihood = (float) ((k+sum)/((k*v)+digitList.size()));
					//System.out.println("digit "+s+" :digit size: "+digitList.size()+" : i "+i+" : j "+j+" :sum "+sum+" : likelihood: "+likelihood);
					trainLikeliHoodMap.get(s)[i][j] = likelihood;
				}
			}
		}
		
		// print the P(Class)
		DecimalFormat format = new DecimalFormat("0.000");
        format.setMaximumFractionDigits(3);
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("\t|||||||||||||||||||||||   P(Class)    ||||||||||||||||||||||");
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		for(int i = 0; i < 10; i++)
		{
			System.out.print("\t||| Count of " + i + " in the document = " + trainDigitMap.get(i).size() + 
					"   :    P("+i+") = " + format.format(getProbabilityOfClass(i)) + " |||\n");
			classProbMap.put(i, getProbabilityOfClass(i));
		}
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
	}
	
	public static float getProbabilityOfClass(Integer i)
	{
		DecimalFormat format = new DecimalFormat("0.000");
        format.setMaximumFractionDigits(3);
		float prob = 0.00f;
		prob = (float) trainDigitMap.get(i).size()/trainDigitList.size();
		return prob;
	}
	
	public static void testingPhase()
	{
		Testing testing = new Testing();
		for(int i = 0; i < testDigitMap.size(); i++)
		{
			List<Digit> digitList = testDigitMap.get(i);
			for(Digit digit : digitList)
			{
				List<Double> postProbsList = getPostProb(digit);
				int index = postProbsList.indexOf(Collections.max(postProbsList));
				testing.addToConfusionMatrix(i, index);
			}
		}
		
		// confusion matrix display
		System.out.println("\n");
		System.out.println("\t|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		System.out.println("\t||||||||||||||||||||||||||||||| Confusion Matrix ||||||||||||||||||||||||||||");
		System.out.println("\t|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		//System.out.println("\n");
		testing.displayConfusionMatrix();
		System.out.println("\t|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		printClassificationAccuracy(testing);
		
	}
	
	public static void printClassificationAccuracy(Testing testing)
	{
		DecimalFormat format = new DecimalFormat("0.000");
        format.setMaximumFractionDigits(3);
        System.out.println();
		System.out.println("\t||||||||||||||||||||||||||||");
		System.out.println("\t|| Classification Accuracy||");
		System.out.println("\t||||||||||||||||||||||||||||");
		double[] classRates = testing.fetchClassificationRates();
		for(int i = 0; i < 10; i++)
		{
			System.out.println("\t|||    Digit " + i + ": " + format.format(classRates[i])+"    |||");
		}
		System.out.println("\t||||||||||||||||||||||||||||");
		
		// Now calculating the average classification rate.
		double sum = 0;
        for (int i = 0; i < classRates.length; i++) 
        {
        	sum = sum + classRates[i];
        }

        double avg =  sum/classRates.length;
        System.out.println("\n\t||||||||||||||||||||||||||||||||||||||||");
		System.out.println("\t|||       Overall Accuracy = " + format.format(avg)+"   |||");
		System.out.println("\t||||||||||||||||||||||||||||||||||||||||");
		
		// call odds ratio
		testing.oddsRatio(trainLikeliHoodMap);

	}
	
	public static List<Double> getPostProb(Digit testDigit)
	{
		List<Double> posteriorProbabilityList = new ArrayList<Double>();
		double logOfProbOfClass;
		double logOfProbIJInClass;
		//for 0 to 9
		for(int digit = 0; digit < 10; digit++)
		{
			logOfProbOfClass = Math.log(classProbMap.get(digit));
			//for each pixel in the testimg. We take log to evade underflow
			for(int i = 0; i < 28; i++ )
			{
				for(int j = 0; j < 28; j++ )
				{
					//int val = testDigit.getValue();
					double likelihood = LikelihoodOfPixel(digit, i, j, testDigit.matrix[i][j]);
					//System.out.println(likelihood);
					logOfProbIJInClass = Math.log(likelihood);
					logOfProbOfClass += logOfProbIJInClass;
					//System.out.println(digit+" "+logOfProbOfClass);
					//System.out.println();
				}
			}
			posteriorProbabilityList.add(logOfProbOfClass);
		}

		return posteriorProbabilityList;
	}
	
	public static double LikelihoodOfPixel(int digit, int i, int j, int tstVal)
	{
		double likelihhod = trainLikeliHoodMap.get(digit)[i][j];
		//System.out.println(likelihhod);
		if(tstVal == 1)
		{
			return likelihhod;
		}
		else
		{
			return 1-likelihhod;
		}
	}
}
