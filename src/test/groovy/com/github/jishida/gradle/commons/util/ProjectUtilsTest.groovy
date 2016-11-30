package com.github.jishida.gradle.commons.util

import nebula.test.ProjectSpec
import org.gradle.api.DefaultTask

class ProjectUtilsTest extends ProjectSpec{
    static class Test{
        final int value

        Test(int value){
            this.value = value
        }
    }

    def 'ProjectUtils test'(){
        ProjectUtils.addTaskType(project, 'TestTask', DefaultTask)
        final extension = ProjectUtils.addExtension(project, Test, 5)
        final createdTask = ProjectUtils.createTask(project, DefaultTask)

        expect:
        project.extensions.extraProperties.get('TestTask') == DefaultTask
        project.extensions.getByName('test') == extension
        project.extensions.getByType(Test) == extension
        project.tasks.getByName('defaultTask') == createdTask
    }
}
