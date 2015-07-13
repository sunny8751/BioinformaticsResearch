import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class coreFrequency {

	public static void main(String[] args) throws IOException {
int i = 1;
		System.out.println("train"+(i+1));
			run("train"+(i+1));
	}
	private static void run(String file) throws IOException{
		List <String> cores = new ArrayList<String>();
		List <Integer> coreFreq = new ArrayList<Integer>();
		
		BufferedReader fp = new BufferedReader(new FileReader(file+".txt"));
		String line = fp.readLine();
		while(line!=null){
			StringTokenizer st = new StringTokenizer (line);
			st.nextToken();
			//because position starts at 0
			String core = st.nextToken().substring((int)Math.floor((Main.mers+1)/2d)-2, (int)Math.ceil((Main.mers+1)/2d)+1);
			if(cores.contains(core)){
				//add one to freq
				coreFreq.set(cores.indexOf(core), coreFreq.get(cores.indexOf(core))+1);
			}else{
				//create new slot
				cores.add(core);
				coreFreq.add(1);
			}
		line = fp.readLine();
		}

		PrintWriter writer = new PrintWriter("coreFrequencies", "UTF-8");
		for(int i = 0; i<cores.size(); i++){
			writer.print(cores.get(i)+" " + coreFreq.get(i));
			System.out.println(cores.get(i)+" " + coreFreq.get(i));
			writer.println();
		}
		fp.close();
		writer.close();
	}
}