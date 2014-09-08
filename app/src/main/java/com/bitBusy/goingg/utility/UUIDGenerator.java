/**
 * 
 */
package com.bitBusy.goingg.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;

/**
 * @author SumaHarsha
 *
 */
public class UUIDGenerator {
	public static final String FILEIOERROR = "File I/O error";
    private static final String INSTALLATION = "INSTALLATION";
	private static String myID = null;

    public synchronized static String id(Context context)
    {
        if (myID == null)
        {  
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try 
            {
                if (!installation.exists())
                    writeInstallationFile(installation);
                myID = readInstallationFile(installation);
            } catch (Exception e) 
            {
                Utility.throwErrorMessage(context, FILEIOERROR, e.getMessage());
            }
        }
        return myID;
      }

    private static String readInstallationFile(File installation) throws IOException 
    {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

}
