package ca.rightsomegoodgames.mayacharm.mayacomms;

import ca.rightsomegoodgames.mayacharm.resources.MayaCharmProperties;
import ca.rightsomegoodgames.mayacharm.resources.MayaNotifications;
import ca.rightsomegoodgames.mayacharm.resources.PythonStrings;
import com.intellij.openapi.application.PathManager;
import com.intellij.notification.Notifications;
import org.apache.sanselan.util.IOUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class MayaCommInterface {
    public static final String LOG_FILENAME_STRING = "/mayalog%s.txt";

    private boolean sendSuccess = false;
    final private String host;
    final private int port;
    final private String logFilename;

    public MayaCommInterface(String host, int port) {
        this.host = host;
        this.port = port;
        this.logFilename = String.format(LOG_FILENAME_STRING, this.port);
    }

    private File writeFile(String text) {
        File tempFile = null;
        BufferedWriter bw;

        try {
            tempFile = File.createTempFile("MayaCharmTemp", ".py");
            if (!tempFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                tempFile.createNewFile();
            }

            bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(text);
            bw.close();
            tempFile.deleteOnExit();
        }
        catch (IOException e) {
            Notifications.Bus.notify(MayaNotifications.FILE_FAIL);
            e.printStackTrace();
        }
        return tempFile;
    }

    private void sendToPort(File message) {
        Socket client = null;
        PrintWriter out = null;

        try {
            client = new Socket(host, port);
            out = new PrintWriter(client.getOutputStream(), true);

            String outString = MessageFormat.format(
                    PythonStrings.EXECFILE, message.toString().replace("\\", "/"));
            out.println(outString);
            sendSuccess = true;
        }
        catch (IOException e) {
            Notifications.Bus.notify(MayaNotifications.CONNECTION_REFUSED);

            // Tack on host and port information
            String extendedMessage = String.format("(%s:%s) %s", host, port, e.getLocalizedMessage());
            try {
                IOException eExtended = e.getClass().getConstructor(String.class).newInstance(extendedMessage);
                eExtended.setStackTrace(e.getStackTrace());
                eExtended.printStackTrace();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e1) {
                new IOException(extendedMessage, e).printStackTrace();
            }
            sendSuccess = false;
        }
        finally {
            if (out != null)
                out.close();
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendCodeToMaya(String message) {
        File file = writeFile(message);
        sendToPort(file);
    }

    public void sendFileToMaya(String path) {
        File file = new File(path);
        sendToPort(file);
    }

    public void pyDevSetup() {
        Path installDir = Paths.get(PathManager.getBinPath()).getParent();
        File debugEggPath = new File(installDir.toString(), "debug-eggs" + File.separator + "pycharm-debug.egg");
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream resourceAsStream = classLoader.getResourceAsStream("python/pydev_setup.py");
        String lines = "";
        try {
            byte[] bytes = IOUtils.getInputStreamBytes(resourceAsStream);
            lines = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lines = String.format(lines, debugEggPath);
        sendCodeToMaya(lines);
    }

    public void pyDevSetup2() {
        sendCodeToMaya(String.format(
                MayaCharmProperties.getString("pydevdscript.syspath"),
                PythonStrings.PYDEVD_FOLDER
        ));
    }

    public void setTrace(int port, boolean suspend, boolean print) {
        sendCodeToMaya(String.format(PythonStrings.SETTRACE, port, suspend ? "True" : "False", print ? "True" : "False"));
    }

    public void stopTrace() {
        sendCodeToMaya(PythonStrings.STOPTRACE);
    }

    public void connectMayaLog() {
        final String mayaLogPath = PathManager.getPluginTempPath() + logFilename;
        String message = PythonStrings.CLOSE_LOG;
        message += System.lineSeparator() + MessageFormat.format(PythonStrings.OPEN_LOG, mayaLogPath);

        try {
            createMayaLog(mayaLogPath);
            sendCodeToMaya(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createMayaLog(String path) throws IOException {
        final File mayaLog = new File(path);
        if (!mayaLog.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mayaLog.createNewFile();
        }
        return mayaLog;
    }

    public boolean isSendSuccess() {
        return sendSuccess;
    }
}
