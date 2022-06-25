package distributed

import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}

def startupWithRole[X](role: String, port: Int)(root: => Behavior[X]): ActorSystem[X] =
  // Override the configuration of the port when the role is passed as a argument
  val config: Config = ConfigFactory
    .parseString(
      s"""
      akka.remote.artery.canonical.port=$port
      akka.cluster.roles = [$role]
      """)
    .withFallback(ConfigFactory.load("cluster"))

  // Create an Akka system with the specified config
  ActorSystem(root, "ClusterSystem", config)
