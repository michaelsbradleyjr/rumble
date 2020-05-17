(ns rumble.util
  (:import [java.nio.file Paths])
  (:require [clojure.core.async :as async :refer [<! go thread]]
            [clojure.java.io :as io]))

(defmacro asyncify [f & opts]
  (let [[arity n buf-or-n] opts
        args-sym (symbol "args")
        copts (count opts)]
    (if (= copts 0)
      `(fn [& ~args-sym] (thread (apply ~f ~args-sym)))
      (let [args (if (= arity :&)
                   `[& ~args-sym]
                   `[~@(for [_ (range arity)] (gensym))])
            appl (if (= arity :&)
                   `(apply ~f ~args-sym)
                   `(~f ~@args))]
        (case copts
          1 `(fn ~args (thread ~appl))
          2 `(fn ~args (async/take ~n (thread ~appl)))
          3 `(fn ~args (async/take ~n (thread ~appl) ~buf-or-n)))))))

(defmacro def-asyncified-fn [name f & opts]
  (let [body (rest (macroexpand-1 `(asyncify ~f ~@opts)))]
    `(defn ~name ~@body)))

;; maybe can use try/catch and impl error-first Node.js style callbacks? once
;; the error-handling pattern is figured out for that purpose, have callbackify
;; deal with arity like asyncify above (will become a macro); if callbackify
;; ends up unused it can be dropped from this namespace
(defn callbackify [<f]
  (fn [& args]
    (let [callback (last args)
          chan (apply <f (butlast args))]
      (go (callback (<! chan))))
    nil))

(defn delete-recursively [path]
  (when (.exists (io/file path))
    (let [f (fn [f path*]
              (when (.isDirectory path*)
                (doseq [path** (.listFiles path*)]
                  (f f path**)))
              (io/delete-file path*))]
      (f f (io/file path)))))

(defn path-join [p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))
