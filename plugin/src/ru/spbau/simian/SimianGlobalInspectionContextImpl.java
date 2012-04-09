package ru.spbau.simian;

import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.codeInspection.lang.GlobalInspectionContextExtension;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;

import java.util.List;

/**
 * @author: maria
 */
public class SimianGlobalInspectionContextImpl implements GlobalInspectionContextExtension<SimianGlobalInspectionContextImpl> {
    public static final Key<SimianGlobalInspectionContextImpl> CONTEXT = Key.create("SimianGlobalInspectionContext");

    @Override
    public Key<SimianGlobalInspectionContextImpl> getID() {
        return CONTEXT;
    }

    @Override
    public void performPreRunActivities(List<Tools> globalTools, List<Tools> localTools, GlobalInspectionContext context) {
        final SearchScope searchScope = context.getRefManager().getScope().toSearchScope();
        for (VirtualFile file : FileTypeIndex.getFiles(JavaFileType.INSTANCE, (GlobalSearchScope) searchScope)) {

        }
    }

    @Override
    public void performPostRunActivities(List<InspectionProfileEntry> inspections, GlobalInspectionContext context) {
    }

    @Override
    public void cleanup() {
    }
}
