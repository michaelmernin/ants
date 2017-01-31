(ns ants-clojure.core
  (:require [clojure.java.io :as io])
  (:gen-class :extends javafx.application.Application))

(def width 800)
(def height 600)
(def ant-count 100)

(def ants (atom []))

(defn create-ants []
  (for [i (range ant-count)]
    {:x (rand-int width)
     :y (rand-int height)
     :color (javafx.scene.paint.Color/BLACK)
     :touching? false}))

(defn draw-ants! [context]
  (.clearRect context 0 0 width height)
  (doseq [ant @ants]
    (.setFill context javafx.scene.paint.Color/BLACK)
    (.fillOval context (:x ant) (:y ant) 5 5)))

(defn random-step []
  (- (* 2 (rand)) 1))

(defn move-ant [ant]
  (Thread/sleep 1)
  (assoc ant
    :x (+ (random-step) (:x ant))
    :y (+ (random-step) (:y ant))))

(defn move-ants []
  (pmap move-ant @ants)) ;pmap runs in parallel

(defn red-ants [ant]
  (Thread/sleep 1)
  (let [red
        (filter (fn [r]
                  (and
                       (< (Math/abs (- (:x ant) (:x a))) 17)
                       (< (Math/abs (- (:y ant) (:y a))) 17)))
          @ants)
        numberAnts (count red)]
    (assoc ant :color
      (if (> numberAnts 1)
        javafx.scene.paint.Color/RED))))

(def last-timestamp (atom 0))

(defn fps [now]
  (let [diff (- now @last-timestamp)
        diff-seconds (/ diff 1000000000)]
    (int (/ 1 diff-seconds))))

(defn -start [app stage]
  (let [root (javafx.fxml.FXMLLoader/load (io/resource "main.fxml"))
        scene (javafx.scene.Scene. root width height)
        canvas (.lookup scene "#canvas")
        context (.getGraphicsContext2D canvas)
        fps-label (.lookup scene "#fps")
        timer (proxy [javafx.animation.AnimationTimer] []
                (handle [now]
                  (.setText fps-label (str (fps now)))
                  (reset! last-timestamp now)
                  (reset! ants (move-ants))
                  (draw-ants! context)))]
    (.setTitle stage "Ants")
    (.setScene stage scene)
    (.show stage)
    (reset! ants (create-ants))
    (.start timer)))
     
    ;(draw-ants! context))) ; reset puts data into an atom


(defn -main []
  (javafx.application.Application/launch ants_clojure.core 
    (into-array String[])))
