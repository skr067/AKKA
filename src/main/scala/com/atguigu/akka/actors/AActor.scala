package com.atguigu.akka.actors

import akka.actor.Actor
import akka.remote.ContainerFormats.ActorRef

class AActor(actorRef: ActorRef) extends  Actor{
  val bActorRef: ActorRef = actorRef
  override def receive: Receive = {
    case "start" => {
      println("AActor 出招了 , start ok")
      self ! "我打"
    }
    case "我打" => {
      //给 BActor 发出消息
        //这里需要持有 BActor 的引用(BActorRef)
        println("AActor(黄飞鸿) 厉害 看我佛山无影脚")
        Thread.sleep(100)
        //bActorRef ! "我打"
    }
  }
}
