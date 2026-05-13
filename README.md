# SIT305 9.1P - Lost and Found Map App

This is an Android app that helps users report lost and found items. It uses a local SQLite database to store advert details on the device, enhanced with Google Maps and location-based services.

## Features

- Create adverts: post details about lost or found items: name, phone number, description, date, and location
- Google Places Autocomplete: search and pick precise addresses when creating adverts
- Current location polling: tap a button to capture device GPS coordinates and reverse-geocode street addresses
- Google Maps integration: view all reported lost and found items pinned on an interactive map
- Radius-based map search: filter map markers to show only items within a specified distance (in km) from your current location
- Categorization: tag posts with specific categories (electronics, pets, wallets) for easier searching
- Image upload: attach image to each post to help identify the item
- Date & time stamp: automatically capture and display the exact date and time an item was posted
- Search & filter: filter the list of all reported items by category
- Remove adverts: users can delete their adverts once an item has been returned to its owner
- SQLite database: all data is stored persistently using Android's SQLite

---

## DEV NOTE

For linux users, android studio emulator is broken on my machine (also, I want a more streamlined development environment). Use the following commands to run the app:

## On the 1st Terminal

### 1. Start emulator

```bash

QT_QPA_PLATFORM=xcb ~/Android/Sdk/emulator/emulator -avd Pixel_7_Pro -gpu software -no-snapshot-load

```

## On the 2nd terminal

### 2. Build and install app

```bash

./gradlew installDebug

```

### 3. Launch app in emulator

```bash

adb shell am start -n com.example.lostnfound/.MainActivity

```
