import com.tst.priceopt._

// The application optimizes CabinPrices for a set of sample values.
object Main extends App {

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

  PriceOptimizer.getBestGroupPrices(sampleRates, samplePrices).map(println)
}

