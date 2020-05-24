(ns rumble.native.status-go
  (:import [com.sun.jna Native]))

(gen-interface
 :name jna.StatusGo
 :extends [com.sun.jna.Library]
 :methods
 [[AddPeer [String] String]
  [CallPrivateRPC [String] String]
  [CallRPC [String] String]
  [HashMessage [String] String]
  [InitKeystore [String] String]
  [MultiAccountGenerateAndDeriveAddresses [String] String]
  [MultiAccountStoreDerivedAccounts [String] String]
  [OpenAccounts [String] String]
  [SaveAccountAndLogin [String String String String String] String]
  [SetSignalEventCallback [com.sun.jna.Callback] Void]])

(defonce +StatusGo+
  (Native/load (.toString (Native/extractFromResourcePath "status"))
               jna.StatusGo))
