(ns rumble.status-go
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json]
            [rumble.native.status-go :as native]
            [rumble.util :as util]))

;; maybe switch to bigint at some point, but remember to stringify for :id
(def ^:private id (atom 0))

;; for call vs. call-private could use multimethods approach to
;; distinguish on the method

;; need helpers to transform numbers to hex, etc., might be able to use spec to
;; validate requests before calling to native

(def base-req ^:private {:jsonrpc "2.0" :params []})

(defn call [request]
  (let [request (merge base-req {:id (swap! id inc)} request)
        req-json (json/write-str request)
        res-json (native/call-rpc req-json)]
    (json/read-str res-json)))

(defn call-private [request]
  (let [request (merge base-req {:id (swap! id inc)} request)
        req-json (json/write-str request)
        res-json (native/call-private-rpc req-json)]
    (json/read-str res-json)))

(defn hash-message [message]
  ((json/read-str (native/hash-message message)) "result"))

(defn set-signal-event-callback! [f]
  (let [f* #(f (json/read-str %))]
    (native/set-signal-event-callback! f*)))

;;;;;;; async variants ;;;;;;;

(def ^:private <call* (util/asyncify call 1))

(defn <call [request]
  (async/take 1 (<call* request)))

(def ^:private <call-private* (util/asyncify call-private 1))

(defn <call-private [request]
  (async/take 1 (<call-private* request)))

(def ^:private <hash-message* (util/asyncify hash-message 1))

(defn <hash-message [message]
  (async/take 1 (<hash-message* message)))
