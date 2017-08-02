package ca.rightsomegoodgames.mayacharm.settings;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.net.ServerSocket;

public class MCSettingsPanel {
    private JPanel myPanel;
    private JTextField hostField;
    private JTextField portField;
    private JTextArea mayaString;
    private JTextArea pydevdString;
    private JButton resetPortButton;
    private JButton resetHostButton;
    private JButton newSocketButton;

    private final String mayaDefaultString;
    private final  MCSettingsProvider mcSettingsProvider;

    public MCSettingsPanel(MCSettingsProvider provider) {
        mcSettingsProvider = provider;
        mayaDefaultString = mayaString.getText();

        portField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mayaString.setText(String.format(mayaDefaultString, portField.getText(), portField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mayaString.setText(String.format(mayaDefaultString, portField.getText(), portField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mayaString.setText(String.format(mayaDefaultString, portField.getText(), portField.getText()));
            }
        });

//        Wire up the button to generate new port
//        resetPortButton.addActionListener(actionEvent -> {
//            if (actionEvent.getSource() == resetPortButton) {
//                try {
//                    setAttachSocket(generateNewAttachSocket());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Messages.showErrorDialog(String.format("Failed to find free socket port, defaulting to %d instead", MCSettingsProvider.DEFAULT_PORT),
//                                             "No Free Ports");
//                }
//            }
//        });
        reset();
    }

    public JComponent createPanel() {
        return myPanel;
    }

    public boolean isModified(){
        return getPythonCommandPort() != mcSettingsProvider.getPort() ||
                !getPythonHost().equals(mcSettingsProvider.getHost());
    }

    public int getPythonCommandPort() {
        return StringUtil.parseInt(portField.getText(), -1);
    }

    public void setPythonCommandPort(Integer value) { portField.setText(value.toString()); }

    public ServerSocket generateNewAttachSocket() throws IOException{
        return new ServerSocket(0);
    }

    public void setAttachSocket(ServerSocket newSocket) {

    }

    public String getPythonHost() {
        return hostField.getText();
    }

    public void setPythonHost(String value) {
        hostField.setText(value);
    }

    public void apply() {
        mcSettingsProvider.setPort(getPythonCommandPort());
        mcSettingsProvider.setHost(getPythonHost());
    }

    public void reset() {
        setPythonCommandPort(mcSettingsProvider.getPort());
        setPythonHost(mcSettingsProvider.getHost());
    }
}
