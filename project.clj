(defproject rumble "0.0.1-alpha.0"
  :description "Experimental desktop app built atop status-go"
  :url "https://github.com/michaelsbradleyjr/rumble"
  :license {:name "Mozilla Public License v2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :version-windows "0.0.1"
  :dependencies [[cljfx "1.7.3"]
                 [cljfx/css "1.1.0"]
                 [net.java.dev.jna/jna "5.5.0"]
                 [org.clojure/core.async "1.1.587"]
                 [org.clojure/data.json "1.0.0"]]
  :plugins [[lein-cljfmt "0.6.7"]
            [lein-shell "0.5.0"]]
  :main rumble.Launcher
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :repl-options {:init-ns rumble.core}
  :jvm-opts ["-Drumble.fx-implicit-exit=true"
             "-Xms64m"
             "-Xmx512m"]
  :aot [rumble.native.status-go.signal]
  :omit-source true
  :jar-name "rumble.jar"
  :uberjar-name "rumble-standalone.jar"
  :profiles {:repl {:jvm-opts ^:replace ["-Xms64m"
                                         "-Xmx512m"]}
             :uberjar {:aot :all
                       :auto-clean false
                       :injections [(javafx.application.Platform/exit)]}}
  :clean-targets [:target-path "input" "pom.xml" "pkg"]
  :aliases {"build" ["do" ["clean"] ["compile"] ["uberjar"]]
            "prep"
            ["do"
             ["build"]
             ["shell" "mkdir" "input"]
             ["shell" "cp" "target/rumble-standalone.jar" "input"]]
            "pkg:linux"
            ["do"
             ["prep"]
             ["shell" "jpackage" "--app-version" "${:version}" "@jpackage/common" "@jpackage/linux"]]
            "pkg:macos"
            ["do"
             ["prep"]
             ["shell" "jpackage" "--app-version" "${:version}" "@jpackage/common" "@jpackage/macos"]]
            "pkg:windows"
            ["do"
             ["prep"]
             ["shell" "jpackage" "--app-version" "${:version-windows}" "@jpackage/common" "@jpackage/windows"]]})
