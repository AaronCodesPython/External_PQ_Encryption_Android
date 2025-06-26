package io.github.chiffre;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContextWrapper;

import io.github.chiffre.data.Contact;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TimerManager {
    long creationTime;
    public TimerManager(){
        this.creationTime = -1;
    }
    public void writeTime(ContextWrapper t){

        try (FileOutputStream fos = t.openFileOutput("timer.txt", MODE_PRIVATE)) {
            fos.write((""+(Instant.now().getEpochSecond()+604800)).getBytes());
        } catch (IOException e) {}
    }
    public long getCreationTime(ContextWrapper t){
            File file = new File(t.getFilesDir(), "timer.txt");
            if(file.exists()){
                try (FileInputStream fis = new FileInputStream(file);
                     InputStreamReader isr = new InputStreamReader(fis);
                     BufferedReader reader = new BufferedReader(isr)) {
                        String line = reader.readLine();
                        creationTime = Long.parseLong(line);
                        return creationTime;
                } catch (IOException e) {

                }
            }
            else{
                writeTime(t);
                return getCreationTime(t);

            }
        return Instant.now().getEpochSecond();

    }
    public  boolean needsRefresh(long goaltime, long Currenttime){
        if(Currenttime > goaltime){
            return true;
        }
        else{
            return false;
        }

    }
    public static String formatTimeDifference(long timestamp1, long timestamp2) {
        long diffSeconds = Math.abs(timestamp2 - timestamp1); // Ensure non-negative

        long hours = diffSeconds / 3600;
        long minutes = (diffSeconds % 3600) / 60;
        long seconds = diffSeconds % 60;

        return String.format("%03d:%02d:%02d", hours, minutes, seconds);
    }



}

