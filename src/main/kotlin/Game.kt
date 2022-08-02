package pers.shennoter

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import pers.shennoter.Type.*
import java.util.Stack

class Game(private val gameGroup: Group) : CompletableJob by SupervisorJob(){
    private var nextToPlay: Player? = null
    private val playerList = mutableListOf<Player>()
    private val cardStack = Stack<Card>()

    fun addPlayer(name:String){
        playerList.add(Player(name))
    }

    fun rmvPlayer(name:String){
        playerList.remove(Player(name))
    }

    suspend fun start(){
        coroutineScope {
            val channel = globalEventChannel()
                .parentJob(this@Game)
                .filterIsInstance<GroupMessageEvent>()
                .filter { event: GroupMessageEvent ->
                    event.group == gameGroup
                }
        }
        if(playerList.size > 1) {
            Deck().initialize()
            distribute()
            nextToPlay = playerList[0]
        }
        else{
            println("人数不足")
        }
    }

    private fun distribute(){ // 发牌
        playerList.forEach{
            it.draw(7, true)
        }
    }

    inner class Player(private val name: String){
        private val hand: MutableList<Card> = mutableListOf()

        fun view(){ // 查看手牌
            print("手牌：\n")
            hand.forEach{
                print((hand.indexOf(it) + 1).toString() + ". " + it.name + '\n')
            }
        }

        fun draw(num: Int, isInit: Boolean) { // 抽牌
            for(i in 0 until num){
                hand.add(cardStack.pop())
            }
            if(!isInit){
                println("$name 手牌数量：${hand.size}")
            }
        }

        fun play(index: Int){ // 打牌
            println("$name 打出 ${hand[index - 1].name}")
            when(hand[index - 1].type){
                REVERSE ->{

                }
                SKIP ->{

                }
                DRAWTWO ->{

                }
                WILD ->{

                }
                WILDDRAWFOUR ->{

                }
                NORMAL -> {

                }
            }
            hand.removeAt(index - 1)
            println("$name 手牌数量：${hand.size}")
        }
    }

    private inner class Deck{
        private var cardList = listOf<Card>()
        fun initialize(){
            cardList = listOf(
                Card(0, NORMAL, Colour.RED),
                Card(0, NORMAL, Colour.BLUE),
                Card(0, NORMAL, Colour.YELLOW),
                Card(0, NORMAL, Colour.GREEN),
                Card(1, NORMAL, Colour.RED),
                Card(1, NORMAL, Colour.RED),
                Card(1, NORMAL, Colour.BLUE),
                Card(1, NORMAL, Colour.BLUE),
                Card(1, NORMAL, Colour.YELLOW),
                Card(1, NORMAL, Colour.YELLOW),
                Card(1, NORMAL, Colour.GREEN),
                Card(1, NORMAL, Colour.GREEN),
                Card(2, NORMAL, Colour.RED),
                Card(2, NORMAL, Colour.RED),
                Card(2, NORMAL, Colour.BLUE),
                Card(2, NORMAL, Colour.BLUE),
                Card(2, NORMAL, Colour.YELLOW),
                Card(2, NORMAL, Colour.YELLOW),
                Card(2, NORMAL, Colour.GREEN),
                Card(2, NORMAL, Colour.GREEN),
                Card(3, NORMAL, Colour.RED),
                Card(3, NORMAL, Colour.RED),
                Card(3, NORMAL, Colour.BLUE),
                Card(3, NORMAL, Colour.BLUE),
                Card(3, NORMAL, Colour.YELLOW),
                Card(3, NORMAL, Colour.YELLOW),
                Card(3, NORMAL, Colour.GREEN),
                Card(3, NORMAL, Colour.GREEN),
                Card(4, NORMAL, Colour.RED),
                Card(4, NORMAL, Colour.RED),
                Card(4, NORMAL, Colour.BLUE),
                Card(4, NORMAL, Colour.BLUE),
                Card(4, NORMAL, Colour.YELLOW),
                Card(4, NORMAL, Colour.YELLOW),
                Card(4, NORMAL, Colour.GREEN),
                Card(4, NORMAL, Colour.GREEN),
                Card(5, NORMAL, Colour.RED),
                Card(5, NORMAL, Colour.RED),
                Card(5, NORMAL, Colour.BLUE),
                Card(5, NORMAL, Colour.BLUE),
                Card(5, NORMAL, Colour.YELLOW),
                Card(5, NORMAL, Colour.YELLOW),
                Card(5, NORMAL, Colour.GREEN),
                Card(5, NORMAL, Colour.GREEN),
                Card(6, NORMAL, Colour.RED),
                Card(6, NORMAL, Colour.RED),
                Card(6, NORMAL, Colour.BLUE),
                Card(6, NORMAL, Colour.BLUE),
                Card(6, NORMAL, Colour.YELLOW),
                Card(6, NORMAL, Colour.YELLOW),
                Card(6, NORMAL, Colour.GREEN),
                Card(6, NORMAL, Colour.GREEN),
                Card(7, NORMAL, Colour.RED),
                Card(7, NORMAL, Colour.RED),
                Card(7, NORMAL, Colour.BLUE),
                Card(7, NORMAL, Colour.BLUE),
                Card(7, NORMAL, Colour.YELLOW),
                Card(7, NORMAL, Colour.YELLOW),
                Card(7, NORMAL, Colour.GREEN),
                Card(7, NORMAL, Colour.GREEN),
                Card(8, NORMAL, Colour.RED),
                Card(8, NORMAL, Colour.RED),
                Card(8, NORMAL, Colour.BLUE),
                Card(8, NORMAL, Colour.BLUE),
                Card(8, NORMAL, Colour.YELLOW),
                Card(8, NORMAL, Colour.YELLOW),
                Card(8, NORMAL, Colour.GREEN),
                Card(8, NORMAL, Colour.GREEN),
                Card(9, NORMAL, Colour.RED),
                Card(9, NORMAL, Colour.RED),
                Card(9, NORMAL, Colour.BLUE),
                Card(9, NORMAL, Colour.BLUE),
                Card(9, NORMAL, Colour.YELLOW),
                Card(9, NORMAL, Colour.YELLOW),
                Card(9, NORMAL, Colour.GREEN),
                Card(9, NORMAL, Colour.GREEN),
                Card(-1, REVERSE, Colour.RED),
                Card(-1, REVERSE, Colour.RED),
                Card(-1, REVERSE, Colour.BLUE),
                Card(-1, REVERSE, Colour.BLUE),
                Card(-1, REVERSE, Colour.YELLOW),
                Card(-1, REVERSE, Colour.YELLOW),
                Card(-1, REVERSE, Colour.GREEN),
                Card(-1, REVERSE, Colour.GREEN),
                Card(-1, SKIP, Colour.RED),
                Card(-1, SKIP, Colour.RED),
                Card(-1, SKIP, Colour.BLUE),
                Card(-1, SKIP, Colour.BLUE),
                Card(-1, SKIP, Colour.YELLOW),
                Card(-1, SKIP, Colour.YELLOW),
                Card(-1, SKIP, Colour.GREEN),
                Card(-1, SKIP, Colour.GREEN),
                Card(-1, DRAWTWO, Colour.RED),
                Card(-1, DRAWTWO, Colour.RED),
                Card(-1, DRAWTWO, Colour.BLUE),
                Card(-1, DRAWTWO, Colour.BLUE),
                Card(-1, DRAWTWO, Colour.YELLOW),
                Card(-1, DRAWTWO, Colour.YELLOW),
                Card(-1, DRAWTWO, Colour.GREEN),
                Card(-1, DRAWTWO, Colour.GREEN),
                Card(-1, WILD, Colour.NONE),
                Card(-1, WILD, Colour.NONE),
                Card(-1, WILD, Colour.NONE),
                Card(-1, WILD, Colour.NONE),
                Card(-1, WILDDRAWFOUR, Colour.NONE),
                Card(-1, WILDDRAWFOUR, Colour.NONE),
                Card(-1, WILDDRAWFOUR, Colour.NONE),
                Card(-1, WILDDRAWFOUR, Colour.NONE)
            )
            cardList = cardList.shuffled() // 打乱顺序
            cardList.forEach{// 加入牌堆
                cardStack.push(it)
            }
        }
    }
}