/* 
    Auteur: Yanis Achab
*/

import java.io.*;

public class MoveLogger extends GameObject
{
    private static final String sFilePath = "assets/Save/MoveLogs.txt";
    private static MoveLogger sInstance = null;

    private BufferedWriter mWriter;

    public static MoveLogger getInstance()
    {
        return sInstance;
    }

    @Override public void onCreate()
    {
        if (sInstance != null)
        {
            this.destroy();
            return;
        }

        sInstance = this;
        mWriter = null;

        try
        {
            mWriter = new BufferedWriter(new FileWriter(sFilePath));
        }
        catch (IOException e)
        {
            System.err.println("[MoveLogger] Impossible d'ouvrir le fichier : " + e.getMessage());
        }
    }

    @Override public void onDestroy()
    {
        if (sInstance == this)
        {
            sInstance = null;
        }

        closeFile();
    }

    @Override protected void receiveMsg(GameObject from, int msg)
    {
        if (mWriter == null)
        {
            return;
        }

        String arrow;

        if (msg == Box.Direction.Up.ordinal())
        {
            arrow = "↥";
        }
        else if (msg == Box.Direction.Down.ordinal())
        {
            arrow ="↧";
        }
        else if (msg == Box.Direction.Left.ordinal())
        {
            arrow = "↤";
        }
        else if(msg == Box.Direction.Right.ordinal())
        {
            arrow ="↦";
        }
        else
        {
            return;
        }

        try
        {
            mWriter.write(arrow);
            mWriter.newLine();
            mWriter.flush();
        }
        catch (IOException e)
        {
            System.err.println("[MoveLogger] Erreur d'écriture : " + e.getMessage());
        }
    }

    private void closeFile()
    {
        if (mWriter != null)
        {
            try
            {
                mWriter.close();
            }
            catch(IOException e)
            {
                System.err.println("[MoveLogger] Erreur lors de la fermeture : " + e.getMessage());
            }

            mWriter = null;
        }
    }
}