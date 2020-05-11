(ns rumble.core
  (:gen-class)
  (:require [rumble.ui :as ui]))

(defn -main [& args]
  (ui/start!))
