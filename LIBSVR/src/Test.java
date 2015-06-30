import java.util.Arrays;

public class Test {

	public static void main(String[] args){
		double[][] values = new double[3][3];
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
		//get min and max values of p and c of top 3 R^2 values
		int [] pValues = new int[3], cValues = new int[3];
		int maxNumC = 3;
		int maxNumP = 3;
		double [] ascendingValues = new double[maxNumP*maxNumC];
		for (int c = 0; c < maxNumC; c++) {
		for (int p = 0; p < maxNumP; p++) {
				double value = values[p][c];
				ascendingValues [p+c*maxNumP] = value;
			}
		}
		//sort in ascending order
		Arrays.sort(ascendingValues);
		//get last three, aka biggest three values
		ascendingValues = new double[]{ascendingValues[ascendingValues.length-1],ascendingValues[ascendingValues.length-2],
				ascendingValues[ascendingValues.length-3] };
		//allocate to pValues and cValues
		
		//determine how to divide fine grid search
		//find range of p and c values
		int indexMinP = 0, indexMinC = 0, indexMaxP = 0, indexMaxC = 0;
		for(int i = 0; i < pValues.length; i ++){
			//min
			if(pValues[i]<pValues[indexMinP]){
				indexMinP = i;
			}
			//max
			if(pValues[i]>pValues[indexMaxP]){
				indexMaxP = i;
			}
		}
		for(int i = 0; i < cValues.length; i ++){
			//min
			if(cValues[i]<cValues[indexMinC]){
				indexMinC = i;
			}
			//max
			if(cValues[i]>cValues[indexMaxC]){
				indexMaxC = i;
			}
		}
		double yP = pValues[indexMinP];
		double yC = cValues[indexMinC];
		double mP = (pValues[indexMaxP]-pValues[indexMinP])/6;
		double mC = (cValues[indexMaxC]-cValues[indexMinC])/6;
	}
}