package com.github.paulpanther.intellijactiondetection.services

import com.intellij.openapi.project.Project
import com.github.paulpanther.intellijactiondetection.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
