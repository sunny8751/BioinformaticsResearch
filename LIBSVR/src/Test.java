import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Test {
	static int maxNumC = 3;
	static int maxNumP = 3;

	public static void main(String[] args) throws IOException {
	}
	
	static int[] findIndices(double d, double [][] values){
		//d is the value to look for in values[0][0]
		int valueP = -1, valueC = -1;
		for (int c = 0; c < maxNumC; c++) {
			for (int p = 0; p < maxNumP; p++) {
				if(values[p][c]==d){
					valueP = p;
					valueC = c;
					break;
				}
			}
			if(valueP!=-1){
				break;
			}
		}
		return new int[]{valueP, valueC};
	}
}