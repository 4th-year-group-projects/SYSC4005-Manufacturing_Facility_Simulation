import java.util.Random;

public class Randomizer {
    private static Random random = new Random(19238912);

    public static int getRandomNumber(int numOptions) {
        return random.nextInt(numOptions);
    }
}
