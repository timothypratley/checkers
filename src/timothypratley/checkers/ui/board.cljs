(ns timothypratley.checkers.ui.board
  (:require
   [timothypratley.checkers.domain :as domain]
   [reagent.core :as reagent]
   [clojure.string :as str]))

;; TODO: where?
(def selected (reagent/atom nil))

(defn squares [app-state]
  (for [x (range 8)
        y (range 8)]
    [:rect
     {:x x
      :y y
      :width 1
      :height 1
      :fill (cond
              (= [x y] @selected) "deepskyblue"
              (even? (+ x y)) "white"
              :else "green")
      :on-click
      (fn square-click [e]
        ;; TODO: what about background square
        (when @selected
          (println (swap! app-state update-in [:games (:current-game @app-state)]
                          domain/move @selected [x y]))))}]))

(defn icon-background [r icon]
  [:g
   {:dangerouslySetInnerHTML
    {:__html
     (str "<defs>
   <pattern id=\"" icon
   "\" patternUnits=\"userSpaceOnUse\" height=\"" (* r 2)
   "\" width=\"" (* r 2)
   "\" patternTransform=\"translate(" (- r) "," (- r) ")\">
     <image height=\"" (* r 2)
     "\" width=\"" (* r 2)
     "\" xlink:href=\"" icon "\"></image>
   </pattern>
  </defs>
  <circle r=\"" r "\" fill=\"url(#" icon ")\"/>")}}])

(defn champion [{:keys [name color icon]} x y]
  [:g
   {:transform (str "translate(" (+ x 0.5) " " (+ y 0.5) ")")
    :on-click
    (fn champion-click [e]
      (if (= @selected [x y])
        (reset! selected nil)
        (reset! selected [x y])))}
   [:title name]
   [:circle
    {:stroke color
     :fill "none"
     :stroke-width 0.1
     :r 0.45}]
   [icon-background 0.4 icon]])

(defn board [app-state board]
  (->
   [:svg.noselect
    {:view-box (str/join " " [0 0 8 8])
     :style {:width "100%"
             :height "100%"}}]
   (into (squares app-state))
   (into
    (for [[y row] (map vector (range) board)
          [x c] (map vector (range) row)
          :when c]
      [champion c x y]))
   (conj
    [:rect
     {:x 0
      :y 0
      :stroke "black"
      :stroke-width 0.02
      :fill "none"
      :width 8
      :height 8} ])))
