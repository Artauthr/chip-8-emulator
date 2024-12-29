package art.chp8.instructions;

public class Decoder {
    // the highest 4 bits of the instruction that indicate the operation type
    public static int op (int opcode) {
        return (opcode & 0xF000);
    }

    // n or nibble - A 4-bit value, the lowest 4 bits of the instruction
    public static int n (int opcode) {
        return (opcode & 0x000F);
    }

    // x - A 4-bit value, the lower 4 bits of the high byte of the instruction
    public static int Vx (int opCode) {
        return (opCode & 0x0F00) >> 8;
    }

    // y - A 4-bit value, the upper 4 bits of the low byte of the instruction
    public static int Vy (int opCode) {
        return (opCode & 0x00F0) >> 4;
    }

    // kk or byte - An 8-bit value, the lowest 8 bits of the instruction
    public static int kk (int opcode) {
        return (opcode & 0x00FF);
    }

    // A 12-bit value, the lowest 12 bits of the instruction
    public static int nnn (int opcode) {
        return (opcode & 0x0FFF);
    }
}
