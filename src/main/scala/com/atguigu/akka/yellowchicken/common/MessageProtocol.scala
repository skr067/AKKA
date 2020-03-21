package com.atguigu.akka.yellowchicken.common

//使用样例类来构建协议
//客户端发给服务器协议（序列化对象）
case class ClientMessage(mes: String)

//服务器端发给客户端的协议
case class ServerMessage(mes: String)
