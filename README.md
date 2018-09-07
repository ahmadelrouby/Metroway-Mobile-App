# Metroway-Mobile-App


This app is designed for people in Egypt who're unaware of the metro lines in Cairo and need a quick ride to some place.

This app is built for android devices and what it does is the following:

* User chooses current location using the GPS or by searching for the place
* User then chooses the final destination.
* The app shows the user the route from the current location to the nearest metro station.
* After reaching the Metro station the map changes the style and turns into a metro map
* The metro map instructs the user during their trip on the metro, telling them about when to change lines and when to get off the metro.
* After getting out of the last metro station, the user is then shown a map from that station to their marked destination.


The whole process depends on Google Maps SDK and Google Directions API.

The internal routing inside the metro is accomplished by the A* Algorithm.
