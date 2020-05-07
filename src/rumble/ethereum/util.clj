(ns rumble.ethereum.util
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json]
            [rumble.native.status-go :as status-go]
            [rumble.util :as util]))

(defn hash-message [message]
  ((json/read-str (status-go/hash-message message)) "result"))

(defn set-signal-event-callback! [f]
  (let [f* #(f (json/read-str %))]
    (status-go/set-signal-event-callback f*)))

;; async variants ;;

(def ^:private <hash-message* (util/asyncify hash-message))

(defn <hash-message [message]
  (async/take 1 (<hash-message* message)))
