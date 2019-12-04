package day1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {


    private static List<String> readListFromFile(String fileName) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static int calculateFuelFromMass(int mass){
        int fuelForMass = (int)Math.floor(mass / 3) - 2;

        //fuel needed for fuel?
        if (fuelForMass <= 0)
            return 0;

        // tail recursion
        return fuelForMass + calculateFuelFromMass(fuelForMass);
    }

    private static List<Integer> convertList(List<String> strings){
        return strings.stream().map( s -> Integer.parseInt(s)).collect(Collectors.toList());
    }

    public static void main(String[] args) {

        List<Integer> massByModule = convertList(readListFromFile("input.txt"));
        List<Integer> fuelByModule = massByModule.stream().map(x -> calculateFuelFromMass(x)).collect(Collectors.toList());
        int sumOfFuelRequirements = fuelByModule.stream().reduce(0, Integer::sum);

        // Part One
        System.out.println("Sum of fuel requirements " +  sumOfFuelRequirements);
    }
}