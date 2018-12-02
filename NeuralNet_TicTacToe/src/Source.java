import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Source {
	
	private static final int NUMBER_OF_ANIMALS = 1000;
	private static final int SAVE_INTERVAL = 200;
	
	public static void Sort(ArrayList<Net> nets) {
		boolean changed = false;
		
		do {
			changed = false;
			for(int i = 1; i < nets.size(); i++) {
				if(nets.get(i).fitness < nets.get(i - 1).fitness) {
					Net temp = new Net(nets.get(i));
					nets.set(i, new Net(nets.get(i - 1)));
					nets.set(i - 1, temp);
					changed = true;
				}
			}
		} while(changed);
	}
	
	public static void PlayMatchVsNet(Net player1, Net player2, Random rand) {
		Net winner = null;
		Board matchBoard = new Board();
		
		int turnsSurvived = 0;
		boolean turn = rand.nextBoolean(); //false = player1, true = player2
		
		for(int i = 0; i < 9; i++) {
			Net curPlayer = turn ? player2 : player1;
			
			Board newBoard = curPlayer.MakeMove(matchBoard);
			if(newBoard == null) {
				break; //tie
			}
			
			matchBoard = newBoard;
			
			if(matchBoard.Won()) {
				winner = curPlayer;
				break;
			}
			
			turn = !turn;
			matchBoard.FlipBoardForEnemy();
			turnsSurvived++;
		}
		
		//tie
		if(winner == null) {
			int turnDouble = matchBoard.AmountOfDoubles();
			matchBoard.FlipBoardForEnemy();
			int enemyDouble = matchBoard.AmountOfDoubles();
			
			double fitness1, fitness2;
			
			if(turn) {
				fitness1 = ((double)enemyDouble / 16.0) * 0.9;
				fitness2 = ((double)turnDouble / 16.0) * 0.9;
			} else {
				fitness1 = ((double)turnDouble / 16.0) * 0.9;
				fitness2 = ((double)enemyDouble / 16.0) * 0.9;
			}
			
			player1.fitness = fitness1;
			player2.fitness = fitness2;
			return;
		}
		
		Net loser = (player1 == winner) ? player2 : player1;
		
		winner.fitness = 1.0;
		loser.fitness = -1.0 + ((double)turnsSurvived / 10.0);
	}
	
	public static void PlayMatchVsAI(Net player, Random rand) {		
		int aiType = rand.nextInt(3);
		Board matchBoard = new Board();
		
		int turnsSurvived = 0;
		boolean turn = rand.nextBoolean(); //false = AI, true = player
		
		for(int i = 0; i < 9; i++) {
			if(turn) {
				Board newBoard = player.MakeMove(matchBoard);
				
				if(newBoard == null) {
					break; //tie
				}
				
				matchBoard = newBoard;
				
			} else {
				Board newBoard = TraditionalAI.MakeMove(matchBoard, rand, aiType);
				
				if(newBoard == null) {
					//flip to calculate doubles
					matchBoard.FlipBoardForEnemy();
					break;
				}
				
				matchBoard = newBoard;
			}
			
			if(matchBoard.Won()) {
				if(turn) {
					player.fitness = 1.0 - ((double)turnsSurvived / 10.0);
				} else {
					player.fitness = -1.0 + ((double)turnsSurvived / 10.0);
				}
				
				return;
			}
			
			turn = !turn;
			matchBoard.FlipBoardForEnemy();
			turnsSurvived++;
		}
		
		//tie
		//int doubles = matchBoard.AmountOfDoubles();
		//player.fitness = ((double)doubles / 16.0) * 0.9;
	}
	
	public static ArrayList<Net> NextGeneration(ArrayList<Integer> structure, ArrayList<Net> neuralNets, Random rand) {
		ArrayList<Net> newNeuralNets = new ArrayList<Net>();
		for(int n = 0; n < Source.NUMBER_OF_ANIMALS / 20; n++) {
			for(int i = 0; i < 5; i++) {
				Net newN = new Net(neuralNets.get(n));
				newNeuralNets.add(newN);
			}
			
			for(int i = 0; i < 15; i++) {
				/*int mate = rand.nextInt(Source.NUMBER_OF_ANIMALS / 20);
				boolean momPref = rand.nextBoolean();
				
				Net nMate = Net.Mate(structure, neuralNets.get(n), neuralNets.get(mate), momPref);*/
				
				Net newNet = neuralNets.get(n).Mutate(structure);
				newNeuralNets.add(newNet);
			}
		}
		
		for(int i = 0; i < newNeuralNets.size(); i++) {
			int newR = rand.nextInt(newNeuralNets.size());
			Net temp = new Net(newNeuralNets.get(newR));
			newNeuralNets.set(newR, new Net(newNeuralNets.get(i)));
			newNeuralNets.set(i, temp);
		}
		
		return newNeuralNets;
	}
	
	public static void Train_BackPropogation(ArrayList<Integer> structure) {
		Random rand = new Random();
		
		ArrayList<Board> inputVals = new ArrayList<Board>();
		ArrayList<ArrayList<Double>> targetVals = new ArrayList<ArrayList<Double>>();
		
		File training = new File("training.txt");
		
		try {
			Scanner scan = new Scanner(training);
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] split = line.split(" ");
				if(split[0].equals("in:")) {
					ArrayList<Double> input = new ArrayList<Double>();
					
					for(int i = 1; i < split.length; i++) {
						input.add(Double.parseDouble(split[i]));
					}
					
					inputVals.add(new Board(input));
				} else if(split[0].equals("out:")) {
					ArrayList<Double> target = new ArrayList<Double>();
					target.add(Double.parseDouble(split[1]));
					targetVals.add(target);
				}
			}
			
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("Training file not found!");
		}
		
		Net trainedNet = new Net(structure);
		
		int curIndex = 0;
		long curSave = 0;
		
		while(true) {
			//code
			trainedNet.FeedForward(inputVals.get(curIndex).StreamToInput());
			trainedNet.BackPropogation(targetVals.get(curIndex));
			
			curIndex++;
			if(curIndex == inputVals.size()) {
				curIndex = 0;
			}
			
			curSave++;
			
			if(curSave == 10000000) {				
				ArrayList<Net> neuralNets = new ArrayList<Net>();
				for(int i = 0; i < Source.NUMBER_OF_ANIMALS; i++) {
					neuralNets.add(new Net(trainedNet));
				}
				
				for(int i = 0; i < (int)((Source.NUMBER_OF_ANIMALS * 9) / 10); i++) {
					neuralNets.set(i, neuralNets.get(i).Mutate(structure));
				}
				
				for(int j = 0; j < neuralNets.size(); j++) {
					int newR = rand.nextInt(neuralNets.size());
					Net temp = new Net(neuralNets.get(newR));
					neuralNets.set(newR, new Net(neuralNets.get(j)));
					neuralNets.set(j, temp);
				}
				
				try {					
					File index = new File("Nets");
					if(index.exists()) {
						for(String s : index.list()) {
							File curFile = new File(index.getPath(), s);
							curFile.delete();
						}
					} else {
						index.mkdir();
					}
					
					for(int n = 0; n < Source.NUMBER_OF_ANIMALS; n++) {
						FileOutputStream fileOut = new FileOutputStream("Nets/NET_" + n + ".ser");
						ObjectOutputStream outObj = new ObjectOutputStream(fileOut);
						outObj.writeObject(neuralNets.get(n));
						outObj.close();
						fileOut.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println("NET RECENT AVERAGE ERROR: " + trainedNet.GetRecentAverageError());
				
				curSave = 0;
			}
		}
	}
	
	public static void Train_Evolution(ArrayList<Integer> structure) {
		Random rand = new Random();
		
		ArrayList<Net> neuralNets = new ArrayList<Net>();
		for(int i = 0; i < Source.NUMBER_OF_ANIMALS; i++) {
			File file = new File("Nets");
			Net newNet = null;
			
			if(file.exists()) {
				try {
					FileInputStream fileIn = new FileInputStream("Nets/NET_" + i + ".ser");
					ObjectInputStream inObj = new ObjectInputStream(fileIn);
					newNet = (Net) inObj.readObject();
					inObj.close();
					fileIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				newNet = new Net(structure);
			}
			
			neuralNets.add(newNet);
		}
		
		int i = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		while (true) {			
			i++;
			
			boolean vsAI = true;//(i % 2 == 0);
			
			if(!vsAI) {
				//play against net
				for(int a = 1; a < Source.NUMBER_OF_ANIMALS; a++) {
					if((a - 1) % 2 == 0) {
						Source.PlayMatchVsNet(neuralNets.get(a), neuralNets.get(a - 1), rand);
					}
				}
				
			} else {
				//play against bot
				for(Net n : neuralNets) {
					PlayMatchVsAI(n, rand);
				}
				
				//System.out.println(lost);
			}
			
			Sort(neuralNets);
			
			int losses = 0;
			for(int l = 0; l < neuralNets.size(); l++) {
				if(neuralNets.get(l).fitness < 0) {
					losses++;
				}
			}
			
			System.out.println("Losses: " + losses);
			
			if(i == Source.SAVE_INTERVAL) {				
				Calendar cal = Calendar.getInstance();
				System.out.println("Saving AI... " + sdf.format(cal.getTime()));
				
				try {					
					File index = new File("Nets");
					if(index.exists()) {
						for(String s : index.list()) {
							File curFile = new File(index.getPath(), s);
							curFile.delete();
						}
					} else {
						index.mkdir();
					}
					
					for(int n = 0; n < Source.NUMBER_OF_ANIMALS; n++) {
						FileOutputStream fileOut = new FileOutputStream("Nets/NET_" + n + ".ser");
						ObjectOutputStream outObj = new ObjectOutputStream(fileOut);
						outObj.writeObject(neuralNets.get(n));
						outObj.close();
						fileOut.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				i = 0;
			}
			
			neuralNets = NextGeneration(structure, neuralNets, rand);
		}
	}
	
	public static void PlayvsNet_Demo() {
		Net player1 = null, player2 = null;
		
		try {
			FileInputStream fileIn = new FileInputStream("Nets/NET_0.ser");
			ObjectInputStream inObj = new ObjectInputStream(fileIn);
			player1 = (Net) inObj.readObject();
			inObj.close();
			fileIn.close();
			
			FileInputStream fileIn1 = new FileInputStream("Nets/NET_1.ser");
			ObjectInputStream inObj1 = new ObjectInputStream(fileIn1);
			player2 = (Net) inObj1.readObject();
			inObj1.close();
			fileIn1.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Board matchBoard = new Board();
		Random rand = new Random();
		boolean turn = rand.nextBoolean(); //false = player1, true = player2
		
		for(int i = 0; i < 9; i++) {
			//Net curPlayer = turn ? player2 : player1;
			
			Board newBoard = !turn ? player1.MakeMove(matchBoard) : TraditionalAI.MakeMove(matchBoard, rand, 0);
			if(newBoard == null) {
				break; //tie
			}
			
			matchBoard = newBoard;
			
			turn = !turn;
			
			if(!turn) {
				matchBoard.FlipBoardForEnemy();
				matchBoard.Print();
				matchBoard.FlipBoardForEnemy();
			} else {
				matchBoard.Print();
			}
			
			if(matchBoard.Won()) {
				System.out.println("won");
				break;
			}
			
			System.out.println();
			System.out.println();
			
			matchBoard.FlipBoardForEnemy();
		}
	}
	
	public static void PlayvsHuman() {
		Net net = null;
		
		try {
			FileInputStream fileIn = new FileInputStream("Nets/NET_0.ser");
			ObjectInputStream inObj = new ObjectInputStream(fileIn);
			net = (Net) inObj.readObject();
			inObj.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Scanner input = new Scanner(System.in);
		
		Random rand = new Random();
		
		Board matchBoard = new Board();
		boolean turn = rand.nextBoolean(); //true = net, false = player
			
		for(int i = 0; i < 9; i++) {
			Board newBoard = null;
			
			if(turn) {
				System.out.println("AI turn: ");
				
				newBoard = net.MakeMove(matchBoard);
				if(newBoard == null) {
					break; //tie
				}
			} else {
				if(matchBoard.AllPossibleMoves().size() == 0) {
					break; //tie
				}
				
				System.out.println("Your turn: ");
				
				//player make turn
				String s = input.nextLine();
				String[] split = s.split(" ");
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				
				do {
					newBoard = matchBoard.PlayerMakeMove(x, y);
				} while(newBoard == null);
			}
			
			matchBoard = newBoard;
			matchBoard.Print();
			System.out.println();
			System.out.println();
			System.out.println();
				
			if(matchBoard.Won()) {
				System.out.println("The AI won!");
				input.close();
				return;
			}
			
			if(matchBoard.Won_Player()) {
				System.out.println("You won!");
				input.close();
				return;
			}
			
			turn = !turn;
		}
			
		System.out.println("You tied!");
		input.close();
	}

	public static void main(String[] args) {
		//GenerateTrainingFile();
		
		ArrayList<Integer> structure = new ArrayList<Integer>(); //input, hidden, output layer sizes
		structure.add(27);
		structure.add(15);
		structure.add(1);
		
		//Train_BackPropogation(structure);
		Train_Evolution(structure);
		//PlayvsHuman();
		//PlayvsNet_Demo();
	}
}