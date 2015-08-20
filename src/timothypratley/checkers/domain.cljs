(ns timothypratley.checkers.domain)

(def games (atom []))

(defn to-idx [x y]
  (/ (+ x (* y 8)) 2))

(def all-champions
  {"Rich Hickey" {:icon "https://avatars0.githubusercontent.com/u/34045?v=3&s=460"}
   "Stuart Halloway" {:icon "https://avatars2.githubusercontent.com/u/2590?v=3&s=460"}
   "Alex Miller" {:icon "https://avatars2.githubusercontent.com/u/171129?v=3&s=460"}
   "Christophe Grand" {:icon "https://avatars1.githubusercontent.com/u/47025?v=3&s=460"}
   "Andy Fingerhut" {:icon "https://avatars2.githubusercontent.com/u/109629?v=3&s=460"}
   "Stuart Sierra" {:icon "https://avatars0.githubusercontent.com/u/95044?v=3&s=460"}
   "Chris Houser" {:icon "https://avatars2.githubusercontent.com/u/36110?v=3&s=460"}
   "Nicola Mometto" {:icon "https://avatars3.githubusercontent.com/u/149410?v=3&s=460"}
   "Fogus" {:icon "https://avatars2.githubusercontent.com/u/12632?v=3&s=460"}
   "Ben Smith-Mannschott" {:icon "https://avatars3.githubusercontent.com/u/32691?v=3&s=460"}
   "Chas Emerick" {:icon "https://avatars1.githubusercontent.com/u/47489?v=3&s=460"}
   "Tassilo Horn" {:icon "https://avatars1.githubusercontent.com/u/103854?v=3&s=460"}
   "Tom Faulhaber" {:icon "https://avatars2.githubusercontent.com/u/37207?v=3&s=460"}
   "Timothy Pratley" {:icon "https://avatars3.githubusercontent.com/u/49298?v=3&s=460"}
   "Steve Miner" {:icon "https://avatars1.githubusercontent.com/u/25400?v=3&s=460"}
   "Meikel Brandmeyer" {:icon "https://avatars2.githubusercontent.com/u/40587?v=3&s=460"}
   "Daniel Solano Gómez" {:icon "https://avatars0.githubusercontent.com/u/152491?v=3&s=460"}
   "David Powell" {:icon "https://avatars3.githubusercontent.com/u/141011?v=3&s=460"}
   "Paul Stadig" {:icon "https://avatars2.githubusercontent.com/u/5656?v=3&s=460"}
   "Gary Fredericks" {:icon "https://avatars1.githubusercontent.com/u/135443?v=3&s=460"}
   "Alan Malloy" {:icon "https://avatars3.githubusercontent.com/u/368685?v=3&s=460"}
   "Phil Hagelberg" {:icon "https://avatars2.githubusercontent.com/u/141?v=3&s=460"}
   "Aaron Bedra" {:icon "https://avatars1.githubusercontent.com/u/2090?v=3&s=460"}
   "Colin Jones" {:icon "https://avatars3.githubusercontent.com/u/15069?v=3&s=460"}
   "Ghadi Shayban" {:icon "https://avatars2.githubusercontent.com/u/829803?v=3&s=460"}
   "Kevin Downey" {:icon "https://avatars1.githubusercontent.com/u/35954?v=3&s=460"}
   "Michael Blume" {:icon "https://avatars1.githubusercontent.com/u/208853?v=3&s=460"}
   "Mike Hinchey" {:icon "https://avatars3.githubusercontent.com/u/22701?v=3&s=460"}
   "Stephen C. Gilardi" {:icon "https://avatars0.githubusercontent.com/u/87927?v=3&s=460"}
   "Bozhidar Batsov" {:icon "https://avatars0.githubusercontent.com/u/103882?v=3&s=460"}
   "Alan Dipert" {:icon "https://avatars1.githubusercontent.com/u/26024?v=3&s=460"}
   "Cosmin Stejerean" {:icon "https://avatars0.githubusercontent.com/u/1358?v=3&s=460"}})

(def movement
  ["shuffles" "runs" "walks" "struts" "marches" "strolls" "dashes"])

(def attack
  ["immolates" "destroys" "slams" "eviscerates" "atomizes" "slays" "vapourizes" "blasts" "skewers" "jumps"])

(defn new-board []
  (let [champions (shuffle all-champions)]
    (vec (for [y (range 8)]
           (vec (for [x (range 8)]
                  (when (odd? (+ x y))
                    (let [[name champion] (nth champions (to-idx x y))]
                      (cond
                        (< y 3) (assoc champion
                                       :name name
                                       :color :red)
                        (> y 4) (assoc champion
                                       :name name
                                       :color :black))))))))))

(defn new-game []
  {:board (new-board)
   :taken []
   :turn :black
   :moves []})

(def all-neighbours
  (for [x (range 8)
        y (range 8)
        :when (even? (+ x y))
        dx [-1 1]
        dy [-1 1]
        :let [nx (+ x dx)
              ny (+ y dy)]
        :when (and (<= 0 nx 7)
                   (<= 0 ny 7))]
    [x y nx ny]))

(defn mid [[fx fy] [tx ty]]
  [(/ (+ fx tx) 2)
   (/ (+ fy fy) 2)])

(def other
  {:red :black
   :black :red})

(defn jump? [board color [fx fy :as a] [tx ty :as b]]
  (and (= 2 (Math/abs (- tx fx)))
       (= 2 (Math/abs (- ty fy)))
       (= (other color)
          (:color (get-in board (mid a b))))))

(defn blank? [board a]
  (nil? (get-in board a)))

(defn neighbor? [[fx fy] [tx ty]]
  (and (= 1 (Math/abs (- tx fx)))
       (= 1 (Math/abs (- ty fy)))))

(defn forward? [color [fx fy] [tx ty]]
  (if (= :red color)
    (< ty fy)
    (> ty fy)))

(defn king? [board a]
  (:king (get-in board a)))

(defn valid-move? [board color a b]
  (and (blank? board b)
       (= color (:color (get-in board a)))
       (or (jump? board color a b)
           (neighbor? a b))
       (or (king? board a)
           (forward? color a b))))

(defn delete
  [x k]
  (if (vector? x)
    (vec (concat (subvec x 0 k) (subvec x (inc k))))
    (dissoc x)))

(defn dissoc-in [m path]
  (update-in m (butlast path) delete (last path)))

(def inc-or-1 (fnil inc 0))

(defn move [game a b]
  (println "MOVE" game a b)
  (let [pa (cons :board a)
        pb (cons :board b)
        peice (get-in game pa)
        _ (println "P" peice)
        new-peice (update peice :moves inc-or-1)]
    (-> game
        (update :moves conj [:move a b new-peice (rand-nth movement)])
        (dissoc-in pa)
        (assoc-in pb new-peice))))

(defn jump [game a b]
  (let [pa (cons :board a)
        pb (cons :board b)
        peice (get-in game pa)
        new-peice (update peice :jumps inc-or-1)
        m (mid a b)
        pm (cons :board m)
        taken (get-in game pm)]
    (-> game
        (update :moves conj [:jump a b (:name peice) (rand-nth attack)])
        (dissoc-in pa)
        (dissoc-in pm)
        (update :taken conj taken)
        (assoc-in pb new-peice))))
