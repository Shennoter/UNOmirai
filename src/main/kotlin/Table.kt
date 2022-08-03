package pers.shennoter

import net.mamoe.mirai.contact.Member
import pers.shennoter.card.HandCards

/**
 * ### 牌桌
 * 记录玩家的数据（如手牌），循环出牌
 */
class Table : Iterable<Member> {
    val players = mutableListOf<Member>() // 玩家的列表
    var playerIndex = 0 // 玩家的编号
    lateinit var handCard: MutableMap<Member, HandCards>

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
        val numberOfPlayer: Int = isValidNumberOfPlayer()
        return if (numberOfPlayer in -1..0 && player !in players) {
            players.add(player)
            true
        } else {
            print("人数已满，不能加人")
            false
        }
    }

    override fun iterator(): Iterator<Member> {
        return TableIterator(playerIndex, players)
    }

    class TableIterator(private var playerIndex: Int = 0, private val players: List<Member>) : Iterator<Member> {
        override fun hasNext(): Boolean {
            return (playerIndex < players.size)
        }

        override fun next(): Member {
            val player = players[playerIndex]
            if (hasNext()) {
                playerIndex++
            } else {
                playerIndex = 0
            }
            return player
        }
    }
}
