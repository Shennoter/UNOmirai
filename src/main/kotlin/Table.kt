package pers.shennoter

import net.mamoe.mirai.contact.Member
import pers.shennoter.card.CardCollection
import pers.shennoter.card.Colour

/**
 * ### 牌桌
 * 记录玩家的数据（如手牌）、行为，循环出牌
 */
class Table(
    val numOfCards: Int = 108 // 牌的数量，默认是108张，即一副牌
) {
    private val numOfBlack = (numOfCards % 100) // 黑牌的数量
    val startOfNum = (numOfBlack shr 1) + 1 // 普通牌开始的编号（除面值为0的牌）
    val startOfFunc = startOfNum + (numOfBlack shl 3) + numOfBlack // 功能牌开始的编号 startOfNum + (numOfBlack * 9)
    val startOfBlack = numOfCards - numOfBlack + 1 // 黑牌的开始编号

    val players = mutableListOf<Member>() // 玩家的列表
    private var playerIndex = 0 // 玩家的编号，需要严格保证范围在0 until players.size
    private var handCard = mutableMapOf<Member, CardCollection.HandCards>() // 玩家和手牌

    lateinit var topCard: Triple<Int, Colour, Int> // 已打出的牌的顶部牌，记录编号、颜色、点数

    var isReverse = false
    var isSkip = false

    // 通过牌的编号直接获取牌的颜色和点数
    fun getColourAndPoint(cardIndex: Int): Triple<Int, Colour, Int> {
        val cardColour: Colour
        var cardPoint: Int
        when (cardIndex) {
            in 1 until startOfNum -> {
                cardPoint = 0
                cardColour = when (cardIndex % 4) {
                    1 -> Colour.RED
                    2 -> Colour.YELLOW
                    3 -> Colour.BLUE
                    0 -> Colour.GREEN
                    else -> Colour.NOT_A_CARD
                }
            }

            in startOfNum until startOfFunc -> {
                cardPoint = (cardIndex - startOfNum + 1) % 9
                if (cardPoint == 0) cardPoint = 9
                cardColour = when ((cardIndex - startOfNum + 1) % 4) {
                    1 -> Colour.RED
                    2 -> Colour.YELLOW
                    3 -> Colour.BLUE
                    0 -> Colour.GREEN
                    else -> Colour.NOT_A_CARD
                }
            }

            in startOfFunc until startOfBlack -> {
                cardPoint = -((cardIndex - startOfFunc + 1) % 3)
                if (cardPoint == 0) cardPoint = -3
                cardColour = when ((cardIndex - startOfFunc + 1) % 4) {
                    1 -> Colour.RED
                    2 -> Colour.YELLOW
                    3 -> Colour.BLUE
                    0 -> Colour.GREEN
                    else -> Colour.NOT_A_CARD
                }
            }

            in startOfBlack..numOfCards -> {
                cardPoint = ((cardIndex - startOfBlack + 1) % 2) - 5
                cardColour = Colour.NONE
            }

            else -> {
                cardPoint = 100
                cardColour = Colour.NOT_A_CARD
            }
        }
        return Triple(cardIndex, cardColour, cardPoint)
    }

    // 判断人数是否在合理范围内
    fun isValidNumberOfPlayer(): Int {
        return if (players.size < 2) {
            -1 //人数过少
        } else if (players.size in 2..9) {
            0 // 人数合理，且可以加人
        } else if (players.size == 10) {
            1 // 人数合理，但是不可加人
        } else {
            2 // 人数过多
        }
    }

    // 进入房间
    fun enter(player: Member): Boolean {
        val validCheck = isValidNumberOfPlayer()
        return if (validCheck in -1..0 && player !in players) {
            players.add(player)
            true
        } else {
            false
        }
    }

    // 获取当前回合的玩家及其下家
    fun getPlayerAndNext(): Pair<Member, Member> {
        if (isReverse) {
            if (playerIndex <= 0) {
                return Pair(players[playerIndex], players[players.size - 1])
            }
            return Pair(players[playerIndex], players[playerIndex - 1])
        } else {
            if (playerIndex >= players.size - 1) {
                return Pair(players[playerIndex], players[0])
            }
            return Pair(players[playerIndex], players[playerIndex + 1])
        }
    }

    // 设置下一个玩家
    // 主要用于+2和+4的处理
    fun setNextPlayer() {
        if (isReverse) {
            if (isSkip) {
                if (players.size >= 3) {
                    when (playerIndex) {
                        players.size - 1 -> playerIndex = 1
                        players.size - 2 -> playerIndex = 0
                        else -> playerIndex += 2
                    }
                }
                isSkip = false
            } else {
                if (playerIndex <= 0) {
                    playerIndex = players.size - 1
                } else {
                    playerIndex -= 1
                }
            }
        } else {
            if (isSkip) {
                if (players.size >= 3) {
                    when (playerIndex) {
                        players.size - 1 -> playerIndex = 1
                        players.size - 2 -> playerIndex = 0
                        else -> playerIndex += 2
                    }
                }
                isSkip = false
            } else {
                if (playerIndex >= players.size - 1) {
                    playerIndex = 0
                } else {
                    playerIndex += 1
                }
            }
        }
    }

    // 添加玩家及其手牌
    fun addHandCards(player: Member, handCards: CardCollection.HandCards) {
        handCard.put(player, handCards)
    }

    // 获取指定玩家的手牌
    fun getHandCards(player: Member): CardCollection.HandCards {
        return handCard[player]!!
    }


}
