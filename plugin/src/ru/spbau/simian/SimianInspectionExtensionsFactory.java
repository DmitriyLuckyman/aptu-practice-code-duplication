package ru.spbau.simian;

import com.intellij.codeInspection.HTMLComposer;
import com.intellij.codeInspection.ex.GlobalJavaInspectionContextImpl;
import com.intellij.codeInspection.lang.GlobalInspectionContextExtension;
import com.intellij.codeInspection.lang.HTMLComposerExtension;
import com.intellij.codeInspection.lang.InspectionExtensionsFactory;
import com.intellij.codeInspection.lang.RefManagerExtension;
import com.intellij.codeInspection.reference.RefManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author: maria
 */
public class SimianInspectionExtensionsFactory extends InspectionExtensionsFactory {
    @Override
    public GlobalInspectionContextExtension createGlobalInspectionContextExtension() {
        return new SimianGlobalInspectionContextImpl();
    }

    @Override
    public RefManagerExtension createRefManagerExtension(RefManager refManager) {
        return null;
    }

    @Override
    public HTMLComposerExtension createHTMLComposerExtension(HTMLComposer composer) {
        return null;
    }

    @Override
    public boolean isToCheckMember(PsiElement element, String id) {
        return true;
    }

    @Nullable
    @Override
    public String getSuppressedInspectionIdsIn(PsiElement element) {
        return null;
    }

    @Override
    public boolean isProjectConfiguredToRunInspections(Project project, boolean online) {
        return GlobalJavaInspectionContextImpl.isInspectionsEnabled(online, project);
    }
}
