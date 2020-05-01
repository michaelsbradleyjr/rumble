(ns rumble.native.status-go
  (:import [com.sun.jna Native]))

(gen-interface
 :name jna.StatusGo
 :extends [com.sun.jna.Library]
 :methods
 [[CallPrivateRPC [String] String]
  [CallRPC [String] String]
  [HashMessage [String] String]
  [InitKeystore [String] String]
  [MultiAccountGenerateAndDeriveAddresses [String] String]
  [MultiAccountStoreDerivedAccounts [String] String]
  [OpenAccounts [String] String]
  [SaveAccountAndLogin [String String String String String] String]])

(defonce +StatusGo+
  (Native/loadLibrary (.toString (Native/extractFromResourcePath "status"))
                      jna.StatusGo))

(defn call-private-rpc [json]
  (.CallPrivateRPC +StatusGo+ json))

(defn call-rpc [json]
  (.CallRPC +StatusGo+ json))

(defn hash-message [message]
  (.HashMessage +StatusGo+ message))

(defn init-keystore [keystore-dir]
  (.InitKeystore +StatusGo+ keystore-dir))

(defn multi-account-generate-and-derive-addresses [json]
  (.MultiAccountGenerateAndDeriveAddresses +StatusGo+ json))

(defn multi-account-store-derived-accounts [json]
  (.MultiAccountStoreDerivedAccounts +StatusGo+ json))

(defn open-accounts [data-dir]
  (.OpenAccounts +StatusGo+ data-dir))

(defn save-account-and-login
  [account-data password settings-json config-json subaccount-data]
  (.SaveAccountAndLogin +StatusGo+
                        account-data
                        password
                        settings-json
                        config-json
                        subaccount-data))