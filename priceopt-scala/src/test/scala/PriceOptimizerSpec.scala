import com.tst.priceopt._
import org.scalatest._

class PriceOptimizerSpec extends FlatSpec {

  val sampleRates = Seq(
    Rate("M1", "Military"), Rate("M2", "Military"), Rate("S1", "Senior"), Rate("S2", "Senior"))

  val samplePrices = Seq(
    CabinPrice("CA", "M1", 200.00),
    CabinPrice("CA", "M2", 250.00),
    CabinPrice("CA", "S1", 225.00),
    CabinPrice("CA", "S2", 260.00),
    CabinPrice("CB", "M1", 230.00),
    CabinPrice("CB", "M2", 260.00),
    CabinPrice("CB", "S1", 245.00),
    CabinPrice("CB", "S2", 270.00))

  val expectedBest = Seq(
    BestGroupPrice("CA", "M1", 200.00, "Military"),
    BestGroupPrice("CA", "S1", 225.00, "Senior"),
    BestGroupPrice("CB", "M1", 230.00, "Military"),
    BestGroupPrice("CB", "S1", 245.00, "Senior"))

  "PriceOptimizer" should "determine the lowest-priced cabin by cabinCode and rateGroup" in {
    assert(PriceOptimizer.getBestGroupPrices(sampleRates, samplePrices) == expectedBest)
  }

  it should "return an empty Seq if given empty Seqs" in {
    assert(PriceOptimizer.getBestGroupPrices(Seq(), Seq()) == Seq())
  }

  it should "ignore unused Rates" in {
    assert(PriceOptimizer.getBestGroupPrices(sampleRates ++ Seq(Rate("M3", "Military"), Rate("C1", "Child")), samplePrices) == expectedBest)
  }

  it should "throw a DuplicateRateCodeMappingException if two or more Rates have the same rateCode" in {
    assertThrows[DuplicateRateCodeMappingException] {
      PriceOptimizer.getBestGroupPrices(sampleRates ++ Seq(Rate("M1", "Armed Forces")), samplePrices)
    }
  }

  it should "throw a MissingGroupForRateCodeException if a CabinPrice includes a rateCode not included in a Rate" in {
    assertThrows[MissingRateGroupForRateCodeException] {
      PriceOptimizer.getBestGroupPrices(sampleRates, samplePrices ++ Seq(CabinPrice("CA", "U2", 1000.00)))
    }
  }
}
