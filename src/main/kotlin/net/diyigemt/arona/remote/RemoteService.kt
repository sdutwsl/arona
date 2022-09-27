package net.diyigemt.arona.remote

import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
interface RemoteService<T> {

  val kType: KType
  val type: RemoteServiceAction
  fun handleService(data: T, time: String)
  fun init() {
    RemoteServiceManager.registerService(this.type, this as RemoteService<Any>)
  }

}

enum class RemoteServiceAction(
  private val action: String
) {
  ANNOUNCEMENT("announcement"),
  NULL("null");
  companion object {
    fun getRemoteServiceActionByName(name: String): RemoteServiceAction =
      RemoteServiceAction.values()
        .firstOrNull { it.action == name }
        ?: NULL
  }
}