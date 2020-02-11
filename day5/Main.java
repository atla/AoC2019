import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // read first line from input
        String input = readListFromFile("input.txt").get(0);

        runProgram(input);
        // runProgram("1101,5,-1,4,77,0");
    }

    static class IntcodeComputer {
        public List<Integer> memory = new ArrayList<Integer>();
        public int input = 0;
        public int output = 0;

        public IntcodeComputer(List<Integer> mem) {
            this.memory = mem;
        }

        public int get(int pos) {
            return this.memory.get(pos);
        }

        public void set(int pos, int val) {
            this.memory.set(pos, val);
        }
    }

    private interface ParameterMode {
        int get(IntcodeComputer comp, int pos);
    }

    static final ParameterMode IMMEDIATE_MODE = (com, pos) -> com.get(pos);
    static final ParameterMode POSITION_MODE = (com, pos) -> com.get(com.get(pos));

    static ParameterMode getParameterMode(int mode) {
        return switch (mode) {
        case 0 -> POSITION_MODE;
        default -> IMMEDIATE_MODE;
        };
    };

    private interface OpcodeProcessor {
        int process(IntcodeComputer com, int pos);
    }

    private static int findParameterMode(int opcode, int paramPosition) {
        // strip away both most right numbers (actual opcode)
        opcode /= 100;
        // leaves us with the parameter modes ( up to 3 depending on 1,10,100)
        int shifted = (opcode >>> paramPosition);
        int masked = shifted & 0x01;
        return masked;
    }

    static final OpcodeProcessor ADD_PROCESSOR = (com, pos) -> {
        final ParameterMode paramMode1 = getParameterMode(findParameterMode(com.get(pos), 0));
        final ParameterMode paramMode2 = getParameterMode(findParameterMode(com.get(pos), 1));
        com.set(com.get(pos + 3), paramMode1.get(com, pos + 1) + paramMode2.get(com, pos + 2));
        return 4;
    };
    static final OpcodeProcessor MULTIPLY_PROCESSOR = (com, pos) -> {
        final ParameterMode paramMode1 = getParameterMode(findParameterMode(com.get(pos), 0));
        final ParameterMode paramMode2 = getParameterMode(findParameterMode(com.get(pos), 1));
        com.set(com.get(pos + 3), paramMode1.get(com, pos + 1) * paramMode2.get(com, pos + 2));
        return 4;
    };
    static final OpcodeProcessor INPUT_PROCESSOR = (com, pos) -> {
        final ParameterMode paramMode1 = getParameterMode(findParameterMode(com.get(pos), 0));
        com.set(paramMode1.get(com, pos + 1), com.input);
        return 2;
    };
    static final OpcodeProcessor OUTPUT_PROCESSOR = (com, pos) -> {
        com.output = com.get(com.get(pos + 1));
        return 2;
    };
    static final OpcodeProcessor TERMINATE_PROCESSOR = (com, pos) -> {
        return 0;
    };

    static Map<Integer, OpcodeProcessor> OPCODE_MAPPING = Map.of(1, ADD_PROCESSOR, 2, MULTIPLY_PROCESSOR, 3,
            INPUT_PROCESSOR, 4, OUTPUT_PROCESSOR, 99, TERMINATE_PROCESSOR);

    private static List<String> readListFromFile(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void runProgram(final String program) {

        List<Integer> mem = Arrays.asList(program.split(",")).stream().map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
        IntcodeComputer comp = new IntcodeComputer(mem);

        // restore computer state "1202 program alarm"
        // comp.set(1, noun);
        // comp.set(2, verb);
        comp.input = 2387;

        for (int pos = 0, step = 1; pos < mem.size() && step > 0; pos += step) {
            int opcode = comp.get(pos) % 100;
            if (OPCODE_MAPPING.containsKey(opcode)) {
                step = OPCODE_MAPPING.get(opcode).process(comp, pos);
            } else {
                step = 1;
            }
        }

        System.out.println("output is " + comp.output);
    }

}
