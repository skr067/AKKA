package com.atguigu.akka.actor

import akka.actor.{Actor,ActorRef, ActorSystem, Props}

//1.继承Actor后，核心方法receive 方法重写
class SayHelloActor extends Actor{
  //1. receive 方法，会被该 Actor 的 MailBox(实现了 Runnable 接口)调用
  //2. 当该 Actor 的 MailBox 接收到消息,就会调用 receive
  //3. type Receive = PartialFunction[Any, Unit]
  override def receive: Receive = {
    case "hello" => println("收到hello,回应hello too:)")
    case "ok" => println("收到ok,回应ok too:)")
    case "exit" => {
      println("接收到 exit 指令，退出系统")
      context.stop(self)
      context.system.terminate()//退出actorsystem

    }
    case _ => println("匹配不到")

  }
}

object SayHelloActorDemo{
  //1. 先创建一个 ActorSystem, 专门用于创建 Actor
  private val actoryFactory = ActorSystem("actoryFactory")
  //2. 创建一个 Actor 的同时，返回 Actor 的 ActorRef
  //(1) Props[SayHelloActor] 创建了一个 SayHelloActor 实例，使用反射
  //(2) "sayHelloActor" 给 actor 取名
  //(3) sayHelloActorRef:ActorRef 就是 Props[SayHelloActor] 的 ActorRef
  //(4) 创建的 SayHelloActor 实例被 ActorSystme 接管
  private val sayHelloActorRef : ActorRef = actoryFactory.actorOf(Props[SayHelloActor],"sayHelloActor1")

  def main(args: Array[String]): Unit = {
    //给 SayHelloActor 发消息(邮箱)
    sayHelloActorRef ! "hello"
    sayHelloActorRef ! "ok"
    sayHelloActorRef ! "ok~"
    //研究异步如何退出 ActorSystem
    sayHelloActorRef ! "exit"
  }

}
