# machine-learning

This project demonstrates two relatively simple neural networks. I was very interested in artificial intelligence, so I read up on resources online and made these two.

LanguageDetection is a back-propagation neural net that attempts to learn whether an input word (max 10 characters) is an English word or a randomly typed sequence of characters, like "jqehwqe". It learns via back-propagation based on a learning file I generated from an online dictionary. It is able to achieve a 90% accuracy on the test case.

NeuralNet_TicTacToe is an attempt at genetic algorithm - allowing the neural networks to evolve. Each generation has a population of 1000, and competes either against a perfect opponent, or against other neural nets. After 1 million generations it achieves a local maximum of about 50% win-rate against the perfect opponent. 

To train, run Train() in main and comment out HumanInput(). To test either of them with manual input, comment out Train() in the main function and allow HumanInput() to run. 
