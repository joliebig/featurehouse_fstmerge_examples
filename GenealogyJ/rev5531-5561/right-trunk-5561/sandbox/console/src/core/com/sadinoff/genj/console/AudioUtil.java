package com.sadinoff.genj.console;






import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;



public class AudioUtil
{
    private static final int    EXTERNAL_BUFFER_SIZE = 128000;
    private static final  BlockingQueue<File> playerInput = new LinkedBlockingQueue<File>();

    private static PlayerThread player = null;
    
    static class PlayerThread implements Runnable
    {
        public PlayerThread()
        {
        }
        public void run() {
            while(true)
            {
                try {
                    File audioFile = playerInput.take();  
                    
                    
                  AudioInputStream    audioInputStream = null;
                      audioInputStream = AudioSystem.getAudioInputStream(audioFile);

                  
                  AudioFormat audioFormat = audioInputStream.getFormat();

                  
                  SourceDataLine  line = null;
                  DataLine.Info   info = new DataLine.Info(SourceDataLine.class,
                                                           audioFormat);
                      line = (SourceDataLine) AudioSystem.getLine(info);

                      
                      line.open(audioFormat);

                      
                  line.start();

                  
                  int nBytesRead = 0;
                  byte[]  abData = new byte[EXTERNAL_BUFFER_SIZE];
                  while (nBytesRead != -1)
                  {
                      try
                      {
                          nBytesRead = audioInputStream.read(abData, 0, abData.length);
                      }
                      catch (IOException e)
                      {
                          e.printStackTrace();
                      }
                      if (nBytesRead >= 0)
                      {
                          line.write(abData, 0, nBytesRead);
                      }
                  }

                  
                  line.drain();

                  
                  line.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void play(String audioFile) 
    {
            if( null == player)
            {
                player =  new PlayerThread();
                new Thread(player).start();
            }

            try {
                playerInput.put(new File(audioFile));
            } catch (InterruptedException e) {
                
                e.printStackTrace();
            }
    }

}







