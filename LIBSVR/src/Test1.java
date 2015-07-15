import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Test1 {

	public static void main(String[] args) throws IOException {
		Test1 t = new Test1();
		t.featureMeaning();
	}
	
	void featureMeaning() throws IOException{
		int counter = 1;
		PrintWriter writer = new PrintWriter("featureMeaning.txt", "UTF-8");
		writer.print("feature seq pos");
		writer.println();
		for (int i = 0; i < 4*Main.mers+64*(Main.mers-2); i++) {
			//only for feat13
			String seq;
			int pos, j = i;
			if(j<4*(Main.mers)){
				Main.features = 1;
				pos = (int) Math.ceil((j+1)/4f);
				seq = test(j%4+1, 1);
			}else{
				j-=4*(Main.mers);
				Main.features = 3;
				pos = (int) Math.ceil((j+1)/64f);
				seq = test(j%64+1, 3);
			}
			if (pos-1 < Main.mers / 2 - 2 || pos-1 >= Main.mers / 2 + 2 + (1 - Main.features)) {
				writer.print(counter+" "+seq+" "+pos);
				writer.println();
				counter ++;
			}
		}
		writer.close();
	}
	
	void duplicates() throws IOException{
		BufferedReader data = new BufferedReader(new FileReader("_test1.txt"));
		String line = data.readLine();
		while(line != null){
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			String seq = st.nextToken();
			for(int i = 0; i<seq.length()-1; i++){
				if(seq.substring(i, i+2).equals("GC")&&(i==13||i==19)){
					System.out.println(seq);
				}
			}
			line = data.readLine();
		}
		data.close();
	}
	
	void cutoff() throws IOException{
		for (int i = 1; i <= 10; i++) {
			BufferedReader data = new BufferedReader(new FileReader("test" + i
					+ ".txt"));
			PrintWriter writer = new PrintWriter("_test" + i + ".txt", "UTF-8");
			String line = data.readLine();
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				writer.print(st.nextToken() + " ");
				String seq = st.nextToken();
				seq = seq.substring(1, seq.length() - 1);
				writer.print(seq);
				writer.println();
				line = data.readLine();
			}
			writer.close();
			data.close();
		}
	}

	void compare() throws IOException {

		List<String> E2F4 = new ArrayList<String>(), E2F1 = new ArrayList<String>();

		BufferedReader data2 = new BufferedReader(new FileReader("testA.txt"));
		String line = data2.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			E2F1.add(st.nextToken());
			line = data2.readLine();
		}
		data2.close();

		BufferedReader data1 = new BufferedReader(new FileReader(
				"convertedE2F4"));
		line = data1.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			E2F4.add(st.nextToken());
			line = data1.readLine();
		}
		data1.close();

		// compare
		Collections.sort(E2F1);
		PrintWriter writer = new PrintWriter("e2f1.txt", "UTF-8");
		for (int i = 0; i < E2F1.size(); i++) {
			writer.println(E2F1.get(i));
		}
		System.out.println(E2F1.size());
		writer.close();
		Collections.sort(E2F4);
		writer = new PrintWriter("e2f4.txt", "UTF-8");
		for (int i = 0; i < E2F4.size(); i++) {
			writer.println(E2F4.get(i));
		}
		System.out.println(E2F4.size());
		writer.close();
	}
	
	private static String test(int value, int feature) {
		// returns the single/pair/triplet value of the input seq at that
		// position
		String seq = "";
		if (feature == 1) {
			switch (value) {
			case 1:
				seq = "A";
				break;
			case 2:
				seq = "C";
				break;
			case 3:
				seq = "G";
				break;
			case 4:
				seq = "T";
				break;
			default:
				seq = "NO";
			}
		} else if (feature == 2) {
			switch (value) {
			case 1:
				seq = "AA";
				break;
			case 2:
				seq = "AC";
				break;
			case 3:
				seq = "AG";
				break;
			case 4:
				seq = "AT";
				break;
			case 5:
				seq = "CA";
				break;
			case 6:
				seq = "CC";
				break;
			case 7:
				seq = "CG";
				break;
			case 8:
				seq = "CT";
				break;
			case 9:
				seq = "GA";
				break;
			case 10:
				seq = "GC";
				break;
			case 11:
				seq = "GG";
				break;
			case 12:
				seq = "GT";
				break;
			case 13:
				seq = "TA";
				break;
			case 14:
				seq = "TC";
				break;
			case 15:
				seq = "TG";
				break;
			case 16:
				seq = "TT";
				break;
			default:
				seq = "NO";
			}
		} else if (feature == 3) {
			// first nucleotide
			if (value <= 16) {
				seq = "A";
			} else if (value <= 32) {
				seq = "C";
				value -= 16;
			} else if (value <= 48) {
				seq = "G";
				value -= 32;
			} else if (value <= 64) {
				seq = "T";
				value -= 48;
			}
			// second nucleotide
			if (value <= 4) {
				seq += "A";
			} else if (value <= 8) {
				seq += "C";
				value -= 4;
			} else if (value <= 12) {
				seq += "G";
				value -= 8;
			} else if (value <= 16) {
				seq += "T";
				value -= 12;
			}
			// third nucleotide
			if (value == 1) {
				seq += "A";
			} else if (value == 2) {
				seq += "C";
			} else if (value == 3) {
				seq += "G";
			} else if (value == 4) {
				seq += "T";
			}
		}
		return seq;
	}
}