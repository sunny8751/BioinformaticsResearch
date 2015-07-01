import java.util.Arrays;

public class Test {
	static int maxNumC = 3;
	static int maxNumP = 3;

	public static void main(String[] args) {
		double[][] values = new double[maxNumP][maxNumC];
		values[0][0] = 1;
		values[0][1] = 2;
		values[0][2] = 9;
		values[1][0] = 6;
		values[1][1] = 7;
		values[1][2] = 3;
		values[2][0] = 5;
		values[2][1] = 4;
		values[2][2] = 8;

		//
		// get min and max values of p and c of top 3 R^2 values
		int[] pValues = new int[3], cValues = new int[3];
		double[] ascendingValues = new double[maxNumP * maxNumC];
		for (int c = 0; c < maxNumC; c++) {
			for (int p = 0; p < maxNumP; p++) {
				ascendingValues[p + c * maxNumP] = values[p][c];
			}
		}
		// sort in ascending order
		Arrays.sort(ascendingValues);
		// get last three, aka biggest three values
		double[] top3 = new double[] {
				ascendingValues[ascendingValues.length - 3],
				ascendingValues[ascendingValues.length - 2],
				ascendingValues[ascendingValues.length - 1] };
		// allocate to pValues and cValues
		for(int i = 0; i<3; i++){
			//find the value in ascendingValues
			pValues[i] = findIndices(top3[i], values)[0];
			cValues[i] = findIndices(top3[i], values)[1];
		}
		double yP = pValues[0];
		double yC = cValues[0];
		double mP = (pValues[2] - pValues[0]) / 6;
		double mC = (cValues[2] - cValues[0]) / 6;
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