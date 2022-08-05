package pers.shennoter.card


/**
 * ### 牌的集合
 * 牌堆、废牌堆、手牌
 * */

//所有牌的集合，用Map存，后续手牌和牌堆都只用一个Set来存牌的编号就好（即Map的键）
class CardCollection : HashMap<Int, Card> {
    var NumOfCards = 108

    constructor(vararg cards: Card, NumOfCards: Int = 108) : super(
        (0 until NumOfCards).toList().zip(cards.toList()).toMap()
    ) {
        this.NumOfCards = NumOfCards
    }

    constructor(cards: List<Card>, NumOfCards: Int = 108) : super(
        (0 until NumOfCards).toList().zip(cards).toMap()
    ) {
        this.NumOfCards = NumOfCards
    }


    inner class HandCards : ArrayList<Int> {
        constructor(vararg cardsNum: Int) : super(cardsNum.toList())
        constructor(cards: List<Int>) : super(cards)

        // 用于展示手牌
        fun cardExhibit(): String {
            // 设置单张牌如何显示，对各种牌各有不同的显示。如：
            // 普通牌：[0][红 5]
            // 换色牌：[1][换色]（王牌：[2][王牌]） // 这两个都是黑色牌
            // 其他功能牌：[3][蓝 +2]
            // 会按照牌的编号顺序输出
            val cardIndexSorted = this@HandCards.sorted()
            var exhibition = ""

            for (idx in cardIndexSorted) {
                val card = this@CardCollection[idx]
                val name = when (card?.type) {
                    Type.NORMAL -> "[$idx][${card.colour.name} ${card.point}]\n"
                    Type.WILD, Type.WILDDRAWFOUR -> "[$idx][${card.type.name}]\n"
                    Type.DRAWTWO, Type.REVERSE, Type.SKIP -> "[$idx][${card.colour.name} ${card.type.name}]\n"
                    else -> "[$idx][Not A Card]\n"
                }
                exhibition += name
            }
            return exhibition
        }

        // 判断玩家是否有该牌
        fun haveCard(cardIndex: Int): Boolean {
            return if (this.contains(cardIndex)) {
                true
            } else {
                print("没在您的手牌中找到您想出的牌")
                false
            }
        }

        // 检测玩家是否有与底牌同色or同点数的牌
        fun haveValidCard(topCard: Triple<Int, Colour, Int>): Boolean {
            // 遍历实现，是否有更好的方法？
            for (cardIndex in this@HandCards) {
                val card = this@CardCollection[cardIndex]
                if (card != null) {
                    if (topCard.second == card.colour || topCard.third == card.point) {
                        return true
                    }
                }
            }
            return false
        }

    }

}

fun List<Card>.toCardCollection(NumOfCards: Int = 108): CardCollection = CardCollection(*this.toTypedArray())


