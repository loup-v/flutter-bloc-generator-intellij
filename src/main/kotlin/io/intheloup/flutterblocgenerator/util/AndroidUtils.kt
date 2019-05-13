package io.intheloup.flutterblocgenerator.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import org.jetbrains.android.dom.manifest.Manifest
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.psi.KtFile

object AndroidUtils {

    fun getFacet(project: Project): AndroidFacet {
        val applicationFacets = AndroidUtils.getApplicationFacets(project)
        check(applicationFacets.isNotEmpty(), { "Android Module not found" })
        return applicationFacets.first()
    }

    fun getManifest(facet: AndroidFacet): Manifest {
        val manifestFile = facet.mainIdeaSourceProvider.manifestFile
                ?: throw IllegalStateException("AndroidManifest.xml not found")

        check(ReadonlyStatusHandler.ensureFilesWritable(facet.module.project, manifestFile), { "AndroidManifest.xml not writable" })

        return AndroidUtils.loadDomElement(facet.module, manifestFile, Manifest::class.java)
                ?: throw IllegalStateException("Failed to parse AndroidManifest.xml")
    }

    fun manifestHasActivity(manifest: Manifest, name: String) =
            manifest.application.activities.find { it.activityClass?.value?.name == name } != null


    fun registerActivityToManifest(manifest: Manifest, activity: KtFile) {
        manifest.application.addActivity().apply {
            activityClass.value = activity.classes.first()
        }
    }
}