(ns amazonica.test.redshift
  (:use [clojure.test]
        [amazonica.core]
        [amazonica.aws.redshift]))

(def cred 
  (apply 
    hash-map 
      (interleave 
        [:access-key :secret-key :endpoint]
        (seq (.split (slurp "aws.config") " ")))))



(deftest redshift []

  (println (describe-cluster-versions cred))
  (println (describe-clusters cred))


  (try
    (create-cluster-subnet-group cred
                                 :cluster-subnet-group-name "my subnet"
                                 :description "some desc"
                                 :subnet-ids ["1" "2" "3" "4"])
    (throw (Exception. "create-cluster-subnet-group did not throw exception"))
    (catch Exception e
      (is (.contains 
            (:message (ex->map e)) 
            "Some input subnets in :[1, 2, 3, 4] are invalid."))))
    
 (amazonica.aws.redshift/describe-events cred :source-type "Cluster")

  (try
    (modify-cluster-parameter-group
      cred 
      :parameter-group-name "myparamgroup"
      :parameters [
       {:source          "user"
        :parameter-name  "my_new_param"
        :parameter-value "some value"
        :data-type       "String"
        :description     "some generic param"}
       {:source          "user"
        :parameter-name  "my_new_param-2"
        :parameter-value 42
        :data-type       "Number"
        :description     "some integer param"}])
    (throw (Exception. "modify-cluster-parameter-group did not throw exception"))
    (catch Exception e
      (is (.contains
            (:message (ex->map e))
            "Could not find parameter with name: my_new_param"))))

)