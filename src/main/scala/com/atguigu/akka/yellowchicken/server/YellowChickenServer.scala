package com.atguigu.akka.yellowchicken.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.atguigu.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

class YellowChickenServer extends Actor{
  override def receive: Receive = {
    case "start" => println("start 小黄鸡客服开始工作了。。。")
    case ClientMessage(msg) => {
      //使用match -- case 匹配（模糊）
      msg match {
        case "大数据学费" => sender() ! ServerMessage("3500")
        case _ => sender() ! ServerMessage("听不懂")
      }
    }

  }
}


//主程序-入口
object YellowChickenServer extends App{

  val host = "127.0.0.1" //服务端 ip 地址
  val port = 9999
  //创建 config 对象,指定协议类型，监听的 ip 和端口
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
        """.stripMargin)

  //统一资源定位
  val serverActorSystem = ActorSystem("Server",config)
  //创建YellowChickenServer的actor和actorRef(actor的代理或引用)
  val yellowChickenServerRef: ActorRef = serverActorSystem.actorOf(Props[YellowChickenServer],"YellowChickenServer")

  yellowChickenServerRef ! "start"

}
