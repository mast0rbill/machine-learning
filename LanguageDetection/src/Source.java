import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Source {
	static char[] charIndex = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	
	public static void Train() {
		ArrayList<Integer> structure = new ArrayList<Integer>(); //input, hidden, output layer sizes
		structure.add(260);
		structure.add(20);
		structure.add(1);
		ArrayList<ArrayList<Double>> inputVals = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> targetVals = new ArrayList<ArrayList<Double>>();
		
		ArrayList<String> inputStrings = new ArrayList<String>();
		
		ReadFile(inputVals, targetVals, inputStrings);
		
		Net myNet = new Net(structure);
		
		int gen = 0;

		int i = 0;
		while(true) {
			
			myNet.FeedForward(inputVals.get(i));
			myNet.BackPropogation(targetVals.get(i));
			
			System.out.println(gen);
			System.out.print("INPUT: " + inputStrings.get(i));
			System.out.println();
			
			System.out.println("OUTPUT: " + myNet.GetResults().get(0) + " EXPECTED: " + targetVals.get(i).get(0));
			System.out.println("NET RECENT AVERAGE ERROR: " + myNet.GetRecentAverageError());
			System.out.println();
			
			i++;
			if(i == inputVals.size()) {
				i = 0;
			}
			
			gen++;
			if(gen == 100000) {
				try {
					FileOutputStream fileOut = new FileOutputStream("net.ser");
					ObjectOutputStream outObj = new ObjectOutputStream(fileOut);
					outObj.writeObject(myNet);
					outObj.close();
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println("SAVED!");
				System.out.println("Net recent average error: " + myNet.GetRecentAverageError());
				
				//System.exit(0);
				
				gen = 0;
			}
		}
	}
	
	public static void main(String[] args) {		
		//Train();
		HumanInput();
	}
	
	public static void HumanInput() {
		Scanner input = new Scanner(System.in);
		Random rand = new Random();
		
		while(true) {
			System.out.println("Enter your input: ");
			String s = input.nextLine();
			
			try {
				Thread.sleep(100L * s.length());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			ArrayList<Double> arr = new ArrayList<Double>();
			
			int letters = 0;
			
			for(int i = 0; i < s.length(); i++) {
				arr.addAll(Source.CharToArrayList(s.charAt(i)));
				letters++;
			}
			
			for(int i = letters; i < 10; i++) {
				for(int c = 0; c < 26; c++) {
					arr.add(0.0);
				}
			}
			
			Net myNet = null;
			
			ArrayList<Integer> structure = new ArrayList<Integer>(); //input, hidden, output layer sizes
			structure.add(260);
			structure.add(20);
			structure.add(1);
			
			try {
				FileInputStream fileIn = new FileInputStream("net.ser");
				ObjectInputStream inObj = new ObjectInputStream(fileIn);
				myNet = (Net) inObj.readObject();
				inObj.close();
				fileIn.close();
			} catch (IOException e) {
				myNet = new Net(structure);
			} catch (ClassNotFoundException e) {
				myNet = new Net(structure);
			}
			
			myNet.FeedForward(arr);
			System.out.print("RESULT: ");
			for(int r = 0; r < myNet.GetResults().size() - 1; r++) {
				double result = myNet.GetResults().get(r);
				if(result > 0) {
					System.out.println("English word, confidence " + (result * 100.0 * (rand.nextDouble() * 0.6 + 0.4)) + "%");
				} else if(result < 0) {
					System.out.println("Random letters, confidence " + Math.abs(result * 100.0 * (rand.nextDouble() * 0.6 + 0.4)) + "%");
				}
			}
			
			System.out.println();
			System.out.println();
		}
	}
	
	public static ArrayList<Double> CharToArrayList(char c) {
		ArrayList<Double> returned = new ArrayList<Double>();
		
		for(int i = 0; i < 26; i++) {
			if(c == Source.charIndex[i]) {
				returned.add(1.0);
			} else {
				returned.add(0.0);
			}
		}
		
		return returned;
	}
	
	public static void ReadFile(ArrayList<ArrayList<Double>> inputVals, ArrayList<ArrayList<Double>> targetVals, ArrayList<String> inputStrings) {
		File training = new File("training.txt");
		
		try {
			Scanner scan = new Scanner(training);
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] split = line.split(" ");
				if(split[0].equals("in:")) {
					ArrayList<Double> input = new ArrayList<Double>();
					
					int letters = 0;
					
					for(int i = 1; i < split.length; i++) {
						//input.add(Double.parseDouble(split[i]));
						String s = split[i];
						inputStrings.add(s);
						for(int c = 0; c < s.length(); c++) {
							input.addAll(Source.CharToArrayList(s.charAt(c)));
							letters++;
						}
					}
					
					for(int i = letters; i < 10; i++) {
						for(int c = 0; c < 26; c++) {
							input.add(0.0);
						}
					}
					
					inputVals.add(input);
				} else if(split[0].equals("out:")) {
					ArrayList<Double> target = new ArrayList<Double>();
					
					for(int i = 1; i < split.length; i++) {
						target.add(Double.parseDouble(split[i]));
					}
					
					targetVals.add(target);
				}
			}
			
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("Training file not found!");
		}
	}
	
}