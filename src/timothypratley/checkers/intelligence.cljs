(ns timothypratley.checkers.intelligence
  "http://en.chessbase.com/post/500-billion-billion-moves-later-computers-solve-checkers"
  (:require
   [timothypratley.checkers.domain :as domain]))

(defn threatened? [game color [x y]]
  (seq
   (for [dx [-1 1]
         dy [-1 1]
         :let [from [(+ x dx) (+ y dy)]
               to [(- x dx) (- y dy)]]
         :when (and (domain/on-board from)
                    (= (domain/other color)
                       (get-in game (cons :board (conj from :color))))
                    (domain/valid-move? (:board game) (domain/other color) from to))]
     [from to])))

(defn threatens? [game color [x y]]
  (seq
   (for [dx [-2 2]
         dy [-2 2]
         :let [to [(+ x dx) (+ y dy)]]
         :when (domain/valid-move? (:board game) color [x y] to)]
     [[x y] to])))

(defn score [game [x y] for-color]
  (if-let [{:keys [color king?] :as champion} (get-in game [:board x y])]
    (let [sign (if (= color for-color) + -)]
      (sign
       (cond
         ;;turn decides order?
         (threatened? game color [x y]) 0.5
         (threatens? game color [x y]) 1.3
         king? 1.2
         (#{0 7} x) 1.1
         :else 1)))
    0))

(defn value [game value-for-color]
  (if (:winner game)
    (if (= (:winner game) value-for-color)
      1000
      -1000)
    (reduce
     +
     (for [x (range 8)
           y (range 8)]
       (score game [x y] value-for-color)))))

(defn with-move [game move]
  (domain/apply-move game move))

(defn easy [game moves]
  (rand-nth
   (domain/all-valid-moves game)))

(defn max-value-move [game moves]
  (apply
   max-key
   (fn a-move-value [move]
     (value (with-move game move) (:turn game)))
   moves))

(defn min-max-reply
  ([game moves] (min-max-reply game moves 0))
  ([game moves depth]
   (apply
    min-key
    (fn opponent-move-value [move]
      (let [g (with-move game move)
            replies (domain/all-valid-moves g)]
        (cond
          (:continue g) -500
          (empty? replies) -1000
          :else (value
                 (with-move g (if (zero? depth)
                                (max-value-move g replies)
                                (min-max-reply g replies (dec depth))))
                 (:turn g)))))
    moves)))

(defn selector [level]
  (case level
    :easy (fn [game moves]
            (rand-nth moves))
    :hard min-max-reply
    :deathmatch (fn [game moves]
                  (min-max-reply game moves 1))
    max-value-move))

(defn choose-move [game level]
  (when-let [moves (domain/all-valid-moves game)]
    ((selector level) game moves)))
