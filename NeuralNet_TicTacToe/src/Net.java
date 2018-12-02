import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Net implements Serializable {
	private static final long serialVersionUID = -5700761739943094005L;

	public ArrayList<ArrayList<Neuron>> neurons = new ArrayList<ArrayList<Neuron>>();
	
	public double fitness;
	
	private static double mutationRate = 0.1;
	private static double mutationMagnitude = 0.5; //0.5 extra on each side
	
	public double error;
	private double recentAverageError;
	
	private static double recentAverageSmoothingFactor = 100.0; //number of training samples to average over
	
	public Net(ArrayList<Integer> topology) { 	
		int numLayers = topology.size();
		for(int layer = 0; layer < numLayers; layer++) {
			neurons.add(new ArrayList<Neuron>());
			int numOutputs = (layer == numLayers - 1) ? 0 : topology.get(layer + 1);
			
			for(int n = 0; n <= topology.get(layer); n++) { //add bias neuron, so <=
				neurons.get(layer).add(new Neuron(numOutputs, n));
			}
			
			//set bias node
			neurons.get(layer).get(neurons.get(layer).size() - 1).SetOutput(1.0);
		}
	}
	
	public Net(Net net) { 	
		int numLayers = net.neurons.size();
		
		for(int layer = 0; layer < numLayers; layer++) {
			neurons.add(new ArrayList<Neuron>());
			
			for(int n = 0; n < net.neurons.get(layer).size(); n++) {
				neurons.get(layer).add(new Neuron(net.neurons.get(layer).get(n)));
			}
			
			//set bias node
			neurons.get(layer).get(neurons.get(layer).size() - 1).SetOutput(1.0);
		}
		
		fitness = net.fitness;
	}
	
	public void Print() {
		for(int layer = 0; layer < neurons.size(); layer++) {
			for(int n = 0; n < neurons.get(layer).size(); n++) { 
				for(int c = 0; c < neurons.get(layer).get(n).outputWeights.size(); c++) {
					System.out.print(neurons.get(layer).get(n).outputWeights.get(c).weight + ", ");
				}
			}
		}
		
		System.out.println();
	}
	
	public Board MakeMove(Board matchBoard) {
		ArrayList<Board> allPossibleMoves = matchBoard.AllPossibleMoves();
		if(allPossibleMoves.size() == 0) { //tie
			return null;
		}
		
		double bestFavorability = -100f; //make sure it starts off at nothing
		Board bestBoard = null;
		for(Board b : allPossibleMoves) {
			FeedForward(b.StreamToInput());
			double favorability = GetResults().get(0);
			if(favorability > bestFavorability) {
				bestBoard = new Board(b);
				bestFavorability = favorability;
			}
		}
		
		return bestBoard;
	}
	
	public void FeedForward(ArrayList<Double> input) {
		if(input.size() != neurons.get(0).size() - 1) {
			System.out.println("INPUT SIZE != INPUT NEURON SIZE");
		}
		
		//assign input values
		for(int i = 0; i < input.size(); i++) {
			neurons.get(0).get(i).SetOutput(input.get(i));
		}
		
		//forward propogate
		for(int layer = 1; layer < neurons.size(); layer++) {
			ArrayList<Neuron> prevLayer = neurons.get(layer - 1);
			for(int n = 0; n < neurons.get(layer).size() - 1; n++) {
				neurons.get(layer).get(n).FeedForward(prevLayer); //pass in previous layer
			}
		}
	}
	
	public void BackPropogation(ArrayList<Double> target) {
		//calculate net error RMS (root mean square error)
		ArrayList<Neuron> outputLayer = neurons.get(neurons.size() - 1);
		
		error = 0.0;
		
		for(int n = 0; n < outputLayer.size() - 1; n++) { //don't include bias
			double delta = target.get(n) - outputLayer.get(n).GetOutput();
			error += delta * delta;
		}
		
		error /= outputLayer.size() - 1;
		error = Math.sqrt(error);
		
		//calculate recent average for debug
		recentAverageError = (recentAverageError * Net.recentAverageSmoothingFactor + error) / (Net.recentAverageSmoothingFactor + 1.0);
		
		//calculate output layer gradients
		for(int n = 0; n < outputLayer.size() - 1; n++) {
			outputLayer.get(n).CalculateOutputGradients(target.get(n));
		}
		
		//calculate gradients on hidden layers
		for(int layer = neurons.size() - 2; layer > 0; layer--) {
			ArrayList<Neuron> hiddenLayer = neurons.get(layer);
			ArrayList<Neuron> nextLayer = neurons.get(layer + 1);
			
			for(int n = 0; n < hiddenLayer.size(); n++) {
				hiddenLayer.get(n).CalculateHiddenGradients(nextLayer);
			}
		}
		
		//update connection weights from output to first hidden layer
		for(int layer = neurons.size() - 1; layer > 0; layer--) {
			ArrayList<Neuron> l = neurons.get(layer);
			ArrayList<Neuron> previousLayer = neurons.get(layer - 1);
			
			for(int n = 0; n < l.size() - 1; n++) {
				l.get(n).UpdateInputWeights(previousLayer);
			}
		}
	}
	
	public ArrayList<Double> GetResults() {
		ArrayList<Double> results = new ArrayList<Double>();
		for(int n = 0; n < neurons.get(neurons.size() - 1).size(); n++) {
			results.add(neurons.get(neurons.size() - 1).get(n).GetOutput());
		}
		
		return results;
	}
	
	public static Net Mate(ArrayList<Integer> structure, Net mom, Net dad, boolean momPreference) {
		ArrayList<Double> cMom = Net.SerializeChromosome(mom);
		ArrayList<Double> cDad = Net.SerializeChromosome(dad);
		
		ArrayList<Double> cNew = new ArrayList<Double>();
		Random rand = new Random();
		int cutpoint1 = (int)(rand.nextDouble() * 1.5 * (cMom.size() * 0.3)); //cut length is 1/5
		int cutpoint2 = cutpoint1 + (int)(cMom.size() * 0.3);
		
		//i < cutpoint1 mom, cutpoint1 <= i < cutpoint2 dad, i > cutpoint2     momPreference, flip mom/dad for dadpreference
		for(int i = 0; i < cMom.size(); i++) {
			if(i < cutpoint1) {
				if(momPreference) {
					cNew.add(cMom.get(i));
				} else {
					cNew.add(cDad.get(i));
				}
			} else if(i >= cutpoint1 && i < cutpoint2) {
				if(momPreference) {
					cNew.add(cDad.get(i));
				} else {
					cNew.add(cMom.get(i));
				}
			} else {
				if(momPreference) {
					cNew.add(cMom.get(i));
				} else {
					cNew.add(cDad.get(i));
				}
			}
		}
		
		if(rand.nextDouble() <= Net.mutationRate) {
			for(int i = 0; i < cNew.size(); i++) {
				double d = rand.nextDouble() * Net.mutationMagnitude;
				int s = rand.nextInt(2);
				
				if(s == 0) {
					d += 1;
				} else {
					d = 1 - d;
				}
				
				cNew.set(i, cNew.get(i) * d);
			}
		}
		
		Net returned = Net.GetNet(structure, cNew);
		
		return returned;
	}
	
	public Net Mutate(ArrayList<Integer> structure) {
		ArrayList<Double> cNew = Net.SerializeChromosome(this);
		
		Random rand = new Random();
		
		for(int i = 0; i < cNew.size(); i++) {
			double d = rand.nextDouble() * Net.mutationMagnitude;
			int s = rand.nextInt(2);
			
			if(s == 0) {
				d = -d;
			} 
			
			cNew.set(i, cNew.get(i) + d);
		}
		
		Net returned = Net.GetNet(structure, cNew);
		
		return returned;
	}
	
	public static ArrayList<Double> SerializeChromosome(Net n) {
		ArrayList<Double> returnedChromosome = new ArrayList<Double>();
		
		for(int layer = 0; layer < n.neurons.size(); layer++) {
			for(int node = 0; node < n.neurons.get(layer).size(); node++) {
				for(int connection = 0; connection < n.neurons.get(layer).get(node).outputWeights.size(); connection++) {
					returnedChromosome.add(n.neurons.get(layer).get(node).outputWeights.get(connection).weight);
				}
			}
		}
		
		return returnedChromosome;
	}
	
	public static Net GetNet(ArrayList<Integer> structure, ArrayList<Double> chromosome) {
		Net n = new Net(structure);
		
		int counter = 0;
		
		for(int layer = 0; layer < n.neurons.size(); layer++) {
			for(int node = 0; node < n.neurons.get(layer).size(); node++) {
				for(int connection = 0; connection < n.neurons.get(layer).get(node).outputWeights.size(); connection++) {
					n.neurons.get(layer).get(node).outputWeights.get(connection).weight = chromosome.get(counter);
					counter++;
				}
			}
		}
		
		return n;
	}
	
	public double GetRecentAverageError() {
		return recentAverageError;
	}
	
}
