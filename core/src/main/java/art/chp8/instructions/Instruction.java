package art.chp8.instructions;

import art.chp8.Keypad;
import art.chp8.Processor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;

import java.util.Arrays;

public enum Instruction {
    /*
    00EX - System
    */
    SYS(0x0000, (processor, opcode) -> {
        int n = Decoder.n(opcode);

        switch (n) {
            case 0x0: // 00E0 - CLS. Clear the display.
                boolean[][] pixels = processor.getPixels();

                for (boolean[] row : pixels) {
                    Arrays.fill(row, false);
                }
                break;
            case 0xE: // 00EE - RET. Return from a subroutine.
                int pop = processor.popStack();
                processor.setProgramCounter(pop);
                break;
            default:
                throw new UnsupportedOperationException("Operation not found: " + Integer.toHexString(opcode));
        }
    }),

    /*
    1nnn - JP addr
    Jump to location nnn.
    */
    JP(0x1000, (processor, opcode) -> processor.setProgramCounter(Decoder.nnn(opcode))),

    /*
    2nnn - CALL addr
    Call subroutine at nnn.
     */
    CALL(0x2000, (processor, opcode) -> {
        int currentPc = processor.getProgramCounter();
        processor.pushStack(currentPc);
        processor.setProgramCounter(Decoder.nnn(opcode));
    }),

    /*
    3xkk - SE Vx, byte
    Skip next instruction if Vx = kk.
    */
    SE_VALUE(0x3000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        int vxValue = vRegisters[Decoder.Vx(opcode)] & 0xFF; // Unsigned value
        int kk = Decoder.kk(opcode);

        if (vxValue == kk) {
            processor.skipNextInstruction();
        }
    }),


    /*
    4xkk - SNE Vx, byte
    Skip next instruction if Vx != kk.
    */
    SNE_VALUE(0x4000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        byte currentRegisterValue = vRegisters[Decoder.Vx(opcode)];
        if (currentRegisterValue != Decoder.kk(opcode)) {
            processor.skipNextInstruction();
        }
    }),

    /*
    5xy0 - SE Vx, Vy
    Skip next instruction if Vx = Vy.
    */
    SE(0x5000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        byte vx = vRegisters[Decoder.Vx(opcode)];
        byte vy = vRegisters[Decoder.Vy(opcode)];

        if (vx == vy) processor.skipNextInstruction();
    }),

    /*
    6xkk - LD Vx, byte
    Set Vx = kk.
    */
    LD_VALUE(0x6000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        vRegisters[Decoder.Vx(opcode)] = (byte) Decoder.kk(opcode);
    }),

    /*
    7xkk - ADD Vx, byte
    Set Vx = Vx + kk.
     */
    ADD_VALUE(0x7000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte currentValue = vRegisters[Decoder.Vx(opcode)];
        vRegisters[Decoder.Vx(opcode)] = (byte) (currentValue + Decoder.kk(opcode));
    }),

    /*
    LD Vx, Vy
     */
    LD(0x8000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        int Vx = Decoder.Vx(opcode);
        int Vy = Decoder.Vy(opcode);
        int n = Decoder.n(opcode);

        switch (n) {
            case 0:
                // 8xy0 - Set Vx = Vy.
                vRegisters[Vx] = vRegisters[Vy];
                break;
            case 0x1:
                // 8xy1 - Set Vx = Vx OR Vy.
                vRegisters[Vx] = (byte) (vRegisters[Vx] | vRegisters[Vy]);
                break;
            case 0x2:
                // 8xy2 - Set Vx = Vx AND Vy.
                vRegisters[Vx] = (byte) (vRegisters[Vx] & vRegisters[Vy]);
                break;
            case 0x3:
                // 8xy3 - Set Vx = Vx XOR Vy.
                vRegisters[Vx] = (byte) (vRegisters[Vx] ^ vRegisters[Vy]);
                break;
            case 0x4:
                // 8xy4 - Set Vx = Vx + Vy, set VF = carry.
                int additionResult = (vRegisters[Vx] & 0xFF) + (vRegisters[Vy] & 0xFF);
                vRegisters[0xF] = (byte) (additionResult > 255 ? 1 : 0);
                vRegisters[Vx] = (byte) (additionResult & 0xFF);
                break;
            case 0x5:
                // 8xy5 - Set Vx = Vx - Vy, set VF = NOT borrow.
                vRegisters[0xF] = (byte) ((vRegisters[Vx] & 0xFF) > (vRegisters[Vy] & 0xFF) ? 1 : 0);
                vRegisters[Vx] = (byte) ((vRegisters[Vx] & 0xFF) - (vRegisters[Vy] & 0xFF));
                break;
            case 0x6:
                // 8xy6 - Set Vx = Vx SHR 1.
                vRegisters[0xF] = (byte) (vRegisters[Vx] & 0x01);
                vRegisters[Vx] = (byte) ((vRegisters[Vx] & 0xFF) >> 1);
                break;
            case 0x7:
                // 8xy7 - Set Vx = Vy - Vx, set VF = NOT borrow.
                vRegisters[0xF] = (byte) ((vRegisters[Vy] & 0xFF) > (vRegisters[Vx] & 0xFF) ? 1 : 0);
                vRegisters[Vx] = (byte) ((vRegisters[Vy] & 0xFF) - (vRegisters[Vx] & 0xFF));
                break;
            case 0xE:
                // 8xyE - Set Vx = Vx SHL 1.
                vRegisters[0xF] = (byte) ((vRegisters[Vx] & 0xFF) >> 7);
                vRegisters[Vx] = (byte) ((vRegisters[Vx] & 0xFF) << 1);
                break;
            default:
                throw new UnsupportedOperationException("Operation not found: " + Integer.toHexString(opcode));
        }
    }),

    /*
    9xy0 - SNE Vx, Vy
    Skip next instruction if Vx != Vy.
     */
    SNE_VX_VY(0x9000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        int Vx = Decoder.Vx(opcode);
        int Vy = Decoder.Vy(opcode);

        if (vRegisters[Vx] == vRegisters[Vy]) processor.skipNextInstruction();
    }),

    /*
    ANNN - LD I, addr
    Set I = nnn.
    */
    ANNN(0xA000, (processor, opcode) -> processor.setIndexRegister(Decoder.nnn(opcode))),

    /*
    Bnnn - JP V0, addr
    Jump to location nnn + V0.
     */
    BNNN(0xB000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        processor.setProgramCounter(vRegisters[0x0] + Decoder.nnn(opcode));
    }),

    /*
    Cxkk - RND Vx, byte
    Set Vx = random byte AND kk.
     */
    CXKK(0xC000, (processor, opcode) -> {
        int random = MathUtils.random(0, 255);
        int kk = Decoder.kk(opcode);

        int Vx = Decoder.Vx(opcode);
        byte[] vRegisters = processor.getVRegisters();

        vRegisters[Vx] = (byte) (random & kk);
    }),

    /*
    Dxyn - DRW Vx, Vy, nibble
    Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    */
    DXYN(0xD000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        boolean[][] pixels = processor.getPixels();
        int indexRegister = processor.getIndexRegister();

        // Get positions and wrap them
        int xStartPos = vRegisters[Decoder.Vx(opcode)] & 0xFF; // Ensure unsigned
        int yStartPos = vRegisters[Decoder.Vy(opcode)] & 0xFF; // Ensure unsigned

        vRegisters[0xF] = 0; // Reset collision register

        // Ensure sprite height (N) is valid
        int n = Decoder.n(opcode);
        if (n < 0 || n > 15) {
            throw new IllegalArgumentException("Invalid sprite height: " + n);
        }

        for (int rowIndex = 0; rowIndex < n; rowIndex++) {
            int memoryAddress = indexRegister + rowIndex;
            byte spriteByte = processor.readMemory(memoryAddress);

            int yPos = (yStartPos + rowIndex) % Processor.SCREEN_HEIGHT;

            for (int bitIndex = 0; bitIndex < Processor.SPRITE_WIDTH; bitIndex++) {
                int bitMask = 1 << (Processor.SPRITE_WIDTH - 1 - bitIndex);
                int pixelState = spriteByte & bitMask;

                if (pixelState == 0) continue;
                int xPos = (xStartPos + bitIndex) % Processor.SCREEN_WIDTH;

                boolean originalState = pixels[xPos][yPos];
                pixels[xPos][yPos] ^= true;

                if (originalState && !pixels[xPos][yPos]) {
                    vRegisters[0xF] = 1; // Collision detected
                }
            }
        }
    }),


    /*
    Ex - Keyboard
     */
    EX(0xE000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte key = vRegisters[Decoder.Vx(opcode)];

        int type = Decoder.kk(opcode);
        Keypad keypad = processor.getKeypad();
        switch (type) {
            case 0x9E: // Ex9E - SKP Vx. Skip next instruction if key with the value of Vx is pressed.
                if (keypad.isKeyDown(key)) processor.skipNextInstruction();
                break;
            case 0xA1: // ExA1 - SKNP Vx. Skip next instruction if key with the value of Vx is not pressed.
                if (!keypad.isKeyDown(key)) processor.skipNextInstruction();
                break;
            default:
                throw new UnsupportedOperationException("Operation not found: " + Integer.toHexString(opcode));
        }
    }),

    /*
    FX - timers, internal fonts
     */
    FX(0xF000, (processor, opcode) -> {
        int Vx = Decoder.Vx(opcode);
        byte[] vRegisters = processor.getVRegisters();
        int indexRegister = processor.getIndexRegister();

        int type = Decoder.kk(opcode);
        switch (type) {
            case 0x07: // Fx07 - LD Vx, DT. Set Vx = delay timer value.
                vRegisters[Vx] = (byte) (processor.getDT() & 0xFF);
                break;
            case 0x0A: // Fx0A - LD Vx, K. Wait for a key press, store the value of the key in Vx.
                Keypad keypad = processor.getKeypad();
                for (int key : Keypad.keys) {
                    if (keypad.isKeyDown(key)) {
                        vRegisters[Vx] = (byte) key;
                        break;
                    }
                }
                // we "wait" by decrementing PC so this instruction will be executed until something is pressed
                int currentPc = processor.getProgramCounter();
                processor.setProgramCounter(currentPc - 2);
                break;
            case 0x15: // Fx15 - LD DT, Vx. Set delay timer = Vx.
                processor.setDT(vRegisters[Vx]);
                break;
            case 0x18: // Fx18 - LD ST, Vx․ Set sound timer = Vx
                processor.setST(vRegisters[Vx]);
                break;
            case 0x1E: // Fx1E - ADD I, Vx․ Set I = I + Vx.
                processor.setIndexRegister(indexRegister + vRegisters[Vx]);
                break;
            case 0x29: // Fx29 - LD F, Vx․ Set I = location of sprite for digit Vx.
                byte digit = vRegisters[Vx];
                processor.setIndexRegister(Processor.FONT_LOAD_START_ADDRESS + (digit * Processor.FONT_SIZE_BYTES));
                break;
            case 0x33: // Fx33 - LD B, Vx. Store BCD representation of Vx in memory locations I, I+1, and I+2.
                int value = vRegisters[Vx] & 0xFF;

                processor.writeMemory(indexRegister + 2, value % 10);
                value /= 10;

                processor.writeMemory(indexRegister + 1, value % 10);
                value /= 10;

                processor.writeMemory(indexRegister, value % 10);
                break;
            case 0x55:
                // Fx55 - LD [I], Vx. Store registers V0 through Vx in memory starting at location I.
                for (int i = 0; i <= Vx; i++) {
                    processor.writeMemory(indexRegister + i, vRegisters[i]);
                }
                break;
            case 0x65:
                // Fx65 - LD Vx, [I]. Read registers V0 through Vx from memory starting at location I.
                for (int i = 0; i <= Vx; i++) {
                    vRegisters[i] = processor.readMemory(indexRegister + i);
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation not found: " + Integer.toHexString(opcode));
        }
    })
    ;

    private final int address;
    private final InstructionExecutor executor;

    Instruction(int address, InstructionExecutor executor) {
        this.address = address;
        this.executor = executor;
    }

    public static final IntMap<Instruction> codeMap = new IntMap<>();

    static {
        for (Instruction value : Instruction.values()) {
            codeMap.put(value.address, value);
        }
    }

    public static Instruction fromOpcode (int opcode) {
        int op = Decoder.op(opcode);
        Instruction instruction = codeMap.get(op);

        if (instruction == null) {
            throw new UnsupportedOperationException("Unknown opcode " + opcode);
        }

        return instruction;
    }

    public void execute (Processor processor, int opcode) {
        executor.execute(processor, opcode);
    }
}
