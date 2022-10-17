package net.diyigemt.arona.entity.schaleDB

import net.diyigemt.arona.db.DB
import net.diyigemt.arona.db.DataBaseProvider
import net.diyigemt.arona.db.data.schaledb.Localization
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

/**
 *@Author hjn
 *@Create 2022/8/18
 */

data class LocalizationDAO(
//    val AdaptationType: AdaptationType,
//    val ArmorType: ArmorType,
//    val BossFaction: BossFaction,
//    val BuffName: BuffName,
//    val BuffNameLong: BuffNameLong,
//    val BuffTooltip: BuffTooltip,
//    val BuffType: BuffType,
//    val BulletType: BulletType,
//    val Club: Club,
//    val ConquestMap: ConquestMap,
//    val EnemyRank: EnemyRank,
//    val EnemyTags: EnemyTags,
  var EventName: Map<String, String> = mutableMapOf(),
//    val IsLimited: Map<String, String>,
//    val ItemCategory: ItemCategory,
//    val NodeQuality: Map<String, String>,
//    val NodeTier: Map<String, String>,
//    val School: School,
//    val SchoolLong: SchoolLong,
//    val SquadType: SquadType,
//    val StageTitle: StageTitle,
//    val StageType: StageType,
//    val Stat: Stat,
//    val TacticRole: TacticRole,
//    val TimeAttackStage: TimeAttackStage,
//    val WeaponPartExpBonus: WeaponPartExpBonus,
//    val furniture_set: Map<String, String>,
//    val ui: Ui
) : BaseDAO{
  override fun sendToDataBase() {
    DataBaseProvider.query(DB.DATA.ordinal) { Localization.deleteAll() }

    for (item in EventName){
      DataBaseProvider.query(DB.DATA.ordinal) {
        Localization.insert {
          it[tag] = "EventName"
          it[eventID] = item.key.toInt()
          it[value] = item.value
        }
      }
    }
  }

  override fun <T : BaseDAO> toModel(dao: T): T {
    dao as LocalizationDAO
    dao.EventName = mapOf()

    val query = kotlin.runCatching {
      DataBaseProvider.query(DB.DATA.ordinal) {
        Localization.selectAll().map { it[Localization.eventID].toString() to it[Localization.value] }
      }
    }.getOrNull()?: mutableListOf()

    for (item in query){
      dao.EventName = dao.EventName.plus(item)
    }

    return dao
  }
}

data class AdaptationType(
    val Indoor: String,
    val Outdoor: String,
    val Street: String
)

data class ArmorType(
    val HeavyArmor: String,
    val LightArmor: String,
    val Normal: String,
    val Unarmed: String
)

data class BossFaction(
    val CommunioSanctorum: String,
    val Decagrammaton: String,
    val Kaitenger: String,
    val Slumpia: String,
    val TheLibraryofLore: String
)

data class BuffName(
    val Buff_ATK: String,
    val Buff_AmmoCount: String,
    val Buff_AttackSpeed: String,
    val Buff_BlockRate: String,
    val Buff_ConcentratedTarget: String,
    val Buff_CostChange: String,
    val Buff_CostRegen: String,
    val Buff_CriticalChance: String,
    val Buff_CriticalChanceResistPoint: String,
    val Buff_CriticalDamage: String,
    val Buff_CriticalDamageRateResist: String,
    val Buff_DEF: String,
    val Buff_DamagedRatio: String,
    val Buff_Dodge: String,
    val Buff_DotHeal: String,
    val Buff_EnhanceExplosionRate: String,
    val Buff_EnhanceMysticRate: String,
    val Buff_EnhancePierceRate: String,
    val Buff_HIT: String,
    val Buff_HealByHit_Damaged: String,
    val Buff_HealEffectiveness: String,
    val Buff_HealPower: String,
    val Buff_MAXHP: String,
    val Buff_MoveSpeed: String,
    val Buff_OppressionPower: String,
    val Buff_OppressionResist: String,
    val Buff_Penetration: String,
    val Buff_Range: String,
    val Buff_Shield: String,
    val Buff_Stability: String,
    val CC_Fear: String,
    val CC_Provoke: String,
    val CC_Stunned: String,
    val Debuff_ATK: String,
    val Debuff_AmmoCount: String,
    val Debuff_AttackSpeed: String,
    val Debuff_BlockRate: String,
    val Debuff_Burn: String,
    val Debuff_Chill: String,
    val Debuff_ConcentratedTarget: String,
    val Debuff_CostChange: String,
    val Debuff_CostRegen: String,
    val Debuff_CriticalChance: String,
    val Debuff_CriticalChanceResistPoint: String,
    val Debuff_CriticalDamage: String,
    val Debuff_CriticalDamageRateResist: String,
    val Debuff_DEF: String,
    val Debuff_DamageByHit_Damaged: String,
    val Debuff_DamagedRatio: String,
    val Debuff_Dodge: String,
    val Debuff_EnhanceExplosionRate: String,
    val Debuff_EnhanceMysticRate: String,
    val Debuff_EnhancePierceRate: String,
    val Debuff_HIT: String,
    val Debuff_HealEffectiveness: String,
    val Debuff_HealPower: String,
    val Debuff_Hieronymus: String,
    val Debuff_MAXHP: String,
    val Debuff_MoveSpeed: String,
    val Debuff_OppressionPower: String,
    val Debuff_OppressionResist: String,
    val Debuff_Penetration: String,
    val Debuff_Poison: String,
    val Debuff_Range: String,
    val Debuff_Stability: String,
    val Special_Accumulation: String,
    val Special_CostRegenBan: String,
    val Special_EnergyBatteryHalf: String,
    val Special_FormChange: String,
    val Special_Fury: String,
    val Special_Immortal: String,
    val Special_ImmuneCC: String,
    val Special_ImmuneDamage: String,
    val Special_LittleDevil: String,
    val Special_Misdeed: String,
    val Special_SuddenDeath: String
)

data class BuffNameLong(
    val Buff_ATK: String,
    val Buff_AmmoCount: String,
    val Buff_AttackSpeed: String,
    val Buff_BlockRate: String,
    val Buff_ConcentratedTarget: String,
    val Buff_CostChange: String,
    val Buff_CostRegen: String,
    val Buff_CriticalChance: String,
    val Buff_CriticalChanceResistPoint: String,
    val Buff_CriticalDamage: String,
    val Buff_CriticalDamageRateResist: String,
    val Buff_DEF: String,
    val Buff_DamagedRatio: String,
    val Buff_Dodge: String,
    val Buff_DotHeal: String,
    val Buff_EnhanceExplosionRate: String,
    val Buff_EnhanceMysticRate: String,
    val Buff_EnhancePierceRate: String,
    val Buff_HIT: String,
    val Buff_HealByHit_Damaged: String,
    val Buff_HealEffectiveness: String,
    val Buff_HealPower: String,
    val Buff_MAXHP: String,
    val Buff_MoveSpeed: String,
    val Buff_OppressionPower: String,
    val Buff_OppressionResist: String,
    val Buff_Penetration: String,
    val Buff_Range: String,
    val Buff_Shield: String,
    val Buff_Stability: String,
    val CC_Fear: String,
    val CC_Provoke: String,
    val CC_Stunned: String,
    val Debuff_ATK: String,
    val Debuff_AmmoCount: String,
    val Debuff_AttackSpeed: String,
    val Debuff_BlockRate: String,
    val Debuff_Burn: String,
    val Debuff_Chill: String,
    val Debuff_ConcentratedTarget: String,
    val Debuff_CostChange: String,
    val Debuff_CostRegen: String,
    val Debuff_CriticalChance: String,
    val Debuff_CriticalChanceResistPoint: String,
    val Debuff_CriticalDamage: String,
    val Debuff_CriticalDamageRateResist: String,
    val Debuff_DEF: String,
    val Debuff_DamageByHit_Damaged: String,
    val Debuff_DamagedRatio: String,
    val Debuff_Dodge: String,
    val Debuff_EnhanceExplosionRate: String,
    val Debuff_EnhanceMysticRate: String,
    val Debuff_EnhancePierceRate: String,
    val Debuff_HIT: String,
    val Debuff_HealEffectiveness: String,
    val Debuff_HealPower: String,
    val Debuff_Hieronymus: String,
    val Debuff_MAXHP: String,
    val Debuff_MoveSpeed: String,
    val Debuff_OppressionPower: String,
    val Debuff_OppressionResist: String,
    val Debuff_Penetration: String,
    val Debuff_Poison: String,
    val Debuff_Range: String,
    val Debuff_Stability: String,
    val Special_Accumulation: String,
    val Special_CostRegenBan: String,
    val Special_EnergyBatteryHalf: String,
    val Special_FormChange: String,
    val Special_Fury: String,
    val Special_Immortal: String,
    val Special_ImmuneCC: String,
    val Special_ImmuneDamage: String,
    val Special_LittleDevil: String,
    val Special_Misdeed: String,
    val Special_SuddenDeath: String
)

data class BuffTooltip(
    val Buff_ATK: String,
    val Buff_AmmoCount: String,
    val Buff_AttackSpeed: String,
    val Buff_BlockRate: String,
    val Buff_ConcentratedTarget: String,
    val Buff_CostChange: String,
    val Buff_CostRegen: String,
    val Buff_CriticalChance: String,
    val Buff_CriticalChanceResistPoint: String,
    val Buff_CriticalDamage: String,
    val Buff_CriticalDamageRateResist: String,
    val Buff_DEF: String,
    val Buff_DamagedRatio: String,
    val Buff_Dodge: String,
    val Buff_DotHeal: String,
    val Buff_EnhanceExplosionRate: String,
    val Buff_EnhanceMysticRate: String,
    val Buff_EnhancePierceRate: String,
    val Buff_HIT: String,
    val Buff_HealByHit_Damaged: String,
    val Buff_HealEffectiveness: String,
    val Buff_HealPower: String,
    val Buff_MAXHP: String,
    val Buff_MoveSpeed: String,
    val Buff_OppressionPower: String,
    val Buff_OppressionResist: String,
    val Buff_Penetration: String,
    val Buff_Range: String,
    val Buff_Shield: String,
    val Buff_Stability: String,
    val CC_Fear: String,
    val CC_Provoke: String,
    val CC_Stunned: String,
    val Debuff_ATK: String,
    val Debuff_AmmoCount: String,
    val Debuff_AttackSpeed: String,
    val Debuff_BlockRate: String,
    val Debuff_Burn: String,
    val Debuff_Chill: String,
    val Debuff_ConcentratedTarget: String,
    val Debuff_CostChange: String,
    val Debuff_CostRegen: String,
    val Debuff_CriticalChance: String,
    val Debuff_CriticalChanceResistPoint: String,
    val Debuff_CriticalDamage: String,
    val Debuff_CriticalDamageRateResist: String,
    val Debuff_DEF: String,
    val Debuff_DamageByHit_Damaged: String,
    val Debuff_DamagedRatio: String,
    val Debuff_Dodge: String,
    val Debuff_EnhanceExplosionRate: String,
    val Debuff_EnhanceMysticRate: String,
    val Debuff_EnhancePierceRate: String,
    val Debuff_HIT: String,
    val Debuff_HealEffectiveness: String,
    val Debuff_HealPower: String,
    val Debuff_Hieronymus: String,
    val Debuff_MAXHP: String,
    val Debuff_MoveSpeed: String,
    val Debuff_OppressionPower: String,
    val Debuff_OppressionResist: String,
    val Debuff_Penetration: String,
    val Debuff_Poison: String,
    val Debuff_Range: String,
    val Debuff_Stability: String,
    val Special_Accumulation: String,
    val Special_CostRegenBan: String,
    val Special_EnergyBatteryHalf: String,
    val Special_FormChange: String,
    val Special_Fury: String,
    val Special_Immortal: String,
    val Special_ImmuneCC: String,
    val Special_ImmuneDamage: String,
    val Special_LittleDevil: String,
    val Special_Misdeed: String,
    val Special_SuddenDeath: String
)

data class BuffType(
    val Buff: String,
    val CC: String,
    val Debuff: String,
    val Special: String
)

data class BulletType(
    val Explosion: String,
    val Mystic: String,
    val Normal: String,
    val Pierce: String,
    val Siege: String
)

data class Club(
    val AriusSqud: String,
    val BookClub: String,
    val Class227: String,
    val CleanNClearing: String,
    val Countermeasure: String,
    val Emergentology: String,
    val EmptyClub: String,
    val Endanbou: String,
    val Engineer: String,
    val FoodService: String,
    val Fuuki: String,
    val GameDev: String,
    val GourmetClub: String,
    val HoukagoDessert: String,
    val Justice: String,
    val KnightsHospitaller: String,
    val Kohshinjo68: String,
    val MatsuriOffice: String,
    val Meihuayuan: String,
    val NinpoKenkyubu: String,
    val Onmyobu: String,
    val PandemoniumSociety: String,
    val RabbitPlatoon: String,
    val RedwinterSecretary: String,
    val RemedialClass: String,
    val SPTF: String,
    val Shugyobu: String,
    val SisterHood: String,
    val TheSeminar: String,
    val TrainingClub: String,
    val TrinityVigilance: String,
    val Veritas: String,
    val anzenkyoku: String
)

data class ConquestMap(
    val `815`: String
)

data class EnemyRank(
    val Champion: String,
    val Elite: String,
    val Minion: String,
    val Summoned: String
)

data class EnemyTags(
    val EnemyLarge: String,
    val EnemyMedium: String,
    val EnemySmall: String,
    val EnemyXLarge: String
)

data class ItemCategory(
    val Artifact: String,
    val Background: String,
    val Badge: String,
    val Bag: String,
    val Bed: String,
    val Box: String,
    val Chair: String,
    val CharacterExpGrowth: String,
    val Charm: String,
    val Closet: String,
    val Coin: String,
    val Collectible: String,
    val Consumable: String,
    val Currency: String,
    val Decorations: String,
    val Equipment: String,
    val Exp: String,
    val Favor: String,
    val Floor: String,
    val FloorDecoration: String,
    val FurnitureEtc: String,
    val Furnitures: String,
    val Gloves: String,
    val Hairpin: String,
    val Hat: String,
    val HomeAppliance: String,
    val Interiors: String,
    val Material: String,
    val Necklace: String,
    val Prop: String,
    val SecretStone: String,
    val Shoes: String,
    val Table: String,
    val WallDecoration: String,
    val Wallpaper: String,
    val Watch: String,
    val WeaponExpGrowthA: String,
    val WeaponExpGrowthB: String,
    val WeaponExpGrowthC: String,
    val WeaponExpGrowthZ: String
)

data class School(
    val Abydos: String,
    val Arius: String,
    val ETC: String,
    val Gehenna: String,
    val Hyakkiyako: String,
    val Millennium: String,
    val RedWinter: String,
    val SRT: String,
    val Shanhaijing: String,
    val Trinity: String,
    val Valkyrie: String
)

data class SchoolLong(
    val Abydos: String,
    val Arius: String,
    val ETC: String,
    val Gehenna: String,
    val Hyakkiyako: String,
    val Millennium: String,
    val RedWinter: String,
    val SRT: String,
    val Shanhaijing: String,
    val Trinity: String,
    val Valkyrie: String
)

data class SquadType(
    val Main: String,
    val Support: String
)

data class StageTitle(
    val Blood: String,
    val ChaserA: String,
    val ChaserB: String,
    val ChaserC: String,
    val FindGift: String,
    val SchoolA: String,
    val SchoolB: String,
    val SchoolC: String
)

data class StageType(
    val Blood: String,
    val Bounty: String,
    val Campaign: String,
    val ChaserA: String,
    val ChaserB: String,
    val ChaserC: String,
    val Conquest: String,
    val Event: String,
    val FindGift: String,
    val Raid: String,
    val SchoolA: String,
    val SchoolB: String,
    val SchoolC: String,
    val SchoolDungeon: String,
    val TimeAttack: String,
    val WeekDungeon: String,
    val WorldRaid: String
)

data class Stat(
    val AccuracyPoint: String,
    val AmmoCount: String,
    val AttackPower: String,
    val AttackSpeed: String,
    val BlockRate: String,
    val CriticalChanceResistPoint: String,
    val CriticalDamageRate: String,
    val CriticalDamageResistRate: String,
    val CriticalPoint: String,
    val DamagedRatio: String,
    val DefensePenetration: String,
    val DefensePower: String,
    val DodgePoint: String,
    val HealEffectivenessRate: String,
    val HealPower: String,
    val Level: String,
    val MaxHP: String,
    val MoveSpeed: String,
    val OppressionPower: String,
    val OppressionResist: String,
    val Range: String,
    val RegenCost: String,
    val SightPoint: String,
    val StabilityPoint: String
)

data class TacticRole(
    val DamageDealer: String,
    val Healer: String,
    val Supporter: String,
    val Tanker: String,
    val Vehicle: String
)

data class TimeAttackStage(
    val Defense: String,
    val Destruction: String,
    val Shooting: String
)

data class WeaponPartExpBonus(
    val WeaponExpGrowthA: String,
    val WeaponExpGrowthB: String,
    val WeaponExpGrowthC: String,
    val WeaponExpGrowthZ: String
)

data class Ui(
    val age: String,
    val attack_type_desc: String,
    val attack_type_normal_desc: String,
    val attacktype: String,
    val birthday: String,
    val bond_req_equip: String,
    val bond_req_upgrade: String,
    val bond_title: String,
    val collection: String,
    val collection_export: String,
    val collection_export_collection: String,
    val collection_export_placeholder: String,
    val collection_import: String,
    val collection_import_collection: String,
    val collection_import_placeholder: String,
    val collection_import_planner: String,
    val collection_import_warning: String,
    val collection_notowned: String,
    val collection_owned: String,
    val combat_power: String,
    val comfort: String,
    val conquest_area: String,
    val craft_rewards: String,
    val craft_using: String,
    val current_events: String,
    val cv: String,
    val default: String,
    val defense_type_desc: String,
    val defense_type_normal_desc: String,
    val defensetype: String,
    val event_ends: String,
    val event_starts: String,
    val favoritem_none: String,
    val favoritem_title: String,
    val furniture_interaction_list: String,
    val furniture_none: String,
    val furniture_title: String,
    val gacha_pickup: String,
    val height: String,
    val hobbies: String,
    val home_timezone: String,
    val illustrator: String,
    val item_artifact_box: String,
    val item_equipment_box: String,
    val item_likedbystudent_list_0: String,
    val item_likedbystudent_list_1: String,
    val item_obtainedfrom_stage: String,
    val item_randombox_tier: String,
    val item_specialgift: String,
    val item_tab_currency: String,
    val item_tab_eleph: String,
    val item_tab_equipment: String,
    val item_tab_furniture: String,
    val item_tab_gifts: String,
    val item_tab_materials: String,
    val item_usedby: String,
    val item_usedby_eleph: String,
    val item_usedby_skill: String,
    val maptile_drone: String,
    val maptile_enemy_default_msg: String,
    val maptile_item_atk: String,
    val maptile_item_def: String,
    val maptile_item_food: String,
    val maptile_remove: String,
    val maptile_spawn: String,
    val maptile_switch_down: String,
    val maptile_switch_target_down: String,
    val maptile_switch_target_up: String,
    val maptile_switch_up: String,
    val maptile_teleport_oneway_entrance: String,
    val maptile_teleport_oneway_exit: String,
    val maptile_teleport_twoway: String,
    val memory_lobby: String,
    val memory_lobby_student: String,
    val memory_lobby_unlock: String,
    val name: String,
    val navbar_craft: String,
    val navbar_home: String,
    val navbar_items: String,
    val navbar_raids: String,
    val navbar_settings_collection: String,
    val navbar_settings_collection_description: String,
    val navbar_settings_contrast: String,
    val navbar_settings_contrast_description: String,
    val navbar_settings_language: String,
    val navbar_settings_language_description: String,
    val navbar_settings_server: String,
    val navbar_settings_theme: String,
    val navbar_settings_theme_description: String,
    val navbar_stages: String,
    val navbar_students: String,
    val options: String,
    val options_short: String,
    val position: String,
    val profile: String,
    val raid_rules: String,
    val raid_season: String,
    val raid_season_select: String,
    val rarity: String,
    val rec_level: String,
    val rewards_findgift_msg: String,
    val rewards_none: String,
    val rewards_season: String,
    val role: String,
    val school: String,
    val search: String,
    val setting_off: String,
    val setting_on: String,
    val setting_open: String,
    val skill_plus: String,
    val sortby: String,
    val stability_mindamage: String,
    val stage_enemies: String,
    val stage_info: String,
    val stage_map: String,
    val stage_reward_default: String,
    val stage_reward_firstclear: String,
    val stage_reward_threestar: String,
    val stage_rewards: String,
    val starcondition_allsurvive: String,
    val starcondition_clear: String,
    val starcondition_cleartime: String,
    val starcondition_clearturns: String,
    val starcondition_complete: String,
    val starcondition_defeatall: String,
    val starcondition_defeatalltime: String,
    val starcondition_earnrewards: String,
    val starcondition_sranks: String,
    val stat: String,
    val stat_info: String,
    val statpreview_summon_ex: String,
    val statpreview_title: String,
    val student: String,
    val student_birthdays: String,
    val student_bond: String,
    val student_bond_alt: String,
    val student_bond_short: String,
    val student_ex_gear: String,
    val student_gear: String,
    val student_gear_short: String,
    val student_gear_t2_desc: String,
    val student_list: String,
    val student_page_gear: String,
    val student_page_skills: String,
    val student_page_stats: String,
    val student_page_weapon: String,
    val student_search_filters: String,
    val student_search_filters_clear: String,
    val student_skill_ex: String,
    val student_skill_normal: String,
    val student_skill_passive: String,
    val student_skill_sub: String,
    val student_skills_materials: String,
    val student_skills_materials_none: String,
    val student_weapon: String,
    val student_weapon_rank_2star_desc: String,
    val student_weapon_rank_3star_desc: String,
    val summon_ex_bonus: String,
    val ta_phase: String,
    val terrain_adaption: String,
    val terrain_adaption_details: String,
    val toast_import_copy: String,
    val toast_import_failure: String,
    val toast_import_success: String,
    val tooltip_collection_add: String,
    val tooltip_collection_remove: String,
    val tooltip_compare: String,
    val tooltip_compare_remove: String,
    val tooltip_equipment_bonus: String,
    val tooltip_import_planner: String,
    val tooltip_passiveskill_bonus: String,
    val tooltip_relationship_bonus: String,
    val tooltip_relationship_bonus_alt: String,
    val tooltip_supportstats: String,
    val tooltip_vehiclestats: String,
    val type: String,
    val weapon: String,
    val weaponbonus_title: String
)