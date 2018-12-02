import java.io.Serializable;
import java.util.ArrayList;

public class Net implements Serializable{
	private static final long serialVersionUID = -592759618095858884L;

	public ArrayList<ArrayList<Neuron>> neurons = new ArrayList<ArrayList<Neuron>>();
	
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
				//System.out.println("Made neuron! Layer: " + layer + " Index: " + n);
			}
			
			//set bias node
			neurons.get(layer).get(neurons.get(layer).size() - 1).SetOutput(1.0);
		}
	}
	
	public void FeedForward(ArrayList<Double> input) {
		if(input.size() != neurons.get(0).size() - 1) {
			System.out.println("INPUT SIZE: " + input.size() + " INPUT NEURON SIZE: " + (neurons.get(0).size() - 1));
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
	
	//learn
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
	
	public double GetRecentAverageError() {
		return recentAverageError;
	}
	
}
