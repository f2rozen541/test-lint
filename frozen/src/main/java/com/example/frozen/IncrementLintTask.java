package com.example.frozen;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.build.gradle.internal.scope.VariantScope;
import com.android.build.gradle.tasks.LintBaseTask;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Set;


public class IncrementLintTask extends LintBaseTask{
    public static final String NAME = "incrementLint";

    private VariantInputs variantInputs;
    private boolean fatalOnly;

    @InputFiles
    @Optional
    public FileCollection getVariantInputs() {
        return variantInputs.getAllInputs();
    }

    @TaskAction
    public void lint() {
        runLint(new IncrementLintTask.IncrementLintTaskDescriptor());
    }

    @Override
    protected void runLint(LintBaseTaskDescriptor descriptor) {
        FileCollection lintClassPath = getLintClassPath();
        if (lintClassPath != null) {
            Set<File> lintClassPathFiles = lintClassPath.getFiles();
//            File clientJar = new File(IncrementLintGradleOperator.class.getProtectionDomain().getCodeSource().getLocation().getFile());
//            lintClassPathFiles.add(clientJar);
            new MyReflectiveLintRunner().runLint(getProject().getGradle(),
                    descriptor, lintClassPathFiles);
        }
//        try {
//            new IncrementLintGradleOperator(descriptor).analyze();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private class IncrementLintTaskDescriptor extends LintBaseTaskDescriptor {
        @Nullable
        @Override
        public String getVariantName() {
            return IncrementLintTask.this.getVariantName();
        }

        @Nullable
        @Override
        public VariantInputs getVariantInputs(@NonNull String variantName) {
            assert variantName.equals(getVariantName());
            return variantInputs;
        }

        @Override
        public boolean isFatalOnly() {
            return fatalOnly;
        }
    }

    public static class VitalConfigAction extends BaseConfigAction<IncrementLintTask> {

        private final VariantScope scope;
        private final Project project;

        VitalConfigAction(@NonNull VariantScope scope, Project project) {
            super(scope.getGlobalScope());
            this.scope = scope;
            this.project = project;
        }

        @NonNull
        @Override
        public String getName() {
            return scope.getTaskName(NAME);
        }

        @NonNull
        @Override
        public Class<IncrementLintTask> getType() {
            return IncrementLintTask.class;
        }

        @Override
        public void execute(@NonNull IncrementLintTask task) {
            super.execute(task);

            String variantName = scope.getVariantConfiguration().getFullName();
                    scope.getVariantData().getVariantConfiguration().getFullName();
            task.setVariantName(variantName);

            task.variantInputs = new VariantInputs(scope);
//            task.fatalOnly = false;
            task.setDescription(
                    "Runs increment lint on just the fatal issues in the " + variantName + " build.");

            project.getTasks().add(task);
        }
    }
}