(ns timothypratley.checkers.store
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [<! timeout]]
   [clojure.data]
   [timothypratley.checkers.domain :as domain]
   [timothypratley.checkers.intelligence :as intelligence]
   [reagent.core :as reagent]))

(defonce db
  (reagent/atom
   {:games [(domain/new-game)]
    :current-game 0
    :current-move 0
    :level :normal}))

(defn get-current-game [& args]
  (get-in @db (concat [:games (:current-game @db)] args)))

(defn unselect! []
  (swap! db dissoc :selected))

(defn select! [a]
  (swap! db assoc :selected a))

(defn selected []
  (:selected @db))

(defn continue []
  (get-current-game :continue))

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
  (swap! db assoc :thinking true)
  (go
    (while (and (not (get-current-game :winner))
               (= :red (get-current-game :turn)))
      (let [m (intelligence/choose-move (get-current-game) (:level @db))]
        (swap! db update-in [:games (:current-game @db)]
               domain/apply-move m))
      (when (get-current-game :continue)
        (<! (timeout 500))))
    (swap! db dissoc :thinking)))

(defn continue-jump [c p]
  (let [m (domain/valid-move?
           (get-current-game :board)
           (current-color)
           c
           p)]
    (when m
      (raise-and-next! m))))

(defn click [p]
  (if-let [c (get-current-game :continue)]
    (continue-jump c p)
    (cond
      (= p (selected)) (unselect!)
      (selectable? p) (select! p)
      :else (when-let [m (move? p)]
              (raise-and-next! m)))))

(defn with-new-game [db]
  (-> db
      (update :games conj (domain/new-game))
      (update :current-game inc)))

(defn new-game! []
  (swap! db with-new-game))

(defn set-current-game! [idx]
  (swap! db assoc :current-game idx))

(defn level
  ([] (:level @db))
  ([l] (swap! db assoc :level l)))

(defn replay! [game-idx move-idx]
  (new-game!)
  (doseq [move (take (inc move-idx) (get-in @db [:games game-idx :moves]))]
    (swap! db update-in [:games (:current-game @db)]
           domain/apply-move move)))
