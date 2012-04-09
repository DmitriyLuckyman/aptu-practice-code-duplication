package ru.spbau.simian.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import ru.spbau.simian.SimianBundle;
import ru.spbau.simian.settings.ui.SimianSettingsPanel;

import javax.swing.*;

/**
 * @author: maria
 */
public class SimianSettingsManager implements Configurable, Configurable.NoScroll {
    private final SimianSettingsPanel myPanel = new SimianSettingsPanel();
    @Nls
    @Override
    public String getDisplayName() {
        return SimianBundle.message("settings.title");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        return myPanel.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !SimianSettings.getPathToSimian().equals(myPanel.getPath());
    }

    @Override
    public void apply() throws ConfigurationException {
        SimianSettings.setPathToSimian(myPanel.getPath());
    }

    @Override
    public void reset() {
        myPanel.setPath(SimianSettings.getPathToSimian());
    }

    @Override
    public void disposeUIResources() {
    }
}
