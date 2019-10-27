package com.example.frozen;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.LibraryVariant;
import com.android.build.gradle.internal.api.ApplicationVariantImpl;
import com.android.build.gradle.internal.api.LibraryVariantImpl;
import com.android.build.gradle.internal.scope.VariantScope;
import com.android.build.gradle.internal.variant.LibraryVariantData;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.DefaultDomainObjectSet;


public class MyClass implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getDependencies().add("lintClassPath", "com.xx.lib_client:lib_client:1.0.0");
        project.afterEvaluate(p -> {
            AppExtension appExtension = p.getExtensions().findByType(AppExtension.class);
            if (appExtension != null) {
                DomainObjectSet<ApplicationVariant> variants = appExtension.getApplicationVariants();
                for (ApplicationVariant variant : variants) {
                    if (variant instanceof ApplicationVariantImpl) {
                        ApplicationVariantImpl variantImpl = (ApplicationVariantImpl) variant;
                        VariantScope globalScope = variantImpl.getVariantData().getScope();
                        project.getTasks().create(globalScope.getTaskName(IncrementLintTask.NAME), IncrementLintTask.class, new IncrementLintTask.VitalConfigAction(globalScope, project));
                    }
                }
            }

            LibraryExtension libraryExtension = p.getExtensions().findByType(LibraryExtension.class);
            if (libraryExtension != null) {
                DefaultDomainObjectSet<LibraryVariant> variants = libraryExtension.getLibraryVariants();
                for (LibraryVariant variant : variants) {
                    if (variant instanceof LibraryVariantImpl) {
                        LibraryVariantImpl variantImpl = (LibraryVariantImpl) variant;
                        LibraryVariantData libraryVariantData = getVariantData(variantImpl);
                        if (libraryVariantData != null) {
                            VariantScope globalScope = libraryVariantData.getScope();
                            project.getTasks().create(globalScope.getTaskName(IncrementLintTask.NAME), IncrementLintTask.class, new IncrementLintTask.VitalConfigAction(globalScope, project));
                        }
                    }
                }
            }
        });
    }

    private LibraryVariantData getVariantData(LibraryVariantImpl variant) {
        try {
            return ReflectionUtils.getField(variant, "variantData");
        } catch (Exception e) {
            return null;
        }
    }
}
