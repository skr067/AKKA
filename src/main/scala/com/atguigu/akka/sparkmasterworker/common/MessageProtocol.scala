package com.atguigu.akka.sparkmasterworker.common


//worker注册信息
case class RegisterWorkerInfo(id:String,cpu:Int,ram:Int)
//这个是workerInfo,这个信息将保存master的hm(用于管理worker),会被扩展（上次心跳时间）
class WorkerInfo(val id:String,val cpu:Int,val ram:Int){
  var lastHeartBeat : Long = System.currentTimeMillis()
}

//当worker注册成功，服务器返回RegisteredWorkerInfo 对象
case object RegisteredWorkerInfo
//worker每隔一定时间由定时器发给自己的一个信息
case object SendHeartBeat
//worker每隔一定时间由定时器触发，而向master发现的协议消息
case class HeartBeat(id:String)

//master给自己发送一个触发检查超时worker的信息
case object StartTimeOutWorker
//master给自己发送消息，检测worker,对于心跳超时的
case object RemoveTimeOutWorker



