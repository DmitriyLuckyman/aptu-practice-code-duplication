package ru.spbau.simian.settings.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: maria
 */
public class SimianSettingsPanel {
    private TextFieldWithBrowseButton myPathField;
    private JLabel myLabel;
    private JPanel myMainPanel;

    public SimianSettingsPanel() {
        myLabel.setLabelFor(myPathField.getButton());

        myPathField.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final VirtualFile file = FileChooser.chooseFile(myMainPanel, new FileChooserDescriptor(true, false, true, true, false, false));
                if(file != null){
                    setPath(file.getPath());
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return myMainPanel;
    }

    public String getPath() {
        return FileUtil.toSystemIndependentName(myPathField.getText());
    }

    public void setPath(String pathToSimian) {
        myPathField.setText(FileUtil.toSystemDependentName(pathToSimian));
    }
}
