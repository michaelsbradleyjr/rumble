# Rumble

[Rumble](https://github.com/michaelsbradleyjr/rumble/tree/first-ui-attempt) is an experiment to build a self-contained desktop app using the JVM and [status-go](https://github.com/status-im/status-go) (eventually [nimbus](https://github.com/status-im/nimbus)).

:warning: **WIP** :warning: the UI is just stubs at present, but the architecture described below is on firm footing.

## Stack

* [OpenJDK](https://adoptopenjdk.net/index.html)
* [JavaFX](https://en.wikipedia.org/wiki/JavaFX) via [OpenJFX](https://github.com/openjdk/jfx)
* [Clojure](https://clojure.org/) on the JVM
* [cljfx](https://github.com/cljfx/cljfx)

## Prelude

Much is missing in this README re: exact instructions (and/or `Makefile`) for how to build and place `libstatus.dylib/dll/so`, as well choices related to installing OpenJDK with the Hotspot VM vs. OpenJ9 VM. Those shortcomings will be remedied in the near future.

## How it works

Rumble uses [JNA](https://github.com/java-native-access/jna) to load and communicate with status-go in-process. For that purpose status-go is built as a shared library with `go build -buildmode=c-shared`. The interface on the JVM is defined at runtime and requires only a [few lines of code](https://github.com/michaelsbradleyjr/rumble/blob/first-ui-attempt/src/clj/rumble/native/status_go.clj#L5-L18) (though, as a JNA first-timer, it took a bit of experimentation to figure it out).

cljfx is used to dynamically render and update a JavaFX [*scene graph*](https://openjfx.io/javadoc/14/javafx.graphics/javafx/scene/package-summary.html) (conceptually similar to a web browser's DOM). The flow of component events and updates happens in a loop that is very similar to the architecture of [re-frame](https://github.com/Day8/re-frame), and components are expressed in a manner that is very similar to how it's done with [reagent](https://github.com/reagent-project/reagent) (both reagent and re-frame are used in [status-react](https://github.com/status-im/status-react)).

The app is visually styled via a CSS stylesheet. That stylesheet can be an actual `.css` file or it can be composed dynamically in Clojure, see [cljfx/css](https://github.com/cljfx/css). The default stylesheet that's bundled in JavaFX is named *modena* and I [extracted it](https://gist.github.com/michaelsbradleyjr/e2b9a638a2806a760deb36c782fb83d9) for easy reference.

## Building and Packaging

The app and its dependencies (including `libstatus.dylib/dll/so`) are packed into an [uberjar](https://stackoverflow.com/questions/11947037/what-is-an-uber-jar) and then [jpackage](https://docs.oracle.com/en/java/javase/14/jpackage/packaging-overview.html) is used to generate a standalone `.dmg`, `.exe`, or `.deb` (for macOS, Windows, or Linux, respectively).

To be clear, the jpackage output results in a *completely standalone* app, the user does ***not*** need to have a JVM already installed. The runtime that's included in the package is a relatively slim *application image*, i.e. it's lightweight compared to installing a whole JDK.

## Advantages over React Native + Qt as a basis for a Desktop app

[Qt](https://www.qt.io/) and JavaFX have roughly the same scope, providing a large set of components for building anywhere from simple to intricate GUIs. Both have been around for many years and are stable, while also being actively developed and improved upon. Both also provide a sandboxed webkit for web views, i.e. an integrated Ðapp browser is a possibility with either toolkit.

However, attempting to marry Qt to React Native seems a bit problematic to me after I learned more about what's involved. I think there is simply a mismatch between the way that Qt is designed to be used and the architecture of React Native.

With Clojure on the JVM driving JavaFX, there is no architectural mismatch; in fact, they're a great match! Moreover, you get all the benefits of Clojure without the complexities involved in ClojureScript -> JS + React Native + Qt backend + etc. + etc.

You can easily do real debugging with breakpoints in a rich development tool like [Cursive](https://cursive-ide.com/index.html). You can do runtime profiling in both development and production builds with [Mission Control](https://adoptopenjdk.net/jmc.html) (generic [screenshots](https://www.google.com/search?tbm=isch&source=hp&biw=840&bih=913&ei=Vk-9XpvXC4WWtQX28I3wCQ&q=mission+control+jdk&oq=mission+control+jdk&gs_lcp=CgNpbWcQAzIGCAAQCBAeOgIIADoFCAAQgwFQvwhYkUBg6EFoBXAAeACAAbQBiAHRCZIBBDE5LjGYAQCgAQGqAQtnd3Mtd2l6LWltZ7ABAA&sclient=img&ved=0ahUKEwib2tOewrPpAhUFS60KHXZ4A54Q4dUDCAY&uact=5)).

The JVM is a high-performance multithreaded runtime. All business logic can be run off the JavaFX rendering thread, so you never have to worry about the UI stuttering because an event handler needs to perform a complex set of tasks (though it could stutter in some situations, depending on VM/GC settings).

The stack is small, fully cross-platform, there is a tiny set of dependencies, and the tools for building and packaging are built into the stack.

## Advantages in general

With the exception of cljfx (and leaving aside status-go / nimbus), all components of the stack are mature, stable, proven technologies with large open source communities. The library ecosystem is huge when you consider that Clojure code can easily leverage any Java library that's available on [Maven Central](https://search.maven.org/).

In summary, I'd say a clear advantage is that while Rumble is an experiment, this is not an experimental stack. There's no *will it work?* concerns or hoops to jump through to get it working. It's a production-grade stack that can work from day-one of starting to use it.

## Possible disadvantages

It will be important to learn about JVM tuning to keep the app from gobbling unacceptable amounts of memory. Even so, its memory usage will always be a bit on the heavy side (hundreds of MBs) compared to a statically compiled Qt app, mostly owing to the hightly dynamic nature of Clojure and the resulting garbage collection in the JVM.

It's Clojure, and not everyone likes Lisp, functional programming, and REPL-driven development.

## License

Source Copyright © 2020 Michael Bradley and [contributors](https://github.com/michaelsbradleyjr/rumble/graphs/contributors). Distributed under the [Mozilla Public License v2.0](https://github.com/michaelsbradleyjr/rumble/blob/master/LICENSE.md).
