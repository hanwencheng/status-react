(ns status-im.ui.screens.accounts.recover.views
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [re-frame.core :refer [dispatch]]
            [status-im.ui.components.text-input-with-label.view :refer [text-input-with-label]]
            [status-im.ui.components.react :refer [view
                                                   text
                                                   image
                                                   keyboard-avoiding-view
                                                   touchable-highlight]]
            [status-im.ui.components.sticky-button :refer [sticky-button]]
            [status-im.ui.components.status-bar.view :refer [status-bar]]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.toolbar.actions :as act]
            [status-im.i18n :as i18n]
            [status-im.ui.screens.accounts.recover.styles :as st]
            [status-im.ui.screens.accounts.recover.db :as v]
            [cljs.spec.alpha :as spec]
            [clojure.string :as str]))

(defview passphrase-input [passphrase]
  (letsubs [error [:get-in [:accounts/recover :passphrase-error]]]
    [view {:margin-top 10}
     [text-input-with-label {:label             (i18n/label :t/passphrase)
                             :description       (i18n/label :t/twelve-words-in-correct-order)
                             :multiline         true
                             :auto-expanding    true
                             :max-height        st/passphrase-input-max-height
                             :default-value     passphrase
                             :auto-capitalize   :none
                             :on-change-text    #(dispatch [:set-in [:accounts/recover :passphrase] %])
                             :error             error}]]))

(defview password-input [password]
  (letsubs [error [:get-in [:accounts/recover :password-error]]]
    [view {:margin-top 10}
     [text-input-with-label {:label             (i18n/label :t/password)
                             :default-value     password
                             :auto-capitalize   :none
                             :on-change-text    #(dispatch [:set-in [:accounts/recover :password] %])
                             :secure-text-entry true
                             :error             error}]]))

(defview recover [& [modal?]]
  (letsubs [{:keys [passphrase password]} [:get :accounts/recover]]
    (let [valid-form? (and
                        (spec/valid? ::v/passphrase passphrase)
                        (spec/valid? ::v/password password))]
      [keyboard-avoiding-view {:style st/screen-container}
       [status-bar]
       [toolbar/toolbar {:modal? modal?} toolbar/default-nav-back
        [toolbar/content-title (i18n/label :t/recover-access)]]
       [passphrase-input (or passphrase "")]
       [password-input (or password "")]
       [view {:flex 1}]
       (when valid-form?
         [sticky-button
          (i18n/label :t/recover-access)
          #(do
             (when modal? (dispatch [:navigate-back]))
             (dispatch [:recover-account passphrase password]))])])))

(defview recover-modal []
  [recover true])
