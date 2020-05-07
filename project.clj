(defproject rumble "0.1.0-SNAPSHOT"
  :description "Experimental desktop app built atop status-go"
  :url "https://github.com/michaelsbradleyjr/rumble"
  :license {:name "Mozilla Public License v2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[cljfx "1.7.0"]
                 [cljfx/css "1.1.0"]
                 [net.java.dev.jna/jna "5.5.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "1.1.587"]
                 [org.clojure/data.json "1.0.0"]]
  :plugins [[lein-cljfmt "0.6.7"]]
  :main rumble.core
  :repl-options {:init-ns rumble.core}
  :profiles {:uberjar {:aot :all
                       :injections [(javafx.application.Platform/exit)]}})
