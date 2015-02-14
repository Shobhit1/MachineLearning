package hw1.ml;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GainCalculation {

	private static int phase = 0;
	private static long startTime, endTime, elapsedTime;
//	private static String fileName = "/Users/shobhitagarwal/Dropbox/Apps/training_set.csv";
	HashMap<String, ArrayList<String>> mapOfData;
	HashMap<String, Double> gainMap ;
	HashMap<String, ArrayList<String>> mapVarianceData;
	HashMap<String, Double> varianceMap;

	/**
	 * Timer function to check the time taken
	 * by a function
	 */
	public static void timer()
	{
		if(phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			System.out.println("\nTime: " + elapsedTime + " msec.");
			//			memory();
			phase = 0;
		}
	}

	public static HashMap<String,ArrayList<ArrayList<String>>> mapBasedOnBestAttribute(ArrayList<ArrayList<String>> data, String bestAttribute){
		HashMap<String, ArrayList<ArrayList<String>>> reducedMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		int index = data.get(0).indexOf(bestAttribute);
		//		ArrayList<ArrayList<String>> head = data.get(0);
		for(int i=1;i<data.size();i++){
			if(data.get(i).get(index).equalsIgnoreCase("0")){
				if(reducedMap.containsKey("0")){
					reducedMap.get("0").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("0",dataAdd);
				}

			}
			else{
				if(reducedMap.containsKey("1")){
					reducedMap.get("1").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("1",dataAdd);
				}
			}
		}

		return reducedMap;
	}


	public static HashMap<String, ArrayList<String>> mapPopulation(ArrayList<ArrayList<String>> data) throws FileNotFoundException{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		ArrayList<String> keys = data.get(0);	//taking all the keys from the first row of the data

		for(int i=0;i<keys.size();i++){
			for(int j=1;j<data.size();j++){
				if (map.containsKey(keys.get(i))){
					map.get(keys.get(i)).add(data.get(j).get(i));
				}
				else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(data.get(j).get(i));
					map.put(keys.get(i), values);
				}
			}
		}
		return map;
	}

	/**
	 * Calculating the log of base 2
	 * @param num
	 * @param base
	 * @return log base 2
	 */
	public static double logOfBase(double num, double base){
		return Math.log(num)/Math.log(base);
	}

	/**
	 * Calculating entropy on the basis of positives and negative values
	 * @param positives
	 * @param negatives
	 * @return entropy
	 */
	public static double entropy(double positives, double negatives){
		double total = positives + negatives;
		double probabilityPositive = positives/total;
		double probabilityNegative = negatives/total;

		if(positives == negatives){
			return 1;
		}
		else if(positives == 0 || negatives == 0){
			return 0;
		}
		else{
			double entropy = ((-probabilityPositive) * (logOfBase (probabilityPositive,2))) + ((-probabilityNegative)*(logOfBase(probabilityNegative, 2)));
			return entropy;
		}

	}
	/**
	 * calculating the information gain 
	 * @param rootPositive
	 * @param rootNegative
	 * @param positiveLeft
	 * @param negativeLeft
	 * @param positiveRight
	 * @param negativeRight
	 * @return gain
	 */
	public double informationGain(double rootPositive, double rootNegative, double positiveLeft, double negativeLeft, double positiveRight, double negativeRight){
		double totalRoot = rootPositive + rootNegative;
		double rootEntropy = entropy(rootPositive, rootNegative);
		double leftEntropy = entropy(positiveLeft,negativeLeft);
		double rightEntropy = entropy(positiveRight, negativeRight);
		double totalLeft = positiveLeft + negativeLeft;
		double totalRight = positiveRight + negativeRight;

		double gain = rootEntropy - (((totalLeft/totalRoot)* leftEntropy) + ((totalRight/totalRoot) * rightEntropy));

		return gain;
	}

	/**
	 * Function to calculate best attribute
	 * @param attributes
	 * @param root
	 * @throws FileNotFoundException
	 */


	public String bestAttribute(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList) throws FileNotFoundException{
		String bestAttribute = "";
		mapOfData = mapPopulation(data);
		gainMap = new HashMap<String, Double>();
		//		root = "Class";						//making the root as the example here, passing the root here to get best attribute on the basis of root
		double classPositive = 0;
		double classNegative = 0;
		for(String value : mapOfData.get("Class")){
			if(value.equalsIgnoreCase("1")){
				classPositive++;
			}
			else{
				classNegative++;
			}
		}

		for(String key: attributeList.subList(0, attributeList.size()-1)){		//the keys can be replaced by the attribute list that can be recursively fed.
			ArrayList<String> temp = mapOfData.get(key);
			double positiveLeft = 0;
			double positiveRight = 0;
			double negativeLeft = 0;
			double negativeRight = 0;
			for(int i=0; i<temp.size();i++){								//loop to check the no of positive instances for 0 and 1 for each attribute
				if(temp.get(i).equalsIgnoreCase("0")){
					if(mapOfData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveLeft++;
					}
					else{
						negativeLeft++;
					}
				}
				else{
					if(mapOfData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveRight++;
					}
					else{
						negativeRight++;
					}
				}
			}

			Double gainForEachKey = informationGain(classPositive, classNegative, positiveLeft, negativeLeft, positiveRight, negativeRight);
			gainMap.put(key, gainForEachKey);
		}

		ArrayList<Double> valueList = new ArrayList<Double>(gainMap.values());
		Collections.sort(valueList);
		Collections.reverse(valueList);
		for(String key: gainMap.keySet()){
			if (valueList.get(0).equals(gainMap.get(key))){
				bestAttribute = key;
				break;
			}
		}
		return bestAttribute;		
	}

	/**
	 * Function to calculate the variance
	 * 
	 */

	public static double variance(double positives, double negatives){
		double total = positives + negatives;
		double probabilityPositive = positives/total;
		double probabilityNegative = negatives/total;

		if(positives == negatives){
			return 1;
		}
		else if(positives == 0 || negatives == 0){
			return 0;
		}
		else{
			double entropy = ((-probabilityPositive) * (logOfBase (probabilityPositive,2))) + ((-probabilityNegative)*(logOfBase(probabilityNegative, 2)));
			return entropy;
		}

	}





	/**
	 * Function to calculate Variance Gain
	 * @param rootPositive
	 * @param rootNegative
	 * @param positiveLeft
	 * @param negativeLeft
	 * @param positiveRight
	 * @param negativeRight
	 * @return
	 */


	public double varianceGain(double rootPositive, double rootNegative, double positiveLeft, double negativeLeft, double positiveRight, double negativeRight){
		double totalRoot = rootPositive + rootNegative;
		double varianceRoot = ((rootPositive * rootNegative)/(totalRoot*totalRoot));
		double totalLeft = positiveLeft + negativeLeft;
		double totalRight = positiveRight + negativeRight;

		double leftVariance = ((totalLeft/totalRoot)*((positiveLeft * negativeLeft)/(totalLeft * totalLeft)));
		double rightVariance = ((totalRight/totalRoot) * ((positiveRight * negativeRight) / (totalRight * totalRight)));

		double gain = varianceRoot - (leftVariance + rightVariance);

		return gain;
	}


	/**
	 * Function to give attribute based on highest variance gain
	 * @param dataVariance
	 * @param attributesVariance
	 * @return
	 * @throws FileNotFoundException
	 */


	public String bestAttributeVariance(ArrayList<ArrayList<String>> dataVariance, ArrayList<String> attributesVariance) throws FileNotFoundException{
		String bestVarianceAttribute = "";
		mapVarianceData = mapPopulation(dataVariance);
		varianceMap = new HashMap<String, Double>();

		double classPositiveVariance = 0;
		double classNegativeVariance = 0;

		for(String value : mapVarianceData.get("Class")){
			if(value.equalsIgnoreCase("1")){
				classPositiveVariance++;
			}
			else{
				classNegativeVariance++;
			}
		}

		for(String key:attributesVariance.subList(0, attributesVariance.size()-1)){
			ArrayList<String> tempVariance = mapVarianceData.get(key);
			double positiveLeft = 0;
			double positiveRight = 0;
			double negativeLeft = 0;
			double negativeRight = 0;

			for(int i=0;i<tempVariance.size();i++){
				if(tempVariance.get(i).equalsIgnoreCase("0")){
					if(mapVarianceData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveLeft++;
					}
					else{
						negativeLeft++;
					}
				}
				else{
					if(mapVarianceData.get("Class").get(i).equalsIgnoreCase("1")){
						positiveRight++;
					}
					else{
						negativeRight++;
					}
				}
			}

			Double varianGainForEachKey = varianceGain(classPositiveVariance, classNegativeVariance, positiveLeft, negativeLeft, positiveRight, negativeRight);
			varianceMap.put(key, varianGainForEachKey);
		}

		ArrayList<Double> valueVarianceList = new ArrayList<Double>(varianceMap.values());
		Collections.sort(valueVarianceList);
		Collections.reverse(valueVarianceList);
		List<String> keys = new ArrayList<>(varianceMap.keySet());
		Collections.sort(keys);
		for(String key: keys){
			if (valueVarianceList.get(0).equals(varianceMap.get(key))){
				bestVarianceAttribute = key;
				break;
			}
		}
		return bestVarianceAttribute;
	}



	//	public static void main(String[] args) throws FileNotFoundException{
	//		new GainCalculation().mapPopulation();
	//				GainCalculation gc = new GainCalculation();
	//		//System.out.println(gc.entropy(3,3));
	//		//System.out.println(gc.informationGain(9,5,3,4,6,1));
	//		gc.bestAttribute();

	//		ArrayList<ArrayList<String>> dataFromFile = new FileRead().read(fileName);
	//		HashMap<String, ArrayList<String>> testMap = mapPopulation(dataFromFile);
	//		List<String> keys = new ArrayList<String>(testMap.keySet());
	//		Collections.sort(keys);
	//		System.out.println("Keys: " + keys);
	//		Set<String> setXB = testMap.keySet();
	//		setXB.clear();
	//		setXB.add("XB");
	//		timer();
	//				gc.bestAttribute(dataFromFile, "Class");
	//		timer();
	//	}
}
