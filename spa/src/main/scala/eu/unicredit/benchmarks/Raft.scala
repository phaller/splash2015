
package eu.unicredit.benchmarks

import scala.scalajs.js
import js.Dynamic.literal

import akka.actor._

class RaftPage extends VueActor {
  import TodoMsgs._

  val vueTemplate =
    """
      <div class="col-md-6">
      <h1>
        Raft Algorithm - TBD
      </h1>
      <div>
       	<div id="RAFT"></div>
       	
    	<br/>
    		<button type="button" v-on='click:startRaft()'>Go!</button>
    	<span>Chrome only!! If you want to see everything happening, make sure to open the Console as well</span>
    	</div>
      </div>
    """

  override val vueMethods = literal(
    startRaft = 
    	() => { js.Dynamic.global.startPlayground(); demo.Main.main() }
    )

  def operational = vueBehaviour

}
