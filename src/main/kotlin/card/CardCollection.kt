package pers.shennoter.card

/**
 * ### 牌的集合
 * 牌堆、废牌堆、手牌
 * */

class CardCollection : HashMap<Int, Card> {
    constructor(vararg cards: Card) : super(cards.toList().associateBy({ it.No }, { it }))
    constructor(cards: List<Card>) : super(cards.associateBy({ it.No }, { it }))


}

class HandCards : ArrayList<Int> {
    constructor(vararg cardsNum: Int) : super(cardsNum.toList())
    constructor(cards: List<Int>) : super(cards)

    fun cardExhibit() {
        // 设置单张牌如何显示，对各种牌各有不同的显示。如：
        // 普通牌：[红 5]
        // 换色牌：[换色]（王牌：[王牌]） // 这两个都是黑色牌
        // 其他牌：[蓝 +2]
        val name = when (type) {
            Type.NORMAL -> "[${colour.name} $point]"
            Type.WILD, Type.WILDDRAWFOUR -> "[${type.name}]"
            else -> "[${colour.name} ${type.name}]"
        }
    }
}