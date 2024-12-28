package art.chp8;

import java.util.Arrays;

public class InstructionExecutor {
    /*
    nnn or addr - A 12-bit value, the lowest 12 bits of the instruction
    n or nibble - A 4-bit value, the lowest 4 bits of the instruction
    x - A 4-bit value, the lower 4 bits of the high byte of the instruction
    y - A 4-bit value, the upper 4 bits of the low byte of the instruction
    kk or byte - An 8-bit value, the lowest 8 bits of the instruction
     */
    private final Processor processor;


    public InstructionExecutor (Processor processor) {
        this.processor = processor;
    }

    public void execute (int opCode) {
        int opPrefix = opCode & 0xF000;
        int vRegister = (opCode & 0x0F00) >> 8;
        int nnValue = opCode & 0x00FF;
        int nnnAddress = opCode & 0x0FFF;

        switch (opPrefix) {
            case CLS:

        }
    }

    /*
    00E0 - CLS
    Clear the display.
     */
    private static final int CLS = 0x00E0;


    private void CLS () {
        boolean[][] pixels = processor.getPixels();

        for (boolean[] row : pixels) {
            Arrays.fill(row, false);
        }
    }

    /*
    00EE - RET
    Return from a subroutine.
     */
    private void RET () {
        int pop = processor.stackPop();
        processor.setProgramCounter(pop);
    }

    /*
    1nnn - JP addr
    Jump to location nnn.
     */
    private void JP_addr (int addr) {
        processor.setProgramCounter(addr);
    }

    /*
    2nnn - CALL addr
    Call subroutine at nnn.
     */
    private void CALL_addr (int addr) {
        int currentPc = processor.getProgramCounter();
        processor.stackPush(currentPc);
        processor.setProgramCounter(addr);
    }

    /*
    3xkk - SE Vx, byte
    Skip next instruction if Vx = kk.
     */
    private void SE_Vx_byte (int Vx, int kkByte) {
        byte[] vRegisters = processor.getvRegisters();

        byte currentRegisterValue = vRegisters[Vx];
        if (currentRegisterValue == kkByte) {
            processor.incrementProgramCounter(2);
        }
    }

    /*
    4xkk - SNE Vx, byte
    Skip next instruction if Vx != kk.
     */
    private void SNE_Vx_byte (int Vx, int kkByte) {
        byte[] vRegisters = processor.getvRegisters();

        byte currentRegisterValue = vRegisters[Vx];
        if (currentRegisterValue != kkByte) {
            processor.incrementProgramCounter(2);
        }
    }

    /*
    5xy0 - SE Vx, Vy
    Skip next instruction if Vx = Vy.
     */
    private void SE_Vx_Vy (int Vx, int Vy) {
        byte[] vRegisters = processor.getvRegisters();

        byte xValue = vRegisters[Vx];
        byte yValue = vRegisters[Vy];

        if (xValue == yValue) {
            processor.incrementProgramCounter(2);
        }
    }

    /*
    6xkk - LD Vx, byte
    Set Vx = kk.
     */
    private void LD_Vx_byte (int Vx, int kkValue) {
        byte[] vRegisters = processor.getvRegisters();
        vRegisters[Vx] = (byte) kkValue;
    }

    /*
    7xkk - ADD Vx, byte
    Set Vx = Vx + kk.
     */
    private void ADD_Vx_byte (int Vx, int kkValue) {
        byte[] vRegisters = processor.getvRegisters();
        byte currentValue = vRegisters[Vx];
        vRegisters[Vx] = (byte) (currentValue + kkValue);
    }

    /*
    8xy0 - LD Vx, Vy
    Set Vx = Vy.
     */
    private void LD_Vx_Vy (int Vx, int Vy) {
        byte[] vRegisters = processor.getvRegisters();
        vRegisters[Vx] = vRegisters[Vy];
    }



    /*
    ANNN - LD I, addr
    Set I = nnn.
     */
    private void LD_I_addr (int nnn) {
        processor.setIndexRegister(nnn);
    }


    /*
    Dxyn - DRW Vx, Vy, nibble
    Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     */
    private void DRW_Vx_Vy_nib (int Vx, int Vy, int N) {
        byte[] vRegisters = processor.getvRegisters();
        byte[] memory = processor.getMemory();

        boolean[][] pixels = processor.getPixels();
        int indexRegister = processor.getIndexRegister();

        // Get positions and wrap them
        int xStartPos = vRegisters[Vx] % Processor.SCREEN_WIDTH;
        int yStartPos = vRegisters[Vy] % Processor.SCREEN_HEIGHT;

        vRegisters[0xF] = 0; // Reset collision register, should turn to 1 if any collisions happen during drawing

        // Each sprite is 8 pixels wide and N pixels tall.
        for (int rowIndex = 0; rowIndex < N; rowIndex++) {
            byte spriteByte = memory[indexRegister + rowIndex];

            for (int bitIndex = 0; bitIndex < Processor.SPRITE_WIDTH; bitIndex++) {
                int bitMask = 1 << (Processor.SPRITE_WIDTH - 1 - bitIndex);
                int pixelState = spriteByte & bitMask;

                if (pixelState == 0) continue; // Skip if the sprite bit is not set

                int xPos = (xStartPos + bitIndex) % Processor.SCREEN_WIDTH;
                int yPos = (yStartPos + rowIndex) % Processor.SCREEN_HEIGHT;

                boolean originalState = pixels[xPos][yPos];
                pixels[xPos][yPos] ^= true;

                if (originalState && !pixels[xPos][yPos]) {
                    vRegisters[0xF] = 1; // Collision detected
                }
            }
        }
    }




}
