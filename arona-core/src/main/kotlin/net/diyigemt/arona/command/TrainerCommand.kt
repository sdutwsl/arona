package net.diyigemt.arona.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import net.diyigemt.arona.Arona
import net.diyigemt.arona.config.AronaTrainerConfig
import net.diyigemt.arona.db.DataBaseProvider
import net.diyigemt.arona.db.image.ImageTableModel
import net.diyigemt.arona.entity.TrainerOverride
import net.diyigemt.arona.service.AronaService
import net.diyigemt.arona.util.GeneralUtils
import net.diyigemt.arona.util.GeneralUtils.toHex
import net.diyigemt.arona.util.other.KWatchChannel
import net.diyigemt.arona.util.other.KWatchEvent
import net.diyigemt.arona.util.other.asWatchChannel
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File

object TrainerCommand : SimpleCommand(
  Arona,"trainer", "攻略",
  description = "主线地图和学生攻略"
), AronaService {
  const val StudentRankFolder: String = "/student_rank"
  const val ChapterMapFolder: String = "/chapter_map"
  const val OtherFolder: String = "/some"
  private const val AutoReadConfigFileName = "trainer_config.yml"
  private lateinit var ConfigFileWatcherChannel: KWatchChannel
  private lateinit var ConfigFile: File
  private var ConfigFileMd5: String = ""
  private val FuzzySearch = mutableListOf<List<String>>()
  private val overrideList = mutableListOf<TrainerOverride>()
  @Handler
  suspend fun UserCommandSender.trainer(str: String) {
    if (str == "阿罗娜" || str == "彩奈") {
      subject.sendMessage("阿罗娜已经被老师攻略啦>_<")
      return
    }
    val override = overrideList
      .filter { it.name.contains(str) }
      .firstOrNull { it.name.split(",")
        .any { s -> s.trim() == str }
      } ?: TrainerOverride(TrainerOverride.OverrideType.RAW, str, str)
    val value = override.value
    when (override.type) {
      TrainerOverride.OverrideType.IMAGE -> {
        val file = GeneralUtils.localImageFile(value)
        if (!file.exists()) {
          Arona.warning("处理攻略指令别名: $str 时失败,没有找到对应的文件: $value")
          return
        }
        sendImage(subject, file)
      }
      TrainerOverride.OverrideType.RAW -> {
        val result = GeneralUtils.loadImageOrUpdate(value)
        val list = result.list.map { it.name }.toMutableList()
        // 没有本地文件
        if (result.file == null) {
          // 模糊搜索建议关闭
          if (!AronaTrainerConfig.tipWhenNull) {
            return
          }
          // 根据配置对数据源进行模糊搜索
          when (AronaTrainerConfig.fuzzySearchSource) {
            FuzzySearchSource.ALL -> {
              list.addAll(fuzzySearch(str))
            }
            FuzzySearchSource.LOCAL_CONFIG -> {
              list.clear()
              list.addAll(fuzzySearch(str))
            }
            FuzzySearchSource.REMOTE -> {}
          }
          // 如果仍然没有搜索建议 说明真的找不到
          if (list.isEmpty()) {
            if (AronaTrainerConfig.tipWhenNull) {
              sendMessage("没有对应信息, 请联系作者添加别名或者在配置文件中指定")
            }
          } else {
            // 无精确匹配结果, 但是有搜索建议, 发送建议
            // 根据相似度进行排序
            val sourceSet = str.toSet()
            list.sortByDescending {
              it.toSet().sumOf { ch -> (if (sourceSet.contains(ch)) 1 else 0).toInt() }
            }
            // 去重
            sendMessage("没有与${str}对应的信息, 是否想要输入:\n${
              list.toSet().filterIndexed { index, _ -> index < 4 }
                .joinToString("\n") { "/攻略 $it" }
            }")
          }
        } else {
          sendImage(subject, result.file)
        }
      }
      TrainerOverride.OverrideType.CODE -> {
        sendMessage(override.value.deserializeMiraiCode(subject))
      }
    }
  }

  private fun fuzzySearch(source: String): List<String> {
    val list = FuzzySearch.map {
      val index = GeneralUtils.fuzzySearchDouble(source, it)
      return@map if (index == -1) "" else it[index]
    }.filter { it.isNotBlank() }.toMutableList()
    // 从数据库查找
    list.addAll(DataBaseProvider.query { _ ->
      ImageTableModel.all().map { it.name }.filter { GeneralUtils.fuzzySearch(it, source) || GeneralUtils.fuzzySearch(source, it) }
    }!!)
    return list
  }

  private suspend fun sendImage(contact: Contact, image: File) {
    val resource = image.toExternalResource()
    contact.sendMessage(contact.uploadImage(resource))
    withContext(Dispatchers.IO) {
      resource.close()
    }
  }

  // 模糊查询缓存
  private fun rebuildFuzzySearchCache() {
    kotlin.runCatching {
      val read = ConfigFile.readText(Charsets.UTF_8).also {
        if (it.isBlank()) {
          return
        }
      }
      GeneralUtils.md5(read).toHex().also {
        if (it == ConfigFileMd5) {
          return
        } else {
          ConfigFileMd5 = it
        }
      }
      net.mamoe.yamlkt.Yaml.decodeFromString(TrainerFileConfig.serializer(), read)
    }.onFailure { err ->
      Arona.warning("序列化别名配置时失败")
      Arona.warning(err.message)
      err.printStackTrace()
      return
    }.onSuccess {
      FuzzySearch.clear()
      overrideList.clear()
      overrideList.addAll(it.override)
    }
    overrideList.addAll(AronaTrainerConfig.override.filter {
      !overrideList.any { already -> already.name == it.name }
    })
    reloadConfig()
    Arona.info("别名配置更新成功")
  }

  private fun reloadConfig() {
    overrideList.forEach {
      FuzzySearch.add(it.name.split(",").map { s -> s.trim() })
    }
  }

  @Serializable
  data class TrainerFileConfig(
    val override: List<TrainerOverride>
  )

  enum class FuzzySearchSource {
    ALL, LOCAL_CONFIG, REMOTE
  }

  override val id: Int = 20
  override val name: String = "地图与学生攻略"
  override var enable: Boolean = true
  override fun init() {
    registerService()
    register()
    overrideList.addAll(AronaTrainerConfig.override)
    reloadConfig()
    // 监视data文件夹下的arona-trainer.yml文件动态添加配置
    ConfigFile = File(Arona.dataFolderPath("/${GeneralUtils.ConfigFolder}/${AutoReadConfigFileName}"))
    if (!ConfigFile.exists()) {
      ConfigFile.writeText("override: []", Charsets.UTF_8)
    }
    Arona.runSuspend {
      ConfigFileWatcherChannel = ConfigFile.asWatchChannel(KWatchChannel.Mode.SingleFile)
      ConfigFileWatcherChannel.consumeEach {
        when (it.kind) {
          KWatchEvent.Kind.Modified, KWatchEvent.Kind.Initialized -> rebuildFuzzySearchCache()
          else -> {}
        }
      }
    }
  }

  override fun disableService() {
    ConfigFileWatcherChannel.close()
    super.disableService()
  }

}