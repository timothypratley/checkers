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

(defn history [app-state]
  [:div]
  [:div
   (into
    [:ul.list-unstyled]
    (for [g (:games @app-state)]
      [:li [game-summary g]]))])
