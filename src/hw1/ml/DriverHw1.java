package hw1.ml;

import java.io.IOException;
import java.util.ArrayList;


public class DriverHw1 {

	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println("Please pass some arguments.");
			System.exit(0);
		}
		else{
			boolean printCounter;
			int L = Integer.parseInt(args[0]);
			int K = Integer.parseInt(args[1]);
			String fileNameTrainingData = args[2];
			String fileNameValidationData = args[3];
			String fileNameTest = args[4];
			String print = args[5];

			if(print.equalsIgnoreCase("Yes")){
				printCounter = Boolean.TRUE;
			}
			else{
				printCounter = Boolean.FALSE;
			}

			FileRead fileRead = new FileRead();
			TreeGrow treeGrow = new TreeGrow();
			
			try {

				ArrayList<ArrayList<String>> dataSetTraining = fileRead.read(fileNameTrainingData);
				ArrayList<ArrayList<String>> dataSetValidation = fileRead.read(fileNameValidationData);
				ArrayList<ArrayList<String>> dataSetTest = fileRead.read(fileNameTest);

				ArrayList<String> attributeList = dataSetTraining.get(0);

				TreeNode treeLearningEntropy = treeGrow.treeConstruct(dataSetTraining, attributeList);
				TreeNode treeLearningVariance = treeGrow.varianceTreeConstruct(dataSetTraining, attributeList);
				
				System.out.println("ACCURACY OF TREE WITH first HEURISTIC ON TEST DATA: " + treeGrow.accuracyOfTree(treeLearningEntropy, dataSetTest));
				System.out.println("ACCURACY OF TREE WITH second HEURISTIC ON TEST DATA: " + treeGrow.accuracyOfTree(treeLearningVariance, dataSetTest));
				System.out.println();
				
				TreeNode prunedTreeEntropy = treeGrow.prunedTree(treeLearningEntropy, L, K, dataSetValidation);
				TreeNode prunedTreeVariance = treeGrow.prunedTree(treeLearningVariance, L, K, dataSetValidation);
				
				System.out.println("ACCURACY OF Pruned - TREE WITH first HEURISTIC ON TEST DATA: " + treeGrow.accuracyOfTree(prunedTreeEntropy, dataSetTest));
				System.out.println("ACCURACY OF Pruned - TREE WITH second HEURISTIC ON TEST DATA: " + treeGrow.accuracyOfTree(prunedTreeVariance, dataSetTest));
				System.out.println();
				
				
				if(printCounter){
					System.out.println("------------Tree Using First Heuristic--------------");
					System.out.println();
					treeLearningEntropy.print();
					System.out.println();
					System.out.println("------------Tree Using Second Heuristic--------------");
					System.out.println();
					treeLearningVariance.print();
					System.out.println();
				}
				
			} catch (IOException e) {
				System.out.println("File could not be found, please check the file name properly");
//				e.printStackTrace();
			}
		}
	}
}
