package ca.rightsomegoodgames.mayacharm.settings;

import ca.rightsomegoodgames.mayacharm.resources.MayaCharmProperties;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;

@State(
        name = "MCSettingsProvider",
        storages = {
                @Storage(file = StoragePathMacros.WORKSPACE_FILE)
        }
)

public class MCSettingsProvider implements PersistentStateComponent<MCSettingsProvider.State> {

    private State myState = new State();

    public void setPort(int port) {
        myState.Port = port;
    }

    public int getPort() {
        return (myState.Port == -1 || myState.Port == 0) ? MayaCharmProperties.getInt("commandport.port", 4434) : myState.Port;
    }

    public void setHost(String host) {
        myState.Host = host;
    }

    public String getHost() {
        return (myState.Host == null || myState.Host.isEmpty()) ? MayaCharmProperties.getString("commandport.host") : myState.Host;
    }

    public void setAttachSocket(ServerSocket socket) { myState.AttachSocket = socket; }

    public ServerSocket getAttachSocket() {
        if (myState.AttachSocket == null) {
            try {
                setAttachSocket(generateNewAttachSocket());
            } catch (IOException e) {
                e.printStackTrace();
                Messages.showErrorDialog("Fatal Error: Failed to find free socket port.\nSocket returned may be null",
                                         "No Free Ports");
            }
        }
        return myState.AttachSocket;
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
        myState.Port = state.Port;
    }
    public ServerSocket generateNewAttachSocket() throws IOException {
        return new ServerSocket(0);
    }
    public static class State {
        public int Port;
        public String Host;
        public ServerSocket AttachSocket;
    }
}
