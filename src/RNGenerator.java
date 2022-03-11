public class RNGenerator {
    private static final long MULTIPLIER = 134775813;
    private static final long INCREMENT = 1;
    private static final long MODULUS = 4294967296L;
    private double lambda;
    private long seed;
    private GeneratorType generatorType;

    public enum GeneratorType {
        UNIFORM, EXPONENTIAL
    }

    /**
     * Constructor for random exponential variate generator
     * @param seed first value of the generator
     * @param lambda rate
     */
    public RNGenerator(long seed, double lambda) {
        this.seed = seed;
        this.generatorType = GeneratorType.EXPONENTIAL;
        this.lambda = lambda;
    }

    /**
     * Constructor for uniform generator with seed
     * @param seed first value of the generator
     */
    public RNGenerator(long seed) {
        this.seed = seed;
        this.generatorType = GeneratorType.UNIFORM;
    }

    /**
     * Constructor for uniform generator without seed. Provides seed
     * using epoch time
     */
    public RNGenerator() {
        this.seed = System.currentTimeMillis();
        this.generatorType = GeneratorType.UNIFORM;
    }

    /**
     * Gets the next uniform value from randomizer (uses linear congruential method)
     * @return uniformly distributed double between 0-1
     */
    private double getNextUniform() {
        seed = (seed*MULTIPLIER + INCREMENT) % MODULUS;
        return  (double) seed/ (double) MODULUS;
    }

    /**
     * Uses inverse-transform technique to generate random exponential variates from uniform distribution
     * @return exponentially distributed double based on provided lambda during construction
     */
    private double getNextExponential() {
        double rand_uniform = getNextUniform();
        double next = Math.log(1-rand_uniform) * -1;
        return next/lambda;
    }

    /**
     * Method to get the next random variate. Decides between distributions based on initial construction
     * @return random variate double
     */
    public double getNext() {
        if(generatorType.equals(GeneratorType.UNIFORM)) return getNextUniform();
        if(generatorType.equals(GeneratorType.EXPONENTIAL)) return getNextExponential();
        else {
            return -1;
        }
    }

}
