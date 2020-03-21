package com.atguigu.akka.sparkmasterworker.worker

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, RegisteredWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
class SparkWorker(masterHost:String,masterPort:Int,masterName:String) extends Actor {
  //masterProxy是Master的代理引用ref
  var masterPorxy : ActorSelection = _
  val id = java.util.UUID.randomUUID().toString

  override def preStart() : Unit = {
    //初始化masterProxy
    masterPorxy = context.actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/${masterName}")
    println(masterPorxy)
  }
  override def receive: Receive = {
    case "start" => {
      println("worker启动了")
      //发出一个注册消息
      masterPorxy !  RegisterWorkerInfo(id,16,16*1024)
    }
    case RegisteredWorkerInfo => {
      println("workerid="+id+"注册成功了")
      //当注册成功后，就定义一个定时器，每隔一定时间，发送SendHeartBeat给自己
      //                                立即执行   每隔3秒      自己     内容
      import context.dispatcher
      context.system.scheduler.schedule(0 millis,3000 millis,self,SendHeartBeat)
    }
    case SendHeartBeat => {
      println("worker="+id+"给master发送心跳")
      masterPorxy ! HeartBeat(id)
    }



  }
}

object SparkWorker{
  def main(args: Array[String]): Unit = {
    if (args.length != 6) {
        println("请输入参数workerHost workerPort workerName masterHost masterPort masterName")
        sys.exit()

    }
    val workerHost = args(0)
    val workerPort = args(1)
    val workerName = args(2)
    val masterHost = args(3)
    val masterPort = args(4)
    val masterName = args(5)
    //先创建ActorSystem
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=${workerHost}
         |akka.remote.netty.tcp.port=${workerPort}
        """.stripMargin)//把边界去掉，默认|
    val sparkWorkerSystem = ActorSystem("SparkWorker",config)
    val sparkMasterRef = {
      sparkWorkerSystem.actorOf(Props(new SparkWorker(masterHost,masterPort.toInt,masterName)), s"${workerName}")
    }
    sparkMasterRef ! "start"
  }

}
