package Helpers;

public class Stringifier {

	static public String ArrayToString(double[] arr) {
		String temp = "";
		temp += arr[0];
		for (int i = 1; i < arr.length; i++) {
			temp += ",";
			temp+= arr[i];
		}
		return temp;
	}

	static public double[] StringToArray(String data) {
		String[] temp1 = data.split(",");
		double[] temp2 = new double[temp1.length];
		for(int i=0;i<temp2.length;i++) {
			temp2[i]= Double.parseDouble(temp1[i]);
		}
		return temp2;
	}
}
