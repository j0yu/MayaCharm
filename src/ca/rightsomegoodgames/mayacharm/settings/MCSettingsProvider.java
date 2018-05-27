package ca.rightsomegoodgames.mayacharm.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

@State(
        name = "MCSettingsProvider",
        storages = {
                @Storage(file = StoragePathMacros.WORKSPACE_FILE)
        }
)

public class MCSettingsProvider implements PersistentStateComponent<MCSettingsProvider.State> {
    private State myState = new State();

    public void setCmdPort(int port) {
        myState.CmdPort = port;
    }

    public int getCmdPort() {
        return (myState.CmdPort == -1 || myState.CmdPort == 0) ? 4434 : myState.CmdPort;
    }

    public void setDebuggerPort(int port) {
        myState.DebuggerPort = port;
    }

    public int getDebuggerPort() {
        return (myState.DebuggerPort == -1 || myState.DebuggerPort == 0) ? 60059 : myState.DebuggerPort;
    }

    public void setRedirectOutput(boolean redirect) { myState.RedirectOutput = redirect; }

    public boolean getRedirectOutput() { return myState.RedirectOutput; }

    public void setSuspendAfterConnect(boolean redirect) { myState.SuspendAfterConnect = redirect; }

    public boolean getSuspendAfterConnect() { return myState.SuspendAfterConnect; }

    public void setHost(String host) {
        myState.Host = host;
    }

    public String getHost() {
        return (myState.Host == null || myState.Host.isEmpty()) ? "localhost" : myState.Host;
    }

    public static MCSettingsProvider getInstance(Project project) {
        return ServiceManager.getService(project, MCSettingsProvider.class);
    }

    @Nullable
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState.Host = state.Host;
        myState.CmdPort = state.CmdPort;
        myState.DebuggerPort = state.DebuggerPort;
        myState.RedirectOutput = state.RedirectOutput;
        myState.SuspendAfterConnect = state.SuspendAfterConnect;
    }

    public static class State {
        public int CmdPort;
        public int DebuggerPort;
        public String Host;
        public boolean RedirectOutput;
        public boolean SuspendAfterConnect;
    }
}
