package io.waldo

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant

class ToolsPlugin implements Plugin<Project> {
    final static String GROUP_NAME = 'Waldo Tools'

    void apply(Project project) {
        project.extensions.create('waldo', ToolsExtension)
        applyTasks(project)
    }       

    void applyTasks(final Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new GradleException("This plugin is meant to be used in an Android project");
        }

        // We capture the plugin version so that we can sign all requests with it
        String pluginVersion = getPluginVersion()

        // Wrap each variant with its own task definition
        AppExtension android = project.android

        android.applicationVariants.all { ApplicationVariant variant ->
            UploadTask task = project.tasks.create("upload${variant.name.capitalize()}ToWaldo", UploadTask)
            task.group = GROUP_NAME
            task.description = "Upload '${variant.name}' to Waldo (plugin: v${pluginVersion})"
            task.applicationVariant = variant
            task.pluginVersion = pluginVersion
            task.variantName = variant.name
            task.outputs.upToDateWhen { false }
            task.dependsOn variant.assemble
        }
    }

    String getPluginVersion() {
        return getClass().getResource('/version.txt').getText().trim()
    }
}
