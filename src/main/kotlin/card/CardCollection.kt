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