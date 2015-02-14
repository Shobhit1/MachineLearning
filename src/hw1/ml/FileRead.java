package hw1.ml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileRead {


	public ArrayList<ArrayList<String>> read(String fileName) throws IOException{

		//using information gained from
		//http://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html

		String fileDirectory = System.getProperty("user.dir");
		StringBuilder builder = new StringBuilder(fileDirectory);
		builder.append(System.getProperty("file.separator"));
		builder.append(fileName);

		String fileNameFinal = builder.toString();
//		System.out.println(fileNameFinal);
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		File file = new File(fileNameFinal);
		Scanner input;
		input = new Scanner(file);
		while(input.hasNext()){
			String[] dataForEachRow = input.next().split(",");
			data.add(new ArrayList<String>(Arrays.asList(dataForEachRow)));

		}
		input.close();
		return data;
	}

//	public static void main(String[] args){
		//		FileRead fl = new FileRead();
		//		String fileName = "/Users/shobhitagarwal/Dropbox/Apps/training_set.csv";
		//		System.out.println((fl.read(fileName)));
		//		ArrayList<ArrayList<String>> train = new ArrayList<>();
		//		train = fl.read(fileName);
		//		for(ArrayList<String> data : train){
		//			System.out.println();
		//		}
//	}

}
