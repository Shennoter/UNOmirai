package pers.shennoter

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import pers.shennoter.card.*

class Game(private val gameGroup: Group) : CompletableJob by SupervisorJob() {
    private val table = Table() // 牌桌，内含玩家

    private lateinit var cardCollection: CardCollection // 牌库，所有的牌，后面可自定义用牌的数量

    // 只标记牌的编号
    private lateinit var cardDeck: MutableList<Int> // 牌堆
    private val cardPile = mutableListOf<Int>() // 弃牌堆，重新洗牌时用
    private val handCardPile = mutableListOf<Int>() // 手牌堆，目前玩家手上的牌

    suspend fun start() {
        coroutineScope {
            val channel = globalEventChannel()
                .parentJob(this@Game)
                .filterIsInstance<GroupMessageEvent>()
                .filter { event: GroupMessageEvent ->
                    event.group == gameGroup
                }
        }
        prepare()
    }

    private suspend fun prepare() {
        var started = false

        val prepareJob = Job(this)
        val scopedChannel = coroutineScope {
            globalEventChannel().parentJob(prepareJob)
                .filterIsInstance<GroupMessageEvent>()
                .filter { event: GroupMessageEvent -> event.group == gameGroup }
        }
        val job = scopedChannel.subscribeGroupMessages {
            (case("上桌") and sentFrom(gameGroup)) reply {
                if (table.enter(sender)) {
                    "加入成功\n当前玩家：${table.players.map { it.nick }}"
                } else {
                    "游戏人数已满或你已经在游戏中了，无法加入！"
                }
            }
            case("下桌") {
                if (sender in table.players) {
                    table.players.remove(sender)
                    subject.sendMessage("<${sender.nick}>下桌成功")
                }
            }
            case("开始游戏") reply {
                if (sender in table.players) {
                    val validCheck = table.isValidNumberOfPlayer()
                    if (validCheck in 0..1) {
                        started = true
                        prepareJob.cancel()
                    } else if (validCheck == -1) {
                        "人数不足，无法开始\n" + "当前玩家：${table.players.map { it.nick }}"
                    } else {
                        "人数过多，无法开始\n" + "当前玩家：${table.players.map { it.nick }}"
                    }
                }
            }
        }
        //等待job结束，即成功开始游戏
        job.join()
        if (started) distribute()
    }

    // 初始发牌阶段
    // 初始牌库、牌堆、手牌、底牌
    // 给每位玩家各分发7张手牌
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
        var tmpIndex = (0 until NumOfCards).random()
        for (player in table.players) {
            val listPi = mutableListOf<Int>()
            // 初始每个人发7张牌
            for (i in 1..7) {
                while (tmpIndex !in cardDeck) tmpIndex = (0 until NumOfCards).random()
                listPi.add(tmpIndex)
                handCardPile.add(tmpIndex)
                cardDeck.remove(tmpIndex)
            }
            table.handCard.put(player, cardCollection.HandCards(listPi))
        }
        // 初始化底牌
        while (true) { // 防止tmpIndex出意外生成无效编号（似乎并不会）
            tmpIndex = (0 until NumOfCards).random()
            while (tmpIndex !in cardDeck) tmpIndex = (0 until NumOfCards).random()
            val tmpTopCard = cardCollection[tmpIndex]
            if (tmpTopCard != null) {
                if (tmpTopCard.colour == Colour.NONE) continue
                table.topCard = Triple(tmpIndex, tmpTopCard.colour, tmpTopCard.point)
            } else {
                continue
            }
            cardPile.add(tmpIndex)
            cardDeck.remove(tmpIndex)
            break
        }

        // TODO 将手牌信息发送给玩家
        for (player in table.players) {
            player.sendMessage(table.handCard[player]?.cardExhibit() ?: "无法找到你，请重试")
        }
        play()
    }

    // 出牌阶段
    private suspend fun play() {
        for (player in table.iterator()) {

        }
    }

}