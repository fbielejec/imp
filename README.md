# Imp

RESTfull web-app for calculating and displaying spatial statistics

## Usage

lein run

Point browser to http://localhost:8080/

### Dev

lein test

(require '[imp-rest.parser :as p])

(require '[imp-rest.settings :as s])

(s/put-setting :coordinateName 'location') ; or ./test_rest

(p/parse-to-json )

## License

Copyright © 2016 @fbielejec

Distributed under the GNU LGPL

