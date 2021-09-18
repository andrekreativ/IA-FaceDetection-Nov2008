import java.util.*;

public class ANN {
    
    // valor de K en la f. signoidal
    private final double K = 1;
    
    protected int layers[];
    protected double active[][];
    protected double weights[][];
    protected double expected[];
    protected double eta; // the learning rate
    protected double alfa; // the momentum
    protected double deltas[][];
    protected double deltas_an[][];
    
    // Generador de Numeros random
    protected Random randomGen;
    
    
        /*
         *
         * @param: learning rate, momentum and the number of neurons per layer
         * separated by comas
         */
    public ANN(double learningRate, double momentum, int[] layers) {
        // init the random generator
        randomGen  = new Random(1455793215);
        
        // save number of neurons per layer
        this.layers = layers;
        
        // Set the learning rate and momentum
        this.eta = learningRate;
        this.alfa = momentum;
        
        // define expected arrays
        expected = new double[layers[layers.length - 1]];
        
        // define weights' matrix and deltas
        weights = new double[layers.length][1];
        deltas = new double[layers.length][1];
        deltas_an = new double[layers.length][1];
        
        // the first array size is equals to the #number of neurons in the
        // imputs layer
        // and is filled with 1
        weights[0] = new double[layers[0]];
        //Arrays.fill(weights[0], 1);
        for (int i = 0; i < this.weights[0].length; i++) {
            weights[0][i] = i;
        }
        
        deltas[0] = new double[layers[0]];
        deltas_an[0] = new double[layers[0]];
        
        // create weigths for each neural connection
        for (int i = 1; i < weights.length; i++)
            // number of conn = #neurons in layer times #neurons in the previous
            // layer
            weights[i] = new double[layers[i] * layers[i - 1]];
        
        for (int i = 1; i < deltas.length; i++) {
            // number of conn = #neurons in layer times #neurons in the previous
            // layer
            deltas[i] = new double[layers[i] * layers[i - 1]];
            deltas_an[i] = new double[layers[i] * layers[i - 1]];
        }
        
        for (int i = 0; i < deltas.length; i++)
            for (int j = 0; j < deltas[i].length; j++)
                deltas[i][j] = 0;
        
        // fill with random weigths
        for (int i = 1; i < weights.length; i++)
            for (int j = 0; j < weights[i].length; j++)
                weights[i][j] = randomGen.nextDouble() * 2 - 1;
        
        // create matrix for activation values, column 0 is for inputs
        active = new double[layers.length + 1][1];
        active[0] = new double[this.layers[0]];
        for (int i = 1; i < active.length; i++)
            active[i] = new double[this.layers[i - 1]];
        
    }
    
        /*
         * Defines the expected output values of the network @param: a list with the
         * expected values
         */
    public void defineExpected(double expected[]) {
        for (int i = 0; i < Math.min(this.expected.length, expected.length); i++) {
            this.expected[i] = expected[i];
        }
    }
    
        /*
         * Defines the input values of the network. The inputs are stored in the
         * first column of the active matrix Note: These input values will be
         * normalized @param: a list with the input values
         */
    public void defineInputs(double inputs[]) {
        for (int i = 0; i < Math.min(this.active[0].length, inputs.length); i++) {
            this.active[0][i] = inputs[i];
        }
    }
    
        /*
         * Signoidal Transfer Function
         */
    private double signoidal(double k, double x) {
        return 1 / (1 + pow(Math.E, -k * x));
    }
    
        /*
         * Normalize a value using Signoidal
         */
    private double g(double x) {
        return signoidal(K, x);
    }
    
    private void propagation() {
        // for each layer...for each neuron...calculate its activation value
        for (int i = 1; i < this.active.length; i++)
            for (int j = 0; j < this.active[i].length; j++)
                this.active[i][j] = a(i, j);
    }
    
    /**
     * Makes a query to the ANN and returns an array with answer
     * @param entradas <description>
     * @return <description>
     */
    public double[] query(double[] entradas) {
        double cp_active[][] = this.active;
        cp_active[0] = entradas;
        for (int i = 1; i < this.active.length; i++)
            for (int j = 0; j < this.active[i].length; j++)
                cp_active[i][j] = aq(i, j, cp_active);
        
        return cp_active[cp_active.length-1];
    }
    
        /*
         * Calculates the activation value of a neuron 1 <= layer <= n, 0 <= neuron <
         * m where n is the number of layers and m the number of neurons in the
         * specified layer @param: the number of layer & the number of neuron
         */
    private double a(int layer, int neuron) {
        double sum = 0;
        // Get the initial index where the corresponding weights to this neuron
        // are located
        // Number of the neuron times the number of neurons in the previous
        // layer
        int offset;
        int num_neuronas;
        
        if (layer == 1) {
            offset = neuron;
            num_neuronas = 1;
        } else {
            offset = neuron * this.layers[layer-2];
            num_neuronas = layers[layer-2];
        }
        
        
        
        // for each connection multiply the weight times the activation value of
        // the previous layer
        //System.out.println(layer +", " +  neuron +", "+ offset);
        for (int i = 0; i < num_neuronas; i++){
            double w = this.weights[layer-1][i + offset];
            double a;
            if (layer == 1) {
                a = this.active[layer-1][neuron];
            } else {
                a = this.active[layer-1][i];
            }
            //System.out.println("w, a: " + w + " " + a);
            sum += w * a;
        }
        return this.g(sum);
    }
    
        /*
         * Calculates the activation value of a neuron 1 <= layer <= n, 0 <= neuron <
         * m where n is the number of layers and m the number of neurons in the
         * specified layer @param: the number of layer & the number of neuron
         */
    private double aq(int layer, int neuron, double[][] cp_a) {
        double sum = 0;
        // Get the initial index where the corresponding weights to this neuron
        // are located
        // Number of the neuron times the number of neurons in the previous
        // layer
        int offset;
        int num_neuronas;
        
        if (layer == 1) {
            offset = neuron;
            num_neuronas = 1;
        } else {
            offset = neuron * this.layers[layer-2];
            num_neuronas = layers[layer-2];
        }
        
        
        
        // for each connection multiply the weight times the activation value of
        // the previous layer
        //System.out.println(layer +", " +  neuron +", "+ offset);
        for (int i = 0; i < num_neuronas; i++){
            double w = this.weights[layer-1][i + offset];
            double a;
            if (layer == 1) {
                a = cp_a[layer-1][neuron];
            } else {
                a = cp_a[layer-1][i];
            }
            //System.out.println("w, a: " + w + " " + a);
            sum += w * a;
        }
        return this.g(sum);
    }
    
    
        /*
         * @return: the cuadratic error of the network
         */
    private double networkCuadraticError() {
        double sum = 0; double sal, esp, err;
        for (int i = 0; i < this.expected.length; i++){
            sal = this.active[this.active.length - 1][i];
            esp = this.expected[i];
            //System.out.println("Sal:" + sal + " esp: " + esp);
            err = esp - sal;
            sum += err*err;
        }
        return sum;
    }
    
        /*
         * Returns the gradient error of neurons in all layers except the output
         * layer @param: number of layer, number of neuron, array with all the ro of
         * layer+1 @return: the gradient error (ro)
         */
    private double ro(int layer, int neuron, double[] ro_k) {
        double er = 0;
        int offset = layers[layer];
        
        // activation value
        double aj = this.active[layer+1][neuron];
        int num_neuronas = layers[layer];
        
        for (int i = 0; i < ro_k.length; i++) {
            int index = (num_neuronas * i) + neuron;
            double w = this.weights[layer+1][index];
            er += ro_k[i] * w;
        }
        
        return aj * (1 - aj) * (er);
    }
    
        /*
         * Returns gradient error for neurons in the output layer @param: number of
         * neuron in the output layer @return: the gradient error (ro)
         */
    private double ro(int neuron) {
        double ak = this.active[this.active.length - 1][neuron];
        return ak * (1 - ak) * (this.expected[neuron] - ak);
    }
    
    private double getDelta(double ro_k, double aj) {
        return this.eta * ro_k * aj;
    }
    
    private void backProp() {
        
        int num_neuronas = layers[layers.length-1];
        double ro[] = new double[num_neuronas];
        int capa = layers.length-1;
        
        // numero de nodos en la capa anterior
        int num_ant = layers[capa-1];
        
        // para cada neurona en la ultima capa
        for (int k = 0; k < num_neuronas; k++) {
            ro[k] = this.ro(k);
            
            // posicion donde inicia el arreglo de pesos correspondientes a la neurona 'k'
            int offset = k*num_ant;
            
            // para cada peso que apunta a neurona 'k'
            for (int j = 0; j < num_ant; j++) {
                int index = offset+j;
                // copy delta of last iteration
                deltas_an[capa][index] = deltas[capa][index];
                // val activacion
                double aj = active[capa][j];
                
                // obtener delta actual ^W
                double delta    = this.getDelta(ro[k], aj);
                
                // delta anterior
                double delta_an = deltas_an[capa][index];
                
                // recalcular peso = (peso actual) + (delta actual) + (momentum * delta anterior)
                double w_an = weights[capa][index];
                double w_nu =  w_an + delta + this.alfa * delta_an;
                weights[capa][index] = w_nu;
                
                // guardar delta de esta iteracion
                deltas[capa][index]  = delta;
            }
        }
        
        double ro_k[];
        int penultima = layers.length-2;
        for(capa = penultima; capa > 0; capa--){
            // copiar ro's anteriores
            ro_k = ro;
            
            // numero de nodos en la capa actual
            num_neuronas = layers[capa];
            
            // arreglo para cada ro, 1 por nodo
            ro = new double[num_neuronas];
            
            // numero d nodos capa anterior,vamos a modificar cada peso que apunta a la nodo de la capa actual
            num_ant = layers[capa-1];
            
            // por cada neurona en la capa actual 'capa'
            for (int j = 0; j < num_neuronas; j++) {
                // sacar ro
                ro[j] = this.ro(capa,j, ro_k);
                
                // posicion donde inicia el arreglo de pesos correspondientes a la neurona 'j'
                int offset = j*num_ant;
                
                // para cada peso que apunta a neurona 'k'
                for (int i = 0; i < num_ant; i++) {
                    int index = offset+i;
                    // copy delta of last iteration
                    deltas_an[capa][index] = deltas[capa][index];
                    // val activacion
                    double ai = active[capa][i];
                    
                    // obtener delta actual ^W
                    double delta    = this.getDelta(ro[j], ai);
                    
                    // delta anterior
                    double delta_an = deltas_an[capa][index];
                    
                    // recalcular peso = (peso actual) + (delta actual) + (momentum * delta anterior)
                    double w_an = weights[capa][index];
                    double w_nu =  w_an + delta + this.alfa * delta_an;
                    weights[capa][index] = w_nu;
                    
                    // guardar delta de esta iteracion
                    deltas[capa][index]  = delta;
                }
            }
        }
        
    }
    
    /**
     * Pow apoximation
     * @param a <description> is the base
     * @param b <description> is the power
     * @return <description> a to the b power
     */
    private static double pow(final double a, final double b) {
        final int x = (int) (Double.doubleToLongBits(a) >> 32);
        final int y = (int) (b * (x - 1072632447) + 1072632447);
        return Double.longBitsToDouble(((long) y) << 32);
    }

    
    public double learn(double[] inputs, double[] expected) {
        this.defineInputs(inputs);
        this.defineExpected(expected);
        double err;
        //do {
        this.propagation();
        this.backProp();
        err = this.networkCuadraticError();
        //} while (err > this.error);
        //System.out.printf("Error: %.6f\n", err);
        return err;
    }
    
    /**
     * <description>Set the weight for the ANN, usefull when you want to load a pre-trainned network
     * @param w <description>A matrix with the weights (doubles)
     */
    public void setWeights(double[][] w) {
         this.weights = w;
    }
    
    public void printNetwork() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                //System.out.printf("%.2f ", weights[i][j]);
            //System.out.println("\n");
            }
        }
    }
    
        /*
        public static void main(String[] args) {
                // Create a neural network
                ANN net = new ANN(0.5, 0.3, 0.1, 2, 2, 2);
         
                double inputs[] = { 4.3, 16.14 };
                double expected[] = { 1, 0 };
                 // pesos q recibe:   i=0,i=1  j = 0      j = 1        k = 0       k = 1
                double weights[][] = {{1,1},{0.33,0.40, 0.33,-0.06}, {0.77, 0.28, -0.52, 0.89}};
                net.testSetWeights(weights);
         
                net.learn(inputs, expected);
         
                net.printNetwork();
        }
        */
                 
}
