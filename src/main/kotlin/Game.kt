package pers.shennoter

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import pers.shennoter.card.*

class Game(private val gameGroup: Group) : CompletableJob by SupervisorJob() {
    private lateinit var table: Table // 牌桌，内含玩家

    private lateinit var cardCollection: CardCollection // 牌库，所有的牌，后面可自定义用牌的数量

    // 只标记牌的编号
    private lateinit var cardDeck: MutableList<Int> // 牌堆
    private var cardPile = mutableListOf<Int>() // 弃牌堆，重新洗牌时用
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
        // TODO 游戏刚开始的操作，待完善
        table = Table() // 初始化牌桌，最主要是指定用多少副牌
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
                    "游戏人数已满或您已经在游戏中了，无法加入！"
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
                    when (table.isValidNumberOfPlayer()) {
                        in 0..1 -> {
                            started = true
                            prepareJob.cancel()
                        }

                        -1 -> {
                            "人数不足，无法开始\n" + "当前玩家：${table.players.map { it.nick }}"
                        }

                        else -> {
                            "人数过多，无法开始\n" + "当前玩家：${table.players.map { it.nick }}"
                        }
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
    private suspend fun distribute() {
        // 初始化牌库
        cardCollection = if (table.numOfCards == 108) {
            (Card.pointOfZero()
                    + Card.pointOfNormal() + Card.pointOfNormal()
                    + Card.functionCards() + Card.functionCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    ).toCardCollection(table.numOfCards)
        } else {
            (Card.pointOfZero() + Card.pointOfZero()
                    + Card.pointOfNormal() + Card.pointOfNormal() + Card.pointOfNormal() + Card.pointOfNormal()
                    + Card.functionCards() + Card.functionCards() + Card.functionCards() + Card.functionCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    + Card.blackCards() + Card.blackCards() + Card.blackCards() + Card.blackCards()
                    ).toCardCollection(table.numOfCards)
        }
        // 初始化牌堆
        cardDeck = (1..table.numOfCards).toMutableList()
        // 初始化手牌
        // 不能调用抽牌的函数，因为需要初始化table的handCard
        for (player in table.players) {
            val listPi = mutableListOf<Int>()
            // 初始每个人发7张牌
            for (i in 1..7) {
                val tmpIndex = cardDeck.random()
                listPi.add(tmpIndex)
                handCardPile.add(tmpIndex)
                cardDeck.remove(tmpIndex)
            }
            table.handCard.put(player, cardCollection.HandCards(listPi))
        }
        // 初始化底牌
        // 限制随机范围，使得黑卡不能作为底牌
        var tmpIndex = (1 until table.startOfBlack).random()
        while (tmpIndex !in cardDeck) tmpIndex = (1 until table.startOfBlack).random()

        table.topCard = table.getColourAndPoint(tmpIndex)
        cardPile.add(tmpIndex)
        cardDeck.remove(tmpIndex)

        // TODO 将手牌信息发送给玩家
        for (player in table.players) {
            player.sendMessage(table.handCard[player]?.cardExhibit() ?: "无法找到你，我的朋友，请重试")
        }
        play()
    }

    // 出牌阶段
    private suspend fun play() {
        var isRunning = true

        var lastCardSet = CardSet()
        var lastCombination: Combination = NotACombination

        // 玩家轮流出牌
        for (player in table.iterator()) {
            if (this.isActive)
                gameGroup.sendMessage(At(player) + "轮到你出牌了")
            val startJob = Job(this)

            val gameEventChannel = coroutineScope {
                globalEventChannel()
                    .parentJob(startJob)
                    .filterIsInstance<MessageEvent>()
                    .filter { event: MessageEvent -> event.sender.id == player.id }
            }

            /**
             * 玩家每次发送以“/“开头的消息，都视为一次出牌请求。出牌请求有多种处理结果，包括
             */
            // TODO 逻辑不太准确，需要改一下
            val job = if (this.isActive) {
                gameEventChannel.subscribeAlways<MessageEvent> playCard@{
                    // 检查玩家是否能跟牌
                    // 无法跟牌就抽一张牌
                    if (!table.handCard[player]!!.haveValidCard(table.topCard)) {
                        player.sendMessage("您无法跟牌哦")
                        table.handCard[player]!!.drawCard(1)
                        startJob.cancel()
                        return@playCard
                    }
                    // 能跟牌就选择打哪张牌
                    if (message.contentToString().startsWith('/')) {
                        val cardToPlay = message.contentToString().substring(1).trimStart('[').trimEnd(']')
                        val cardIndex: Int
                        // 检查出牌的指令是否合法
                        if (cardToPlay.matches("^\\d+$".toRegex())) {
                            cardIndex = cardToPlay.toInt()
                        } else {
                            player.sendMessage("出牌指令不合法，请重试。")
                            return@playCard
                        }
                        // 检查是否有想要出的牌
                        if (!table.handCard[player]!!.haveCard(cardIndex)) {
                            player.sendMessage("在您的手牌中没有找到您想出的牌哦")
                            return@playCard
                        }
                        // 出牌
                        val card = cardCollection[cardIndex] //玩家想出的牌
                        // TODO 可能会因莫名其妙的原因乱出了牌？在这里似乎不会出现NOT_A_CARD的情况，后续要把逻辑补上
                        if (card == Card.NOT_A_CARD || card == null) {
                            player.sendMessage("似乎没有这张牌啦")
                            return@playCard
                        }
                        /**
                         *  出牌的可能情况：
                         *  1.普通牌 -> 颜色or点数
                         *  2.功能牌 -> 颜色or点数
                         *  3.黑牌
                         */
                        if (!(card.colour == table.topCard.second || card.point == table.topCard.third || card.colour == Colour.NONE)) {
                            player.sendMessage("你出的牌不合法喔，请重新选择你的牌吧")
                            return@playCard
                        }

                    }
                }
            } else {
                return
            }
            job.join()
            if (!isRunning) {
                this.cancel()
                break
            }
        }
    }

    // 一些工具函数
    // 抽牌，包括无法出牌时的抽牌和因功能牌的抽牌
    private fun CardCollection.HandCards.drawCard(numOfDraws: Int) {
        // 抽指定的张数
        for (ithDraw in (1..numOfDraws)) {
            // 牌堆空了需要洗牌
            if (cardDeck.isEmpty()) {
                cardDeck = cardPile
                cardPile = mutableListOf<Int>()
            }
            // 抽牌
            val tmpIndex = cardDeck.random()
            this.add(tmpIndex)
            handCardPile.add(tmpIndex)
            cardDeck.remove(tmpIndex)
        }
    }

    // 将牌打出
    private fun CardCollection.HandCards.playCard(cardIndex: Int) {
        // 从手牌中打出
        this.remove(cardIndex)
        // 从手牌堆中删除
        handCardPile.remove(cardIndex)
        // 加入到弃牌堆中
        cardPile.add(cardIndex)
        // 重置顶牌
        table.topCard = table.getColourAndPoint(cardIndex)
    }
}