package com.atguigu.akka.sparkmasterworker.master

import akka.actor.{Actor, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable

class SparkMaster extends Actor {

  val workers = mutable.Map[String,WorkerInfo]()
  override def receive: Receive = {
    case "start" => {
      println("master服务器启动了。。。")
      self ! StartTimeOutWorker
    }
    case RegisterWorkerInfo(id,cpu,ram) => {
      //接收到Worker注册信息
      if(!workers.contains(id)){
        //创建worker注册信息
        val workerInfo = new WorkerInfo(id,cpu,ram)
        //加入到workers
        workers += ((id,workerInfo))
        println(workers)
        //回复一个消息，注册成功
        sender() ! RegisteredWorkerInfo
      }
    }
    case HeartBeat(id) => {
      //更新对应的worker的心跳时间
      val workInfo = workers(id)
      workInfo.lastHeartBeat = System.currentTimeMillis()
      println("master更新了"+id+"心跳时间")
    }
    case StartTimeOutWorker => {
      println("开始定时检查worker心跳的任务")
      import context.dispatcher
      context.system.scheduler.schedule(0 millis,10000 millis,self,RemoveTimeOutWorker)
    }
      //消息处理，超时从map删除
    case RemoveTimeOutWorker =>{
      val workInfos = workers.values
      val nowTime = System.currentTimeMillis()
      workInfos.filter(workerInfo => (nowTime - workerInfo.lastHeartBeat)>6000).foreach(workerInfo =>workers.remove(workerInfo.id))
      println("d当前有"+workers.size+"个worker存活着")
    }
  }
}

object SparkMaster{
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("请输入参数host,port,sparkMasterActor名字")
      sys.exit()
    }
    val host = args(0)
    val port = args(1)
    val name = args(2)

    //先创建ActorSystem
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=${host}
         |akka.remote.netty.tcp.port=${port}
        """.stripMargin)//把边界去掉，默认|
    val sparkMasterSystem = ActorSystem("SparkMaster",config)
    //创建SparkMaster -actor
    val sparkMasterRef = sparkMasterSystem.actorOf(Props[SparkMaster],s"${name}")
    //启动SparkMaster
    sparkMasterRef ! "start"
  }

}
