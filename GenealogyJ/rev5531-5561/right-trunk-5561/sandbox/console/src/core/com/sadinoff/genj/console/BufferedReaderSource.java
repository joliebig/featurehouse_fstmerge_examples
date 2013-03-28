package com.sadinoff.genj.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

public class BufferedReaderSource extends BufferedReader implements LineSource {
    String prompt;
    final PrintWriter out;
    public BufferedReaderSource(Reader reader, PrintWriter out) {
        super(reader);
        this.out = out;
    }
    
    public String readLine() throws IOException
    {
        out.print(prompt);
        out.flush();
        return super.readLine();
    }
    public void setPrompt(String str)
    {
        this.prompt = str;
    }

}
