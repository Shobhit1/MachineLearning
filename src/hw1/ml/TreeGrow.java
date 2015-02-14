package hw1.ml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TreeGrow {

	private static String fileName = "training_set.csv";
	private static String fileNameTest  = "test_set.csv";
	private static String fileNameValidation  = "validation_set.csv";

	private int numberNonLeafNodes = 0;

	TreeNode treeBest;
	TreeNode treePrime;


	public TreeNode treeConstruct(ArrayList<ArrayList<String>> sampleSpace, ArrayList<String> attributeList) throws FileNotFoundException{
		int countZero = 0;
		int countOne = 0;

		for(int i=1; i < sampleSpace.size();i++){
			if(sampleSpace.get(i).get(sampleSpace.get(i).size()-1).equalsIgnoreCase("1")){
				countOne++;
			}
			else{
				countZero++;
			}
		}
		if (attributeList.isEmpty() || countZero == sampleSpace.size()-1){
			return new TreeNode("0");

		}
		else if(attributeList.isEmpty() || countOne == sampleSpace.size()-1){
			return new TreeNode("1");
		}
		else{

			GainCalculation gc = new GainCalculation();
			String bestAttribute = gc.bestAttribute(sampleSpace,attributeList);

			ArrayList<String> attributes2 = new ArrayList<String>();

			HashMap<String, ArrayList<ArrayList<String>>> newMap = GainCalculation.mapBasedOnBestAttribute(sampleSpace, bestAttribute);
			for(String att: attributeList){
				if(!att.equalsIgnoreCase(bestAttribute)){
					attributes2.add(att);
				}
			}


			if (newMap.size() < 2){
				String value = "0";
				if(countOne > countZero){
					value = "1";
				}

				return new TreeNode(value);
			}


			return new TreeNode(bestAttribute, treeConstruct(newMap.get("0"),attributes2),treeConstruct(newMap.get("1"),attributes2));
		}


	}

	/**
	 * Generating Tree based on Variance
	 * @throws FileNotFoundException
	 */
	public TreeNode varianceTreeConstruct(ArrayList<ArrayList<String>> sampleVarianceSpace, ArrayList<String> attributeVarianceList) throws FileNotFoundException{
		int countZero = 0;
		int countOne = 0;

		for(int i=1; i < sampleVarianceSpace.size();i++){
			if(sampleVarianceSpace.get(i).get(sampleVarianceSpace.get(i).size()-1).equalsIgnoreCase("1")){
				countOne++;
			}
			else{
				countZero++;
			}
		}
		if (countZero == sampleVarianceSpace.size()-1){
			return new TreeNode("0");

		}
		else if (countOne == sampleVarianceSpace.size()-1){
			return new TreeNode("1");
		}
		else{
			GainCalculation gc = new GainCalculation();
			String bestVarianceAttribute = gc.bestAttributeVariance(sampleVarianceSpace, attributeVarianceList);

			ArrayList<String> attributeVarianceList2 = new ArrayList<String>();

			HashMap<String, ArrayList<ArrayList<String>>> newMap2 = GainCalculation.mapBasedOnBestAttribute(sampleVarianceSpace, bestVarianceAttribute);


			if (newMap2.size() < 2){
				String value = "0";
				if(countOne > countZero){
					value = "1";
				}

				return new TreeNode(value);
			}


			for(String att: attributeVarianceList){
				if(!att.equalsIgnoreCase(bestVarianceAttribute)){
					attributeVarianceList2.add(att);
				}
			}

			return new TreeNode(bestVarianceAttribute, varianceTreeConstruct(newMap2.get("0"),attributeVarianceList2),varianceTreeConstruct(newMap2.get("1"),attributeVarianceList2));
		}
	}


	/**
	 * Function to check each row in the tree against a particular row of the data set
	 * @param root
	 * @param row
	 * @param attributeList
	 * @return
	 */

	public boolean treeOutputCheck(TreeNode root, ArrayList<String> row, ArrayList<String> attributeList){
		TreeNode nodeCopy = root;
		while(true){
			if(nodeCopy.isLeafNode()){
				if(nodeCopy.getLeafValue().equalsIgnoreCase(row.get(row.size()-1))){
					return true;
				}
				else{
					return false;
				}
			}

			int index = attributeList.indexOf(nodeCopy.getName());
			String value = row.get(index);
			if(value.equalsIgnoreCase("0")){
				nodeCopy = nodeCopy.getLeft();
			}
			else{
				nodeCopy = nodeCopy.getRight();
			}
		}
	}


	//Functions used in pruning starts from here


	/**
	 * Function to calculate the accuracy of the tree learned based on a certain test data set
	 * @param node
	 * @param dataToBeChecked
	 * @return
	 */


	public double accuracyOfTree(TreeNode node, ArrayList<ArrayList<String>> dataToBeChecked){
		double accuracy = 0;
		int positiveExamples = 0;

		ArrayList<String> attributes = dataToBeChecked.get(0);
		for(ArrayList<String> row : dataToBeChecked.subList(1, dataToBeChecked.size())){	//looping over each row
			boolean exampleCheck = treeOutputCheck(node, row, attributes);					//passing each row to check if the output of each data instance matches tree traversing
			if(exampleCheck){
				positiveExamples++;
			}
		}
		accuracy = (((double) positiveExamples / (double) (dataToBeChecked.size()-1)) * 100.00);

		return accuracy;
	}

	/**
	 * Function to copy the whole tree - used in post pruning
	 * @param first
	 * @param second
	 */


	public void copyTree(TreeNode first, TreeNode second){
		second.setLeafNode(first.isLeafNode());
		second.setName(first.getName());
		second.setLeafValue(first.getLeafValue());

		if(!first.isLeafNode()){
			second.setLeft(new TreeNode());
			second.setRight(new TreeNode());

			copyTree(first.getLeft(), second.getLeft());
			copyTree(first.getRight(), second.getRight());

		}
	}


	public int getNumberNonLeafNodes() {
		int number = numberNonLeafNodes;
		setNumberNonLeafNodes(0);
		return number;
	}


	public void setNumberNonLeafNodes(int numberNonLeafNodes) {
		this.numberNonLeafNodes = numberNonLeafNodes;
	}


	public void calculateNumLeafNodes(TreeNode root){
		if(!root.isLeafNode()){
			numberNonLeafNodes++;
			root.setNodeNumber(numberNonLeafNodes);
			calculateNumLeafNodes(root.getLeft());
			calculateNumLeafNodes(root.getRight());
		}
	}

	public List<TreeNode> retrieveLeafNodeList(TreeNode root){
		List<TreeNode> leafNodeList = new ArrayList<>();
		if(root.isLeafNode()){ 
			leafNodeList.add(root);
		}
		else{
			if(!root.getLeft().isLeafNode()){
				retrieveLeafNodeList(root.getLeft());
			}
			if(!root.getRight().isLeafNode()){
				retrieveLeafNodeList(root.getRight());
			}
		}
		return leafNodeList;
	}

	public String calculateMajorityClass(TreeNode root){
		int countZero = 0;
		int countOne = 0;
		String majority = "0";
		List<TreeNode> leafNodes = retrieveLeafNodeList(root);
		for(TreeNode node : leafNodes){
			if(node.getLeafValue().equalsIgnoreCase("1")){
				countOne++;
			}
			else{
				countZero++;
			}
		}
		if(countOne>countZero){
			majority = "1";
		}

		return majority;
	}

	public void replaceNode(TreeNode root, int P){
		if(!root.isLeafNode()){
			if(root.getNodeNumber() == P){
				//				System.out.println("Root at which P is: " + root.getName());
				String leafValueToBeChanged = calculateMajorityClass(root);
				root.setLeafNode(Boolean.TRUE);
				root.setLeft(null);
				root.setRight(null);
				root.setLeafValue(leafValueToBeChanged);
			}
			else{
				replaceNode(root.getLeft(), P);
				replaceNode(root.getRight(), P);
			}

		}
	}

	//pruning the tree

	public TreeNode prunedTree(TreeNode root, int l, int k, ArrayList<ArrayList<String>> validationData){
		treeBest = new TreeNode();
		copyTree(root, treeBest);
		//		treeBest = root;
		double bestAccuracyOfTree = accuracyOfTree(treeBest, validationData);
		treePrime = new TreeNode();
		for(int i=1; i<=l;i++){
			copyTree(root, treePrime);
			//			TreeNode treePrime = root;
			Random random = new Random();

			int M = 1 + random.nextInt(k);
			for(int j=0; j<=M; j++){
				calculateNumLeafNodes(treePrime);			//sets the number of leafnodes in the class variable
				int N = getNumberNonLeafNodes();
				//				if (N > 0){
				if(N>1){
					int P = random.nextInt(N) + 1;
					replaceNode(treePrime, P);
				}
				else{
					break;
				}
			}
			double accuracyOfPrimeTree = accuracyOfTree(treePrime, validationData);
			if (accuracyOfPrimeTree > bestAccuracyOfTree){
				bestAccuracyOfTree = accuracyOfPrimeTree;
				copyTree(treePrime, treeBest);
				//	treeBest = treePrime;
			}
		}
		return treeBest;
	}



	public static void main(String[] args) {
		//		GainCalculation.timer();
		try{
			ArrayList<ArrayList<String>> dataFromFile = new FileRead().read(fileName);
			ArrayList<ArrayList<String>> dataFromTestFile = new FileRead().read(fileNameTest);
			ArrayList<ArrayList<String>> dataFromValidationFile = new FileRead().read(fileNameValidation);
			ArrayList<String> attributes = dataFromFile.get(0);
			TreeGrow tg = new TreeGrow();
			int[] lValues = new int[10];
			Random rand = new Random();
			for(int i=0;i<lValues.length;i++)
			{
				lValues[i] = 1 + rand.nextInt(10000);
			}

			int[] kValues = new int[10];
			for(int i=0;i<kValues.length;i++)
			{
				kValues[i] = 1 + rand.nextInt(10); 
			}


						TreeNode learningRoot = tg.treeConstruct(dataFromFile,attributes);
//			TreeNode learningRoot = tg.varianceTreeConstruct(dataFromFile,attributes);
			//		learningRoot.print();
			System.out.println("Accuracy of learned tree with test set " + tg.accuracyOfTree(learningRoot, dataFromTestFile));
			for(int j=0;j<10;j++){
				TreeNode prunedTree = tg.prunedTree(learningRoot,lValues[j],kValues[j], dataFromValidationFile);
				System.out.println("L: " + lValues[j] + " and K: " + kValues[j]);
				System.out.println("Accuracy of Pruned Tree with Test Set " + tg.accuracyOfTree(prunedTree, dataFromTestFile));
			}

			//		prunedTree.print();

			//		GainCalculation.timer();
		}
		catch(IOException e){
			System.out.println("Exception");
		}
	}
}
