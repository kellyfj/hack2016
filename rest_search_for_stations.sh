appid=inhesa7azejETefrudAC
appcode=UP6A4YcFEAgshQMhc-sYsA

#Watertown Square Geo Coords
lat=42.365813
lon=-71.185237
curl -v -G "https://cit.transit.api.here.com/search/by_geocoord.json?app_id=${appid}&app_code=${appcode}&y=${lat}&x=${lon}&radius=100&max=10" | python -m json.tool 



