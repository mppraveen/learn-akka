package com.allaboutscala.learn.akka.fsm

import akka.actor.{ActorSystem, Props, LoggingFSM}

/**
  * Created by Nadim Bahadoor on 28/06/2016.
  *
  *  Tutorial: Learn How To Use Akka
  *
  * [[http://allaboutscala.com/scala-frameworks/akka/]]
  *
  * Copyright 2016 Nadim Bahadoor (http://allaboutscala.com)
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
  * use this file except in compliance with the License. You may obtain a copy of
  * the License at
  *
  *  [http://www.apache.org/licenses/LICENSE-2.0]
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations under
  * the License.
  */
object Tutorial_06_AkkaFSM_PartThree extends App {

  println("Step 1: Create ActorSystem")
  val system = ActorSystem("DonutActorFSM")



  println("\nStep 6: Create DonutBakingActor")
  val bakingActor = system.actorOf(Props[DonutBakingActor], "donut-baking-actor")



  println("\nStep 7: Send events to actor to switch states and process events")
  bakingActor ! BakeDonut
  Thread.sleep(2000)



  bakingActor ! "some random event"
  Thread.sleep(2000)



  println("\nStep 8: Shutdown actor system")
  system.terminate()



  println("\nStep 2: Defining events")
  sealed trait BakingEvents
  case object BakeDonut extends BakingEvents
  case object AddTopping extends BakingEvents
  case object StopBaking extends BakingEvents



  println("\nStep 3: Defining states")
  sealed trait BakingStates
  case object Start extends BakingStates
  case object Stop extends BakingStates



  println("\nStep 4: Defining mutatable data")
  case class BakingData(qty: Int) {
    def addOneDonut = copy(qty + 1)
  }

  object BakingData {
    def initialQuantity = BakingData(0)
  }



  println("\nStep 5: Define DonutBakingActor using LoggingFSM trait")
  class DonutBakingActor extends LoggingFSM[BakingStates, BakingData] {
    startWith(Stop, BakingData.initialQuantity)

    when(Stop) {
      case Event(BakeDonut, _) =>
        println("Current state is [Stop], switching to [Start]")
        goto(Start).using(stateData.addOneDonut)

      case Event(AddTopping, _) =>
        println("Current state is [Stop], you first need to move to [BakeDonut]")
        stay
    }


    when(Start) {
      case Event(StopBaking, _) =>
        println(s"Event StopBaking, current donut quantity = ${stateData.qty}")
        goto(Stop)
    }


    whenUnhandled {
      case Event(event, stateData) =>
        println(s"We've received an unhandled event = [$event] for the state data = [$stateData]")
        goto(Stop)
    }

    initialize()
  }
}
