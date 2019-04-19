package com.tst.priceopt

// PriceOptimizer defines functions for finding optimal cabin prices.
object PriceOptimizer {

  // toRateMap converts a Seq[Rate] to a Map[String, String] for translation of rateCodes to rateGroups.
  // If duplicate rateCodes are defined, a DuplicateRateCodeMappingException is thrown.
  def toRateMap(rates: Seq[Rate]): Map[String, String] = {
    rates
      .groupBy(_.rateCode)
      .map({
        case (rc, Seq(r)) => (rc, r.rateGroup)
        case (rc, rs) => throw new DuplicateRateCodeMappingException(
          s"Found multiple RateGroups for RateCode $rc: ${rs.map(_.toString).mkString(", ")}")
      })
  }

  // getBestGroupPrices returns the lowest-priced CabinPrice,
  // grouped and ordered by the cabinCode and rateGroup.
  // Each rateCode must only be defined in a single Rate.
  // Every CabinPrice.rateCode must have a corresponding Rate.rateCode.
  def getBestGroupPrices(rates: Seq[Rate],
    prices: Seq[CabinPrice]): Seq[BestGroupPrice] = {
    val rateMap: Map[String, String] = toRateMap(rates)
    // Group the prices by cabinCode and corresponding rateGroup for rateCode
    // (throwing if the rateGroup is missing),
    // grab the lowest-priced CabinPrice for each group,
    // and convert to an ordered Seq of BestGroupPrice.
    prices
      .groupBy(p => (p.cabinCode, rateMap.getOrElse(p.rateCode,
        throw new MissingRateGroupForRateCodeException(
          s"No RateGroup defined for RateCode ${p.rateCode}."))))
      .mapValues(cps => cps.minBy(cp => cp.price))
      .map({ case ((_, rg), cp) => BestGroupPrice(cp.cabinCode, cp.rateCode, cp.price, rg) })
      .toSeq
      .sortBy(bcp => (bcp.cabinCode, bcp.rateGroup))
  }
}

// Rate relates a rateCode to its corresponding rateGroup.
case class Rate(rateCode: String, rateGroup: String)

// CabinPrice defines the price for the combined cabinCode and rateCode.
case class CabinPrice(
  cabinCode: String,
  rateCode: String,
  price: BigDecimal)

// BestGroupPrice represents the lowest price and corresponding rateCode for the combined cabinCode and rateGroup. 
case class BestGroupPrice(
  cabinCode: String,
  rateCode: String,
  price: BigDecimal,
  rateGroup: String)

final class DuplicateRateCodeMappingException(message: String) extends Exception(message)
final class MissingRateGroupForRateCodeException(message: String) extends Exception(message)
