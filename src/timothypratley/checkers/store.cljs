(ns timothypratley.checkers.store
  (:require
   [clojure.data]
   [timothypratley.checkers.domain :as domain]
   [timothypratley.checkers.intelligence :as intelligence]
   [reagent.core :as reagent]))

(defonce db
  (reagent/atom
   {:games [(domain/new-game)]
    :current-game 0
    :current-move 0}))

(defn get-current-game [& args]
  (get-in @db (concat [:games (:current-game @db)] args)))

(defn unselect! []
  (swap! db dissoc :selected))

(defn select! [a]
  (swap! db assoc :selected a))

(defn selected []
  (:selected @db))

(defn current-color []
  :black)

(defn selectable? [[x y]]
  (= (get-current-game :board x y :color)
     (current-color)))

(defn move? [to]
  (and (selected)
       (domain/valid-move?
        (get-current-game :board)
        (current-color)
        (selected)
        to)))

(defn raise-and-next! [move]
  (swap! db update-in [:games (:current-game @db)]
         domain/apply-move move)
  (unselect!)
  (when (= :red (get-current-game :turn))
    (when-let [m (intelligence/ai-move (get-current-game))]
      (swap! db update-in [:games (:current-game @db)]
             domain/apply-move m))))

(defn click [p]
  (cond
    (= p (selected)) (unselect!)
    (selectable? p) (select! p)
    :else (when-let [m (move? p)]
            (raise-and-next! m))))
