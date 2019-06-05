package Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ArrayLoader {

	public static int[][] loadArray(String fileName) {
		ArrayList<String> data = new ArrayList<String>();
		try (Scanner in = new Scanner(new File("res/" + fileName + ".csv"))) {
			while (in.hasNextLine()) {
				data.add(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be found");
			e.printStackTrace();
		}

		int[][] temp1 = new int[data.size()][data.get(0).split(",").length];

		for (int i = 0; i < temp1.length; i++) {
			String[] temp2 = data.get(i).split(",");
			for (int j = 0; j < temp2.length; j++) {
				temp1[i][j] = Integer.parseInt(temp2[j]);
			}
		}
		return temp1;
	}

	public static int[][] loadArrayFromString(String[] mat) {
		ArrayList<String> data = new ArrayList<String>();
		for(String temp: mat) {
			data.add(temp);
		}

		int[][] temp1 = new int[data.size()][data.get(0).split(",").length];

		for (int i = 0; i < temp1.length; i++) {
			String[] temp2 = data.get(i).split(",");
			for (int j = 0; j < temp2.length; j++) {
				temp1[i][j] = Integer.parseInt(temp2[j])-1;
			}
		}
		return temp1;
	}
}
