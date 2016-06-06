
appid=inhesa7azejETefrudAC
appcode=UP6A4YcFEAgshQMhc-sYsA

#Watertown Stop Ids
stopids=419805761,419805763,419801373

curl -v -G "https://cit.transit.api.here.com/search/by_stopids.json?app_id=${appid}&app_code=${appcode}&stopIds=${stopids}&lang=en" | python -m json.tool 




