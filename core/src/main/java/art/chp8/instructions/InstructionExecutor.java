package art.chp8.instructions;

import art.chp8.Processor;

public interface InstructionExecutor {
    void execute (Processor processor, int opcode);
}
