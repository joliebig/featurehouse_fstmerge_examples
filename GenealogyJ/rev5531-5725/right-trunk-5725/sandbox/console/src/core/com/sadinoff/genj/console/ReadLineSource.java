package com.sadinoff.genj.console;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.gnu.readline.Readline;
import org.gnu.readline.ReadlineLibrary;

public class ReadLineSource extends Readline implements LineSource {
    String prompt = ">";
    
    public ReadLineSource()
    {
        String appName = "GenJ-Console";
        
        
        try {
            Readline.load(ReadlineLibrary.GnuReadline);
        }
        catch (UnsatisfiedLinkError ignore_me) {
            try
            {
                System.err.println("couldn't load GnuReadline lib.  Trying Pure-Java...");
                Readline.load(ReadlineLibrary.PureJava);
            }
            catch( UnsatisfiedLinkError ignore_me2)
            {
                System.err.println("couldn't load readline lib. Using simple stdin.");
            }
        }


        System.out.println("initializing Readline...");
        Readline.initReadline(appName); 
        System.out.println("... done");

        

        File history = new File(System.getProperty("user.home"),".rltest_history");

        try {
            if (history.exists())
                Readline.readHistoryFile(history.getPath());
        } catch (Exception e) {
            System.err.println("Error reading history file!");
        }
          
        

        Readline.parseAndBind("\"\\e[18~\": \"Function key F7\"");
        Readline.parseAndBind("\"\\e[19~\": \"Function key F8\"");

        
        try {
            Readline.setWordBreakCharacters(" \t;");
        }
        catch (UnsupportedEncodingException enc) {
            System.err.println("Could not set word break characters");
            System.exit(0);
        }
        System.out.println("encoding is "+getEncoding());
        



        
                
    }
    
    public String readLine() throws IOException {
        return Readline.readline(prompt);
    }

    public void setPrompt(String prompt) {
       this.prompt =prompt; 
    }
 
}
