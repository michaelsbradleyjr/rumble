OpenJDK14 with OpenJ9 JVM seems to give good results with respect to memory
usage related to garbage collection; to install on macOS see:
https://github.com/AdoptOpenJDK/homebrew-openjdk

See `rumble.ui/start!` re: rumble.fx-implicit-exit

With auto-clean disabled, before running `lein uberjar` do:
`lein do clean, compile`
See: https://github.com/technomancy/leiningen/issues/2540
and maybe: https://github.com/technomancy/leiningen/issues/1586

Re: uberjar injections see:
https://github.com/cljfx/cljfx#aot-compilation-is-complicated

The OpenJDK builds of Mission Control are available for download from:
https://adoptopenjdk.net/jmc.html

When using OpenJDK with OpenJ9 add to repl's :jvm-opts to enable connections
from Mission Control (sadly some features not available when using OpenJ9):
"-Dcom.sun.management.jmxremote"

YourKit is a commercial product available for download from:
https://www.yourkit.com/java/profiler/features/
It requires a license; can get a 15 day trial license and it's possible for
open source projects to apply for a free license

Add to repl's :jvm-opts to enable profiling with YourKit (on macOS):
"-agentpath:/Applications/YourKit-Java-Profiler-2019.8.app/Contents/Resources/bin/mac/libyjpagent.dylib=_no_java_version_check"
^ the "_no_java_version_check" option is needed at present when using Java 14

There is a free monitoring/profiling tool named IBM Health Center that
should integrate well with OpenJ9; it can easily be installed/launched in
Eclispe but it's not clear yet how to configure lein to enable connections
(should be do-able, just have to figure out java options)

Should look into ProGuard for additional optimization of the uberjar prior
to packaging with jpackage: https://github.com/Guardsquare/proguard
It seems (based on internet searches) that configuring ProGuard to work with a
Clojure project may be difficult to the point it's not practical, but is still
worth exploring at some point

https://github.com/FXMisc/Flowless
Explore if/how it could be used with cljfx.
Would it be useful for better performance / lower resource consumption?
Would it be difficult / impossible to wrap for usage in cljfx?

https://github.com/vlaaad/reveal
Seems like a promising tool but I couldn't get it to work as shown in demo.gif
in the README, which may be user error / lack-of-understanding on my part
and/or it may be buggy because it's in such an early state of development

https://github.com/JonathanGiles/scenic-view
Seems like it could be an amazing power tool when developing JavaFX apps, but
it doesn't seem to be actively developed at this point and requires running the
app with Java 11 (OpenJDK11 with Hotspot JVM works for that purpose); moreover,
it seems to be in a partially working somewhat buggy state: stacktraces seen in
terminal used to launch it and/or in terminal running the app/repl, and there
were some weird results when live-editing the JavaFX objects
