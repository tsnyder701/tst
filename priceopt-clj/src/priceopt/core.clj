(ns priceopt.core
  (:require [clojure.set :as set])
  (:use [clojure.pprint :only [pprint]])
  (:gen-class))

(defn get-best-group-prices
  "Returns a seq of the least prices for each [::cabin-code ::rate-group], augmented with ::rate-group"
  ([rates prices]
   (->> prices
        (set/join rates)
        (group-by #(select-keys %1 [::cabin-code ::rate-group]))
        (map (fn [[k vs]] (first (sort-by ::price vs))))
        (into #{}))))

(defn rate [rate-code rate-group] {::rate-code rate-code, ::rate-group rate-group})
(defn cabin-price [cabin-code rate-code price] {::cabin-code cabin-code, ::rate-code rate-code, ::price price})
(defn best-cabin-price [cabin-code rate-code price rate-group]
  {::cabin-code cabin-code,
   ::rate-code rate-code,
   ::price price,
   ::rate-group rate-group})

(def sample-rates
  [(rate "M1" "Military")
   (rate "M2" "Military")
   (rate "S1" "Senior")
   (rate "S2" "Senior")])

(def sample-cabin-prices
  [(cabin-price "CA" "M1" 200)
   (cabin-price "CA" "M2" 250)
   (cabin-price "CA" "S1" 225)
   (cabin-price "CA" "S2" 260)
   (cabin-price "CB" "M1" 230)
   (cabin-price "CB" "M2" 260)
   (cabin-price "CB" "S1" 245)
   (cabin-price "CB" "S2" 270)])

(def expected-best
  #{(best-cabin-price "CA" "M1" 200 "Military")
    (best-cabin-price "CA" "S1" 225 "Senior")
    (best-cabin-price "CB" "M1" 230 "Military")
    (best-cabin-price "CB" "S1" 245 "Senior")})

(defn -main [& args]
  (let [computed-best (get-best-group-prices sample-rates sample-cabin-prices)]
    (pprint computed-best)))
