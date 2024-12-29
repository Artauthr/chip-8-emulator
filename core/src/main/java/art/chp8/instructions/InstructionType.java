package art.chp8.instructions;

import art.chp8.InstructionDecoder;
import art.chp8.Processor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;

import java.util.Arrays;

public enum InstructionType {
    /*
    00E0 - CLS
    Clear the display.
    */
    CLS(0x00E0, (processor, opcode) -> {
        boolean[][] pixels = processor.getPixels();

        for (boolean[] row : pixels) {
            Arrays.fill(row, false);
        }
    }),

    /*
    00EE - RET
    Return from a subroutine.
    */
    RET(0x00EE, (processor, opcode) -> {
        int pop = processor.popStack();
        processor.setProgramCounter(pop);
    }),

    /*
    1nnn - JP addr
    Jump to location nnn.
    */
    JP(0x1000, (processor, opcode) -> {
        processor.setProgramCounter(InstructionDecoder.nnn(opcode));
    }),

    /*
    2nnn - CALL addr
    Call subroutine at nnn.
     */
    CALL(0x2000, (processor, opcode) -> {
        int currentPc = processor.getProgramCounter();
        processor.pushStack(currentPc);
        processor.setProgramCounter(InstructionDecoder.nnn(opcode));
    }),

    /*
    3xkk - SE Vx, byte
    Skip next instruction if Vx = kk.
    */
    SE_VALUE(0x3000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte currentRegisterValue = vRegisters[InstructionDecoder.Vx(opcode)];

        if (currentRegisterValue == InstructionDecoder.kk(opcode)) {
            processor.skipNextInstruction();
        }
    }),

    /*
    4xkk - SNE Vx, byte
    Skip next instruction if Vx != kk.
    */
    SNE_VALUE(0x4000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        byte currentRegisterValue = vRegisters[InstructionDecoder.Vx(opcode)];
        if (currentRegisterValue != InstructionDecoder.kk(opcode)) {
            processor.skipNextInstruction();
        }
    }),

    /*
    5xy0 - SE Vx, Vy
    Skip next instruction if Vx = Vy.
    */
    SE(0x5000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        byte vx = vRegisters[InstructionDecoder.Vx(opcode)];
        byte vy = vRegisters[InstructionDecoder.Vy(opcode)];

        if (vx == vy) processor.skipNextInstruction();
    }),

    /*
    6xkk - LD Vx, byte
    Set Vx = kk.
    */
    LD_VALUE(0x6000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        vRegisters[InstructionDecoder.Vx(opcode)] = (byte) InstructionDecoder.kk(opcode);
    }),

    /*
    7xkk - ADD Vx, byte
    Set Vx = Vx + kk.
     */
    ADD_VALUE(0x7000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte currentValue = vRegisters[InstructionDecoder.Vx(opcode)];
        vRegisters[InstructionDecoder.Vx(opcode)] = (byte) (currentValue + InstructionDecoder.kk(opcode));
    }),

    /*
    LD Vx, Vy
     */
    LD(0x8000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();

        int Vx = InstructionDecoder.Vx(opcode);
        int Vy = InstructionDecoder.Vy(opcode);
        int n = InstructionDecoder.n(opcode);

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
                throw new IllegalArgumentException("Unknown 8XYN operation: 0x" + Integer.toHexString(n));
        }
    }),

    SNE_VX_VY(0x9000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        int Vx = InstructionDecoder.Vx(opcode);
        int Vy = InstructionDecoder.Vy(opcode);

        if (vRegisters[Vx] == vRegisters[Vy]) processor.skipNextInstruction();
    }),

    /*
    ANNN - LD I, addr
    Set I = nnn.
    */
    ANNN(0xA000, (processor, opcode) -> {
        processor.setIndexRegister(InstructionDecoder.nnn(opcode));
    }),

    /*
    Bnnn - JP V0, addr
    Jump to location nnn + V0.
     */
    BNNN(0xB000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        processor.setProgramCounter(vRegisters[0x0] + InstructionDecoder.nnn(opcode));
    }),

    /*
    Cxkk - RND Vx, byte
    Set Vx = random byte AND kk.
     */
    CXKK(0xC000, (processor, opcode) -> {
        int random = MathUtils.random(0, 255);
        int kk = InstructionDecoder.kk(opcode);

        int Vx = InstructionDecoder.Vx(opcode);
        byte[] vRegisters = processor.getVRegisters();

        vRegisters[Vx] = (byte) (random & kk);
    }),

    /*
    Dxyn - DRW Vx, Vy, nibble
    Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    */
    DXYN(0xD000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte[] memory = processor.getMemory();

        boolean[][] pixels = processor.getPixels();
        int indexRegister = processor.getIndexRegister();

        // Get positions and wrap them
        int xStartPos = vRegisters[InstructionDecoder.Vx(opcode)] % Processor.SCREEN_WIDTH;
        int yStartPos = vRegisters[InstructionDecoder.Vy(opcode)] % Processor.SCREEN_HEIGHT;

        vRegisters[0xF] = 0; // Reset collision register, should turn to 1 if any collisions happen during drawing

        // Each sprite is 8 pixels wide and N pixels tall.
        for (int rowIndex = 0; rowIndex < InstructionDecoder.n(opcode); rowIndex++) {
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
    }),

    /*
    Ex - Keyboard
     */
    EX(0xE000, (processor, opcode) -> {
        byte[] vRegisters = processor.getVRegisters();
        byte key = vRegisters[InstructionDecoder.Vx(opcode)];

        int type = InstructionDecoder.kk(opcode);
        switch (type) {
            case 0x9E:
                if (processor.isKeyDown(key)) processor.skipNextInstruction();
                break;
            case 0xA1:
                if (!processor.isKeyDown(key)) processor.skipNextInstruction();
                break;
        }
    }),

    /*
    Fx
     */
    FX(0xF000, (processor, opcode) -> {
        int Vx = InstructionDecoder.Vx(opcode);
        byte[] vRegisters = processor.getVRegisters();

        int type = InstructionDecoder.kk(opcode);
        switch (type) {
            case 0x07:
                vRegisters[Vx] = processor.getDT();
                break;
            case 0x0A:
                if (processor.)
                break;
            case 0x15:
                processor.setDT(vRegisters[Vx]);
                break;
            case 0x18:
                processor.setST(vRegisters[Vx]);
                break;
            case 0x1E:
                int indexRegister = processor.getIndexRegister();
                processor.setIndexRegister(indexRegister + vRegisters[Vx]);
                break;
            case 0x29:
                pr
        }


    })





    ;

    private final int address;
    private final InstructionExecutor executor;

    InstructionType (int address, InstructionExecutor executor) {
        this.address = address;
        this.executor = executor;
    }

    public static final IntMap<InstructionType> codeMap = new IntMap<>();

    static {
        for (InstructionType value : InstructionType.values()) {
            codeMap.put(value.address, value);
        }
    }

    public static InstructionType fromOpcode (int opcode) {
        if (opcode == CLS.address) {
            return CLS;
        }
        if (opcode == RET.address) {
            return RET;
        }

        int op = InstructionDecoder.op(opcode);
        InstructionType instructionType = codeMap.get(op);

        if (instructionType == null) {
            throw new RuntimeException("Unknown opcode " + opcode);
        }

        return instructionType;
    }

    public void execute (Processor processor, int opcode) {
        executor.execute(processor, opcode);
    }
}
