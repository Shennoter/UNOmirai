package pers.shennoter

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import pers.shennoter.card.*

class Game(private val gameGroup: Group) : CompletableJob by SupervisorJob() {
    private val table = Table() // 牌桌，内含玩家

    private lateinit var cardCollection: CardCollection // 牌库，所有的牌，后面可自定义用牌的数量

    // 只标记牌的编号
    private lateinit var cardDeck: MutableList<Int> // 牌堆
    private var cardPile = mutableListOf<Int>() // 弃牌堆，重新洗牌时用
    private var handCardPile = mutableListOf<Int>() // 手牌堆，目前玩家手上的牌

    private lateinit var topCard: Pair<Colour, Int> // 已打出的牌的顶部牌，记录颜色和点数

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
//            Deck().initialize()
            distribute()
            nextToPlay = playerList[0]
        } else {
            println("人数不足")
        }
    }

    // 初始发牌阶段
    private suspend fun distribute(NumOfCards: Int = 108) {
        // 初始化牌库
        if (NumOfCards == 108) {
            cardCollection = (Card.pointOfZero()
                    + Card.pointOfNormal() + Card.pointOfNormal()
                    + Card.functionCards() + Card.functionCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    ).toCardCollection(NumOfCards)
        } else {
            cardCollection = (Card.pointOfZero()
                    + Card.pointOfNormal() + Card.pointOfNormal()
                    + Card.functionCards() + Card.functionCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    + Card.pointOfZero()
                    + Card.pointOfNormal() + Card.pointOfNormal()
                    + Card.functionCards() + Card.functionCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    ).toCardCollection(NumOfCards * 2)
        }
        // 初始化牌堆
        cardDeck = (0 until NumOfCards).toMutableList()
        // 初始化手牌
        for (player in table.players) {
            val listPi = mutableListOf<Int>()
            var tmp = (0 until NumOfCards).random()
            for (i in 1..7) {
                while (tmp in listPi) tmp = (0 until NumOfCards).random()
                listPi.add(tmp)
                cardDeck.remove(tmp)
            }
            table.handCard.put(player, HandCards(listPi))
        }
        // 将手牌信息发送给玩家
        for (player in table.players) {
            player.sendMessage(cardCollection.cardExhibit(table.handCard[player]))
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
}