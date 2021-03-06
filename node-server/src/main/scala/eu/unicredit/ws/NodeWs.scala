
package eu.unicredit.ws

import scala.scalajs.js
import js.Dynamic.{global => g}
import js.Dynamic.literal
import js.DynamicImplicits._

import akka.actor._

object NodeWs {

	val system = ActorSystem("nodeserver")

	val wsManager = system.actorOf(Props(new WsManagerActor()))

	case class RegisterConn(conn: js.Dynamic)

    case class RunBench(name: String)

	class WsManagerActor extends Actor {

		def receive = {
			case RegisterConn(c) =>
				context.actorOf(Props(new ConnectionActor(c)))
            case msg : String =>
                val splitted = msg.split(",")
                if (splitted(0) == "benchmark") {
                    sender ! RunBench(splitted(1))
                    println("start benchmark "+splitted(1))
                } else
                    context.children.foreach(_ ! msg)
		}
	}

	class ConnectionActor(connection: js.Dynamic) extends Actor {

    	connection.on("message", (message: js.Dynamic) => {
            println("on ws received "+message)
            println("received "+message.utf8Data)

            context.parent ! message.utf8Data
    	})
    
    	connection.on("close", (reasonCode: js.Dynamic, description: js.Dynamic) => {
            println("connection closed...")
        	self ! PoisonPill
    	})

    	def receive = {
            case RunBench(name) =>
                println("has to run benchmarks now!")
                context.actorOf(Props(new BenchActor(name)))
            case BenchResult(name, time) =>
                println("result "+name+" -> "+time)
                connection.send(name+","+time)
    		case msg: String =>
                println("received string "+msg)
                connection.send(msg)
    	}
	}

	val WebSocketServer = g.require("websocket").server
	
	val http: js.Dynamic = g.require("http")

	val server = http.createServer((request: js.Dynamic, response: js.Dynamic) => {
			response.writeHead(404)
			response.end
	})

	val wsServer = js.Dynamic.newInstance(WebSocketServer)(literal(
    	httpServer = server,
	    autoAcceptConnections = false
	))

	wsServer.on("request", (request: js.Dynamic) => {
    	wsManager ! RegisterConn(request.accept(false, request.origin))
	})

}