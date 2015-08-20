(ns ^:figwheel-always timothypratley.checkers.core
    (:require
     [reagent.core :as reagent :refer [atom]]
     [timothypratley.checkers.domain :refer [new-game]]
     [timothypratley.checkers.ui.board :refer [board]]
     [timothypratley.checkers.ui.controls :refer [controls]]
     [timothypratley.checkers.ui.history :refer [history]]))

(enable-console-print!)

(defonce app-state (atom {:games [(new-game)]
                          :current-game 0
                          :current-move 0}))

(defn hello-world []
  [:div
   [:h1
    {:style {:font-family "Courier New"
             :text-align "center"}}
    "Deathmatch Checkers"]
   [:div.row
    [:div.col-xs-6
     [board app-state (get-in @app-state [:games (:current-game @app-state) :board])]]
    [:div.col-xs-6
     [controls app-state]
     [history app-state]]]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  (reagent/render-component [hello-world]
                            (. js/document (getElementById "app"))))
