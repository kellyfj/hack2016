#HERE Hackathon 2016 - Perkins BusStop Challenge

##Team Members
* Frank Kelly
* Anish Bivalkar

##Problem Statement
Click on the YouTube video image

[![YouTube Video Image](https://img.youtube.com/vi/4Mkq881Gu9Q/0.jpg)](https://www.youtube.com/watch?v=4Mkq881Gu9Q)

##Solution Outline
Based on the paper below the idea is to "decorate" existing [HERE Transit data](https://developer.here.com/develop/rest-apis) with User-Generated Content that would be crowd-sourced and then curated by one or both of HERE and Perkins. There are over 8,000 bus stops on the MBTA alone so it's a significant effort.

The code here is for the Android App that takes the data from both the HERE Transit API and the Social Common Back-end (SCBE) and blends them together. The UX is envisaged as a highly accessible one with
* Voice Activation e.g. "OK Google, show me bus stops nearby"
* Text-to-Speech of screen content e.g. "There is a trash can one meter to the right of the bus stop"

The Voice Activation was not implemented at this time because
1. Many of the features are only available in Android 6.0 (my phone only supports Android 4.4)
2. The Emulator does not support Voice Activation

##App Data Flow Proposed

![Alt text](/docs/dataflow.jpg?raw=true "Data Flow Diagram")

##Resources
Great paper describing viability of Crowd-sourced descriptive data via Google / Here Street View
https://www.cs.umd.edu/~jonf/publications/Hara_ImprovingPublicTransitAccessibilityUsingGoogleStreetView-AnExtendedAnalysis_TACCESS2015.pdf


