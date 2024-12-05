import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AsmTester {
    public static void main(String[] args) {

        String sourceFilePath = "src/EvenOrOdd.asm";
        String expectedOutputTextFile = "src/EvenOrOddExpected.text";
        String expectedOutputDataFile = "src/EvenOrOddExpected.data";

        try {
            // Step 1: Execute the assembler on the ASM file
            System.out.println("Running MIPS Assembler...");
            Reduced_ISA_MIPS_Assembler.main(new String[]{sourceFilePath});

            // Step 2: Validate the .text and .data files
            boolean isTextFileMatching = fileContentMatches("src/EvenOrOdd.text", expectedOutputTextFile, ".text");
            boolean isDataFileMatching = fileContentMatches("src/EvenOrOdd.data", expectedOutputDataFile, ".data");

            // Step 3: Display test results
            if (isTextFileMatching && isDataFileMatching) {
                System.out.println("All tests passed! Generated output matches expected results.");
            } else {
                System.out.println("Tests failed:");
                if (!isTextFileMatching) System.out.println("Mismatch found in .text section.");
                if (!isDataFileMatching) System.out.println("Mismatch found in .data section.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Helper method to compare two files line by line
    private static boolean fileContentMatches(String generatedFilePath, String expectedFilePath, String section) throws IOException {
        List<String> generatedFileLines = Files.readAllLines(Paths.get(generatedFilePath));
        List<String> expectedFileLines = Files.readAllLines(Paths.get(expectedFilePath));

        if (generatedFileLines.size() != expectedFileLines.size()) {
            System.out.println("File length mismatch in " + section + " section: Generated file has "
                    + generatedFileLines.size() + " lines, Expected file has " + expectedFileLines.size() + " lines.");
            return false; // Files have different lengths
        }

        for (int lineIndex = 0; lineIndex < generatedFileLines.size(); lineIndex++) {
            if (!generatedFileLines.get(lineIndex).equals(expectedFileLines.get(lineIndex))) {
                System.out.println("Mismatch in " + section + " at line " + (lineIndex + 1));
                System.out.println("Generated: " + generatedFileLines.get(lineIndex));
                System.out.println("Expected:  " + expectedFileLines.get(lineIndex));
                return false;
            }
        }
        return true;
    }
}

