(ns rumble.native.status-go.signal-event-callback
  (:gen-class :name rumble.native.status-go.signal-event-callback.SignalEventCallback
              :implements [com.sun.jna.Callback]
              :constructors {[clojure.lang.IFn] []}
              :init init
              :state state
              :prefix "-"
              :methods [[callback [String] Void]]))

(defn -init [f]
  [[] (atom f)])

(defn -callback [this signal]
  (@(.state this) signal)
  nil)
