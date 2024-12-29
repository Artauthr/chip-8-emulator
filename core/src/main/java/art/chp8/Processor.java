package art.chp8;

import art.chp8.instructions.Instruction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class Processor {
    private final static short START_ADDRESS = 0x200;

    public static final int FONT_LOAD_START_ADDRESS = 0x50;
    public static final int FONT_SIZE_BYTES = 5;

    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;

    public static final int SPRITE_WIDTH = 8;

    /*
    64x32 pixel display
    */
    private final boolean[][] pixels = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];

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
    private int programCounter;

    /*
     The stack for subroutine return addresses (max 16 entries)
     */
    private final int[] stack = new int[16];

    /*
     Stack pointer to track the top of the stack
     */
    private int stackPointer = 0;

    /*
    The delay timer is active whenever the delay timer register (DT) is non-zero.
    This timer does nothing more than subtract 1 from the value of DT at a rate of 60Hz. When DT reaches 0, it deactivates.
     */
    private int DT;


    /*The sound timer is active whenever the sound timer register (ST) is non-zero.
     This timer also decrements at a rate of 60Hz, however, as long as ST's value is greater than zero, the Chip-8 buzzer will sound. When ST reaches zero, the sound timer deactivates.
     */
    private int ST;

    private final Keypad keypad;

    public Keypad getKeypad () {
        return this.keypad;
    }

    public Processor() {
        programCounter = START_ADDRESS;
        loadInternalFonts();

        keypad = new Keypad();
        loadROM("tetris");
    }

    public void tick () {
        // FETCH
        int currentInstruction = fetchCurrentInstruction();

        // DECODE
        Instruction instruction = Instruction.fromOpcode(currentInstruction);

        // EXECUTE
        instruction.execute(this, currentInstruction);

        if (DT > 0) {
            DT--;
        }

        if (ST > 0) {
            ST--;
        }
    }

    public void pushStack (int value) {
        if (stackPointer >= stack.length) {
            throw new IllegalStateException("Stack overflow");
        }
        stack[stackPointer++] = value;
    }

    public int popStack () {
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

    private void loadInternalFonts () {
        for (int i = 0; i < fonts.length; i++) {
            memory[FONT_LOAD_START_ADDRESS + i] = (byte) fonts[i];
        }
    }

    private final int[] fonts = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    /***
     *
     * @param name Name of file without file extension located in assets roms folder
     */
    private void loadROM (String name) {
        FileHandle romFile = Gdx.files.internal("roms/" + name + ".ch8");

        if (!romFile.exists()) {
            throw new GdxRuntimeException("Could not find ROM file: " + romFile.path());
        }

        try {
            byte[] romBytes = romFile.readBytes();
            System.arraycopy(romBytes, 0, memory, START_ADDRESS, romBytes.length);
        } catch (Exception e) {
            throw new GdxRuntimeException("Failed to load ROM: " + romFile.path(), e);
        }
    }

    public void skipNextInstruction () {
        this.programCounter += 2;
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

    public byte readMemory (int address) {
        return memory[address];
    }

    public void writeMemory (int address, int value) {
        memory[address] = (byte) value;
    }

    public byte[] getVRegisters() {
        return vRegisters;
    }

    public int getIndexRegister () {
        return iRegister;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setST(byte ST) {
        this.ST = ST;
    }

    public int getDT() {
        return DT;
    }

    public void setDT(byte DT) {
        this.DT = DT;
    }

    public void setProgramCounter (int value) {
        this.programCounter = value;
    }
}
