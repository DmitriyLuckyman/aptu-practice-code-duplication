package ru.spbau.simian;

import com.harukizaemon.simian.Block;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fedor.korotkov
 */
public class SimianGlobalInspectionTool extends GlobalInspectionTool {
    @Override
    public void runInspection(AnalysisScope scope, final InspectionManager manager, final GlobalInspectionContext globalContext, final ProblemDescriptionsProcessor problemDescriptionsProcessor) {
        final SimianGlobalInspectionContext simianInspectionContext = globalContext.getExtension(SimianGlobalInspectionContext.KEY);
        if (simianInspectionContext == null) {
            return;
        }

        for (final List<Block> problem : simianInspectionContext.getMyResults()) {
            final Block firstBlock = problem.get(0);
            final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(firstBlock.getSourceFile().getFilename());
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    final PsiManager psiManager = PsiManager.getInstance(globalContext.getProject());
                    final PsiFile psiFile = psiManager.findFile(virtualFile);

                    if (psiFile == null) {
                        return;
                    }

                    final ProblemDescriptor[] descriptors = computeProblemDescriptors(problem, manager, psiManager);
                    if (descriptors.length > 0) {
                        problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(psiFile), descriptors);
                    }
                }
            });
        }
    }

    @NotNull
    private ProblemDescriptor[] computeProblemDescriptors(@NotNull List<Block> problem, @NotNull InspectionManager manager, PsiManager psiManager) {
        final List<ProblemDescriptor> result = new ArrayList<ProblemDescriptor>();
        for (Block block : problem) {
            final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(block.getSourceFile().getFilename());
            if (virtualFile == null) {
                continue;
            }
            final PsiFile psiFile = psiManager.findFile(virtualFile);
            if (psiFile == null) {
                continue;
            }

            final int startOffset = offsetOfLine(psiFile, block.getStartLineNumber());
            final int endOffset = offsetOfLine(psiFile, block.getEndLineNumber() + 1) - 1;

            result.add(manager.createProblemDescriptor(
                    psiFile,
                    new TextRange(startOffset, endOffset),
                    SimianBundle.message("duplicated.lines"),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    false
            ));
        }
        return result.toArray(new ProblemDescriptor[result.size()]);
    }

    private int offsetOfLine(PsiFile psiFile, int lineNumber) {
        final LineTokenizer lineTokenizer = new LineTokenizer(psiFile.getViewProvider().getContents());
        while (lineNumber-- > 0) lineTokenizer.advance();
        return lineTokenizer.getOffset();
    }
}
