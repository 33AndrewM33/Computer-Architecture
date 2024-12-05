import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Reduced_ISA_MIPS_Assembler {
    // Existing mappings from Milestone 1
    private static final Map<String, Integer> regIdentifierMap = new HashMap<>();
    private static final Map<String, Integer> opcodeIdentifierMap = new HashMap<>();
    private static final Map<String, Integer> funcIdentifierMap = new HashMap<>();

    // New mappings for labels and data
    private static final Map<String, Integer> labelAddrMap = new HashMap<>();
    private static final Map<String, Integer> dataAddrMap = new HashMap<>();

    private static final List<Byte> dataByteList = new ArrayList<>();
    private static final int TEXT_SEGMENT_START = 0x00400000;
    private static final int DATA_SEGMENT_START = 0x10010000;

    static {
        // Register mappings
        String[] registerList = {"zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
                "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
                "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
                "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra"};
        for (int i = 0; i < registerList.length; i++) {
            regIdentifierMap.put("$" + registerList[i], i);
            regIdentifierMap.put("$" + i, i);
        }

        // Opcode mappings
        opcodeIdentifierMap.put("add", 0);
        opcodeIdentifierMap.put("addiu", 9);
        opcodeIdentifierMap.put("and", 0);
        opcodeIdentifierMap.put("andi", 12);
        opcodeIdentifierMap.put("beq", 4);
        opcodeIdentifierMap.put("bne", 5);
        opcodeIdentifierMap.put("j", 2);
        opcodeIdentifierMap.put("lui", 15);
        opcodeIdentifierMap.put("lw", 35);
        opcodeIdentifierMap.put("or", 0);
        opcodeIdentifierMap.put("ori", 13);
        opcodeIdentifierMap.put("slt", 0);
        opcodeIdentifierMap.put("sub", 0);
        opcodeIdentifierMap.put("sw", 43);
        opcodeIdentifierMap.put("syscall", 0);

        // Function mappings
        funcIdentifierMap.put("add", 32);
        funcIdentifierMap.put("and", 36);
        funcIdentifierMap.put("or", 37);
        funcIdentifierMap.put("slt", 42);
        funcIdentifierMap.put("sub", 34);
        funcIdentifierMap.put("syscall", 12);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java AssemblyConverter <input.asm>");
            return;
        }

        try {
            String inputFileName = args[0];
            System.out.println("Reading input file: " + inputFileName);

            List<String> fileLines = Files.readAllLines(Paths.get(inputFileName));
            handleFileProcessing(inputFileName, fileLines);

            System.out.println("Assembler completed successfully.");
        } catch (IOException ioException) {
            System.err.println("Error reading file: " + ioException.getMessage());
        }
    }


    private static void handleFileProcessing(String inputFileName, List<String> fileLines) {
        // Split into sections
        List<String> dataSegmentLines = new ArrayList<>();
        List<String> textSegmentLines = new ArrayList<>();
        boolean isInDataSegment = false;
        boolean isInTextSegment = false;

        for (String line : fileLines) {
            line = line.split("#")[0].trim(); // Remove comments
            if (line.isEmpty()) continue;

            if (line.equals(".data")) {
                isInDataSegment = true;
                isInTextSegment = false;
            } else if (line.equals(".text")) {
                isInDataSegment = false;
                isInTextSegment = true;
            } else if (isInDataSegment) {
                dataSegmentLines.add(line);
            } else if (isInTextSegment) {
                textSegmentLines.add(line);
            }
        }

        // Process sections
        parseDataSegment(dataSegmentLines);
        List<Integer> machineCodeInstructions = parseTextSegment(textSegmentLines);

        // Write output files
        String baseFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
        generateDataFile(baseFileName + ".data");
        generateTextFile(baseFileName + ".text", machineCodeInstructions);
    }

    private static void parseDataSegment(List<String> dataSegmentLines) {
        int currentDataAddress = DATA_SEGMENT_START;

        for (String line : dataSegmentLines) {
            if (line.contains(":")) {
                String[] lineParts = line.split(":", 2);
                String labelName = lineParts[0].trim();
                dataAddrMap.put(labelName, currentDataAddress);
                if (lineParts.length > 1) {
                    String[] declarationParts = lineParts[1].trim().split("\\s+", 2);
                    if (declarationParts[0].equals(".asciiz")) {
                        // Extract string between quotes
                        String stringLiteral = declarationParts[1].trim();
                        stringLiteral = stringLiteral.substring(1, stringLiteral.length() - 1); // Remove quotes
                        currentDataAddress += stringLiteral.length() + 1; // +1 for null terminator

                        byte[] asciiBytesArray = stringLiteral.getBytes();
                        int byteCounter = 0;

                        for (byte asciiByte : asciiBytesArray) {
                            byteCounter++;
                            dataByteList.add(asciiByte);
                        }
                        dataByteList.add((byte) 0);
                    }
                }
            }
        }
    }

    private static List<Integer> parseTextSegment(List<String> textSegmentLines) {
        int currentInstructionAddress = TEXT_SEGMENT_START;
        List<Integer> machineCodeList = new ArrayList<>();

        // First pass: collect label addresses
        for (String line : textSegmentLines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.endsWith(":")) {
                String labelName = line.substring(0, line.length() - 1).trim();
                labelAddrMap.put(labelName, currentInstructionAddress);
            } else if (!line.contains(":")) {
                String[] lineParts = line.split("\\s+");
                String instructionName = lineParts[0].toLowerCase();
                if (isPseudoInstruction(instructionName)) {
                    currentInstructionAddress += getPseudoInstructionSize(instructionName) * 4;
                } else {
                    currentInstructionAddress += 4;
                }
            }
        }

        // Second pass: generate machine code
        currentInstructionAddress = TEXT_SEGMENT_START;
        for (String line : textSegmentLines) {
            line = line.trim();
            if (line.isEmpty() || line.endsWith(":")) continue;

            if (line.contains(":")) {
                line = line.split(":", 2)[1].trim();
            }

            String[] lineParts = line.split("\\s+");
            String instructionName = lineParts[0].toLowerCase();

            if (isPseudoInstruction(instructionName)) {
                List<Integer> pseudoInstructionsList = buildPseudoInstruction(line, currentInstructionAddress);
                machineCodeList.addAll(pseudoInstructionsList);
                currentInstructionAddress += getPseudoInstructionSize(instructionName) * 4;
            } else {
                int machineCodeInstruction = assembleInstruction(line, currentInstructionAddress);
                machineCodeList.add(machineCodeInstruction);
                currentInstructionAddress += 4;
            }
        }

        return machineCodeList;
    }


    private static boolean isPseudoInstruction(String instrName) {
        return instrName.equals("li") || instrName.equals("la") ||
                instrName.equals("move") || instrName.equals("blt");
    }

    private static int getPseudoInstructionSize(String instrName) {
        switch (instrName) {
            case "li":
                return 2;
            case "la":
                return 2;
            case "move":
                return 1;
            case "blt":
                return 2;
            default:
                return 1;
        }
    }

    private static List<Integer> buildPseudoInstruction(String instrLine, int instrAddress) {
        List<Integer> pseudoInstrCodeList = new ArrayList<>();
        String[] parts = instrLine.split("[,\\s]+");
        String instrName = parts[0].toLowerCase();

        switch (instrName) {
            case "li":
                int immediateValue = parseImmediateValue(parts[2]);
                if (immediateValue >= -32768 && immediateValue <= 32767) {
                    pseudoInstrCodeList.add(assembleImmediateInstr(new String[]
                            {"addiu", parts[1], "$zero", String.valueOf(immediateValue)}));
                } else {
                    int upperBits = (immediateValue >> 16) & 0xFFFF;
                    int lowerBits = immediateValue & 0xFFFF;
                    if (upperBits != 0) {
                        pseudoInstrCodeList.add(assembleLoadUpperInstr(new String[]{"lui",
                                parts[1], String.valueOf(upperBits)}));
                    }
                    pseudoInstrCodeList.add(assembleImmediateInstr(new String[]{"ori",
                            parts[1], parts[1], String.valueOf(lowerBits)}));
                }
                break;

            case "la":
                int dataAddress = dataAddrMap.get(parts[2]);
                int upperDataBits = (dataAddress >> 16) & 0xFFFF;
                int lowerDataBits = dataAddress & 0xFFFF;
                pseudoInstrCodeList.add(assembleLoadUpperInstr(new String[]{"lui", "$at",
                        String.valueOf(upperDataBits)}));
                pseudoInstrCodeList.add(assembleImmediateInstr(new String[]{"ori",
                        parts[1], "$at", String.valueOf(lowerDataBits)}));
                break;

            case "move":
                pseudoInstrCodeList.add(assembleRTypeInstr(new String[]{"add", parts[1],
                        "$zero", parts[2]}));
                break;

            case "blt":
                String tempRegister = "$at";
                pseudoInstrCodeList.add(assembleRTypeInstr(new String[]{"slt", tempRegister,
                        parts[1], parts[2]}));
                int branchOffset = calculateBranchOffset(instrAddress + 4,
                        labelAddrMap.get(parts[3]));
                pseudoInstrCodeList.add(assembleBranchInstr(new String[]{"bne", tempRegister,
                        "$zero", String.valueOf(branchOffset)}));
                break;
        }

        return pseudoInstrCodeList;
    }

    private static int assembleInstruction(String instrLine, int instrAddress) {
        String[] parts = instrLine.split("[,\\s]+");
        String opcodeName = parts[0].toLowerCase();

        if (opcodeName.equals("syscall")) {
            return 0x0000000c;
        }

        if (funcIdentifierMap.containsKey(opcodeName)) {
            return assembleRTypeInstr(parts);
        } else if (opcodeIdentifierMap.containsKey(opcodeName)) {
            if (opcodeName.equals("beq") || opcodeName.equals("bne")) {
                int targetAddr = labelAddrMap.get(parts[3]);
                int branchOffset = calculateBranchOffset(instrAddress + 4, targetAddr);
                parts[3] = String.valueOf(branchOffset);
                return assembleBranchInstr(parts);
            } else if (opcodeName.equals("j")) {
                int jumpAddr = labelAddrMap.get(parts[1]);
                int formattedJumpAddr = (jumpAddr & 0x0FFFFFFF) >> 2;
                parts[1] = String.valueOf(formattedJumpAddr);
                return assembleJumpInstr(parts);
            } else if (opcodeName.equals("addiu") || opcodeName.equals("andi") ||
                    opcodeName.equals("ori")) {
                return assembleImmediateInstr(parts);
            } else if (opcodeName.equals("lui")) {
                return assembleLoadUpperInstr(parts);
            } else if (opcodeName.equals("lw") || opcodeName.equals("sw")) {
                return assembleMemoryInstr(parts);
            }
        }
        return 0;
    }

    private static int calculateBranchOffset(int fromAddr, int toAddr) {
        return ((toAddr - fromAddr) / 4);
    }

    private static int assembleRTypeInstr(String[] instrParts) {
        int srcReg1 = lookupRegister(instrParts[2]);
        int srcReg2 = lookupRegister(instrParts[3]);
        int destReg = lookupRegister(instrParts[1]);
        int shiftAmt = 0;
        int funcCode = funcIdentifierMap.get(instrParts[0]);
        return (0 << 26) | (srcReg1 << 21) | (srcReg2 << 16) | (destReg << 11) |
                (shiftAmt << 6) | funcCode;
    }

    private static int assembleImmediateInstr(String[] instrParts) {
        int opCode = opcodeIdentifierMap.get(instrParts[0]);
        int srcReg = lookupRegister(instrParts[2]);
        int destReg = lookupRegister(instrParts[1]);
        int immediateVal = parseImmediateValue(instrParts[3]);
        return (opCode << 26) | (srcReg << 21) | (destReg << 16) | (immediateVal & 0xFFFF);
    }

    private static int assembleBranchInstr(String[] instrParts) {
        int opCode = opcodeIdentifierMap.get(instrParts[0]);
        int srcReg1 = lookupRegister(instrParts[1]);
        int srcReg2 = lookupRegister(instrParts[2]);
        int branchOffset = parseImmediateValue(instrParts[3]);
        return (opCode << 26) | (srcReg1 << 21) | (srcReg2 << 16) | (branchOffset & 0xFFFF);
    }

    private static int assembleLoadUpperInstr(String[] instrParts) {
        int opCode = opcodeIdentifierMap.get(instrParts[0]);
        int destReg = lookupRegister(instrParts[1]);
        int immediateVal = parseImmediateValue(instrParts[2]);
        return (opCode << 26) | (destReg << 16) | (immediateVal & 0xFFFF);
    }

    private static int assembleMemoryInstr(String[] instrParts) {
        int opCode = opcodeIdentifierMap.get(instrParts[0]);
        int destReg = lookupRegister(instrParts[1]);
        String[] offsetBaseReg = instrParts[2].split("\\(|\\)");
        int offset = offsetBaseReg[0].isEmpty() ? 0 : parseImmediateValue(offsetBaseReg[0]);
        int baseReg = lookupRegister(offsetBaseReg[1]);
        return (opCode << 26) | (baseReg << 21) | (destReg << 16) | (offset & 0xFFFF);
    }

    private static int assembleJumpInstr(String[] instrParts) {
        int opCode = opcodeIdentifierMap.get(instrParts[0]);
        int addressVal = parseImmediateValue(instrParts[1]);
        return (opCode << 26) | (addressVal & 0x3FFFFFF);
    }

    private static int lookupRegister(String regName) {
        return regIdentifierMap.getOrDefault(regName, 0);
    }

    private static int parseImmediateValue(String immediateStr) {
        try {
            if (immediateStr.startsWith("0x")) {
                return Integer.parseInt(immediateStr.substring(2), 16);
            } else {
                return Integer.parseInt(immediateStr);
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Write output files
    private static void generateDataFile(String dataFilename) {
        List<String> outputLines = new ArrayList<>();
        try (PrintWriter writer = new PrintWriter(dataFilename)) {
            Byte[] dataWord = new Byte[4];
            int byteIndex = 0;
            for (byte dataByte : dataByteList) {
                int bytePosition = 3 - (byteIndex % 4);
                dataWord[bytePosition] = dataByte;
                if (bytePosition == 0) {
                    String hexString = String.format("%02x%02x%02x%02x%n",
                            dataWord[0], dataWord[1], dataWord[2], dataWord[3]);
                    writer.printf(hexString);
                    outputLines.add(hexString);
                    dataWord = new Byte[4];
                }
                byteIndex++;
            }
            while (outputLines.size() < 1024) {
                outputLines.add("00000000");
                writer.printf("00000000%n");
            }
            System.out.println("Successfully wrote to: " + dataFilename);
        } catch (IOException e) {
            System.err.println("Error writing data file: " + e.getMessage());
        }
    }

    private static void generateTextFile(String textFilename, List<Integer> machineCodeList) {
        try (PrintWriter writer = new PrintWriter(textFilename)) {
            for (int instructionCode : machineCodeList) {
                writer.printf("%08x%n", instructionCode);
            }
            System.out.println("Successfully wrote to: " + textFilename);
        } catch (IOException e) {
            System.err.println("Error writing data file: " + e.getMessage());
        }
    }
}