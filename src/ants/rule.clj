(ns ants.rule
  (:import  (java.awt Color Font))
  (:require [clojure.string :as string]
            [ants.utility :as ut])
  )

;facings
(def north [0 -1])
(def south [0 1])
(def east [1 0])
(def west [-1 0])

(def facings [north east south west])
(def num-facings (count facings))

(def facing-lookup
  {:n north
   :s south
   :e east
   :w west})

;directions (for turning)
(def right 1) ;clockwise
(def left -1)
(def straight 0) ; no turn


(def turn-lookup
  { :r right
   :l left
   :t  straight})

(def default-rules
  {:cell-width 1
   :cell-height 1
   :status-height 16
   :timer-milliseconds 100})

(defn read-rules
  [filename]
  (loop [lines (string/split-lines (slurp filename))
         prefix ""
         rule-map default-rules]
    (if (empty? lines)
      rule-map
      (let [line (first lines)
            lc (string/split line #"#")
            active-line (first lc)]   ;only process first token, as the rest is a comment
        (if (not-empty active-line)
          (let [tokens (map string/trim (string/split active-line #":"))]
            (cond (re-find #"^as " (first tokens))
                  (recur (rest lines) (second (string/split (first tokens) #"\s+")) rule-map)
                  (re-find #"^end " (first tokens))
                  (recur (rest lines) "" rule-map)
                  :else
                  (let [new-key (ut/keyword-append prefix (first tokens))
                        new-val (read-string (second tokens))]
                    (recur (rest lines) prefix (assoc rule-map new-key new-val)))))
          (recur (rest lines) prefix rule-map))))))


(defn rule-to-seq
  [ruleset key-prefix]
  (let [pat (re-pattern (str "^" key-prefix))]
    (map #(ruleset %) (sort (filter #(re-find pat (str %)) (keys ruleset))))))

(defn rule-to-array
  [ruleset key-prefix]
  (into [] (rule-to-seq ruleset key-prefix)))



(defn rule-to-color
  [ruleset key]
  (apply ut/to-color (ruleset key)))

(defn rule-to-color-array
  [ruleset key-prefix]
  (into [] (map #(apply ut/to-color %) (rule-to-seq ruleset key-prefix))))


(defn to-valid-state-lookup
  "Should return an integer or a keyword"
  [i]
  (if-let [po (keyword i)]
    po
    i))

(defn rule-to-agent-rule
  [ruleset key-prefix]
  (for [r (rule-to-seq ruleset key-prefix)]
    (let [rule-type (keyword (first r))
          state-lookup (into [] (map to-valid-state-lookup (rest r)))]
      [rule-type state-lookup])))

(defn main
  [filename]
  (let [ruleset (read-rules filename)]
    (prn ruleset)
    (prn (rule-to-agent-rule ruleset :ant-rule))
    ))
