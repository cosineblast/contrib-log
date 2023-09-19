(ns progress
  (:require [clojure.string :as s])
  (:import [java.time LocalDate Duration]))

(def hours-regex #"(\d+)\s+horas?\.?")
(def minutes-regex #"(\d+)\s+minutos?\.?")
(def hours-minutes-regex #"(\d+)\s+horas?\s+e\s+(\d+)\s+minutos?\.?")

(defn parse-time [time]
  #_(println time)
  (if-let [[_ hours] (re-matches hours-regex time)]
    (Integer/parseInt hours)
    (if-let [[_ minutes] (re-matches minutes-regex time)]
      (/ (Integer/parseInt minutes) 60)
      (if-let [[_ hours minutes] (re-matches hours-minutes-regex time)]
        (+ (Integer/parseInt hours) (/ (Integer/parseInt minutes) 60))
        (throw (Exception. (str "Invalid time format: " time)))))))

(defn get-hours-from-log []
  (->> (slurp "log.org")
       (s/split-lines)
       (filter #(.contains % "Tempo gasto:"))
       (map s/trim)
       (map #(.substring % 12))
       (map s/trim)
       (map parse-time)
       (apply +)
       (double)))

(def semester-start-date "2023-08-07")

(defn get-days-since-semester-start []
  (let [start (LocalDate/parse semester-start-date)
        now (LocalDate/now)]

    (println "Today is " (.toString now))

    (.toDays (Duration/between
              (.atStartOfDay start)
              (.atStartOfDay now)))))

(def semester-size 136)

(defn get-hours-status []

  (let [hours (get-hours-from-log)
        hours-percent (/ hours 100)
        days (get-days-since-semester-start)
        days-percent (double (/ days semester-size))
        hours-as-days (* hours-percent semester-size)
        days-late (- days hours-as-days)]
    (println (format "Hours percent: %.4f" hours-percent))
    (println (format "Days percent: %.4f" days-percent))
    (println (format "Late by %.2f days" days-late))))

(get-hours-status)
