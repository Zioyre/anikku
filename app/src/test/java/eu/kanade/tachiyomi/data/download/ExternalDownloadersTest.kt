package eu.kanade.tachiyomi.data.download

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.io.File

class ExternalDownloadersTest {

    @Test
    fun manifestQueriesMatchAllowlist() {
        val queries = queriesBlock(manifestText())
        assertFalse(queries.contains("<intent"))
        val declared = Regex("""<package android:name="([^"]+)"\s*/>""")
            .findAll(queries)
            .map { it.groupValues[1] }
            .toList()
        assertEquals(ExternalDownloaders.entries.map { it.packageName }, declared)
    }

    @Test
    fun handoffIsAllowlistOnly() {
        assertEquals(
            ExternalDownloaders.Handoff.ONE_DM,
            ExternalDownloaders.handoffFor("idm.internet.download.manager"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.ONE_DM,
            ExternalDownloaders.handoffFor("idm.internet.download.manager.plus"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.ONE_DM,
            ExternalDownloaders.handoffFor("idm.internet.download.manager.adm.lite"),
        )
        assertEquals(ExternalDownloaders.Handoff.ADM, ExternalDownloaders.handoffFor("com.dv.adm"))
        assertEquals(
            ExternalDownloaders.Handoff.GENERIC,
            ExternalDownloaders.handoffFor("com.gopeed.gopeed"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.GENERIC,
            ExternalDownloaders.handoffFor("com.abdownloadmanager"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.GENERIC,
            ExternalDownloaders.handoffFor("org.freedownloadmanager.fdm"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.GENERIC,
            ExternalDownloaders.handoffFor("com.tachibana.downloader"),
        )
        assertEquals(
            ExternalDownloaders.Handoff.GENERIC,
            ExternalDownloaders.handoffFor("com.gianlu.aria2app"),
        )
        assertEquals(null, ExternalDownloaders.handoffFor("org.videolan.vlc"))
        assertEquals(null, ExternalDownloaders.handoffFor("com.dv.adm.pay"))
        assertEquals(
            ExternalDownloaders.entries.map { it.packageName }.distinct().size,
            ExternalDownloaders.entries.size,
        )
    }

    private fun queriesBlock(manifest: String): String {
        return Regex("<queries>(.*?)</queries>", RegexOption.DOT_MATCHES_ALL)
            .find(manifest)
            ?.groupValues
            ?.get(1)
            ?: error("AndroidManifest.xml has no <queries> block")
    }

    private fun manifestText(): String {
        val candidates = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml"),
        )
        val manifest = candidates.firstOrNull { it.isFile }
            ?: error("AndroidManifest.xml not found from ${File(".").absolutePath}")
        return manifest.readText()
    }
}
