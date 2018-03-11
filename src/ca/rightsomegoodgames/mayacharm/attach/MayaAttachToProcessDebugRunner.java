package ca.rightsomegoodgames.mayacharm.attach;

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

import java.io.IOException;
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
                    PyRemoteDebugProcess pyDebugProcess =
                        new PyRemoteDebugProcess(session, serverSocket, result.getExecutionConsole(),
                                result.getProcessHandler(), "") {
                            @Override
                            protected void printConsoleInfo() {
                                this.printToConsole(String.format(
                                        "Starting debug server at port %2$d\nUse the following code to connect to the debugger:\n\n\n" +
                                        MayaCharmProperties.getString("attachlocal.script") + "\n\n\n",
                                        PythonStrings.PYDEVD_FOLDER, serverSocket.getLocalPort()), ConsoleViewContentType.SYSTEM_OUTPUT
                                );
                            }

                            @Override
                            protected String getConnectionMessage() {
                                return "Attaching to a Maya(py) process with PID = " + myPid;
                            }

                            @Override
                            protected String getConnectionTitle() {
                                return "Attaching Debugger";
                            }
                        };
                    pyDebugProcess.setPositionConverter(new PyLocalPositionConverter());


                    createConsoleCommunicationAndSetupActions(myProject, result, pyDebugProcess, session);

                    return pyDebugProcess;
                    }
                });
    }
}
