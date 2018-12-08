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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.KabOOm356.File.AbstractFiles.NetworkFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class UrlIO {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    public static final Charset outputCharset = Charset.forName("UTF-8");
    public static final String ALPHA = "alpha";
    public static final String BETA = "beta";
    public static final String RELEASE_CANDIDATE = "rc";
    private static final Logger log = LogManager.getLogger(UrlIO.class);

    private UrlIO() {
    }

    public static int getResponse(URLConnection connection) throws IOException {
        return ((HttpURLConnection)connection).getResponseCode();
    }

    public static boolean isResponseValid(URLConnection connection) throws IOException {
        return UrlIO.getResponse(connection) == 200;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void downloadFile(NetworkFile abstractFile, File destination) throws IOException {
        HttpURLConnection connection;
        URL url;
        URLConnection dlUrlConnection;
        if (abstractFile == null && destination == null) {
            throw new IllegalArgumentException("Both the abstract file and destination file cannot be null!");
        }
        if (abstractFile == null) {
            throw new IllegalArgumentException("The abstract file cannot be null!");
        }
        if (destination == null) {
            throw new IllegalArgumentException("The destination file cannot be null!");
        }
        if (!destination.exists()) {
            destination.createNewFile();
        }
        if (!UrlIO.isResponseValid(connection = (HttpURLConnection)(dlUrlConnection = (url = new URL(abstractFile.getURL())).openConnection()))) {
            throw new IOException(String.format("Connection response [%d] is not valid!", connection.getResponseCode()));
        }
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), abstractFile.getEncoding()));
            out = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(destination), outputCharset.name()));
            while ((line = in.readLine()) != null) {
                out.write(line);
                out.newLine();
            }
            destination.setLastModified(new Date().getTime());
        }
        finally {
            block21 : {
                block20 : {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    }
                    catch (IOException e) {
                        if (!log.isDebugEnabled()) break block20;
                        log.warn("Failed to close output!", (Throwable)e);
                    }
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException e) {
                    if (!log.isDebugEnabled()) break block21;
                    log.warn("Failed to close input!", (Throwable)e);
                }
            }
            connection.disconnect();
        }
    }

    public static String getVersion(String fileName) {
        if (fileName.contains(" v")) {
            return fileName.substring(fileName.lastIndexOf(" v") + 2);
        }
        if (fileName.contains(" V")) {
            return fileName.substring(fileName.lastIndexOf(" V") + 2);
        }
        if (fileName.contains(" ")) {
            int lastIndex = fileName.lastIndexOf(32);
            String version = fileName.substring(lastIndex + 1);
            fileName = fileName.substring(0, lastIndex);
            if (version.equalsIgnoreCase(ALPHA) || version.equalsIgnoreCase(BETA) || version.equalsIgnoreCase(RELEASE_CANDIDATE)) {
                version = UrlIO.getVersion(fileName + '-' + version);
            }
            return version;
        }
        return "";
    }

    public static String getNodeValue(Element element, String tagName, String defaultValue) {
        return UrlIO.getNodeValue(element, tagName) != null ? UrlIO.getNodeValue(element, tagName) : defaultValue;
    }

    public static String getNodeValue(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getChildNodes().item(0).getNodeValue();
    }
}

