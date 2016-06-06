
#Watertown Square Geo Coords
lat=42.365813
lon=-71.185237
curl -v -G "https://cit.transit.api.here.com/search/by_geocoord.json?app_id=inhesa7azejETefrudAC&app_code=UP6A4YcFEAgshQMhc-sYsA&y=${lat}&x=${lon}&radius=500&max=20" | python -m json.tool 



