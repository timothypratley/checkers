(ns timothypratley.checkers.ui.main
  (:require
   [timothypratley.checkers.store :as store]
   [timothypratley.checkers.ui.board :refer [board]]
   [timothypratley.checkers.ui.controls :refer [controls]]
   [timothypratley.checkers.ui.history :refer [history]]))

(defn main []
  [:div
   [:h1
    {:style {:font-family "Courier New"
             :text-align "center"}}
    "Deathmatch Checkers"]
   [:div.row
    [:div.col-xs-6
     [board (store/get-current-game :board)]]
    [:div.col-xs-6
     [controls]
     [history]]]])
