package ca.rightsomegoodgames.mayacharm.resources;

import com.jetbrains.python.PythonHelper;

import java.io.File;

public final class PythonStrings {
    /**Absolute path to the {@code pydevd} folder in PyCharm's helper folder */
    public static final String PYDEVD_FOLDER = new File(PythonHelper.DEBUGGER.asParamString()).getParent();

    public static final String OPEN_LOG = "import maya.cmds as cmds; cmds.cmdFileOutput(o=r\"{0}\")";
    public static final String CLOSE_LOG = "import maya.cmds as cmds; cmds.cmdFileOutput(closeAll=True)";
    public static final String EXECFILE = "python (\"execfile (\\\"{0}\\\")\");";

    public static final String SETTRACE = "import pydevd; pydevd.settrace(\"localhost\", port=%1$s, suspend=%2$s, stdoutToServer=%3$s, stderrToServer=%3$s)";
    public static final String STOPTRACE = "import pydevd; pydevd.stoptrace()";

    public static final String PYSTDERR = "# Error: ";
    public static final String PYSTDWRN = "# Warning: ";
}
