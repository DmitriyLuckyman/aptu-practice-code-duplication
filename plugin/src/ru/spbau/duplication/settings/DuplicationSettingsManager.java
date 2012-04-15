package ru.spbau.duplication.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import ru.spbau.duplication.DuplicationBundle;
import ru.spbau.duplication.settings.ui.DuplicationSettingsPanel;

import javax.swing.*;

/**
 * @author: maria
 */
public class DuplicationSettingsManager implements Configurable, Configurable.NoScroll {
    private final DuplicationSettingsPanel myPanel = new DuplicationSettingsPanel();
    @Nls
    @Override
    public String getDisplayName() {
        return DuplicationBundle.message("settings.title");
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
        return !DuplicationSettings.getPathToSimian().equals(myPanel.getPath());
    }

    @Override
    public void apply() throws ConfigurationException {
        DuplicationSettings.setPathToSimian(myPanel.getPath());
    }

    @Override
    public void reset() {
        myPanel.setPath(DuplicationSettings.getPathToSimian());
    }

    @Override
    public void disposeUIResources() {
    }
}
