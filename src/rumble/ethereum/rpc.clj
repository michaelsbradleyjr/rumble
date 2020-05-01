(ns rumble.ethereum.rpc
  (:require [clojure.core.async :as async]
            [clojure.data.json :as json]
            [rumble.native.status-go :as status-go]
            [rumble.util :as util]))

;; maybe switch to bigint at some point, but remember to stringify for :id
(def ^:private id (atom 0))

;; for call-rpc vs. call-private-rpc could use multimethods approach to
;; distinguish on the method

;; need helpers to transform numbers to hex, etc., might be able to use spec to
;; validate requests before calling to status-go

(def base-req ^:private {:jsonrpc "2.0" :params []})

(defn call-rpc [request]
  (let [request (merge base-req {:id (swap! id inc)} request)
        req-json (json/write-str request)
        res-json (status-go/call-rpc req-json)]
    (json/read-str res-json)))

(defn call-private-rpc [request]
  (let [request (merge base-req {:id (swap! id inc)} request)
        req-json (json/write-str request)
        res-json (status-go/call-private-rpc req-json)]
    (json/read-str res-json)))

;; async variants ;;

(def ^:private <call-rpc* (util/asyncify call-rpc))

(defn <call-rpc [request]
  (async/take 1 (<call-rpc* request)))

(def ^:private <call-private-rpc* (util/asyncify call-private-rpc))

(defn <call-private-rpc [request]
  (async/take 1 (<call-private-rpc* request)))
