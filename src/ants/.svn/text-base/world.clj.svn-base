(ns ants.world
  (:require [ants.utility :as utility]
            [ants.rule :as rule]))

(defn wrap-coordinate
  [x limit]
  (if (< x 0)
    (+ limit x)
    (if (>= x limit)
      (- x limit)
      x)))
;===================================================
; Points




(defn turn
  [current direction]
  (let [idx (.indexOf rule/facings current)
        new-idx (wrap-coordinate (+ idx direction) rule/num-facings)]
    (rule/facings new-idx)))


(defn add-point
  [p1 p2]
  (let [[x1 y1] p1
        [x2 y2] p2]
    [(+ x1 x2) (+ y1 y2)]))


(defn move
  [world p dir]
  (let [[px py] (add-point p dir)]
    [(wrap-coordinate px (world :width)) (wrap-coordinate py (world :height))]))


(defn rand-position
  [ruleset]
  [(rand-int (ruleset :world-width)) (rand-int (ruleset :world-height))])

(defn center-position
  [ruleset]
  [ (quot (ruleset :world-width) 2) (quot (ruleset :world-height) 2)])

(defn rand-facing
  []
  (rand-nth rule/facings))

;==========================================================
; Agents
(defn make-agent
  [ruleset agent-id pos-code]
  { :id agent-id
    :rules (rule/rule-to-agent-rule ruleset (utility/keyword-append agent-id :rule))
   ;TODO for num 1-4 position them evenly across the halves/quadrants...5+ is truly random
    :position (case pos-code :random (rand-position ruleset) :center (center-position ruleset))
    :facing rule/north ;(rand-facing)
    :color (rule/rule-to-color ruleset (utility/keyword-append agent-id :color))
   }
  )

(defn make-agents
  [ruleset]
  (let [agent-id :ant
        num-key (utility/keyword-append "num" agent-id)
        num-to-create (ruleset num-key)]
    (if (> num-to-create 1)
      (repeatedly num-to-create #(make-agent ruleset agent-id :random))
      (list (make-agent ruleset agent-id :center)))
  ))
;==========================================================
; World
(defn make-world
  [ruleset]
  (let [ agents (make-agents ruleset)] 
    { :cells (into [] (repeatedly (ruleset :world-width) #(into [] (repeat (ruleset :world-height) 0))))
      :width (ruleset :world-width)
      :height (ruleset :world-height)
      :agents agents
    } 
   ))




(defn get-state
  ([world x y]
   (assert (and (not (nil? x)) (not (nil? y))) (str "X " x " y " y))
   (assert (not (nil? world)) "World is nil!")
   (assert (not (nil? (world :cells))) (str "Cells is not there:" world))
   (assert ((world :cells) x) (str "Column not there " x))
   (((world :cells) x) y))
  ([world p]
   (let [[x y] p]
     (get-state world x y))))

(defn get-cell-state
  ([cells x y]
    ((cells x) y))
  ([cells p]
   (let [[x y] p]
     (get-cell-state cells x y))))

(defn move-agent
  [world agent move-type]
  (let [new-pos (if (= move-type :f)
                  (move world (agent :position) (agent :facing))
                  (when-let [d (rule/facing-lookup move-type)]
                    (move world (agent :position) d)))]
    (assoc agent :position new-pos)))
    
(defn turn-agent
  [world agent turn-type]
  (let [new-facing (if-let [td (rule/turn-lookup turn-type)]
                     (turn (agent :facing) td)
                     (when-let [fd (rule/facing-lookup turn-type)]
                       fd))]
    (assoc agent :facing new-facing)))


(defn update-cell
  ([world x y new-state]
    (let [curr-cells (world :cells)
          curr-col (curr-cells x)
          new-col (assoc curr-col y new-state)]
      (assoc world :cells (assoc curr-cells x new-col))))
  ([world pos new-state]
    (let [[x y] pos]
     (update-cell world x y new-state)))) 

            

(defn update-agents
  [world ]
  (for [a (world :agents)]
    (loop [ag a
           cell-updates ()
           rules (a :rules)]
      (let [loc-state (get-state world (ag :position))] 
        (if (empty? rules)
          [ag cell-updates]
          (let [[rule-type lookup] (first rules)]
            (recur 
              (case rule-type
                :move (move-agent world ag (lookup loc-state))
                :set ag ; no op
                :turn (turn-agent world ag (lookup loc-state)))
              (case rule-type
                :move cell-updates ; no op
                :set (conj cell-updates [ (ag :position) (lookup loc-state)])
                :turn cell-updates ; no op
                )
              (rest rules))))))))


(defn update
  [world]
  (let [agent-updates (update-agents world)
        agents (map #(% 0) agent-updates)
        updates (keep identity (map #(first (% 1)) agent-updates)) ;TODO why is first required?
        world-a (assoc world :agents agents)]
    (if (not-empty updates)
      (reduce (fn [w upd]
                (let [[pos new-state] upd]
                  (update-cell w pos new-state)))  
              world-a updates)
      world-a)
    ))

