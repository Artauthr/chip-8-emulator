package art.chp8;

import java.util.Arrays;


public class Processor {
    private final static short START_ADDRESS = 0x200;

    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;

    public static final int SPRITE_WIDTH = 8;

    /*
    64x32 pixel display
    */
    private final boolean[][] pixels = new boolean[64][32];

    /*
    Total memory (4KB in size)
     */
    private final byte[] memory = new byte[4096];

    /*
    General purpose "V" registers that range from 0x00 to 0x10
     */
    private final byte[] vRegisters = new byte[16];

    /*
     A 16-bit register for memory addresses.
     */
    private int iRegister;

    /*
     Program Counter: A 16-bit register for tracking the current instruction.
     */
    private int programCounter = START_ADDRESS;

    /*
     The stack for subroutine return addresses (max 16 entries)
     */
    private final int[] stack = new int[16];

    /*
     Stack pointer to track the top of the stack
     */
    private int stackPointer = 0;


    public void stackPush (int value) {
        if (stackPointer >= stack.length) {
            throw new IllegalStateException("Stack overflow");
        }
        stack[stackPointer++] = value;
    }

    public int stackPop () {
        if (stackPointer <= 0) {
            throw new IllegalStateException("Stack underflow");
        }
        return stack[--stackPointer];
    }

    public int fetchCurrentInstruction () {
        int b1 = memory[programCounter] & 0xFF;
        int b2 = memory[programCounter + 1] & 0xFF;

        // Shift high byte to left and combine 1st and 2nd bytes at program counter to get the instruction
        int combinedOpcode = (b1 << 8) | b2;

        // increment PC by 2 byes
        programCounter += 2;

        return combinedOpcode;
    }


    /*
    F: Instruction type (first 4 bits).
    X: Register (next 4 bits).
    NN: Immediate value (last 8 bits).
    Sometimes, the last nibble (N) or 12 bits (NNN) represent additional parameters.
     */
    private void decodeInstruction (int opCode) {
        int opType = opCode & 0xF000;

        int vRegister = (opCode & 0x0F00) >> 8;
        int nnValue = opCode & 0x00FF;
        int nnnAddress = opCode & 0x0FFF;



        switch (opType) {
            case 0x0000:
                if (opCode == 0x00E0) {
                    handleScreenClear();
                } else if (opCode == 0x00EE) {
                    handleReturnFromSubroutine();
                }
                break;
            case 0x2000:
                handleCallSubroutine(nnnAddress);
                break;
            case 0x1000:
                handleJump(nnnAddress); // Jump to address
                break;
            case 0x6000:
                handleSetRegister(vRegister, nnValue); // Set VX to NN
                break;
            case 0x7000:
                handleAddToRegister(vRegister, nnValue); // Add NN to VX
                break;
        }
    }

    private void handleCallSubroutine (int callAddrNNN) {
        stackPush(programCounter);
        programCounter = callAddrNNN;
    }

    private void handleReturnFromSubroutine () {
        programCounter = stackPop();
    }

    private void handleScreenClear () {
        for (boolean[] row : pixels) {
            Arrays.fill(row, false);
        }
    }

    private void handleJump (int jumpAddress) {
        this.programCounter = jumpAddress;
    }

    private void handleSetRegister (int iRegister, int nn) {
        vRegisters[iRegister] = (byte) nn;
    }

    private void handleAddToRegister (int iRegister, int nn) {
        byte currentValue = vRegisters[iRegister];
        vRegisters[iRegister] = (byte) (currentValue + nn);
    }

    public void incrementProgramCounter (int amount) {
        this.programCounter += amount;
    }

    public void setIndexRegister (int value) {
        this.iRegister = value;
    }

    public boolean[][] getPixels() {
        return pixels;
    }

    public byte[] getMemory() {
        return memory;
    }

    public byte[] getvRegisters() {
        return vRegisters;
    }

    public int getIndexRegister () {
        return iRegister;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int[] getStack() {
        return stack;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void setProgramCounter (int value) {
        this.programCounter = value;
    }
}
