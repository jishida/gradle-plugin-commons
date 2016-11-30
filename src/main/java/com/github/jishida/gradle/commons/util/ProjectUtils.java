package com.github.jishida.gradle.commons.util;

import org.gradle.api.Project;
import org.gradle.api.Task;

public final class ProjectUtils {
    public static <T extends Task> void addTaskType(final Project project, final Class<T> taskClass) {
        Checker.checkNull(project, "project");
        Checker.checkNull(taskClass, "taskClass");
        project.getExtensions().getExtraProperties().set(taskClass.getSimpleName(), taskClass);
    }

    public static <T extends Task> void addTaskType(final Project project, final String name, final Class<T> taskClass) {
        Checker.checkNull(project, "project");
        Checker.checkNull(name, "name");
        Checker.checkNull(taskClass, "taskClass");
        project.getExtensions().getExtraProperties().set(name, taskClass);
    }

    public static <T extends Task> T createTask(final Project project, final Class<T> taskClass) {
        Checker.checkNull(project, "project");
        Checker.checkNull(taskClass, "taskClass");
        return project.getTasks().create(lowerCamel(taskClass.getSimpleName()), taskClass);
    }

    public static <T extends Task> T createTask(final Project project, final String name, final Class<T> taskClass) {
        Checker.checkNull(project, "project");
        Checker.checkNull(name, "name");
        Checker.checkNull(taskClass, "taskClass");
        return project.getTasks().create(name, taskClass);
    }

    public static <T> T addExtension(final Project project, final String name, final Class<T> extensionClass, final Object... constructionArgs) {
        Checker.checkNull(project, "project");
        Checker.checkNull(name, "name");
        Checker.checkNull(extensionClass, "extensionClass");
        return project.getExtensions().create(name, extensionClass, constructionArgs);
    }

    public static <T> T addExtension(final Project project, final Class<T> extensionClass, final Object... constructionArgs) {
        Checker.checkNull(project, "project");
        Checker.checkNull(extensionClass, "extensionClass");
        return project.getExtensions().create(lowerCamel(extensionClass.getSimpleName()), extensionClass, constructionArgs);
    }

    private static String lowerCamel(final String upperCamel) {
        return upperCamel.isEmpty() ? "" : upperCamel.substring(0, 1).toLowerCase() + upperCamel.substring(1);
    }
}
