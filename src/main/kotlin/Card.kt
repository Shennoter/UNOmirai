package pers.shennoter


class Card(
           val point: Int,
           val type: Type,
           val colour: Colour
){
    val name = when(type){
        Type.NORMAL -> "[${colour.name} $point]"
        Type.WILD -> "[${type.name}]"
        Type.WILDDRAWFOUR -> "[${type.name}]"
        else -> "[${colour.name} ${type.name}]"
    }
}

enum class Type(name: String){
    NORMAL(""),
    REVERSE("反转"),
    SKIP("跳过"),
    DRAWTWO("+2"),
    WILD("换色"),
    WILDDRAWFOUR("王牌")
}

enum class Colour(name: String){
    NONE(""),
    RED("红"),
    YELLOW("黄"),
    BLUE("蓝"),
    GREEN("绿")
}




