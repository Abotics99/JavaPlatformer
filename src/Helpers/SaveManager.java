package Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SaveManager {

	String fileName = "";

	public SaveManager(String fileName) {
		this.fileName = fileName;
	}

	public String[] loadData() {
		ArrayList<String> data = new ArrayList<String>();
		try (Scanner in = new Scanner(new File("res/"+ fileName + ".txt"))) {
			while(in.hasNextLine()) {
				data.add(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be found");
			e.printStackTrace();
		}
		return data.toArray(new String[data.size()]);
	}

	public void saveData(String[] data) {
		try (PrintWriter out = new PrintWriter(new File("res/"+ fileName + ".txt"))) {
			for(String temp: data) {
				out.println(temp);
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be made/saved");
			e.printStackTrace();
		}
	}
	
	public void saveData(String data) {
		try (PrintWriter out = new PrintWriter(new File("res/"+ fileName + ".txt"))) {
			out.println(data);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be made/saved");
			e.printStackTrace();
		}
	}
	
	public void addData(String[] data) {
		try (PrintWriter out = new PrintWriter(new FileWriter(new File("res/"+ fileName + ".txt"), true))) {
			for(String temp: data) {
				out.println(temp);
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be made/saved");
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(fileName + ".txt could not be made/saved");
			e1.printStackTrace();
		}
	}
	
	public void addData(String data) {
		try (PrintWriter out = new PrintWriter(new FileWriter(new File("res/"+ fileName + ".txt"), true))) {
			out.println(data);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName + ".txt could not be made/saved");
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println(fileName + ".txt could not be made/saved");
			e1.printStackTrace();
		}
	}
}
