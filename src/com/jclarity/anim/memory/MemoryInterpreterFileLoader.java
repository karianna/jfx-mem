package com.jclarity.anim.memory;

import com.jclarity.anim.memory.model.MemoryInstruction;
import com.jclarity.anim.memory.model.OpCode;
import static com.jclarity.anim.memory.model.OpCode.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An interpreter which loads a run from a file
 *
 * @author jpgough
 */
public class MemoryInterpreterFileLoader implements IMemoryInterpreter {

    private final String filePath;
    private List<String> lines = null;
    private Iterator<String> it = null;

    public MemoryInterpreterFileLoader(String filePath_) {
        filePath = filePath_;
    }

    /**
     * Alternate constructor - used for testing etc
     * @param lines_ 
     */
    public MemoryInterpreterFileLoader(List<String> lines_) {
        filePath = "";
        lines = lines_;
    }

    @Override
    public MemoryInstruction getNextStep() {
        if (lines == null) {
            loadLines();
        }
        if (it == null) {
            it = lines.iterator();
        }

        // Process next line in the file
        if (!it.hasNext()) return new MemoryInstruction(EOF);

        return parseLine(it.next());
    }

    private void loadLines() {
        try {
            lines = Files.readAllLines(Paths.get(filePath), Charset.defaultCharset());
        } catch (IOException ex) {
            throw new RuntimeException("File: " + filePath + " not found", ex);
        }
    }

    /**
     * Used to model tokens from the stream, and patterns which represent them
     */
    private static enum Token {
        NOP("NOP\\s*(\\d+)?", OpCode.NOP),
        ALLOC("ALLOC", OpCode.ALLOC),
        LARGE_ALLOC("TENLOC", OpCode.LARGE_ALLOC),
        KILL("KILL\\s+(\\d+)", OpCode.KILL);
        private final String toMatch;
        private final Pattern p;
        private final OpCode op;

        private Token(String toMatch_, OpCode op_) {
            toMatch = toMatch_;
            p = Pattern.compile(toMatch);
            op = op_;
        }

        Pattern getP() {
            return p;
        }

        OpCode getOpCode() {
            return op;
        }
    }

    private MemoryInstruction parseLine(String line) {
        for (Token t : Token.values()) {
            Matcher m = t.getP().matcher(line);
            if (m.find()) {
                if (m.groupCount() == 0) {
                    return new MemoryInstruction(t.getOpCode());
                }
                String argStr = m.group(1);
                int arg = Integer.parseInt(argStr);
                return new MemoryInstruction(t.getOpCode(), arg);
            }
        }
        return new MemoryInstruction(EOF);
    }
}
