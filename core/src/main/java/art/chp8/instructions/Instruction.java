package art.chp8.instructions;

public enum Instruction {
    // Display and flow control
    CLS(0x00E0),           // Clear the display
    RET(0x00EE),           // Return from subroutine
    JMP_ADDR(0x1000),      // Jump to address NNN
    CALL_ADDR(0x2000),     // Call subroutine at NNN

    // Conditional branches
    SE_VX_BYTE(0x3000),    // Skip next instruction if VX == NN
    SNE_VX_BYTE(0x4000),   // Skip next instruction if VX != NN
    SE_VX_VY(0x5000),      // Skip next instruction if VX == VY

    // Constant manipulation
    LD_VX_BYTE(0x6000),    // Load NN into VX
    ADD_VX_BYTE(0x7000),   // Add NN to VX (carry flag is not changed)

    // Bitwise operations
    LD_VX_VY(0x8000),      // Load the value of VY into VX
    OR_VX_VY(0x8001),      // Set VX to VX | VY (bitwise OR)
    AND_VX_VY(0x8002),     // Set VX to VX & VY (bitwise AND)
    XOR_VX_VY(0x8003),     // Set VX to VX ^ VY (bitwise XOR)
    ADD_VX_VY(0x8004),     // Add VY to VX, set VF = carry
    SUB_VX_VY(0x8005),     // Subtract VY from VX, set VF = NOT borrow
    SHR_VX(0x8006),        // Shift VX right by 1, VF = least significant bit of VX
    SUBN_VX_VY(0x8007),    // Set VX = VY - VX, set VF = NOT borrow
    SHL_VX(0x800E),        // Shift VX left by 1, VF = most significant bit of VX

    // Conditional skips
    SNE_VX_VY(0x9000),     // Skip next instruction if VX != VY

    // Address manipulation
    LD_I_ADDR(0xA000),     // Set I = NNN
    JP_V0_ADDR(0xB000),    // Jump to address NNN + V0

    // Random
    RND_VX_BYTE(0xC000),   // Set VX = random byte AND NN

    // Drawing
    DRW_VX_VY_NIBBLE(0xD000), // Display N-byte sprite starting at memory location I at (VX, VY), set VF = collision

    // Key operations
    SKP_VX(0xE09E),        // Skip next instruction if key with the value of VX is pressed
    SKNP_VX(0xE0A1),       // Skip next instruction if key with the value of VX is not pressed

    // Timers and sound
    LD_VX_DT(0xF007),      // Set VX = delay timer value
    LD_VX_KEY(0xF00A),     // Wait for a key press, store the value of the key in VX
    LD_DT_VX(0xF015),      // Set delay timer = VX
    LD_ST_VX(0xF018),      // Set sound timer = VX

    // Memory
    ADD_I_VX(0xF01E),      // Set I = I + VX
    LD_F_VX(0xF029),       // Set I = location of sprite for digit VX
    LD_B_VX(0xF033),       // Store BCD representation of VX in memory locations I, I+1, and I+2
    LD_I_VX(0xF055),       // Store registers V0 through VX in memory starting at location I
    LD_VX_I(0xF065);       // Read registers V0 through VX from memory starting at location I

    private final int code;

    Instruction(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
