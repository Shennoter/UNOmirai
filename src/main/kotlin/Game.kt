package pers.shennoter

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import pers.shennoter.Type.*
import java.util.Stack

class Game(private val gameGroup: Group) : CompletableJob by SupervisorJob() {
    private var nextToPlay: Player? = null // 下一个出牌的玩家
    private val playerList = mutableListOf<Player>() // 已加入游戏的玩家
    private val cardStack = Stack<Card>() // 已打乱的牌堆

    fun addPlayer(name: String) {
        playerList.add(Player(name))
    }

    fun rmvPlayer(name: String) {
        playerList.remove(Player(name))
    }

    suspend fun start() {
        coroutineScope {
            val channel = globalEventChannel()
                .parentJob(this@Game)
                .filterIsInstance<GroupMessageEvent>()
                .filter { event: GroupMessageEvent ->
                    event.group == gameGroup
                }
        }
        if (playerList.size > 1) {
            Deck().initialize()
            distribute()
            nextToPlay = playerList[0]
        } else {
            println("人数不足")
        }
    }

    private fun distribute() { // 发牌
        playerList.forEach {
            it.draw(7, true)
        }
    }

    inner class Player(private val name: String) {
        private val hand: MutableList<Card> = mutableListOf() // 手牌

        fun view() { // 查看手牌
            print("手牌：\n")
            hand.forEach {
                print((hand.indexOf(it) + 1).toString() + ". " + it.name + '\n')
            }
        }

        fun draw(num: Int, isInit: Boolean) { // 抽牌
            for (i in 0 until num) {
                hand.add(cardStack.pop())
            }
            if (!isInit) {
                println("$name 手牌数量：${hand.size}")
            }
        }

        fun play(index: Int) { // 打牌
            println("$name 打出 ${hand[index - 1].name}")
            when (hand[index - 1].type) {
                REVERSE -> {

                }

                SKIP -> {

                }

                DRAWTWO -> {

                }

                WILD -> {

                }

                WILDDRAWFOUR -> {

                }

                NORMAL -> {

                }
            }
            hand.removeAt(index - 1)
            println("$name 手牌数量：${hand.size}")
        }
    }

    private inner class Deck { // 牌堆
        private var cardList = listOf<Card>()
        fun initialize() {

            cardList = cardList.shuffled() // 打乱顺序
            cardList.forEach {// 加入牌堆
                cardStack.push(it)
            }
        }
    }
}