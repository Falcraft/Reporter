/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import net.KabOOm356.File.RevisionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileIO {
    public static final String encoding = "UTF-8";
    public static final String BACKUP_FILE_EXTENSION = ".backup";
    private static final Logger log = LogManager.getLogger(FileIO.class);

    private FileIO() {
    }

    public static void copyTextFile(File in, File out) throws IOException {
        FileIO.copyTextFile(in, out, encoding, encoding);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyTextFile(File in, File out, String inEncoding, String outEncoding) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("Input file cannot be null!");
        }
        if (out == null) {
            throw new IllegalArgumentException("Output file cannot be null!");
        }
        if (!out.exists()) {
            out.createNewFile();
        }
        if (!in.exists()) {
            in.createNewFile();
        }
        BufferedReader input = null;
        BufferedWriter output = null;
        try {
            String line;
            input = new BufferedReader(new InputStreamReader(in.toURI().toURL().openStream(), inEncoding));
            output = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(out), outEncoding));
            while ((line = input.readLine()) != null) {
                output.write(line);
                output.newLine();
            }
            output.flush();
        }
        finally {
            block20 : {
                block19 : {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    }
                    catch (IOException e) {
                        if (!log.isDebugEnabled()) break block19;
                        log.warn("Closing input failed!", (Throwable)e);
                    }
                }
                try {
                    if (output != null) {
                        output.close();
                    }
                }
                catch (IOException e) {
                    if (!log.isDebugEnabled()) break block20;
                    log.warn("Closing output failed!", (Throwable)e);
                }
            }
        }
    }

    public static RevisionFile createBackup(File file) {
        RevisionFile backup = null;
        try {
            backup = new RevisionFile(file.getParent(), file.getName() + BACKUP_FILE_EXTENSION);
            backup.incrementToNextRevision();
            backup.createNewFile();
            FileIO.copyTextFile(file, backup.getFile());
        }
        catch (Exception e) {
            log.error("Failed to create backup file for " + file.getName(), (Throwable)e);
            if (backup != null) {
                backup.delete();
            }
            return null;
        }
        return backup;
    }
}

