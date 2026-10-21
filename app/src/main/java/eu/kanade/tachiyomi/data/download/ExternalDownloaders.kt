package eu.kanade.tachiyomi.data.download

import android.content.pm.PackageManager

// ANK -->
/**
 * Download managers the picker may offer, and the handoff [Downloader] uses.
 * Shown only when installed. 1DM and ADM keep their dedicated handoff.
 */
object ExternalDownloaders {
    enum class Handoff {
        ONE_DM,
        ADM,
        GENERIC,
    }

    data class Entry(
        val packageName: String,
        val handoff: Handoff,
    )

    val entries: List<Entry> = listOf(
        Entry("idm.internet.download.manager", Handoff.ONE_DM),
        Entry("idm.internet.download.manager.plus", Handoff.ONE_DM),
        Entry("idm.internet.download.manager.adm.lite", Handoff.ONE_DM),
        Entry("com.dv.adm", Handoff.ADM),
        Entry("com.abdownloadmanager", Handoff.GENERIC),
        Entry("com.gopeed.gopeed", Handoff.GENERIC),
        Entry("org.freedownloadmanager.fdm", Handoff.GENERIC),
        Entry("com.tachibana.downloader", Handoff.GENERIC),
        Entry("com.gianlu.aria2app", Handoff.GENERIC),
    )

    fun handoffFor(packageName: String): Handoff? {
        return entries.find { it.packageName == packageName }?.handoff
    }

    fun installedLabels(pm: PackageManager, ownPackage: String): Map<String, String> {
        return entries
            .map { it.packageName }
            .filter { it != ownPackage }
            .mapNotNull { pkg ->
                val label = try {
                    pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
                } catch (_: PackageManager.NameNotFoundException) {
                    return@mapNotNull null
                }
                pkg to label
            }
            .sortedBy { it.second.lowercase() }
            .toMap()
    }
}
// ANK <--
