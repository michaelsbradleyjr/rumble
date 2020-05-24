(ns rumble.kludge
  (:require [clojure.java.io :refer [make-parents]]
            [rumble.status-go :as status-go]
            [rumble.util :as util :refer [path-join]]))

;; get status-go up-and-running to an extent it's possible to use RPC
;; credit: https://github.com/richard-ramos/status-node/blob/master/index.js

(def account-1
  {:bip39Passphrase ""
   :mnemonicPhraseLength 12
   :n 5
   :paths ["m/43'/60'/1581'/0'/0" "m/44'/60'/0'/0/0"]})

;; "qwerty" hashed with keccak-256
(def password "0x2cd9bf92c5e20b1b410f5ace94d963a96e89156fbe65b70365e8596b37f1f165")

(def photo-path "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAmElEQVR4nOzX4QmAIBBA4Yp2aY52aox2ao6mqf+SoajwON73M0J4HBy6TEEYQmMIjSE0htCECVlbDziv+/n6fuzb3OP/UmEmYgiNITRNm+LPqO2UE2YihtAYQlN818ptoZzau1btOakwEzGExhCa5hdi7d2p1zZLhZmIITSG0PhCpDGExhANEmYihtAYQmMIjSE0bwAAAP//kHQdRIWYzToAAAAASUVORK5CYII=")

(def networks
  [{:id "testnet_rpc"
    :etherscan-link "https://ropsten.etherscan.io/address/"
    :name "Ropsten with upstream RPC"
    :config {:NetworkId 3
             :DataDir "/ethereum/testnet_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://ropsten.infura.io/v3/f315575765b14720b32382a61a89341a"}}}
   {:id "rinkeby_rpc"
    :etherscan-link "https://rinkeby.etherscan.io/address/"
    :name "Rinkeby with upstream RPC"
    :config {:NetworkId 4
             :DataDir "/ethereum/rinkeby_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://rinkeby.infura.io/v3/f315575765b14720b32382a61a89341a"}}}
   {:id "goerli_rpc"
    :etherscan-link "https://goerli.etherscan.io/address/"
    :name "Goerli with upstream RPC"
    :config {:NetworkId 5
             :DataDir "/ethereum/goerli_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://goerli.blockscout.com/"}}}
   {:id "mainnet_rpc"
    :etherscan-link "https://etherscan.io/address/"
    :name "Mainnet with upstream RPC"
    :config {:NetworkId 1
             :DataDir "/ethereum/mainnet_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://mainnet.infura.io/v3/f315575765b14720b32382a61a89341a"}}}
   {:id "xdai_rpc"
    :name "xDai Chain"
    :config {:NetworkId 100
             :DataDir "/ethereum/xdai_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://dai.poa.network"}}}
   {:id "poa_rpc"
    :name "POA Network"
    :config {:NetworkId 99
             :DataDir "/ethereum/poa_rpc"
             :UpstreamConfig {:Enabled true
                              :URL "https://core.poa.network"}}}])

(def boot-nodes
  ["enode://23d0740b11919358625d79d4cac7d50a34d79e9c69e16831c5c70573757a1f5d7d884510bc595d7ee4da3c1508adf87bbc9e9260d804ef03f8c1e37f2fb2fc69@47.52.106.107:443"
   "enode://5395aab7833f1ecb671b59bf0521cf20224fe8162fc3d2675de4ee4d5636a75ec32d13268fc184df8d1ddfa803943906882da62a4df42d4fccf6d17808156a87@178.128.140.188:443"
   "enode://6e6554fb3034b211398fcd0f0082cbb6bd13619e1a7e76ba66e1809aaa0c5f1ac53c9ae79cf2fd4a7bacb10d12010899b370c75fed19b991d9c0cdd02891abad@47.75.99.169:443"
   "enode://5405c509df683c962e7c9470b251bb679dd6978f82d5b469f1f6c64d11d50fbd5dd9f7801c6ad51f3b20a5f6c7ffe248cc9ab223f8bcbaeaf14bb1c0ef295fd0@35.223.215.156:443"])

(def rendezvous-nodes
  ["/ip4/34.70.75.208/tcp/30703/ethv4/16Uiu2HAm6ZsERLx2BwVD2UM9SVPnnMU6NBycG8XPtu8qKys5awsU"
   "/ip4/178.128.140.188/tcp/30703/ethv4/16Uiu2HAmLqTXuY4Sb6G28HNooaFUXUKzpzKXCcgyJxgaEE2i5vnf"
   "/ip4/47.52.106.107/tcp/30703/ethv4/16Uiu2HAmEHiptiDDd9gqNY8oQqo8hHUWMHJzfwt5aLRdD6W2zcXR"])

(def static-nodes
  ["enode://887cbd92d95afc2c5f1e227356314a53d3d18855880ac0509e0c0870362aee03939d4074e6ad31365915af41d34320b5094bfcc12a67c381788cd7298d06c875@178.128.141.0:443"
   "enode://fbeddac99d396b91d59f2c63a3cb5fc7e0f8a9f7ce6fe5f2eed5e787a0154161b7173a6a73124a4275ef338b8966dc70a611e9ae2192f0f2340395661fad81c0@34.67.230.193:443"])

(def trusted-mail-servers
  ["enode://2c8de3cbb27a3d30cbb5b3e003bc722b126f5aef82e2052aaef032ca94e0c7ad219e533ba88c70585ebd802de206693255335b100307645ab5170e88620d2a81@47.244.221.14:443"
   "enode://ee2b53b0ace9692167a410514bca3024695dbf0e1a68e1dff9716da620efb195f04a4b9e873fb9b74ac84de801106c465b8e2b6c4f0d93b8749d1578bfcaf03e@104.197.238.144:443"
   "enode://8a64b3c349a2e0ef4a32ea49609ed6eb3364be1110253c20adc17a3cebbc39a219e5d3e13b151c0eee5d8e0f9a8ba2cd026014e67b41a4ab7d1d5dd67ca27427@178.128.142.94:443"
   "enode://7aa648d6e855950b2e3d3bf220c496e0cae4adfddef3e1e6062e6b177aec93bc6cdcf1282cb40d1656932ebfdd565729da440368d7c4da7dbd4d004b1ac02bf8@178.128.142.26:443"
   "enode://c42f368a23fa98ee546fd247220759062323249ef657d26d357a777443aec04db1b29a3a22ef3e7c548e18493ddaf51a31b0aed6079bd6ebe5ae838fcfaf3a49@178.128.142.54:443"
   "enode://30211cbd81c25f07b03a0196d56e6ce4604bb13db773ff1c0ea2253547fafd6c06eae6ad3533e2ba39d59564cfbdbb5e2ce7c137a5ebb85e99dcfc7a75f99f55@23.236.58.92:443"])

(defn login! []
  (let [data-dir "data"
        rumble-dir (path-join (System/getProperty "user.home") ".rumble")
        no-backup-dir "no-backup"
        no-backup-dir-abs (path-join rumble-dir no-backup-dir)
        keystore-dir "keystore"
        keystore-dir-abs (path-join no-backup-dir-abs keystore-dir)]
    (util/delete-recursively rumble-dir)
    ;; make-parents doesn't create a "foo" file, only ensures directories exist
    (make-parents (path-join keystore-dir-abs "foo"))
    (status-go/init-keystore keystore-dir-abs)
    (status-go/open-accounts no-backup-dir-abs)
    (let [derived-addresses
          (status-go/multi-account-generate-and-derive-addresses account-1)
          account-1-id ((first derived-addresses) "id")
          account-2 {:accountID account-1-id
                     :password password
                     :paths ["m/44'/60'/0'/0"
                             "m/43'/60'/1581'"
                             "m/43'/60'/1581'/0'/0"
                             "m/44'/60'/0'/0/0"]}
          derived-accounts
          (status-go/multi-account-store-derived-accounts account-2)
          multi-account-data {:name "Delectable Overjoyed Nauplius"
                              :address ((first derived-addresses) "address")
                              :photo-path photo-path
                              :key-uid ((first derived-addresses) "keyUid")
                              :keycard-pairing nil}
          settings
          {:key-uid ((first derived-addresses) "keyUid")
           :mnemonic ((first derived-addresses) "mnemonic")
           :public-key ((derived-accounts "m/43'/60'/1581'/0'/0") "publicKey")
           :name (multi-account-data :name)
           :address  ((first derived-addresses) "address")
           :eip1581-address ((derived-accounts "m/43'/60'/1581'") "address")
           :dapps-address ((derived-accounts "m/44'/60'/0'/0/0") "address")
           :wallet-root-address ((derived-accounts "m/44'/60'/0'/0") "address")
           :preview-privacy? true
           :signing-phrase "dust gear boss"
           :log-level "INFO"
           :latest-derived-path 0
           "networks/networks" networks
           :currency "usd"
           :photo-path photo-path
           :waku-enabled true
           "wallet/visible-tokens" {:mainnet ["SNT"]}
           :appearance 0
           "networks/current-network" "mainnet_rpc"
           :installation-id "5d6bc316-a97e-5b89-9541-ad01f8eb7397"}
          accounts-data
          [{:public-key ((derived-accounts "m/44'/60'/0'/0/0") "publicKey")
            :address ((derived-accounts "m/44'/60'/0'/0/0") "address")
            :color "#4360df"
            :wallet true
            :path "m/44'/60'/0'/0/0"
            :name "Status account"}
           {:public-key ((derived-accounts "m/43'/60'/1581'/0'/0") "publicKey")
            :address ((derived-accounts "m/43'/60'/1581'/0'/0") "address")
            :name "Delectable Overjoyed Nauplius"
            :photo-path photo-path
            :path "m/43'/60'/1581'/0'/0"
            :chat true}]
          final-config
          {:BrowsersConfig {:Enabled true}
           :ClusterConfig {:BootNodes boot-nodes
                           :Enabled true
                           :Fleet "eth.prod"
                           :RendezvousNodes rendezvous-nodes
                           :StaticNodes static-nodes
                           :TrustedMailServers trusted-mail-servers}
           :DataDir data-dir
           :EnableNTPSync true
           :KeyStoreDir keystore-dir
           :ListenAddr ":30305"
           :LogEnabled true
           :LogFile "geth.log"
           :LogLevel "INFO"
           :MailserversConfig {:Enabled true}
           :Name "StatusIM"
           :NetworkId 1
           :NoDiscovery false
           :PermissionsConfig {:Enabled true}
           :Rendezvous true
           :RequireTopics {:whisper {:Max 2 :Min 2}}
           :ShhextConfig {:BackupDisabledDataDir "./"
                          :DataSyncEnabled true
                          :InstallationID "aef27732-8d86-5039-a32e-bdbe094d8791"
                          :MailServerConfirmations true
                          :MaxMessageDeliveryAttempts 6
                          :PFSEnabled true
                          :VerifyENSContractAddress "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e"
                          :VerifyENSURL "https://mainnet.infura.io/v3/f315575765b14720b32382a61a89341a"
                          :VerifyTransactionChainID 1
                          :VerifyTransactionURL "https://mainnet.infura.io/v3/f315575765b14720b32382a61a89341a"}
           :StatusAccountsConfig {:Enabled true}
           :UpstreamConfig {:Enabled true
                            :URL "https://mainnet.infura.io/v3/f315575765b14720b32382a61a89341a"}
           :WakuConfig {:BloomFilterMode nil
                        :Enabled true
                        :LightClient true
                        :MinimumPoW 0.001}
           :WalletConfig {:Enabled true}}]
      (status-go/save-account-and-login multi-account-data
                                        password
                                        settings
                                        final-config
                                        accounts-data))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (ns rumble.kludge)

;; (login!)

;; (ns rumble.status-go)

;; (def signals (atom []))

;; (set-signal-event-callback! (fn [s] (swap! signals #(conj % s))))

;; (add-peer "enode://2c8de3cbb27a3d30cbb5b3e003bc722b126f5aef82e2052aaef032ca94e0c7ad219e533ba88c70585ebd802de206693255335b100307645ab5170e88620d2a81@47.244.221.14:443")

;; (call-private {:method "wakuext_startMessenger"})

;; (call-private {:method "wakuext_loadFilters"
;;                :params [[{:ChatID "test"
;;                           :OneToOne false}]]})

;; (call-private {:method "wakuext_saveChat"
;;                :params [{:lastClockValue 0
;;                          :color "#51d0f0"
;;                          :name "test"
;;                          :lastMessage nil
;;                          :active true
;;                          :id "test"
;;                          :unviewedMessagesCount 0
;;                          :chatType 2
;;                          :timestamp 1588940692659}]})

;; (call-private {:method "wakuext_sendChatMessage"
;;                :params [{:chatId "test"
;;                          :text "Hello from Clojure JVM REPL"
;;                          :responseTo nil
;;                          :ensName nil
;;                          :sticker nil
;;                          :contentType 1}]})
