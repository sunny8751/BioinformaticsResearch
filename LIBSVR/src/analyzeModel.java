import java.io.IOException;

public class analyzeModel {

	public static void main(String[] args) throws IOException {
		// see what the feature #s are
		int[] features = new int[] {
					1002,
					1063,
					1114,
					1191,
					1190
			};
		for (int a = 0; a < features.length; a++) {
			int featureValue = features[a];
			//position starting from 1, 2, ... N
			int position = (int) Math.ceil(featureValue
					/ Math.pow(4, Main.features));
			featureValue -= (position - 1) * Math.pow(4, Main.features);
			System.out.println(test(featureValue) + " at " + position);
		}
	}

	private static String test(int value) {
		// returns the single/pair/triplet value of the input seq at that
		// position
		String seq = "";
		if (Main.features == 1) {
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
		} else if (Main.features == 2) {
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
		} else if (Main.features == 3) {
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
