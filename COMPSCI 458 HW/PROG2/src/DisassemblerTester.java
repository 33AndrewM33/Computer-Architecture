import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DisassemblerTester {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java DisassemblerTester <test_file_path>");
            return;
        }

        String filePath = args[0];
        int totalTests = 0;
        int passedTests = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Parse the line into its components
                String[] parts = line.split(" ", 3);
                if (parts.length < 3) {
                    System.err.println("Invalid test case format: " + line);
                    continue;
                }

                String hexInstruction = parts[0];
                String expectedMnemonic = parts[1];
                String expectedOutput = parts[2];

                try {
                    // Disassemble the instruction
                    String actualOutput = Disassembler.disassemble(hexInstruction);

                    // Normalize formatting for comparison, including the mnemonic
                    String normalizedExpected = normalizeOutput(expectedMnemonic, expectedOutput);
                    String normalizedActual = normalizeOutputFromActual(actualOutput);

                    // Compare normalized outputs
                    if (normalizedActual.equals(normalizedExpected)) {
                        passedTests++;
                        System.out.println("Test Passed: " + line);
                    } else {
                        System.out.println("Test Failed: " + line);
                        System.out.println("  Expected: " + normalizedExpected);
                        System.out.println("  Actual  : " + normalizedActual);
                    }
                } catch (Exception e) {
                    System.out.println("Error Disassembling Instruction: " + hexInstruction);
                    System.out.println("  Exception: " + e.getMessage());
                }

                totalTests++;
            }
        } catch (IOException e) {
            System.err.println("Error reading test file: " + e.getMessage());
        }

        // Print summary
        System.out.println("\nTest Summary:");
        System.out.printf("  Total Tests: %d\n", totalTests);
        System.out.printf("  Passed     : %d\n", passedTests);
        System.out.printf("  Failed     : %d\n", totalTests - passedTests);
    }

    // Normalize the expected output, including the mnemonic
    private static String normalizeOutput(String mnemonic, String details) {
        return String.format("%s %s", mnemonic.toUpperCase(), details.toUpperCase());
    }

    // Normalize the actual output from the Disassembler
    private static String normalizeOutputFromActual(String actual) {
        return actual.toUpperCase();
    }
}

