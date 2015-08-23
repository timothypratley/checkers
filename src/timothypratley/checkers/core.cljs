(ns ^:figwheel-always timothypratley.checkers.core
  (:require
   [reagent.core :as reagent]
   [timothypratley.checkers.ui.main :refer [main]]))

(enable-console-print!)

(defn on-js-reload []
  (reagent/render-component
   [main]
   (. js/document (getElementById "checkers"))))

(on-js-reload)
