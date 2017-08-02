package ca.rightsomegoodgames.mayacharm.settings;

import ca.rightsomegoodgames.mayacharm.resources.MayaCharmProperties;
import ca.rightsomegoodgames.mayacharm.resources.PythonStrings;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
//import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JTextField attachSocketPortField;

    private ServerSocket newAttachSocket;
    private final String mayaDefaultString;
    private final String attachDefaultString;
    private final MCSettingsProvider mcSettingsProvider;

    public MCSettingsPanel(MCSettingsProvider provider) {
        mcSettingsProvider = provider;
        mayaDefaultString = mayaString.getText();
        attachDefaultString = pydevdString.getText();

        portField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateMayaString() {
                mayaString.setText(String.format(mayaDefaultString, portField.getText(), portField.getText()));
            }

            @Override
            public void insertUpdate(DocumentEvent e) { updateMayaString(); }

            @Override
            public void removeUpdate(DocumentEvent e) { updateMayaString(); }

            @Override
            public void changedUpdate(DocumentEvent e) { updateMayaString(); }
        });

        attachSocketPortField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateAttachString() {
                pydevdString.setText(String.format(attachDefaultString, PythonStrings.PYDEVD_FOLDER, PythonStrings.PYDEVD_FOLDER, attachSocketPortField.getText()));
            }

            @Override
            public void insertUpdate(DocumentEvent e) { updateAttachString(); }

            @Override
            public void removeUpdate(DocumentEvent e) { updateAttachString(); }

            @Override
            public void changedUpdate(DocumentEvent e) { updateAttachString(); }
        });

        ActionListener buttonActionListener = actionEvent -> {
            Object buttonPressed = actionEvent.getSource();
            if (buttonPressed == resetPortButton) {
                resetPythonCommandPort();

            } else if (buttonPressed == resetHostButton) {
                resetPythonHost();

            } else if (buttonPressed == newSocketButton) {
                generateNewAttachSocket();
            }
        };

//        // PyCharm suggested the above "lambda" instead (works fine). Remove this if "lambda" is ok for coding style/readability
//        ActionListener buttonActionListener = new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                Object buttonPressed = actionEvent.getSource();
//                if (buttonPressed == resetPortButton) {
//                    resetPythonCommandPort();
//
//                } else if (buttonPressed == resetHostButton) {
//                    resetPythonHost();
//
//                } else if (buttonPressed == newSocketButton) {
//                    generateNewAttachSocket();
//                }
//            }
//        };
        resetPortButton.addActionListener(buttonActionListener);
        resetHostButton.addActionListener(buttonActionListener);
        newSocketButton.addActionListener(buttonActionListener);
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

    public void resetPythonCommandPort() {
        setPythonCommandPort(MayaCharmProperties.getInt("commandport.port", 4434));
    }

    public void generateNewAttachSocket() {
        try {
            final ServerSocket newAttachSocket = mcSettingsProvider.generateNewAttachSocket();
            setAttachSocket(newAttachSocket);
            mcSettingsProvider.setAttachSocket(newAttachSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAttachSocket(ServerSocket newSocket) {
        newAttachSocket = newSocket;
        attachSocketPortField.setText(String.format("%d", newSocket.getLocalPort()));
    }

    public String getPythonHost() {
        return hostField.getText();
    }

    public void setPythonHost(String value) {
        hostField.setText(value);
    }

    public void resetPythonHost() {
        setPythonHost(MayaCharmProperties.getString("commandport.host"));
    }

    public void apply() {
        mcSettingsProvider.setPort(getPythonCommandPort());
        mcSettingsProvider.setHost(getPythonHost());
        mcSettingsProvider.setAttachSocket(newAttachSocket);
    }

    public void reset() {
        setPythonCommandPort(mcSettingsProvider.getPort());
        setPythonHost(mcSettingsProvider.getHost());
        setAttachSocket(mcSettingsProvider.getAttachSocket());
    }
}
