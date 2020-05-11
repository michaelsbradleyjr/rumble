(ns rumble.ui
  (:import [javafx.scene.input KeyCode KeyEvent])
  (:require [cljfx.api :as fx]
            [clojure.core.async :as async]
            [clojure.core.cache :as cache]
            [clojure.edn :as edn]
            [rumble.kludge :as kludge]))

(defn first-ui-attempt []
  (fx/on-fx-thread
   (fx/create-component
    {:fx/type :stage
     :showing true
     :title "Rumble"
     :width 1024
     :height 768
     :scene {:fx/type :scene
             :root {:fx/type :v-box
                    :alignment :center
                    :children [{:fx/type :label
                                :text "Hello world"}]}}})))

;; (setImplicitExit true) results in the jvm shutting down when the app window
;; is closed; might be a short-term solution until the app takes on more shape
;; (could do an explicit exit for quit action); don't want it when developing
;; the app, only when doing `lein run` or running packaged app
(defn start! []
  (when (System/getProperty "rumble.fx-implicit-exit")
    (javafx.application.Platform/setImplicitExit true))
  ;; (kludge/login!)
  (first-ui-attempt)
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; in scene map: :on-key-pressed {:event/type :event/scene-key-press}
;; maybe can capture cmd-Q

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
