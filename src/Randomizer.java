import java.util.Random;

public class Randomizer {
    public static int getRandomNumber(int numOptions) {
        Random random = new Random();
        return random.nextInt(numOptions) - 1;
    }
}
