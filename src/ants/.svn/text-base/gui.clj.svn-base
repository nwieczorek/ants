
(ns ants.gui
  (:import (javax.swing JFrame Timer JPanel)
         (java.awt.event KeyListener ActionListener)
         (java.awt Color RenderingHints Toolkit Image Font))
  (:use ants.utility)
  (:require [ants.rule :as rule]
            [ants.world :as world]))




;=============================================================================
(defn get-text-height
  [^java.awt.Graphics2D g2d]
  (let [frc (.getFontRenderContext g2d)
        lm (.getLineMetrics (.getFont g2d), "A", frc )]
    (int (.getAscent lm))))

(defn translate-coords
  [x y cell-width cell-height]
  [ (* x cell-width) (* y cell-height)])

(defn ants-panel
  [ruleset ]
  (let [cell-width (ruleset :cell-width)
        cell-height (ruleset :cell-height)
        text-x (* 2 cell-width)
        text-y (+ (* (ruleset :world-height) cell-height) 1)
        state-colors (rule/rule-to-color-array ruleset :state-color)
        world (atom (world/make-world ruleset))
        counter (atom 0)
        pnl (doto (proxy [javax.swing.JPanel] []
                    (paintComponent [^java.awt.Graphics g]
                      (proxy-super paintComponent g)
                      (let [g2d (doto ^java.awt.Graphics2D
                                  (.create g))
                            text-height (get-text-height g2d)]
                        (doseq [x (range (ruleset :world-width))
                                y (range (ruleset :world-height))]
                          (let [state (world/get-state @world x y)
                                [rx ry] (translate-coords x y cell-width cell-height)]
                            (.setColor g2d  (state-colors state))
                            (.fillRect g2d rx ry cell-width cell-height)))
                        (doseq [agnt (@world :agents)]
                          (let [[x y] (agnt :position)
                                color (agnt :color)
                                [rx ry] (translate-coords x y cell-width cell-height)]
                            (.setColor g2d color)
                            (.fillRect g2d rx ry cell-width cell-height)))
                        (.setColor g2d Color/BLACK)
                        (.drawString g2d (str "Iteration " @counter) text-x (+ text-height text-y))
                        ))
                    ))
        timer (Timer. (ruleset :timer-milliseconds)
                      (proxy [ActionListener] []
                        (actionPerformed [event]
                          (reset! world (world/update @world))
                          (reset! counter (inc @counter))
                          (.repaint pnl)
                          )))]
    (.start timer)
    pnl
    ))



(defn get-display-width
  [ruleset]
  (* (ruleset :world-width) (ruleset :cell-width) ))

(defn get-display-height
  [ruleset]
  (+ (* (ruleset :world-height) (ruleset :cell-height))
    (ruleset :status-height))) 
        
