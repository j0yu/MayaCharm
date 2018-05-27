package ca.rightsomegoodgames.mayacharm.settings;

import ca.rightsomegoodgames.mayacharm.resources.PythonStrings;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MCSettingsPanel {
    private JPanel myPanel;
    private JTextField hostField;
    private JTextField cmdPortField;
    private JTextArea mayaString;

    private final  MCSettingsProvider mcSettingsProvider;

    public MCSettingsPanel(MCSettingsProvider provider) {
        mcSettingsProvider = provider;
        cmdPortField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mayaString.setText(String.format(PythonStrings.CMDPORT_SETUP_SCRIPT, cmdPortField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mayaString.setText(String.format(PythonStrings.CMDPORT_SETUP_SCRIPT, cmdPortField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mayaString.setText(String.format(PythonStrings.CMDPORT_SETUP_SCRIPT, cmdPortField.getText()));
            }
        });
        reset();
    }

    public JComponent createPanel() {
        return myPanel;
    }

    public boolean isModified(){
        return getPythonCommandPort() != mcSettingsProvider.getCmdPort() ||
                !getPythonHost().equals(mcSettingsProvider.getHost());
    }

    public int getPythonCommandPort() {
        return StringUtil.parseInt(cmdPortField.getText(), -1);
    }

    public void setPythonCommandPort(Integer value) {
        cmdPortField.setText(value.toString());
    }

    public String getPythonHost() {
        return hostField.getText();
    }

    public void setPythonHost(String value) {
        hostField.setText(value);
    }

    public void apply() {
        mcSettingsProvider.setCmdPort(getPythonCommandPort());
        mcSettingsProvider.setHost(getPythonHost());
    }

    public void reset() {
        setPythonCommandPort(mcSettingsProvider.getCmdPort());
        setPythonHost(mcSettingsProvider.getHost());
    }
}
