package pers.shennoter

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object UNOmirai : KotlinPlugin(
    JvmPluginDescription(
        id = "pers.shennoter",
        name = "UNOmirai",
        version = "0.1.0",
    ) {
        author("Shennoter")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
    }
}