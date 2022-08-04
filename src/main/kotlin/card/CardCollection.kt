package pers.shennoter.card

/**
 * ### 牌的集合
 * 牌堆、废牌堆、手牌
 * */

//所有牌的集合，用Map存，后续手牌和牌堆都只用一个Set来存牌的编号就好（即Map的键）
class CardCollection : HashMap<Int, Card> {
    constructor(vararg cards: Card, NumOfCards: Int = 108) : super(
        (0 until NumOfCards).toList().zip(cards.toList()).toMap()
    )

    constructor(cards: List<Card>, NumOfCards: Int = 108) : super((0 until NumOfCards).toList().zip(cards).toMap())

    // 用于展示手牌
    fun cardExhibit(cardIndex: HandCards): String {
        // 设置单张牌如何显示，对各种牌各有不同的显示。如：
        // 普通牌：[0][红 5]
        // 换色牌：[1][换色]（王牌：[2][王牌]） // 这两个都是黑色牌
        // 其他功能牌：[3][蓝 +2]
        // 会按照牌的编号输出
        val cardIndexSorted = cardIndex.sorted()
        var exhibition = ""
        for (idx in cardIndexSorted) {
            val card = this[idx]
            val name = when (card?.type) {
                Type.NORMAL -> "[$idx][${card.colour.name} ${card.point}]\n"
                Type.WILD, Type.WILDDRAWFOUR -> "[$idx][${card.type.name}]\n"
                else -> "[$idx][${card?.colour?.name} ${card?.type?.name}]\n"
            }
            exhibition += name
        }
        return exhibition
    }
}

fun List<Card>.toCardCollection(NumOfCards: Int = 108): CardCollection = CardCollection(*this.toTypedArray())

class HandCards : ArrayList<Int> {
    constructor(vararg cardsNum: Int) : super(cardsNum.toList())
    constructor(cards: List<Int>) : super(cards)

    // 出牌
    fun play() {
        this.forEach {

        }
    }
}