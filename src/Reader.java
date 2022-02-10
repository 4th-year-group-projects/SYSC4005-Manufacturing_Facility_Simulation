import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Reader {
    // Assumes that all entries will have 3 decimal places.
    public static ArrayList<Integer> readFile(String fileName) {
        ArrayList array = new ArrayList<Integer>();
        try {
            Scanner scanner = new Scanner(new File("./SimulationData/" + fileName));
            while (scanner.hasNext()) {
                float temp = scanner.nextFloat();
                array.add(Integer.valueOf(Math.round(temp * 1000)));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return array;
    }
}
