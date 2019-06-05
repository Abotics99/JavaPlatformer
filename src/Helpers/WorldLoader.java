package Helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Game.Object;

public class WorldLoader {
	
	private int[][] layer;
	private int width;
	private int height;
	ArrayList<Object> objects = new ArrayList<>();

	public WorldLoader(String file) {
		try (Scanner in = new Scanner(new File("res/" + file + ".tmx"))) {
			while (in.hasNextLine()) {
				String temp = in.nextLine();
				if(temp.contains("<layer")) {
					int beginning = temp.indexOf("width=\"") + 7;
					int end = temp.substring(beginning).indexOf('\"')+beginning;
					this.width = Integer.parseInt(temp.substring(beginning, end));
					beginning = temp.indexOf("height=\"") + 8;
					end = temp.substring(beginning).indexOf("\"") + beginning;
					this.height = Integer.parseInt(temp.substring(beginning, end));
					in.nextLine();
					String[] mat = new String[height];
					for(int i =0;i<height;i++) {
						mat[i] = in.nextLine();
					}
					this.layer = ArrayLoader.loadArrayFromString(mat);
				}
				if(temp.contains("<object ")) {
					int beginning = temp.indexOf("gid=\"") + 5;
					int end = temp.substring(beginning).indexOf('\"')+beginning;
					int id = Integer.parseInt(temp.substring(beginning, end));
					beginning = temp.indexOf("x=\"") + 3;
					end = temp.substring(beginning).indexOf("\"") + beginning;
					int posX = Integer.parseInt(temp.substring(beginning, end));
					beginning = temp.indexOf("y=\"") + 3;
					end = temp.substring(beginning).indexOf("\"") + beginning;
					int posY = Integer.parseInt(temp.substring(beginning, end));
					objects.add(new Game.Object(id, posX, posY));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println(file + ".tmx could not be found");
			e.printStackTrace();
		}
	}

	public int[][] getLayer() {
		return layer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ArrayList<Object> getObjects() {
		return objects;
	}
	
	
}
