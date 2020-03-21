package com.atguigu.akka.yellowchicken.client

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.atguigu.akka.yellowchicken.server.YellowChickenServer.{host, port}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
import scala.sys.Prop

class CustomerActor(ServerHost:String,ServerPort:Int) extends Actor {
  //定义一个YellowChickenServerRef
  var serverActorRef : ActorSelection = _

  //在Actor中有一个方法PreSatart方法，他会在actor运行前执行
  //在akka的开发中，通常将初始化的工作，放在preStart方法
  override def preStart(): Unit = {
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${ServerHost}:${ServerPort}/user/YellowChickenServer")
    println(serverActorRef)
  }

  override def receive: Receive = {
    case "start" => println("start,客户端运行，可以咨询问题")
    case mes:String => {
      //发给小黄鸡客服
      serverActorRef ! ClientMessage(mes) //
    }
    //如果接收到服务器的回复
    case ServerMessage(mes) => {
      println(s"收到小黄鸡客服（Server）: $mes")
    }

  }
}

//主程序入口
object CustomerActor extends App{
  val (clientHost,clientPort,serverHost,serverPort) = ("127.0.0.101",9990,"127.0.0.1",9999)
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$clientHost
       |akka.remote.netty.tcp.port=$clientPort
        """.stripMargin)//把边界去掉，默认|
  val clientActorSystem = ActorSystem("client",config)
  //创建实例和引用
  val customerActorRef: ActorRef = clientActorSystem.actorOf(Props(new CustomerActor(serverHost,serverPort)),"CustomerActor")
  //启动CustomerRef
  customerActorRef !  "start"

  //客户端可以发送信息给服务器
  while(true){
    println("请输入要咨询的问题")
    val mes = StdIn.readLine()
    customerActorRef ! mes
  }



}