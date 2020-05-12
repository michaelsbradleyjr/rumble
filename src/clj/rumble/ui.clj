(ns rumble.ui
  (:import [javafx.scene.input KeyCode KeyEvent])
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [clojure.core.async :as async]
            [clojure.core.cache :as cache]
            [clojure.edn :as edn]
            [rumble.kludge :as kludge]
            [rumble.ui.styles :as styles]))

(defn placeholder []
  {:fx/type :v-box
   :spacing 5
   :fill-width true
   :alignment :top-center
   :children []})

(defn tab-graphic [path]
  {:fx/type :image-view
   :fit-width 28
   :fit-height 28
   :style-class "app-tab"
   :image path})

(defn topics []
  {:fx/type :anchor-pane
   :min-width 56
   :children []})

(defn messages []
  {:fx/type :anchor-pane
   :children []})

(defn chats []
  {:fx/type :split-pane
   :divider-positions [0.25]
   :items [(topics) (messages)]})

(defn chats-tab []
  {:fx/type :tab
   :graphic (tab-graphic "images/message@3x.png")
   :closable false
   :content (chats)})

(defn wallet-tab []
  {:fx/type :tab
   :graphic (tab-graphic "images/wallet@3x.png")
   :closable false
   :content (placeholder)})

(defn browser-tab []
  {:fx/type :tab
   :graphic (tab-graphic "images/browser@3x.png")
   :closable false
   :content (placeholder)})

(defn profile-tab []
  {:fx/type :tab
   :graphic (tab-graphic "images/profile@3x.png")
   :closable false
   :content (placeholder)})

(defn debug-log-tab []
  {:fx/type :tab
   :graphic (tab-graphic "images/history@3x.png")
   :closable false
   :content (placeholder)})

(defn app-tabs []
  {:fx/type :tab-pane
   :pref-width 1024
   :pref-height 768
   :side :left
   :tab-max-height 30
   :tab-max-width 30
   :tab-min-height 30
   :tab-min-width 30
   :tabs [(chats-tab)
          (wallet-tab)
          (browser-tab)
          (profile-tab)
          (debug-log-tab)]})

(defn first-ui-attempt []
  (fx/on-fx-thread
   (fx/create-component
    {:fx/type :stage
     :showing true
     :title "Rumble"
     :scene {:fx/type :scene
             :stylesheets [(::css/url styles/app)]
             :root (app-tabs)}})))

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
