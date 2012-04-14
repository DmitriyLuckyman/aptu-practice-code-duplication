package ru.spbau.simian;

import com.harukizaemon.simian.Block;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.reference.RefEntity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maria
 */
public class SimianGlobalInspectionTool extends GlobalInspectionTool {
    @Override
    public void runInspection(AnalysisScope scope, final InspectionManager manager, final GlobalInspectionContext globalContext, final ProblemDescriptionsProcessor problemDescriptionsProcessor) {
        final SimianGlobalInspectionContext simianInspectionContext = globalContext.getExtension(SimianGlobalInspectionContext.KEY);
        if (simianInspectionContext == null) {
            return;
        }

        for (final List<Block> problem : simianInspectionContext.getMyResults()) {
            for (final Block block : problem) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(block.getSourceFile().getFilename());
                        if (virtualFile == null) {
                            return;
                        }
                        final PsiManager psiManager = PsiManager.getInstance(globalContext.getProject());
                        final PsiFile psiFile = psiManager.findFile(virtualFile);

                        if (psiFile == null) {
                            return;
                        }

                        final ProblemDescriptor descriptor = computeProblemDescriptor(block, manager, psiManager);
                        if (descriptor != null) {
                            problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(psiFile), descriptor);
                        }
                    }
                });
            }
        }
    }

    @Nullable
    private ProblemDescriptor computeProblemDescriptor(@NotNull Block block, @NotNull InspectionManager manager, PsiManager psiManager) {
        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(block.getSourceFile().getFilename());
        if (virtualFile == null) {
            return null;
        }
        final PsiFile psiFile = psiManager.findFile(virtualFile);
        if (psiFile == null) {
            return null;
        }
        final int startOffset = offsetOfLine(psiFile, block.getStartLineNumber());
        final int endOffset = offsetOfLine(psiFile, block.getEndLineNumber() + 1) - 1;

        final PsiElement start = psiFile.findElementAt(startOffset);
        final PsiElement end = psiFile.findElementAt(endOffset);
        return manager.createProblemDescriptor(
                start == null ? psiFile : start,
                end == null ? psiFile : end,
                SimianBundle.message("duplicated.lines", block.getEndLineNumber() - block.getStartLineNumber()),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                false
        );
    }

    private int offsetOfLine(PsiFile psiFile, int lineNumber) {
        final LineTokenizer lineTokenizer = new LineTokenizer(psiFile.getViewProvider().getContents());
        while (lineNumber-- > 0) lineTokenizer.advance();
        return lineTokenizer.getOffset();
    }

    @Override
    public void compose(StringBuffer buf, RefEntity refEntity, HTMLComposer composer) {
        super.compose(buf, refEntity, composer);
    }
}
