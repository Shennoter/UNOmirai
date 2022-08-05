package pers.shennoter.card

/**
 * ### 牌
 * 各种关于牌的枚举类
 * */

// 牌的种类
enum class Type(name: String) {
    NORMAL("普通"),
    SKIP("禁出"),
    REVERSE("反转"),
    DRAWTWO("+2"),
    WILD("换色"),
    WILDDRAWFOUR("王牌"),
    NOT_A_CARD("非法")
}

// 牌的颜色
enum class Colour(name: String) {
    NONE("黑"),
    RED("红"),
    YELLOW("黄"),
    BLUE("蓝"),
    GREEN("绿"),
    NOT_A_CARD("非法")
}

// 按顺序的108张牌
// 由于枚举类，实际上只有56张，实际时要看情况加牌
enum class Card(
    val type: Type, // 牌的种类
    val colour: Colour, // 牌的颜色
    val point: Int // 牌的点数
) {
    /**
     * ### 各种功能牌解释
     * 1.“Draw 2 Cards”下一个玩家从牌堆顶摸两张牌，并跳过一个回合。当第一张底牌为他时，起始玩家摸两张牌。
     * 2.“Reverse Card”出牌顺序颠倒，所有人的上下家互换。当第一张底牌为他时，起始玩家变为原起始者右手第一个玩家，并执行卡牌效果。
     * 3.“Skip Card”下一个玩家停止一个回合。当第一张底牌为他时，起始玩家停止一个回合，由由其左手第一个玩家先开始。
     * 4.“Wild Card”下一个玩家改为出你指定颜色的卡片。当第一张底牌为他时，起始玩家左手边第一个玩家可以指定一种颜色作为底牌。
     * 5.“Wild Draw 4 Card ”下一位玩家摸四张牌并停止一个回合，之后的玩家改为出你指定颜色的卡片。当第一张底牌为他时，将这张牌洗入牌堆并重新摸一张底牌。
     * */
    // 普通牌：
    // 19张红色卡片(0－9)
    // 19张黄色卡片(0－9)
    // 19张蓝色卡片(0－9)
    // 19张绿色卡片(0－9)
    // 0每色各1张，1-9后面需x2
    Nr0(Type.NORMAL, Colour.RED, 0),
    Nr1(Type.NORMAL, Colour.RED, 1),
    Nr2(Type.NORMAL, Colour.RED, 2),
    Nr3(Type.NORMAL, Colour.RED, 3),
    Nr4(Type.NORMAL, Colour.RED, 4),
    Nr5(Type.NORMAL, Colour.RED, 5),
    Nr6(Type.NORMAL, Colour.RED, 6),
    Nr7(Type.NORMAL, Colour.RED, 7),
    Nr8(Type.NORMAL, Colour.RED, 8),
    Nr9(Type.NORMAL, Colour.RED, 9),
    Ny0(Type.NORMAL, Colour.YELLOW, 0),
    Ny1(Type.NORMAL, Colour.YELLOW, 1),
    Ny2(Type.NORMAL, Colour.YELLOW, 2),
    Ny3(Type.NORMAL, Colour.YELLOW, 3),
    Ny4(Type.NORMAL, Colour.YELLOW, 4),
    Ny5(Type.NORMAL, Colour.YELLOW, 5),
    Ny6(Type.NORMAL, Colour.YELLOW, 6),
    Ny7(Type.NORMAL, Colour.YELLOW, 7),
    Ny8(Type.NORMAL, Colour.YELLOW, 8),
    Ny9(Type.NORMAL, Colour.YELLOW, 9),
    Nb0(Type.NORMAL, Colour.BLUE, 0),
    Nb1(Type.NORMAL, Colour.BLUE, 1),
    Nb2(Type.NORMAL, Colour.BLUE, 2),
    Nb3(Type.NORMAL, Colour.BLUE, 3),
    Nb4(Type.NORMAL, Colour.BLUE, 4),
    Nb5(Type.NORMAL, Colour.BLUE, 5),
    Nb6(Type.NORMAL, Colour.BLUE, 6),
    Nb7(Type.NORMAL, Colour.BLUE, 7),
    Nb8(Type.NORMAL, Colour.BLUE, 8),
    Nb9(Type.NORMAL, Colour.BLUE, 9),
    Ng0(Type.NORMAL, Colour.GREEN, 0),
    Ng1(Type.NORMAL, Colour.GREEN, 1),
    Ng2(Type.NORMAL, Colour.GREEN, 2),
    Ng3(Type.NORMAL, Colour.GREEN, 3),
    Ng4(Type.NORMAL, Colour.GREEN, 4),
    Ng5(Type.NORMAL, Colour.GREEN, 5),
    Ng6(Type.NORMAL, Colour.GREEN, 6),
    Ng7(Type.NORMAL, Colour.GREEN, 7),
    Ng8(Type.NORMAL, Colour.GREEN, 8),
    Ng9(Type.NORMAL, Colour.GREEN, 9),

    // 功能牌，在牌局中这些牌的数量都要x2
    // 禁止行动
    Sr(Type.SKIP, Colour.RED, -1),
    Sy(Type.SKIP, Colour.YELLOW, -1),
    Sb(Type.SKIP, Colour.BLUE, -1),
    Sg(Type.SKIP, Colour.GREEN, -1),

    // 顺序颠倒
    Rr(Type.REVERSE, Colour.RED, -2),
    Ry(Type.REVERSE, Colour.YELLOW, -2),
    Rb(Type.REVERSE, Colour.BLUE, -2),
    Rg(Type.REVERSE, Colour.GREEN, -2),

    // 多摸两张
    Dr(Type.DRAWTWO, Colour.RED, -3),
    Dy(Type.DRAWTWO, Colour.YELLOW, -3),
    Db(Type.DRAWTWO, Colour.BLUE, -3),
    Dg(Type.DRAWTWO, Colour.GREEN, -3),

    // 黑牌，在牌局中这些牌的数量都要x4
    // 万能卡
    Wn(Type.WILD, Colour.NONE, -4),

    // 万能且摸四张
    WDFn(Type.WILDDRAWFOUR, Colour.NONE, -5),

    // 乱出牌的类型
    NOT_A_CARD(Type.NOT_A_CARD, Colour.NOT_A_CARD, 100);

    internal companion object {
        // 每种牌的列表，方便后续转成Map
        internal fun pointOfZero(): MutableList<Card> =
            mutableListOf<Card>(
                Nr0, Ny0, Nb0, Ng0
            )

        internal fun pointOfNormal(): MutableList<Card> =
            mutableListOf<Card>(
                Nr1, Ny1, Nb1, Ng1,
                Nr2, Ny2, Nb2, Ng2,
                Nr3, Ny3, Nb3, Ng3,
                Nr4, Ny4, Nb4, Ng4,
                Nr5, Ny5, Nb5, Ng5,
                Nr6, Ny6, Nb6, Ng6,
                Nr7, Ny7, Nb7, Ng7,
                Nr8, Ny8, Nb8, Ng8,
                Nr9, Ny9, Nb9, Ng9
            )

        internal fun functionCards(): MutableList<Card> =
            mutableListOf<Card>(
                Sr, Sy, Sb, Sg,
                Rr, Ry, Rb, Rg,
                Dr, Dy, Db, Dg
            )

        internal fun blackCards(): MutableList<Card> =
            mutableListOf<Card>(
                Wn, WDFn
            )
    }
}






