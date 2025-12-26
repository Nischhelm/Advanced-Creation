package com.deadtiger.advcreation.logging;

import com.deadtiger.advcreation.handler.ServerConfigurationHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

@SideOnly(Side.SERVER)
public class LoggingServer
{

    public static boolean loggingActive = false;
    public static File mod_logs_dir;


    public static LocalDateTime time;
    public static HashMap<String,Boolean> initialisedLogging = new HashMap<>();

    public static HashMap<String,Long> prevLogTime = new HashMap<>();
    public static HashMap<String,Integer> playerInactiveCount = new HashMap<>();
    public static boolean LogTimeAndPlayerInactiveCountLock = false;

    public static HashMap<String,String> filename = new HashMap<>();
    public static HashMap<String,File> mod_logs_player_dir = new HashMap<>();
    public static boolean filenameAndPlayerDirLock = false;

    public static int lineCount = 0;
    public static int fileCount = 1;

    public LoggingServer()
    {
        time = LocalDateTime.now(ZoneId.systemDefault());

        mod_logs_dir = new File(".","mod_logs");

    }

    public static boolean createLogfile(LocalDateTime time,String playerName)
    {
        while(!attemptLockingFilenameAndPlayerDirLock()){}
        mod_logs_player_dir.put(playerName,new File(mod_logs_dir,"logs_" + playerName));


        if (!mod_logs_dir.exists())
        {
            if (!mod_logs_dir.mkdirs())
            {
                return false;
            }
        }
        if (!mod_logs_player_dir.get(playerName).exists())
        {
            if (!mod_logs_player_dir.get(playerName).mkdirs())
            {
                return false;
            }
        }

        if (mod_logs_dir.isDirectory())
        {
            if (!mod_logs_player_dir.get(playerName).isDirectory())
            {
                return false;
            }
        }
        filename.put(playerName, fileCount + "_" + time.toString().replace('.','-').replace(':','-')  + ".txt");
        File file2 = new File(mod_logs_player_dir.get(playerName), filename.get(playerName));
        releaseLockFilenameAndPlayerDirLock();

        OutputStreamWriter outputstream = null;
        boolean flag2;

        try
        {
            outputstream = new FileWriter(file2);
            PrintWriter p = new PrintWriter( outputstream );

            p.println("Started logging at  " + time);
            p.println("SystemTime(ms); MainModeId; ToolId; ButtonId; ToolStageId; GuiOverlayClick;Comment");
            flag2 = true;
        }
        catch (Throwable var13)
        {
            System.out.println(var13);
            flag2 = false;

        }
        finally
        {
            IOUtils.closeQuietly(outputstream);

        }
        if(!flag2)
            return false;

        return true;

    }

    public static void initialiseLogging(String playerName)
    {
        if(!initialisedLogging.containsKey(playerName))
            initialisedLogging.put(playerName,false);
        if(!initialisedLogging.get(playerName))
        {
            loggingActive = createLogfile(time,playerName);
            if(loggingActive)
                System.out.println("start logging at time time " + time );
            else
                System.out.println("UNABLE TO start logging at time time " + time );
            while (!attemptLockingLogTimeAndPlayerInactiveCount()){}
            initialisedLogging.put(playerName,true);
            prevLogTime.put(playerName,System.currentTimeMillis());
            playerInactiveCount.put(playerName,0);

            releaseLockLogTimeAndPlayerInactiveCount();
        }

    }

    public static void tickLogging()
    {
        if(loggingActive)
        {
            long time = System.currentTimeMillis();
            ArrayList<String> logPlayers = new ArrayList<>();
            while(!attemptLockingLogTimeAndPlayerInactiveCount()){}

            for (String playerName : prevLogTime.keySet())
            {
                if((time - prevLogTime.get(playerName) > 1000))
                {
                    logPlayers.add(playerName);
//                    LoggingServer.logMouseClick(-1,1,-1,-1,false,"",playerName);

                    prevLogTime.put(playerName, time);
                    int count = playerInactiveCount.get(playerName);
                    playerInactiveCount.put(playerName,count+1 );
                }
                if(playerInactiveCount.containsKey(playerName) && playerInactiveCount.get(playerName) >1000)
                {
                    System.out.println("removed from logging player " + playerName + " because he was inactive for 100 sec");
                    prevLogTime.remove(playerName);
                    playerInactiveCount.remove(playerName);
                }
            }
            releaseLockLogTimeAndPlayerInactiveCount();

            for(String playerName: logPlayers)
            {
                LoggingServer.logMouseClick(-1,1,-1,-1,false,"",playerName);

            }
        }
    }

    public static boolean logMouseClick(int mainMode, int toolname, int mouseButton, int toolStage,boolean guiOverlayClick,String comment,String playerName)
    {
        initialiseLogging(playerName);
        if(loggingActive) {
            if(lineCount > 10000)
            {
                time = LocalDateTime.now(ZoneId.systemDefault());
                fileCount++;
                lineCount = 0;
                createLogfile(time,playerName);
            }
            while(!attemptLockingFilenameAndPlayerDirLock()){}
            File file2 = new File(mod_logs_player_dir.get(playerName), filename.get(playerName));
            releaseLockFilenameAndPlayerDirLock();
            OutputStream outputstream = null;
            boolean flag2;

            int guiOverlayClickIndex = 0;
            if(guiOverlayClick)
                guiOverlayClickIndex = 1;

            if(file2.exists())
            {

                String log = "logging Failed";
                FileWriter fw = null;
                try
                {
                    while(!attemptLockingLogTimeAndPlayerInactiveCount()){}

                    String filename= file2.getCanonicalPath();
//                    System.out.println("write to " + filename);
                    fw = new FileWriter(filename,true); //the true will append the new data
                    prevLogTime.put(playerName, System.currentTimeMillis());
                    log = prevLogTime.get(playerName) +"; "+ mainMode +"; "+ toolname + "; " + mouseButton + "; " + toolStage + "; " + guiOverlayClickIndex + "; " + playerName + " " + comment;
                    if(ServerConfigurationHandler.PRINT_ACTIONS && mouseButton >= 0)
                    {
                        playerInactiveCount.put(playerName,0);
                        System.out.println(log);
                    }

                    releaseLockLogTimeAndPlayerInactiveCount();

                    fw.write(log + "\n");//appends the string to the file

                }
                catch(IOException ioe)
                {
                    System.err.println("IOException: " + ioe.getMessage());
                }
                finally
                {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                lineCount++;



                return true;
            }
        }
        return false;
    }

    public static boolean attemptLockingLogTimeAndPlayerInactiveCount()
    {
        if(!LogTimeAndPlayerInactiveCountLock)
        {
            LogTimeAndPlayerInactiveCountLock = true;
            return true;
        }
        return false;
    }

    public static void releaseLockLogTimeAndPlayerInactiveCount()
    {
        LogTimeAndPlayerInactiveCountLock = false;
    }

    public static boolean attemptLockingFilenameAndPlayerDirLock()
    {
        if(!filenameAndPlayerDirLock)
        {
            filenameAndPlayerDirLock = true;
            return true;
        }
        return false;
    }

    public static void releaseLockFilenameAndPlayerDirLock()
    {
        filenameAndPlayerDirLock = false;
    }


}
