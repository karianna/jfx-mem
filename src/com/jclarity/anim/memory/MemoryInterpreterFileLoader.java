package com.jclarity.anim.memory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static com.jclarity.anim.memory.OpCode.*;

/**
 *
 * @author jpgough
 */
public class MemoryInterpreterFileLoader implements IMemoryInterpreter {

    private final String filePath;
    private List<String> lines = null;
    private Iterator<String> it = null;
    
    private enum Command {
        
    }
    
    private final static String nopStr = "NOP\\s+\\d+";
    private final static String allocStr = "ALLOC";
    private final static String largeAllocStr = "LARGE_ALLOC";

    
    public MemoryInterpreterFileLoader(String filePath) {
        //Potentially load the file here rather than holding the string
        this.filePath = filePath;
    }

    @Override
    public MemoryInstruction getNextStep() {
        if (lines == null) {
            loadLines();
        }

        // Process next line in the file
        String line = it.next();
        if (line == null) {
            return new MemoryInstruction(EOF);
        }

        return parseLine(line);
    }

    private void loadLines() {
        try {
            lines = Files.readAllLines(Paths.get(filePath), Charset.defaultCharset());
            it = lines.iterator();
        } catch (IOException ex) {
            throw new RuntimeException("File: " + filePath + " not found");
        }
    }

    private MemoryInstruction parseLine(String line) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
