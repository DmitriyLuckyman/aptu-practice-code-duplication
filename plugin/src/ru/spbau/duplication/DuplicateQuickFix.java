package ru.spbau.duplication;

import com.intellij.codeInsight.completion.JavaCompletionUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.ImportHelper;
import com.intellij.refactoring.util.duplicates.Match;
import org.jetbrains.annotations.NotNull;

/**
 * @author: maria
 */
public class DuplicateQuickFix implements LocalQuickFix {
    private final Match match;
    private final PsiMethod method;

    public DuplicateQuickFix(Match match, PsiMethod method) {
        this.match = match;
        this.method = method;
    }

    @NotNull
    @Override
    public String getName() {
        return DuplicationBundle.message("replace.quick.fix.name");
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return DuplicationBundle.message("replace.quick.fix.name");
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiClass containingClass = method.getContainingClass();
        assert containingClass != null;
        final String text = containingClass.getName() + "." + method.getName() + "()";
        PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) factory.createExpressionFromText(text, null);
        methodCallExpression = (PsiMethodCallExpression) CodeStyleManager.getInstance(method.getManager()).reformat(methodCallExpression);

        final PsiFile matchFile = match.getFile();
        match.replace(method, methodCallExpression, null);
        if(methodCallExpression.getMethodExpression().resolve() == null){
            final PsiClass aClass = method.getContainingClass();
            assert aClass != null;
            JavaCodeStyleManager.getInstance(project).addImport((PsiJavaFile) matchFile, aClass);
        }
    }
}
