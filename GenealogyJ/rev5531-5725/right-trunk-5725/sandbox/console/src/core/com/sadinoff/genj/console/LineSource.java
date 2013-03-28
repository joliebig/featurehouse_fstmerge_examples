package com.sadinoff.genj.console;

import java.io.IOException;

public interface LineSource 
{
    String readLine() throws IOException;
    void setPrompt(String prompt);
}
