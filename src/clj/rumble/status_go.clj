(ns rumble.status-go
  (:import [rumble.native.status-go.signal SignalEventCallback])
  (:require [clojure.core.async :as async :refer [thread]]
            [clojure.data.json :as json]
            [rumble.native.status-go :as native]
            [rumble.util :as util :refer [asyncify def-asyncified-fn]]))

(defn add-peer [enode]
  (json/read-str
   (.AddPeer native/+StatusGo+ enode)))

;; maybe switch to bigint at some point, but remember to stringify for :id
(def ^:private id (atom 0))

;; for call vs. call-private could use multimethods approach to
;; distinguish on the method

;; need helpers to transform numbers to hex, etc., might be able to use spec to
;; validate requests before calling to native

(def base-req ^:private {:jsonrpc "2.0" :params []})

(defn call [request]
  (let [request (merge base-req {:id (swap! id inc)} request)]
    (json/read-str
     (.CallRPC native/+StatusGo+ (json/write-str request)))))

(defn call-private [request]
  (let [request (merge base-req {:id (swap! id inc)} request)]
    (json/read-str
     (.CallPrivateRPC native/+StatusGo+ (json/write-str request)))))

(defn hash-message [message]
  ((json/read-str
    (.HashMessage native/+StatusGo+ message)) "result"))

(defn init-keystore [keystore-dir]
  (json/read-str
   (.InitKeystore native/+StatusGo+ keystore-dir)))

(defn multi-account-generate-and-derive-addresses [settings]
  (json/read-str
   (.MultiAccountGenerateAndDeriveAddresses native/+StatusGo+
                                            (json/write-str settings))))

(defn multi-account-store-derived-accounts [settings]
  (json/read-str
   (.MultiAccountStoreDerivedAccounts native/+StatusGo+
                                      (json/write-str settings))))

(defn open-accounts [data-dir]
  (json/read-str
   (.OpenAccounts native/+StatusGo+ data-dir)))

(defn save-account-and-login
  [account-data password settings config subaccount-data]
  (json/read-str
   (.SaveAccountAndLogin native/+StatusGo+
                         (json/write-str account-data)
                         password
                         (json/write-str settings)
                         (json/write-str config)
                         (json/write-str subaccount-data))))

(def ^:private signal-event-callback (atom nil))

(defn set-signal-event-callback! [^clojure.lang.IFn f]
  (let [f* #(f (json/read-str %))
        f** (asyncify f* 1 0)]
    (if-let [callback @signal-event-callback]
      (do (reset! (.-state callback) f**)
          nil)
      (let [callback (SignalEventCallback. f**)]
        (reset! signal-event-callback callback)
        (.SetSignalEventCallback native/+StatusGo+ callback)
        nil))))

;;;;;;; async variants ;;;;;;;

(def-asyncified-fn <call call 1 1)

(def-asyncified-fn <call-private call-private 1 1)

(def-asyncified-fn <hash-message hash-message 1 1)
