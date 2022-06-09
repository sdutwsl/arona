package net.diyigemt.arona.util

import kotlinx.serialization.json.*
import net.diyigemt.arona.entity.Activity
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.floor

object ActivityUtil {

  private val N3H3Plus = Regex("([3双2翻])倍")
  private val NormalTime = Regex("[Nn]ormal.*?(\\d+)[月/](\\d+).*?([–-]|~).*?(\\d+)[月/](\\d+)")
  private val HardTime = Regex("[Hh]ard.*?(\\d)[月/](\\d+).*?([–-]|~).*?(\\d)[月/](\\d+)")
  private val PickUpRegex = Regex("(\\d)([\\u4e00-\\u9fa5A-z]+)(\\([\\u4e00-\\u9fa5A-z]+\\))?")
  private val PickUpTime = Regex("(\\d+)/(\\d+).*?[–-](\\d+)/(\\d+)")
  private val MaintenanceRegex = Regex("(\\d+)月(\\d+)日.*?[上下]午(\\d+)点")
  private val TotalAssault = Regex("([\\u4e00-\\u9fa5A-z. \\d]+) ?[（(]([\\u4e00-\\u9fa5A-z]+)[)）].*?(\\d+)/(\\d+)")

  fun fetchJPActivity(): Pair<List<Activity>, List<Activity>> {
    val document = Jsoup.connect("https://wiki.biligame.com/bluearchive/%E9%A6%96%E9%A1%B5").get()
    val activities = document.getElementsByClass("activity")
    val active = mutableListOf<Activity>()
    val pending = mutableListOf<Activity>()
    activities.forEach {
      val startTime = it.attr("data-start").replace("维护后","17:00")
      val endTime = it.attr("data-end").replace("维护前","11:00")
      val parseStart = SimpleDateFormat("yyyy/MM/dd HH:mm").parse(startTime)
      val parseEnd = SimpleDateFormat("yyyy/MM/dd HH:mm").parse(endTime)
      val now = Calendar.getInstance().time
      if (now.after(parseEnd)) return@forEach
      val content = it.getElementsByClass("activity__name").text()
      var level = 1
      if (content.indexOf("倍") != -1) {
        level = 2
      } else if (content.indexOf("总力战") != -1) {
        level = 3
      }
      if (now.before(parseStart)) {
        pending.add(Activity(content, level, TimeUtil.calcTime(now, parseStart, true)))
      } else {
        active.add(Activity(content, level, TimeUtil.calcTime(now, parseEnd, false)))
      }
    }
    pending.sortByDescending { at -> at.level }
    active.sortByDescending { at -> at.level }
    return Pair(active, pending)
  }

  fun fetchENActivity(): Pair<List<Activity>, List<Activity>> {
    val fetchActivities = fetchActivities()
    val active = mutableListOf<Activity>()
    val pending = mutableListOf<Activity>()
    fetchActivities.forEach {
      val content = it["orig_text"].toString().replace("\\r", "").replace("\\n", "")
      // N3H3
      if (content.contains(Regex("[Nn]ormal")) and content.contains(Regex("[Hh]ard"))) {
        val matchEntire = N3H3Plus.find(content)
        if (matchEntire != null && matchEntire.groups[0] != null) {
          val plus = matchEntire.groups[0]!!.value
          var power = 2
          if (plus.contains("3")) {
            power = 3
          }
          val nTime = NormalTime.find(content)
          val hTime = HardTime.find(content)
          if ((nTime != null) and (hTime != null) and (nTime!!.groups.size == 6) and (hTime!!.groups.size == 6)) {
            val groupN = nTime.groups
            val groupH = hTime.groups
            val n1 = groupN[1]!!.value
            val n2 = groupN[2]!!.value
            val n3 = groupN[4]!!.value
            val n4 = groupN[5]!!.value
            val h1 = groupH[1]!!.value
            val h2 = groupH[2]!!.value
            val h3 = groupH[4]!!.value
            val h4 = groupH[5]!!.value
            val year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
            val startN = "${year}/${if (n1.toInt() < 10) "0$n1" else n1}/${if (n2.toInt() < 10) "0$n2" else n2}"
            val endN = "${year}/${if (n3.toInt() < 10) "0$n3" else n3}/${if (n4.toInt() < 10) "0$n4" else n4}"
            val startH = "${year}/${if (h1.toInt() < 10) "0$h1" else h1}/${if (h2.toInt() < 10) "0$h2" else h2}"
            val endH = "${year}/${if (h3.toInt() < 10) "0$h3" else h3}/${if (h4.toInt() < 10) "0$h4" else h4}"
            val parseStartN = SimpleDateFormat("yyyy/MM/dd").parse(startN)
            val parseEndN = SimpleDateFormat("yyyy/MM/dd").parse(endN)
            val parseStartH = SimpleDateFormat("yyyy/MM/dd").parse(startH)
            val parseEndH = SimpleDateFormat("yyyy/MM/dd").parse(endH)
            val titleN = "Normal${power}倍掉落"
            val titleH = "Hard${power}倍掉落"
            val now = Calendar.getInstance().time
            if (now.before(parseStartN)) {
              pending.add(Activity(titleN, power, TimeUtil.calcTime(now, parseStartN, true)))
            } else if (now.before(parseEndN)) {
              active.add(Activity(titleN, 2, TimeUtil.calcTime(now, parseEndN, false)))
            }
            if (now.before(parseStartH)) {
              pending.add(Activity(titleH, power, TimeUtil.calcTime(now, parseStartH, true)))
            } else if (now.before(parseEndH)) {
              active.add(Activity(titleH, 2, TimeUtil.calcTime(now, parseEndH, false)))
            }
          }
        }
        return@forEach
      }
      // PickUp
      if (content.contains("PICK") and content.contains("UP") and (content.contains("募集") or content.contains("招募"))) {
        val source = content.substringAfter("PICK UP学生").replace("★", "").replace("＆", "").replace(" ", "")
        val timeSource = content.replace(" ", "")
        val findAll = PickUpRegex.findAll(source).toList()
        var student = "pick up"
        if (findAll.isNotEmpty()) {
          findAll.forEach {
            val groups = it.groups
            val size = groups.size
            if (size > (floor((size / 2).toDouble()) * 2)) {
              val extraName = groups[size - 1]!!.value
              val studentName = groups[size - 2]!!.value
              val studentStar = groups[size - 3]!!.value
              student = "$student $studentStar★$studentName($extraName)"
            } else {
              val studentName = groups[size - 2]!!.value
              val studentStar = groups[size - 3]!!.value
              student = "$student $studentStar★$studentName"
            }
          }
        }
        val findTime = PickUpTime.find(timeSource)
        if (findTime!!.groups.size >= 5) {
          val m1 = findTime.groups[1]!!.value
          val m2 = findTime.groups[3]!!.value
          val d1 = findTime.groups[2]!!.value
          val d2 = findTime.groups[4]!!.value
          val year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
          val start = "${year}/${if (m1.toInt() < 10) "0$m1" else m1}/${if (d1.toInt() < 10) "0$d1" else d1}"
          val end = "${year}/${if (m2.toInt() < 10) "0$m2" else m2}/${if (d2.toInt() < 10) "0$d2" else d2}"
          val parseStart = SimpleDateFormat("yyyy/MM/dd").parse(start)
          val parseEnd = SimpleDateFormat("yyyy/MM/dd").parse(end)
          val now = Calendar.getInstance().time
          if (now.before(parseStart)) {
            pending.add(Activity(student, 2, TimeUtil.calcTime(now, parseStart, true)))
          } else if (now.before(parseEnd)) {
            active.add(Activity(student, 2, TimeUtil.calcTime(now, parseEnd, false)))
          }
        }
        return@forEach
      }
      // 维护公告
      if (content.contains("维护公告")) {
        val find = MaintenanceRegex.find(content)
        if (find != null && find.groups.size >= 4) {
          val m = find.groups[1]!!.value
          val d = find.groups[2]!!.value
          val hour = find.groups[3]!!.value
          val year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
          val start = "${year}/${if (m.toInt() < 10) "0$m" else m}/${if (d.toInt() < 10) "0$d" else d} ${if (hour.toInt() < 10) "0$hour" else hour}"
          val parseStart = SimpleDateFormat("yyyy/MM/dd HH").parse(start)
          val now = Calendar.getInstance().time
          val title = "游戏维护"
          if (now.before(parseStart)) {
            pending.add(Activity(title, 2, TimeUtil.calcTime(now, parseStart, true)))
          }
        }
        return@forEach
      }
      // 总力战
      if (content.contains("总力战预告")) {
        val source = content.substringAfter("总力战预告").trim()
        val find = TotalAssault.find(source)
        if (find != null && find.groups.size >= 5) {
          val name = find.groups[1]!!.value
          val terrain = find.groups[2]!!.value
          val m = find.groups[3]!!.value
          val d = find.groups[4]!!.value
          val year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
          val start = "${year}/${if (m.toInt() < 10) "0$m" else m}/${if (d.toInt() < 10) "0$d" else d}"
          val parseStart = SimpleDateFormat("yyyy/MM/dd").parse(start)
          val now = Calendar.getInstance().time
          val title = "总力战 $name($terrain)"
          if (now.before(parseStart)) {
            pending.add(Activity(title, 2, TimeUtil.calcTime(now, parseStart, true)))
          }
        }
        return@forEach
      }
    }
    return Pair(active, pending)
  }

  private fun fetchActivities(): List<JsonObject> {
    var offset = ""
    val res = mutableListOf<JsonObject>()
    for (i in 0 .. 2) {
      val document = Jsoup.connect("https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space?offset=${offset}&host_mid=1585224247&timezone_offset=-480")
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36")
        .ignoreContentType(true)
        .get()
      val data = Json.parseToJsonElement(document.text()).jsonObject
      if (clearExtraQute((data["code"] ?: "-1").toString()).toInt() != 0 || clearExtraQute((data["message"] ?: "-1").toString()).toInt() != 0) {
        println("catch BA en activities error, ${data["message"].toString()}")
        continue
      }
      val items = forceGets(data, "data.items").jsonArray
      offset = clearExtraQute(forceGets(data, "data.offset").toString())
      items.forEach {
        val pubType = forceGets(it, "modules.module_author.pub_action")
        if (pubType.toString().length > 2) return@forEach
        val desc = forceGets(it, "modules.module_dynamic.desc.rich_text_nodes").jsonArray
        res.add(desc[0].jsonObject)
      }
    }
    return res
  }

  private fun forceGet(target: JsonElement, key: String): JsonElement {
    return target.jsonObject[key]!!
  }

  private fun forceGets(target: JsonElement, key: String): JsonElement {
    val keys = key.split(".")
    var result: JsonElement = target
    keys.forEach {
      result = result.jsonObject
      result = (result as JsonObject)[it]!!
    }
    return result
  }

  private fun clearExtraQute(s: String): String {
    if (s.replace("\"", "").length + 2 == s.length) {
      return s.replaceFirst("\"", "").substring(0, s.length - 2)
    }
    return s
  }

}