package com.rxu.duoesports.models

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

import scala.util.{Success, Try}

object Rank extends Enumeration {
  type Rank = Value

  val CHALLENGER = Value("CHALLENGER")
  val MASTER = Value("MASTER")
  val D1 = Value("DIAMOND I")
  val D2 = Value("DIAMOND II")
  val D3 = Value("DIAMOND III")
  val D4 = Value("DIAMOND IV")
  val D5 = Value("DIAMOND V")
  val P1 = Value("PLATINUM I")
  val P2 = Value("PLATINUM II")
  val P3 = Value("PLATINUM III")
  val P4 = Value("PLATINUM IV")
  val P5 = Value("PLATINUM V")
  val G1 = Value("GOLD I")
  val G2 = Value("GOLD II")
  val G3 = Value("GOLD III")
  val G4 = Value("GOLD IV")
  val G5 = Value("GOLD V")
  val S1 = Value("SILVER I")
  val S2 = Value("SILVER II")
  val S3 = Value("SILVER III")
  val S4 = Value("SILVER IV")
  val S5 = Value("SILVER V")
  val B1 = Value("BRONZE I")
  val B2 = Value("BRONZE II")
  val B3 = Value("BRONZE III")
  val B4 = Value("BRONZE IV")
  val B5 = Value("BRONZE V")

  private val comparator = List(CHALLENGER, MASTER, D1, D2, D3, D4, D5, P1, P2, P3, P4, P5, G1, G2,
    G3, G4, G5, S1, S2, S3, S4, S5, B1, B2, B3, B4, B5)

  def compare(rank1: Rank, rank2: Rank): Int = {
    comparator.indexOf(rank2).compareTo(comparator.indexOf(rank1))
  }

  def max(maybeRanks: Seq[Rank]): Option[Rank] = {
    maybeRanks match {
      case ranks if ranks.nonEmpty => var max = ranks.head
        for(i <- ranks.indices) {
          val temp = ranks(i)
          if(compare(temp, max) > 0) max = temp
        }
        Some(max)
      case _ => None
    }
  }

  implicit val rankColumn: Column[Rank] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case s: String => Try(Rank.withName(s)) match {
        case Success(rank) => Right(rank)
        case _ => Left(TypeDoesNotMatch(s"Unknown Rank $value"))
      }
      case _ => Left(TypeDoesNotMatch(s"Unknown Rank $value"))
    }
  }

}
