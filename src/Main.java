public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        simulator.simulate();

//        RNGenerator generator = new RNGenerator(420);
//        double[] rands = new double[49];
//        for(int i = 0; i < rands.length; i++) {
//            rands[i] = generator.getNext();
//        }
//        double sum = 0;
//        for(int i = 1; i < rands.length; i++) {
//            sum += rands[i] * rands[i-1];
//        }
//        double pim = (sum/49) - 0.25;
//        System.out.println("Pim: " + pim);
//        double sigma = Math.sqrt(13*48 + 7) / (12*(49));
//        System.out.println("Sigma Pim: " + sigma);
//        System.out.println("Z0: " + pim/sigma);
    }
}
