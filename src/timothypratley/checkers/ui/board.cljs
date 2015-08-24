(ns timothypratley.checkers.ui.board
  (:require
   [timothypratley.checkers.store :as store]
   [reagent.core :as reagent]
   [clojure.string :as str]))

(defn gandalf [x y]
  [:g
   {:dangerouslySetInnerHTML
    {:__html (str "<image xlink:href=\"http://media.giphy.com/media/TcdpZwYDPlWXC/giphy.gif\""
                  " height=\"8\" width=\"8\" x=\"" x "\" y=\"" y "\" />")}}])

(defn squares [winner?]
  (concat
   (for [x (range 8)
         y (range 8)]
     [:rect
      {:x x
       :y y
       :width 1
       :height 1
       :fill (cond
               (= [x y] (store/continue)) "purple"
               (= [x y] (store/selected)) "deepskyblue"
               (odd? (+ x y)) "green"
               :else "white")
       :on-click
       (fn square-click [e]
         (store/click [x y]))}])
   (when winner?
     [[gandalf]])))

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

(defn crown [color]
  [:path
   {:fill color
    :transform (str "translate(-1,-1) scale(0.002)")
    :d "m 493.05175,135.39141 c -17.08748,14.98171 -19.63646,34.97622 -18.07378,51.51026 -1.14674,-0.25352 -2.39176,-0.30123 -3.61475,-0.30123 -9.06918,0 -16.41701,7.34788 -16.41701,16.41701 0,9.06908 7.34783,16.41701 16.41701,16.41701 3.95353,0 7.70892,-1.4378 10.54304,-3.76537 0.3393,0.85 0.60245,1.50615 0.60245,1.50615 -15.77069,47.82601 -53.29595,24.09577 -66.42112,10.54304 3.83631,-2.03752 6.47643,-6.04705 6.47643,-10.69365 0,-6.69969 -5.3495,-12.1998 -12.04918,-12.1998 -6.69968,0 -12.1998,5.50011 -12.1998,12.1998 0,6.69973 5.50016,12.04918 12.1998,12.04918 0.29063,0 0.46751,0.0193 0.75307,0 -3.24282,36.26111 -25.19306,52.46167 -54.82378,13.55533 0.44871,0.0506 1.04375,0 1.50614,0 6.69974,0 12.04919,-5.50011 12.04919,-12.19979 0,-6.69974 -5.34945,-12.04919 -12.04919,-12.04919 -5.56547,0 -10.31945,3.77665 -11.74795,8.88627 -6.44954,-10.2899 -36.45447,-16.88765 -26.05636,-8.43442 26.19165,21.29274 59.49285,94.88732 59.49285,94.88732 l 0.15037,0 c 8.77581,-23.0231 56.48465,-40.666 114.0154,-40.666 57.52623,0 105.08354,17.6456 113.86479,40.666 l 0.15038,0 c 6.68088,-14.29173 35.0397,-76.23234 57.9867,-94.88732 10.3981,-8.45323 -19.60682,-1.85548 -26.05637,8.43442 -1.4285,-5.10962 -6.03182,-8.88627 -11.59733,-8.88627 -6.69969,0 -12.1998,5.34945 -12.1998,12.04919 0,6.69968 5.50011,12.19979 12.1998,12.19979 0.4622,0 0.90658,0.0516 1.35553,0 -29.63073,38.90634 -51.58106,22.70578 -54.82379,-13.55533 0.28533,0.0193 0.61326,0 0.90369,0 6.69968,0 12.04918,-5.34945 12.04918,-12.04918 0,-6.69969 -5.3495,-12.1998 -12.04918,-12.1998 -6.69968,0 -12.1998,5.50011 -12.1998,12.1998 0,4.6466 2.64017,8.65613 6.47644,10.69365 -13.12518,13.55273 -50.49982,37.28297 -66.27052,-10.54304 0,0 0.26316,-0.65615 0.60246,-1.50615 2.83407,2.32757 6.43885,3.76537 10.39243,3.76537 9.06908,0 16.41701,-7.34793 16.41701,-16.41701 0,-9.06913 -7.34793,-16.41701 -16.41701,-16.41701 -1.22305,0 -2.46801,0.0482 -3.61476,0.30123 1.56268,-16.53404 -0.83568,-36.52855 -17.92316,-51.51026 z"}])

(defn champion [{:keys [name color icon king? jumps moves] :as c} x y]
  (let [selected? (#{(store/continue) (store/selected)} [x y])]
    [:g
     {:transform (str "translate(" (+ x 0.5) " " (+ y 0.5) ")"
                      (when selected?
                        (str " scale(" 1.25 ")")))
      :on-click
      (fn champion-click [e]
        (store/click [x y]))}
     (cond-> [:title
              [:strong name]]
       jumps (into
              [[:br] (str jumps "  jumps")])
       moves (into
              [[:br] (str moves " moves")]))
     (when (not selected?)
       [:circle
        {:stroke color
         :fill "none"
         :stroke-width 0.1
         :r 0.45}])
     [icon-background 0.4 icon]
     (when king? [crown color])]))

(defn board [{:keys [board winner]}]
  (->
   [:svg.noselect
    {:view-box (str/join " " [-0.3 -0.3 8.3 8.3])
     :style {:width "100%"
             :height "100%"}}]
   (into (squares (= winner :black)))
   (into
    (for [[x row] (map vector (range) board)
          [y c] (map vector (range) row)
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
      :height 8}])
   (conj
    (when (or winner (:thinking @store/db))
      [:text
       {:x 4
        :y 4
        :stroke "black"
        :stroke-width 0.02
        :fill "gold"
        :font-size 1.5
        :text-anchor "middle"}
       (cond
         (:thinking @store/db) "Thinking"
         (= :black winner) "You won!"
         :else "You lost.")]))))
