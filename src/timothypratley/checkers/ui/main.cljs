(ns timothypratley.checkers.ui.main
  (:require
   [timothypratley.checkers.store :as store]
   [timothypratley.checkers.ui.board :refer [board]]
   [timothypratley.checkers.ui.controls :refer [controls]]))

(defn main []
  [:div
   {:style {:font-family "Courier New"}}
   [:div.row
    [:div.col-xs-8
     [:h1
      {:style {:text-align "center"}}
      "Deathmatch Checkers"]
     [board (store/get-current-game)]
     (when (= :black (store/get-current-game :winner))
       [:audio {:controls "true"
                :auto-play "true"}
        [:source {:src "http://download1792.mediafire.com/lmy78nj783cg/c44g2atgst76u3q/Epic+sax+Guy.mp3"
                  :type "audio/mpeg"}]
        "Your browser does not support the audio element."])
     (when (seq (store/get-current-game :taken))
       [:div {:style {:text-align "center"}}
        [:h4 "Fallen heros:"]
        (into
         [:ul.list-unstyled]
         (for [champion (sort-by (juxt :jumps :moves) (store/get-current-game :taken))]
           [:p {:style {:color (name (:color champion))}}
            (pr-str (dissoc champion :icon :color))]))])]
    [:div.col-xs-4
     [controls]]]])
