package ru.spbau.duplication;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.reference.RefEntity;
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

        for (final Pair<Match, PsiMethod> problem : duplicationInspectionContext.getMatches()) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final Match match = problem.getFirst();
                    final PsiMethod method = problem.getSecond();

                    final ProblemDescriptor descriptor = computeProblemDescriptor(match, method, manager);
                    if (descriptor != null) {
                        problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(match.getFile()), descriptor);
                    }
                }
            });
        }
    }

    @Nullable
    private ProblemDescriptor computeProblemDescriptor(Match match, PsiMethod method, InspectionManager manager) {
        final PsiClass psiClass = method.getContainingClass();
        return manager.createProblemDescriptor(
                match.getMatchStart(),
                match.getMatchEnd(),
                DuplicationBundle.message("duplicated.method.from.class", psiClass == null ? "" : psiClass.getQualifiedName(),
                        method.getName(),
                        method.getContainingFile().getVirtualFile().getUrl(),
                        method.getTextOffset()),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                false
        );
    }

    @Override
    public void compose(StringBuffer buf, RefEntity refEntity, HTMLComposer composer) {
        super.compose(buf, refEntity, composer);
    }

    /**
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-family:verdana; font-weight:bold; color:#005555; size = 3">Name:
     * </font><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Main.java</b><b>Main.java</b><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-family:verdana; font-weight:bold; color:#005555; size = 3">Location:</font><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-family:verdana;">package&nbsp;<code></code>file&nbsp;<code><a HREF="file:/home/maria/IdeaProjects/newtest/src/Main.java#0">Main.java</a></code></font><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font style="font-family:verdana; font-weight:bold; color:#005555; size = 3">Problem synopsis:</font><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Duplicates method: Main2:test4 (at line <a HREF="file:/home/maria/IdeaProjects/newtest/src/Main.java#233">11</a>)<br><br>
     */
}
