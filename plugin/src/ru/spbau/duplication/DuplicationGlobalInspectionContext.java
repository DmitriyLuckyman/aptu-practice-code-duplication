package ru.spbau.duplication;

import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.codeInspection.lang.GlobalInspectionContextExtension;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.refactoring.util.duplicates.Match;
import com.intellij.refactoring.util.duplicates.MethodDuplicatesHandler;
import gnu.trove.THashSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: maria
 */
public class DuplicationGlobalInspectionContext implements GlobalInspectionContextExtension<DuplicationGlobalInspectionContext> {
    public static final Key<DuplicationGlobalInspectionContext> KEY = Key.create("DuplicationGlobalInspectionContext");

    private final List<Pair<Match, PsiMethod>> matches = new ArrayList<Pair<Match, PsiMethod>>();

    @Override
    public Key<DuplicationGlobalInspectionContext> getID() {
        return KEY;
    }

    public List<Pair<Match, PsiMethod>> getMatches() {
        return matches;
    }

    @Override
    public void performPreRunActivities(List<Tools> globalTools, List<Tools> localTools, final GlobalInspectionContext context) {
        final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        final Set<PsiMethod> utilMethods = findUtilMethods(context, indicator);
        findDuplicates(context, utilMethods, indicator);
    }

    private Set<PsiMethod> findUtilMethods(final GlobalInspectionContext context, @Nullable final ProgressIndicator indicator) {
        final Set<PsiMethod> utilMethods = new THashSet<PsiMethod>();
        final SearchScope searchScope = context.getRefManager().getScope().toSearchScope();
        for (final VirtualFile virtualFile : FileTypeIndex.getFiles(JavaFileType.INSTANCE, (GlobalSearchScope) searchScope)) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final PsiFile psiFile = PsiManager.getInstance(context.getProject()).findFile(virtualFile);
                    if (psiFile == null) {
                        return;
                    }
                    if (indicator != null) {
                        ProgressWrapper.unwrap(indicator).setText(DuplicationBundle.message("finding.util.methods", psiFile.getName()));
                    }


                    psiFile.accept(new PsiElementVisitor() {
                        @Override
                        public void visitElement(PsiElement element) {
                            if (element instanceof PsiMethod && isPublicStatic((PsiMethod) element)) {
                                utilMethods.add((PsiMethod) element);
                            }
                            element.acceptChildren(this);
                        }
                    });
                }
            });
        }
        return utilMethods;
    }

    private void findDuplicates(final GlobalInspectionContext context, final Set<PsiMethod> utilMethods, final ProgressIndicator indicator) {
        final SearchScope searchScope = context.getRefManager().getScope().toSearchScope();
        for (final VirtualFile virtualFile : FileTypeIndex.getFiles(JavaFileType.INSTANCE, (GlobalSearchScope) searchScope)) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final PsiFile psiFile = PsiManager.getInstance(context.getProject()).findFile(virtualFile);
                    if (psiFile == null) {
                        return;
                    }
                    if (indicator != null) {
                        ProgressWrapper.unwrap(indicator).setText(DuplicationBundle.message("finding.duplicates.in.file", psiFile.getName()));
                    }

                    for (PsiMethod psiMethod : utilMethods) {
                        for (Match match : MethodDuplicatesHandler.hasDuplicates(psiFile, psiMethod)) {
                            matches.add(new Pair<Match, PsiMethod>(match, psiMethod));
                        }
                    }
                }
            });
        }
    }

    private boolean isPublicStatic(PsiMethod method) {
        return method.getModifierList().hasExplicitModifier(PsiModifier.PUBLIC) &&
                method.getModifierList().hasExplicitModifier(PsiModifier.STATIC);
    }

    @Override
    public void performPostRunActivities(List<InspectionProfileEntry> inspections, GlobalInspectionContext context) {
    }

    @Override
    public void cleanup() {
        matches.clear();
    }
}
