package ca.rightsomegoodgames.mayacharm.attach;

import ca.rightsomegoodgames.mayacharm.mayacomms.MayaCommInterface;
import ca.rightsomegoodgames.mayacharm.resources.MayaCharmProperties;
import ca.rightsomegoodgames.mayacharm.resources.PythonStrings;
import ca.rightsomegoodgames.mayacharm.settings.MCSettingsProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.jetbrains.python.debugger.PyLocalPositionConverter;
import com.jetbrains.python.debugger.PyRemoteDebugProcess;
import com.jetbrains.python.debugger.attach.PyAttachToProcessCommandLineState;
import com.jetbrains.python.debugger.attach.PyAttachToProcessDebugRunner;
import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.net.ServerSocket;

public class MayaAttachToProcessDebugRunner extends PyAttachToProcessDebugRunner {
    private Project myProject;
    private final int myPid;
    private String mySdkPath;

    public MayaAttachToProcessDebugRunner(@NotNull Project project, int pid, String sdkPath) {
        super(project, pid, sdkPath);
        myProject = project;
        myPid = pid;
        mySdkPath = sdkPath;
    }

    public XDebugSession launch() throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        return launchRemoteDebugServer();
    }

    private XDebugSession launchRemoteDebugServer() throws ExecutionException {
        final ServerSocket serverSocket = MCSettingsProvider.getInstance(myProject).getAttachSocket();
        PyAttachToProcessCommandLineState state = PyAttachToProcessCommandLineState.create(myProject, mySdkPath, serverSocket.getLocalPort(), myPid);
        final ExecutionResult result = state.execute(state.getEnvironment().getExecutor(), this);

        //start remote debug server
        return XDebuggerManager.getInstance(myProject).
                startSessionAndShowTab(String.valueOf(myPid), null, new XDebugProcessStarter() {
                    @org.jetbrains.annotations.NotNull
                    public XDebugProcess start(@NotNull final XDebugSession session) {
                    String attachLocalScript = String.format(
                            MayaCharmProperties.getString("attachlocal.script"),
                            PythonStrings.PYDEVD_FOLDER, serverSocket.getLocalPort()
                    );
                    PyRemoteDebugProcess pyDebugProcess =
                        new PyRemoteDebugProcess(session, serverSocket, result.getExecutionConsole(),
                                result.getProcessHandler(), "\n"+attachLocalScript+"\n\n") {
                            private boolean autoAttached = false;

                            @Override
                            protected void printConsoleInfo() {
                                if (!autoAttached) super.printConsoleInfo();
                            }

                            @Override
                            protected String getConnectionMessage() {
                                return "Attaching to a Maya(py) process with PID = " + myPid;
                            }

                            @Override
                            protected String getConnectionTitle() {
                                return "Attaching Debugger";
                            }

                            @Override
                            protected void afterConnect() {
                                super.afterConnect();

                                MCSettingsProvider settings = MCSettingsProvider.getInstance(getProject());
                                MayaCommInterface connection = new MayaCommInterface(settings.getHost(), settings.getPort());

                                try {
                                    connection.sendCodeToMaya(attachLocalScript);
                                    autoAttached = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                    pyDebugProcess.setPositionConverter(new PyLocalPositionConverter());


                    createConsoleCommunicationAndSetupActions(myProject, result, pyDebugProcess, session);

                    return pyDebugProcess;
                    }
                });
    }
}
