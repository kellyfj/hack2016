
echo "Attempt #1"
curl -v -G "https://transit.api.here.com/search/by_name?app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA&x=-73.99162351623576&y=40.751036894211815&max=5&name=New%20York"

echo "Attempt #2"
curl -v -G "https://cit.transit.api.here.com/search/by_name?app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA&x=-73.99162351623576&y=40.751036894211815&max=5&name=New%20York"

echo "Attempt #3"
curl -v -G --data-urlencode "app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA&x=-73.99162351623576&y=40.751036894211815&max=5&name=New%20York" "https://transit.api.here.com/search/by_name"

echo "Attempt #4"
curl -v -G --data-urlencode "app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA&x=-73.99162351623576&y=40.751036894211815&max=5&name=New%20York" "https://cit.transit.api.here.com/search/by_name"

