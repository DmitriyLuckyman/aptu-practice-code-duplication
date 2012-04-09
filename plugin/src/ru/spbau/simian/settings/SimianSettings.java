package ru.spbau.simian.settings;

import com.intellij.ide.util.PropertiesComponent;

/**
 * @author: maria
 */
public class SimianSettings {

    public static final String PATH_TO_SIMIAN_KEY = "path.to.simian";

    public static String getPathToSimian() {
        return PropertiesComponent.getInstance().getValue(PATH_TO_SIMIAN_KEY, "");
    }

    public static void setPathToSimian(String pathToSimian) {
        PropertiesComponent.getInstance().setValue(PATH_TO_SIMIAN_KEY, pathToSimian);
    }
}
