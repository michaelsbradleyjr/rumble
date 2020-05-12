(ns rumble.ui.styles
  (:require [cljfx.css :as css]))

(def app
  (css/register
   ::app
   {}
   ;; {".app-tab" {:-fx-opacity "0.5"}
   ;;  ".root" {:-fx-base :white}
   ;;  ".tab" {:-fx-background-color :white}
   ;;  ;; ".tab:selected" {}
   ;;  ".tab-label" {:-fx-background-color :white}
   ;;  ".tab-pane" {:-fx-background-color :white}
   ;;  ;; ".tab-header-area" {:-fx-background-color :white}
   ;;  ".tab-header-background" {:-fx-background-color :white
   ;;                            :-fx-border-color :lightgray}
   ;;  }
   ))
