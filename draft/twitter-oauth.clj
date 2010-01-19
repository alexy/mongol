(require 'twitter ['oauth.client :as 'oauth])

(def consumer (oauth/make-consumer "j4xbT8vAhurx5cno20oBHw"
                                   "ULcnVFAIxwsd8IkPW4n9xuQi5zqwhtlqeW8J249ziE"
                                   "http://twitter.com/oauth/request_token"
                                   "http://twitter.com/oauth/access_token"
                                   "http://twitter.com/oauth/authorize"
                                   :hmac-sha1))

(def request-token (:oauth_token (oauth/request-token consumer)))

(oauth/user-approval-uri consumer request-token "http://mindeconomy.com/")

(def access-token-response (oauth/access-token consumer request-token 4868097))

(twitter/with-oauth consumer (:oauth_token access-token-response)
                             (:oauth_token_secret access-token-response)
  (twitter/update-status "#clojure is awesome"))
