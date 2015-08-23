(ns timothypratley.checkers.intelligence
  (:require
   [timothypratley.checkers.domain :as domain]))

(defn ai-move [game]
  (rand-nth
   (domain/all-valid-moves game)))
