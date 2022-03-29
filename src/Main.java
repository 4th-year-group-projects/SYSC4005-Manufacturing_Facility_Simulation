import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator(true);
        simulator.runRNGSimulations();

        Simulator simulatorNoRNG = new Simulator(false);
        simulatorNoRNG.simulate();
        simulatorNoRNG.getRecordList().printAllStatisticsString();
    }
}
