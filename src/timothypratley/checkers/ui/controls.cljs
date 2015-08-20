(ns timothypratley.checkers.ui.controls
  (:require [timothypratley.checkers.domain :as domain]))

(defn controls [app-state]
  [:div
   [:button.btn.btn-default
    {:on-click
     (fn new-game [e]
       (swap! app-state update :games conj (domain/new-game)))}
    "New game"]])
