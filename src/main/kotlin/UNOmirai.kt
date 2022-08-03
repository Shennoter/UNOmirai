package pers.shennoter

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info

object UNOmirai : KotlinPlugin(
    JvmPluginDescription(
        id = "pers.shennoter",
        name = "UNOmirai",
        version = "0.1.0",
    ) {
        author("Shennoter")
        author("Lynchrocket")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        startListen()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun startListen() {
    GlobalEventChannel.parentScope(GlobalScope).subscribeAlways<GroupMessageEvent> { event ->
        if (event.message.content == "开始UNO") {
            val game = Game()
            game.start()
        }
    }
}
