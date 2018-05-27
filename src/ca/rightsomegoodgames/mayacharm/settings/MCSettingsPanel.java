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
    private JTextField debuggerPortField;
    private JTextArea pydevSetupString;
    private JCheckBox redirectOutputField;
    private JCheckBox suspendField;

    private final  MCSettingsProvider mcSettingsProvider;

    public MCSettingsPanel(MCSettingsProvider provider) {
        mcSettingsProvider = provider;
        cmdPortField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { UpdateMayaString(); }

            @Override
            public void removeUpdate(DocumentEvent e) { UpdateMayaString(); }

            @Override
            public void changedUpdate(DocumentEvent e) { UpdateMayaString(); }
        });
        debuggerPortField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { UpdatePydevSetup(); }

            @Override
            public void removeUpdate(DocumentEvent e) { UpdatePydevSetup(); }

            @Override
            public void changedUpdate(DocumentEvent e) { UpdatePydevSetup(); }
        });
        redirectOutputField.addItemListener(e -> UpdatePydevSetup());
        suspendField.addItemListener(e -> UpdatePydevSetup());
        reset();
    }

    private void UpdatePydevSetup() {
        String setTraceCmd = String.format(
                PythonStrings.SETTRACE,
                getPythonHost(),
                getDebuggerPort(),
                isSuspendAfterConnect() ? "True" : "False",
                isRedirectOutput() ? "True" : "False"
        );
        pydevSetupString.setText(PythonStrings.PYDEV_SETUP_SCRIPT + setTraceCmd);
    }

    private void UpdateMayaString() {
        mayaString.setText(String.format(PythonStrings.CMDPORT_SETUP_SCRIPT, cmdPortField.getText()));
    }

    public JComponent createPanel() {
        return myPanel;
    }

    public boolean isModified(){
        return getPythonCommandPort() != mcSettingsProvider.getCmdPort() ||
                !getPythonHost().equals(mcSettingsProvider.getHost()) ||
                getDebuggerPort() != mcSettingsProvider.getDebuggerPort() ||
                isRedirectOutput() != mcSettingsProvider.getRedirectOutput() ||
                isSuspendAfterConnect() != mcSettingsProvider.getSuspendAfterConnect();
    }

    public int getPythonCommandPort() {
        return StringUtil.parseInt(cmdPortField.getText(), -1);
    }

    public void setPythonCommandPort(Integer value) {
        cmdPortField.setText(value.toString());
    }

    public int getDebuggerPort() {
        return StringUtil.parseInt(debuggerPortField.getText(), -1);
    }

    public void setDebuggerPort(Integer value) { debuggerPortField.setText(value.toString()); }

    public String getPythonHost() {
        return hostField.getText();
    }

    public void setPythonHost(String value) {
        hostField.setText(value);
    }

    public boolean isRedirectOutput() {
        return redirectOutputField.isSelected();
    }

    public void setRedirectOutput(boolean b) { redirectOutputField.setSelected(b); }

    public boolean isSuspendAfterConnect() {
        return suspendField.isSelected();
    }

    public void setSuspendAfterConnect(boolean b) { suspendField.setSelected(b); }

    public void apply() {
        mcSettingsProvider.setCmdPort(getPythonCommandPort());
        mcSettingsProvider.setHost(getPythonHost());
        mcSettingsProvider.setDebuggerPort(getDebuggerPort());
        mcSettingsProvider.setRedirectOutput(isRedirectOutput());
        mcSettingsProvider.setSuspendAfterConnect(isSuspendAfterConnect());
    }

    public void reset() {
        setPythonCommandPort(mcSettingsProvider.getCmdPort());
        setPythonHost(mcSettingsProvider.getHost());
        setDebuggerPort(mcSettingsProvider.getDebuggerPort());
        setRedirectOutput(mcSettingsProvider.getRedirectOutput());
        setSuspendAfterConnect(mcSettingsProvider.getSuspendAfterConnect());
    }
}
