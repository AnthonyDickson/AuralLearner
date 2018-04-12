# COSC345 Assignment 1 - Report

## Group Members

* Rory Jackson - 2208377,
* Anthony Dickson - 3348967,
* Johnny Mann - 3891999

## What We Are Building

The options for the visually impaired to learn music theory and music theory ear training are limited and expensive, usually requiring cumbersome CDs or 1 on 1 tuition. We will make an Android app that will allow the visually-impaired to learn many of the aural components of music theory on their own. The idea is that the app will start with some basic pitch matching exercises which will involve matching your vocal pitch to some musical tone. Then the exercises will become more and more complicated, involving singing musical intervals, melodies, and later slightly longer melodies. By the end of using our app, users will have a good foundation in aural music theory and improved their singing ability. This app will be fully usable by both those are visually impaired and those who are not.

## The Disability We Are Supporting

The disability we are supporting is visual impairment. By this we mean anyone who has trouble reading (e.g. blind people, dyslexic people, etc). Visually impaired people have trouble using Android devices because they cannot read what is on the screen. To remedy this, users will be able to use our app purely with voice commands, and the app will use text-to-speech to provide feedback to the user. Traditional aural theory and singing apps require the user to to sing a pitch and then match it while only providing on-screen affirmation and guidance. Our app will guide the user with a pitch that tells them where their vocal pitch is in relation to the target pitch and provide positive affirmation.

## How We Will Build the App

We will build the app so that it can run on Android devices with built-in microphones. We will most likely use the Fast Fourier algorithm for pitch detection. It will allow us to convert microphone input data into a format that we can use to figure out the note the person is singing (or the closest note) based on the frequency of each note (for example, the note A is 440Hz in the fourth octave). This will be used in conjunction with music theory exercises, which we can write ourselves based on our own personal music theory knowledge. We will build a user interface that visually impaired users can easily use. The user interface will have two parts: the GUI and the AUI (Auditory User Interface). The GUI will be your typical Android app GUI, and the AUI will include voice-based navigation and text-to-speech. For these features we will use the CMUSphinx voice-recognition system and the built-in text-to-speech API.

## Platforms That We Will Support

Our app will run on Android devices that have a built-in microphone (phones, tablets, etc). We will aim to support Android versions 4.x onwards, as this will cover the vast majority (~99%) of android users. If we have enough time, we will also make the app available for computers running Java.

## Distribution of Workload

The app will be built by Rory Jackson, Anthony Dickson and Johnny Mann. Johnny and Rory will be in charge of creating the exercises and the features that are required for those exercises, such as processing microphone input and matching it with a target pitch. Anthony will be in charge of creating the user interface. This will include creating the GUI, and adding voice-based navigation and text-to-speech capabilities.

## How Long the App Will Take

We estimate that the app will take the rest of the academic year to build. The alpha version will be ready before the end of this semester. This version will include the pitch matching functionality and a simple user interface. The beta version will be ready before the end of the second semester. This version will include more complex music theory exercises and a more fleshed-out user interface.
