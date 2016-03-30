# Imp

RESTfull web-app for calculating and displaying spatial statistics

## Usage

lein run

Point browser to http://localhost:8080/

### Settings

(require '[imp-rest.settings :as s])

(s/put-setting :coordinateName 'location')

(s/list-settings )

## License

Copyright © 2016 @fbielejec

Distributed under the GNU LGPL

