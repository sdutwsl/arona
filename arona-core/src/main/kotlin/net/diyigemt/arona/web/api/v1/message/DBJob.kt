package net.diyigemt.arona.web.api.v1.message

import net.diyigemt.arona.web.database.Strategy


/**
 *@Author hjn
 *@Create 2022/10/22
 */
data class DBJob(
  val jobName: String? = null,
  val description: String? = null,
  val properties: Properties
)


data class Properties(
  var db: String = "",
  var table: String = "",
  val task: Task
)


data class Task(
  val strategy: String,
  val where: Map<String, Any?>? = null,
  val args: Map<String, Any?>? = null
){

  fun getStrategyModel() : Strategy? = when(strategy.uppercase()){
    Strategy.INSERT.name -> Strategy.INSERT
    Strategy.DELETE.name -> Strategy.DELETE
    Strategy.UPDATE.name -> Strategy.UPDATE
    Strategy.SELECT.name -> Strategy.SELECT
    else -> null
  }
}