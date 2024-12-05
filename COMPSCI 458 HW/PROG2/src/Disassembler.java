import java.util.HashMap;
import java.util.Map;

public class Disassembler {

    // Maps for instruction mnemonics
    private static final Map<Integer, String> opcodeMap = new HashMap<>();
    private static final Map<Integer, String> functMap = new HashMap<>();

    // Initialize opcode and funct maps
    static {
        // R-Type functions
        functMap.put(0x20, "add");
        functMap.put(0x22, "sub");
        functMap.put(0x24, "and");
        functMap.put(0x25, "or");
        functMap.put(0x2A, "slt");
        functMap.put(0x0C, "syscall");

        // I-Type opcodes
        opcodeMap.put(0x09, "addiu");
        opcodeMap.put(0x0C, "andi");
        opcodeMap.put(0x04, "beq");
        opcodeMap.put(0x05, "bne");
        opcodeMap.put(0x0F, "lui");
        opcodeMap.put(0x23, "lw");
        opcodeMap.put(0x0D, "ori");
        opcodeMap.put(0x2B, "sw");

        // J-Type opcodes
        opcodeMap.put(0x02, "j");
    }

    // Disassemble a single instruction
    public static String disassemble(String hexInstruction) {
        int instruction = Integer.parseUnsignedInt(hexInstruction, 16);
        int opcode = (instruction >>> 26) & 0x3F; // Top 6 bits
        String result;

        if (opcode == 0) {
            // R-Type
            int rs = (instruction >>> 21) & 0x1F;
            int rt = (instruction >>> 16) & 0x1F;
            int rd = (instruction >>> 11) & 0x1F;
            int shamt = (instruction >>> 6) & 0x1F;
            int funct = instruction & 0x3F;

            if (funct == 0x0C) {
                // Special case for syscall
                result = String.format("syscall {opcode: %02X, code: 000000, funct: %02X}",
                        opcode, funct);
            } else {
                String mnemonic = functMap.get(funct);

                if (mnemonic == null) {
                    throw new IllegalArgumentException("Unknown R-Type function: " + funct);
                }

                result = String.format("%s {opcode: %02X, rs: %02X, rt: %02X, rd: %02X, shmt: %02X, funct: %02X}",
                        mnemonic, opcode, rs, rt, rd, shamt, funct);
            }
        } else if (opcodeMap.containsKey(opcode)) {
            String mnemonic = opcodeMap.get(opcode);

            if (mnemonic.equals("j")) {
                // J-Type
                int address = instruction & 0x03FFFFFF;
                result = String.format("%s {opcode: %02X, index: %07X}", mnemonic, opcode, address);
            } else {
                // I-Type
                int rs = (instruction >>> 21) & 0x1F;
                int rt = (instruction >>> 16) & 0x1F;
                int immediate = instruction & 0xFFFF;

                if (mnemonic.equals("lui")) {
                    // Special case for lui
                    rs = 0; // base is always 00
                }

                result = String.format("%s {opcode: %02X, rs(base): %02X, rt: %02X, immediate(offset): %04X}",
                        mnemonic, opcode, rs, rt, immediate);
            }
        } else {
            throw new IllegalArgumentException("Unknown opcode: " + opcode);
        }

        return result;
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar PROG2_Milestone1.jar <8-digit hex instruction>");
            return;
        }

        try {
            String hexInstruction = args[0];
            String output = disassemble(hexInstruction);
            System.out.println(output);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

