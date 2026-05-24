/* 
    Auteur: Yanis Achab, Ryane Menaï
*/

import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SaveDataManager
{
    private static SaveDataManager sInstance = null;

    private static String sSaveDataPath = "assets/Save/SaveData.bin";
    private static int sSaveDataSize = 4 + GameManager.getNumLevels() * 4 + 4 + 4;

    public int pUnlockedLevelId;
    public int pSavedLevelId;
    public float pSavedLevelCompletionTime;
    public float[] pCompletionTimes;
    private RandomAccessFile mSaveFile;
    private FileOutputStream mSaveFileWriter;
    private FileInputStream mSaveFileReader;

    public static SaveDataManager getInstance()
    {
        return sInstance;
    }

    public static void createInstance()
    {
        if (sInstance == null)
        {
            sInstance = new SaveDataManager();
        }
        else
        {
            System.out.println("[SaveDataManager] Impossible de créer SaveDataManager car l'instance n'est pas null.");
        }
    }

    public static void destroyInstance()
    {
        if (sInstance == null)
        {
            System.out.println("[SaveDataManager] Impossible de détruire SaveDataManager il n'y a pas d'instance.");
        }
        else
        {
            sInstance.close();
            sInstance = null;
        }
    }

    private SaveDataManager()
    {
        pCompletionTimes = new float[GameManager.getNumLevels()];

        try
        {
            new java.io.File("assets/Save").mkdirs(); //crée le dossier automatiquement dans le constructeur
            mSaveFile = new RandomAccessFile(sSaveDataPath, "rw");
            mSaveFileWriter = new FileOutputStream(mSaveFile.getFD());
            mSaveFileReader = new FileInputStream(mSaveFile.getFD());
        }
        catch (IOException fileException)
        {
            System.out.println("[SaveDataManager] Error lors de l'ouverture du fichier: " + fileException.getMessage());
            return;
        }

        sInstance = this;
        this.load();
    }

    private void close()
    {
        this.save();

        try
        {
            mSaveFileWriter.close();
            mSaveFileReader.close();
        }
        catch (IOException closeException)
        {
            System.out.println("[SaveDataManager] Error lors de la fermeture du fichier: " + closeException.getMessage());
        }
    }

    public boolean reset()
    {
        pUnlockedLevelId = 0;
        pSavedLevelId = -2;
        pSavedLevelCompletionTime = Float.MAX_VALUE;

        for (int  i = 0; i < pCompletionTimes.length; i++)
        {
            pCompletionTimes[i] = Float.MAX_VALUE;
        }

        return this.save();
    }

    public boolean load()
    {
        if (mSaveFile == null) return false;
        try
        {
            mSaveFile.seek(0);

            byte[] rawFileBytes = mSaveFileReader.readAllBytes();
            ByteBuffer fileBytes = ByteBuffer.wrap(rawFileBytes);
            fileBytes.order(ByteOrder.LITTLE_ENDIAN);

            if (rawFileBytes.length != sSaveDataSize)
            {
                if (rawFileBytes.length == 0)
                {
                    return this.reset();
                }
                else
                {
                    throw new IllegalStateException("Le fichier ne possède pas une bonne forme.");
                }
            }

            pUnlockedLevelId = fileBytes.getInt(0);

            for (int i = 0; i < pCompletionTimes.length; i++)
            {
                pCompletionTimes[i] = fileBytes.getFloat(4 + i * 4);
            }

            pSavedLevelCompletionTime = fileBytes.getFloat(4 + pCompletionTimes.length * 4);
            pSavedLevelId = fileBytes.getInt(8 + pCompletionTimes.length * 4);
        }
        catch (Exception loadException)
        {
            System.out.println("[SaveDataManager] Erreur lors de la lecture du fichier: " + loadException.getMessage());
            return false;
        }

        return true;
    }

    public boolean save()
    {
        if (mSaveFile == null) return false;
        try
        {
            mSaveFile.seek(0);

            ByteBuffer saveDataBytes = ByteBuffer.allocate(sSaveDataSize);
            saveDataBytes.order(ByteOrder.LITTLE_ENDIAN);

            saveDataBytes.putInt(0, pUnlockedLevelId);
            for (int i = 0; i < pCompletionTimes.length; i++)
            {
                saveDataBytes.putFloat(i * 4 + 4, pCompletionTimes[i]);
            }
            saveDataBytes.putFloat(pCompletionTimes.length * 4 + 4, pSavedLevelCompletionTime);
            saveDataBytes.putInt(pCompletionTimes.length * 4 + 8, pSavedLevelId);

            mSaveFileWriter.write(saveDataBytes.array());
            mSaveFileWriter.flush();
        }
        catch (IOException writeException)
        {
            System.out.println("[SaveDataManager] Erreur lors de l'écriture du fichier: " + writeException.getMessage());
            return false;
        }

        return true;
    }
}
