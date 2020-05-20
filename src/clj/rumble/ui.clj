(ns rumble.ui
  (:import [javafx.scene.input KeyCode KeyEvent])
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [clojure.core.async :as async :refer [thread]]
            [clojure.core.cache :as cache]
            [clojure.edn :as edn]
            [rumble.kludge :as kludge]
            [rumble.status-go :as status-go]
            [rumble.ui.state :as state]
            [rumble.ui.styles :as styles]))

(defn placeholder [{:keys [children]}]
  {:fx/type :v-box
   :spacing 5
   :fill-width true
   :alignment :top-center
   :children children})

(defn topics [{:keys [children]}]
  {:fx/type :anchor-pane
   :min-width 56
   :children children})

(defn messages [{:keys [children]}]
  {:fx/type :anchor-pane
   :children children})

(defn chats [{:keys [topics messages]}]
  {:fx/type :split-pane
   :divider-positions [0.34]
   :items [{:fx/type topics
            :children []}
           {:fx/type messages
            :children []}]})

(defn app-tab-graphic [{:keys [path]}]
  {:fx/type :image-view
   :fit-width 28
   :fit-height 28
   :style-class "app-tab-graphic"
   :image path})

(defn app-tab [{:keys [content path]}]
  {:fx/type :tab
   :graphic {:fx/type app-tab-graphic
             :path path}
   :closable false
   :content content})

(def app-tabs
  [{:fx/type app-tab
    :content {:fx/type chats
              :topics topics
              :messages messages}
    :path "images/message@3x.png"}
   {:fx/type app-tab
    :content {:fx/type placeholder
              :children []}
    :path "images/wallet@3x.png"}
   {:fx/type app-tab
    :content {:fx/type placeholder
              :children []}
    :path "images/browser@3x.png"}
   {:fx/type app-tab
    :content {:fx/type placeholder
              :children []}
    :path "images/profile@3x.png"}
   {:fx/type app-tab
    :content {:fx/type placeholder
              :children []}
    :path "images/history@3x.png"}])

(defn root [{:keys [tabs]}]
  {:fx/type :stage
   :showing true
   :title "Rumble"
   :scene {:fx/type :scene
           :stylesheets [(::css/url styles/app)]
           :root {:fx/type :tab-pane
                  :pref-width 1232
                  :pref-height 770
                  :side :left
                  :tab-max-height 30
                  :tab-max-width 30
                  :tab-min-height 30
                  :tab-min-width 30
                  :tabs tabs}}})

(defn first-ui-attempt []
  (fx/on-fx-thread
   (fx/create-component
    {:fx/type root
     :tabs app-tabs})))

;; (setImplicitExit true) results in the jvm shutting down when the app window
;; is closed; might be a short-term solution until the app takes on more shape
;; (could do an explicit exit for quit action); don't want it when developing
;; the app, only when doing `lein run` or running packaged app
(defn start! []
  (when (System/getProperty "rumble.fx-implicit-exit")
    (javafx.application.Platform/setImplicitExit true))
  (thread
    (kludge/login!)
    (status-go/set-signal-event-callback!
     (fn [s] (swap! state/*signals #(conj % s)))))
  (first-ui-attempt)
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; IDEAS

;; in scene map: :on-key-pressed {:event/type :event/scene-key-press}
;; maybe can capture cmd-Q

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
