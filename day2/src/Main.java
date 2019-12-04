import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private interface OpcodeProcessor {
        void process(List<Integer> mem, int pos);
    }

    private static OpcodeProcessor getOpcodeProcessor(int opcode) {

        return switch (opcode) {
        case 1 -> (mem, pos) -> mem.set(mem.get(pos + 3), mem.get(mem.get(pos + 1)) + mem.get(mem.get(pos + 2)));
        case 2 -> (mem, pos) -> mem.set(mem.get(pos + 3), mem.get(mem.get(pos + 1)) * mem.get(mem.get(pos + 2)));
        case 99 -> (mem, pos) -> {
        };
        default -> null;
        };
    }

    private static List<String> readListFromFile(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static boolean findInput(final String input, int noun, int verb, int target) {

        List<Integer> mem = Arrays.asList(input.split(",")).stream().map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
        // restore computer state "1202 program alarm"
        mem.set(1, noun);
        mem.set(2, verb);

        for (int pos = 0; pos + 4 < mem.size(); pos += 4) {
            getOpcodeProcessor(mem.get(pos)).process(mem, pos);
        }

        if (mem.get(0) == target) {
            System.out.println("Found matching verb and noun at " + (100 * noun + verb));
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // read first line from input
        String input = readListFromFile("input.txt").get(0);

        for (int noun = 0; noun <= 99; ++noun) {
            final int spawnNoun = noun;
            // Runnable task = () -> {
            for (int verb = 0; verb <= 99; ++verb) {
                if (findInput(input, spawnNoun, verb, 19690720))
                    return;
            }
            // };
            // task.run();
            // Thread thread = new Thread(task);
            // thread.start();
        }
    }
}
