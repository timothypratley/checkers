(ns timothypratley.checkers.ui.history)

(defn game-summary [game]
  [:div
   (into
    [:dl.dl-horizontal]
    (apply
     concat
     (for [[k v] (dissoc game :board)]
       [[:dt (name k)]
        [:dd (pr-str v)]])))])

(defn history [games]
  [:div]
  [:div
   (into
    [:ul.list-unstyled]
    (for [g games]
      [:li [game-summary g]]))])
