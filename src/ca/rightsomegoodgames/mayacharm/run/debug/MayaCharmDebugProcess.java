package ca.rightsomegoodgames.mayacharm.run.debug;

import ca.rightsomegoodgames.mayacharm.mayacomms.MayaCommInterface;
import ca.rightsomegoodgames.mayacharm.settings.MCSettingsProvider;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyRemoteDebugProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ServerSocket;

public class MayaCharmDebugProcess extends PyRemoteDebugProcess {
    final private ServerSocket socket;
    final private MayaCharmDebugConfig config;
    final private MCSettingsProvider settings;
    final protected MayaCommInterface mayaCommInterface;
    private boolean canConnect;

    public MayaCharmDebugProcess(@NotNull XDebugSession xDebugSession, @NotNull ServerSocket serverSocket, @NotNull ExecutionConsole executionConsole, @Nullable ProcessHandler processHandler, @Nullable String s) {
        super(xDebugSession, serverSocket, executionConsole, processHandler, s);
        socket = serverSocket;
        config = (MayaCharmDebugConfig) xDebugSession.getRunProfile();
        settings = MCSettingsProvider.getInstance(getProject());
        mayaCommInterface = new MayaCommInterface(settings.getHost(), settings.getPort());
    }

    @Override
    public void sessionInitialized() {
        canConnect = true;
        super.sessionInitialized();
    }

    @Override
    protected void beforeConnect() {
        if (canConnect) {
            mayaCommInterface.pyDevSetup2();
            if (mayaCommInterface.isSendSuccess()) {
                if (config == null) {
                    mayaCommInterface.setTrace(socket.getLocalPort(), false, true);
                } else {
                    mayaCommInterface.setTrace(socket.getLocalPort(), config.isSuspendAfterConnect(), config.isRedirectOutput());
                }
            }
        }
        super.beforeConnect();
    }

    @Override
    protected void afterConnect() {
        canConnect = false;
        super.afterConnect();

        switch (config.getExecutionType()) {
            case CODE:
                mayaCommInterface.sendCodeToMaya(config.getScriptCodeText());
                break;
            case FILE:
                mayaCommInterface.sendFileToMaya(config.getScriptFilePath());
                break;
        }
    }

    @Override
    public void stop() {
        canConnect = false;
        mayaCommInterface.stopTrace();
        super.stop();
    }

    @Override
    protected void disconnect() {
        canConnect = false;
        super.disconnect();
    }
}
