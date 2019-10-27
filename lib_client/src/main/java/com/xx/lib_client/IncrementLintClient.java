package com.xx.lib_client;

import com.android.builder.model.Variant;
import com.android.sdklib.BuildToolInfo;
import com.android.tools.lint.LintCliFlags;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.client.api.LintRequest;
import com.android.tools.lint.gradle.LintGradleClient;
import com.android.tools.lint.gradle.api.VariantInputs;

import org.gradle.api.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class IncrementLintClient extends LintGradleClient {
    private Project gradleProject;
    public IncrementLintClient(String version, IssueRegistry registry, LintCliFlags flags, Project gradleProject, File sdkHome, Variant variant, VariantInputs variantInputs, BuildToolInfo buildToolInfo, boolean isAndroid, String baselineVariantName) {
        super(version, registry, flags, gradleProject, sdkHome, variant, variantInputs, buildToolInfo, isAndroid, baselineVariantName);
        this.gradleProject = gradleProject;
    }

    @Override
    protected LintRequest createLintRequest(List<File> files) {
        LintRequest lintRequest = super.createLintRequest(files);
        for (com.android.tools.lint.detector.api.Project project : lintRequest.getProjects()) {
            try {
                addFiles(project);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lintRequest;
    }

    private void addFiles(com.android.tools.lint.detector.api.Project project) throws IOException {
        File file = new File(gradleProject.getRootDir(), "commitFiles.txt");
        if (file.exists()) {
            FileInputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String str = null;
                while((str = bufferedReader.readLine()) != null) {
                    System.out.println(str);
                    File tmpFile = new File(str);
                    if (tmpFile.exists()) {
                        System.out.println("添加文件" + tmpFile.getAbsolutePath() + "到lint检查");
                        project.addFile(file);
                    }
                }

            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
    }
}
