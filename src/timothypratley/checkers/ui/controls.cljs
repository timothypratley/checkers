(ns timothypratley.checkers.ui.controls
  (:require [clojure.string :as str]
            [timothypratley.checkers.store :as store]))

(defn controls []
  [:div
   [:br] [:br] [:br] [:br] [:br]
   [:div.btn-group {:role "group"}
    [:button.btn.btn-default
     {:type "button"
      :class (when (= (store/level) :easy) "active")
      :on-click (fn [e] (store/level :easy))}
     "Easy"]
    [:button.btn.btn-default
     {:type "button"
      :class (when (= (store/level) :normal) "active")
      :on-click (fn [e] (store/level :normal))}
     "Normal"]
    [:button.btn.btn-default
     {:type "button"
      :class (when (= (store/level) :hard) "active")
      :on-click (fn [e] (store/level :hard))}
     "Hard"]
    [:button.btn.btn-default
     {:type "button"
      :class (when (= (store/level) :deathmatch) "active")
      :on-click (fn [e] (store/level :deathmatch))}
     "Deathmatch!"]]
   [:button.btn.btn-default.pull-right
    {:on-click
     (fn new-game [e]
       (store/new-game!))}
    "New game"]
   (into
    [:ul.nav.nav-pills.nav-stacked]
    (map-indexed
     (fn [game-idx g]
       [:li
        {:role "presentation"
         :class (when (= game-idx (:current-game @store/db))
                  "active")}
        [:a {:href "#"
             :on-click
             (fn game-click [e]
               (store/set-current-game! game-idx))}
         (str "Game " game-idx
              (when-let [winner (get-in @store/db [:games game-idx :winner])]
                (str " " (name winner) " won")))]
        (when (= game-idx (:current-game @store/db))
          (into
           [:ul.nav.nav-pills.nav-stacked
            {:style {:font-family "Arial"}}]
           (map-indexed
            (fn [move-idx [type from to name verb taken]]
              [:li
               {:role "presentation"}
               [:a {:href "#"
                    :on-click (fn [e] (store/replay! game-idx move-idx))}
                (let [text (str/join " " [name verb taken from "to" to])]
                  (if (= :jump type)
                    [:strong text]
                    text))]])
            (store/get-current-game :moves))))])
     (:games @store/db)))])
