package ru.spbau.duplication;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.util.duplicates.Match;
import org.jetbrains.annotations.Nullable;

/**
 * @author maria
 */
public class DuplicationGlobalInspectionTool extends GlobalInspectionTool {
    @Override
    public void runInspection(AnalysisScope scope, final InspectionManager manager, final GlobalInspectionContext globalContext, final ProblemDescriptionsProcessor problemDescriptionsProcessor) {
        final DuplicationGlobalInspectionContext duplicationInspectionContext = globalContext.getExtension(DuplicationGlobalInspectionContext.KEY);
        if (duplicationInspectionContext == null) {
            return;
        }

        for (final Pair<Match, PsiMethod> problem : duplicationInspectionContext.getUtilMatches()) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final Match match = problem.getFirst();
                    final PsiMethod method = problem.getSecond();

                    final ProblemDescriptor descriptor = computeUtilMethodProblemDescriptor(match, method, manager, true);
                    if (descriptor != null) {
                        problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(match.getFile()), descriptor);
                    }
                }
            });
        }

        for (final Pair<Match, PsiMethod> problem : duplicationInspectionContext.getHierarchyMatches()) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final Match match = problem.getFirst();
                    final PsiMethod method = problem.getSecond();

                    final ProblemDescriptor descriptor = computeUtilMethodProblemDescriptor(match, method, manager, false);
                    if (descriptor != null) {
                        problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(match.getFile()), descriptor);
                    }
                }
            });
        }
    }

    @Nullable
    private ProblemDescriptor computeUtilMethodProblemDescriptor(Match match, PsiMethod method, InspectionManager manager, boolean appendClassNameInFix) {
        final PsiClass psiClass = method.getContainingClass();
        return manager.createProblemDescriptor(
                match.getMatchStart(),
                match.getMatchEnd(),
                DuplicationBundle.message("duplicated.method.from.class",
                        psiClass == null ? "" : psiClass.getQualifiedName(),
                        method.getName(),
                        method.getContainingFile().getVirtualFile().getUrl(),
                        method.getTextOffset()),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                false,
                new DuplicateQuickFix(match, method, appendClassNameInFix)
        );
    }
}
