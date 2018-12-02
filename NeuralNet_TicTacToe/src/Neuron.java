import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Neuron implements Serializable {	
	private static final long serialVersionUID = 108300505564917402L;

	class Connection implements Serializable {
		private static final long serialVersionUID = 5030952193707533762L;
		
		public double weight;
		public double deltaWeight;
	}
	
	private int index;
	private double gradient;
	private double outputVal;
	public ArrayList<Connection> outputWeights = new ArrayList<Connection>();
	
	private static double eta = 0.25; //learning rate
	private static double alpha = 0.5; //learning momentum
	
	public Neuron(int numOutputs, int index) {
		this.index = index;
		
		Random rand = new Random();
		
		for(int c = 0; c < numOutputs; c++) {
			Connection newConnection = new Connection();
			newConnection.weight = rand.nextDouble();
			outputWeights.add(newConnection);
		}
	}
	
	public Neuron(Neuron n) {
		this.index = n.index;
		
		for(int c = 0; c < n.outputWeights.size(); c++) {
			Connection newConnection = new Connection();
			newConnection.weight = n.outputWeights.get(c).weight;
			outputWeights.add(newConnection);
		}
	}
	
	public void FeedForward(ArrayList<Neuron> previousLayer) {
		double sum = 0.0;
		
		for(int n = 0; n < previousLayer.size(); n++) {
			sum += previousLayer.get(n).GetOutput() * previousLayer.get(n).outputWeights.get(index).weight;
		}
		
		//use transfer function on the sum
		outputVal = Neuron.TransferFunction(sum);
	}
	
	public void CalculateOutputGradients(double target) {
		double delta = target - outputVal;
		gradient = delta * Neuron.TransferFunctionDerivative(outputVal);
	}
	
	public void CalculateHiddenGradients(ArrayList<Neuron> nextLayer) {
		double dow = SumDOW(nextLayer);
		gradient = dow * Neuron.TransferFunctionDerivative(outputVal);
	}
	
	private double SumDOW(ArrayList<Neuron> nextLayer) {
		double sum = 0;
		
		//sum contributions of errors at nodes we feed
		for(int n = 0; n < nextLayer.size() - 1; n++) {
			sum += outputWeights.get(n).weight * nextLayer.get(n).gradient;
		}
		
		return sum;
	}
	
	public void UpdateInputWeights(ArrayList<Neuron> prevLayer) {
		for(int n = 0; n < prevLayer.size(); n++) {
			Neuron neuron = prevLayer.get(n);
			double oldDeltaWeight = neuron.outputWeights.get(index).deltaWeight;
			//eta = learning rate, alpha = momentum of learning
			double newDeltaWeight = Neuron.eta * neuron.GetOutput() * gradient + Neuron.alpha * oldDeltaWeight;
			
			neuron.outputWeights.get(index).deltaWeight = newDeltaWeight;
			neuron.outputWeights.get(index).weight += newDeltaWeight;
		}
	}
	
	public void SetOutput(double d) {
		outputVal = d;
	}
	
	public double GetOutput() {
		return outputVal;
	}
	
	private static double TransferFunction(double d) {
		return Math.tanh(d);
	}
	
	private static double TransferFunctionDerivative(double d) {
		double t = Math.tanh(d);
		return 1 - (t * t);
	}
}
