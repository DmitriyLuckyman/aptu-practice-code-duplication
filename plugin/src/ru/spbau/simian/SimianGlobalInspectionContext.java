package ru.spbau.simian;

import com.harukizaemon.simian.*;
import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.codeInspection.lang.GlobalInspectionContextExtension;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.util.ProgressWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: maria
 */
public class SimianGlobalInspectionContext implements GlobalInspectionContextExtension<SimianGlobalInspectionContext> {
    public static final Key<SimianGlobalInspectionContext> KEY = Key.create("SimianGlobalInspectionContext");

    final List<List<Block>> myResults = new ArrayList<List<Block>>();

    @Override
    public Key<SimianGlobalInspectionContext> getID() {
        return KEY;
    }

    public List<List<Block>> getMyResults() {
        return myResults;
    }

    @Override
    public void performPreRunActivities(List<Tools> globalTools, List<Tools> localTools, GlobalInspectionContext context) {
        final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
        if(indicator != null){
            ProgressWrapper.unwrap(indicator).setText(SimianBundle.message("loading.files.to.analyze"));
        }

        AuditListener listener = new MyAuditListener(myResults, indicator);

        Options options = new Options();
        options.setThreshold(6);
        options.setOption(Option.IGNORE_STRINGS, true);

        Checker checker = new Checker(listener, options);

        StreamLoader streamLoader = new StreamLoader(checker);

        final SearchScope searchScope = context.getRefManager().getScope().toSearchScope();
        for (VirtualFile file : FileTypeIndex.getFiles(JavaFileType.INSTANCE, (GlobalSearchScope) searchScope)) {
            try {
                streamLoader.load(file.getPath(), file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(indicator != null){
            ProgressWrapper.unwrap(indicator).setText(SimianBundle.message("analyzing.files"));
        }

        checker.check();
    }

    @Override
    public void performPostRunActivities(List<InspectionProfileEntry> inspections, GlobalInspectionContext context) {
    }

    @Override
    public void cleanup() {
    }

    private class MyAuditListener implements AuditListener {
        private final List<List<Block>> myResults;
        @Nullable
        private final ProgressIndicator indicator;

        private List<Block> currentBlocks = null;

        private MyAuditListener(List<List<Block>> myResults, ProgressIndicator indicator) {
            this.myResults = myResults;
            this.indicator = indicator;
        }

        @Override
        public void startCheck(Options options) {
        }

        @Override
        public void fileProcessed(SourceFile sourceFile) {
        }

        @Override
        public void startSet(int lineCount) {
            if(indicator != null){
                ProgressWrapper.unwrap(indicator).setText(SimianBundle.message("founded.duplicates", myResults.size() + 1));
            }
            currentBlocks = new ArrayList<Block>();
        }

        @Override
        public void block(Block block) {
            currentBlocks.add(block);
        }

        @Override
        public void endSet(String s) {
            myResults.add(currentBlocks);
            currentBlocks = null;
        }

        @Override
        public void endCheck(CheckSummary checkSummary) {
        }
    }
}
