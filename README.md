# COSC345 Project

[![Build Status](https://travis-ci.org/eight0153/ourcontributiontoblindmusicians.svg?branch=master)](https://travis-ci.org/eight0153/ourcontributiontoblindmusicians)

For our project we aim to make an Android app that teaches some of the basic
parts of aural music theory through exercises that aim to improve the users'
skill in recognising notes, intervals, and improving their ability to sing 
accurately. We also aim to make this application fully usable by those who are visually
impaired and have trouble reading.

## Reports
All of the reports for the project can be found under the directory 'reports'.

## Javadocs Documentation
The javadocs for this project are hosted on GitHub pages and can be found
[here](https://eight0153.github.io/ourcontributiontoblindmusicians/).

## How to Build

1. Clone this project to your computer.
2. Open the folder in Android Studio.
3. From the menu select `Build -> Make Project`.
    If you encounter any issues that prevent you from building the project, 
    try `Build -> Clean` then try again.

## How to Run the Application in an Emulator

Make sure your computer has a microphone available, otherwise you will not be able
to use the application. 

You can use pretty much any emulator as long as it is running a version of
Android that is equivalent to, or newer than, Android API level 15
(Android 4.0.3).

The first two steps are the same as the steps for building the app. However 
when you get to step three you need to select from the menu 
`Run -> Run 'app'`. If this option does appear or you get an error saying 
something like 'select the Android APK' try selecting from the menu 
`File -> Sync Project With Gradle Files`.

## Disability support

### Voice Control

At present voice control is set up so that you can navigate from the menus to 
anywhere else in the application. You can activate voice control by saying
'menu' while in any of the menus. A list of commands can be found from the 
main menu or by saying 'menu', followed by 'help'.

The user should also be able to select various aural music exercises by saying
the exercise and a difficulty if one applies to the exercise. Voice control
is not available during the exercises.

### Text to Speech

The information that a user would need from the app, such as grades and how to
use the app is spoken to the user when it is relevant.

## Exercises

### Pitch Matching Exercise

The first type of exercise we have implemented is a pitch matching exercise.
The goal is for the user to sing a given pitch (or note) as accurately as
possible. Accuracy is measured by how many semitones and how many cents away
from the target pitch the user is singing, and the closer these number are to
zero the better. The exercise can be started by selecting 'Pitch Matching' 
from the main menu and pressing the 'start' button.

The user can also have the target pitch played to them by pressing the 
button that says 'Play Target Pitch'. The user can also change the target
pitch by pressing the button 'Change Target Pitch'.

### Intervals Exercise

The goal of this exercise is similar to the pitch matching exercise, however
this time the user needs to sing a musical interval. The user is graded based
on how accurately they sing both of the notes in the interval. The setup is 
similar to the setup of the pitch matching exercise however the user can
change both the root note of the interval and the type of interval. 
Unfortunately this exercise is still buggy and not working correctly.

### Random Interval Exercise

Once the user has chosen this exercise and a difficulty, an algorithm
generates a sequence of random notes given the various constraints as difficulty
which include the likely hood of an inversion, and the quality/type of interval.
Once this is generated, it is played to the user two times, once to listen, the 
second to practice and the third to recieve a grade. The grade is spoken back to 
the user at the end, a grade of 'bad' to 'perfect'.

### Random Melody Exercise

The melody exercise is very similiar to the interval exercise except that it
is forms tonal melodies according to musical rules of tonal melody. This exercise starts
by creating a scale, from that scale it accesses the scale degrees to add notes to a list.
The difficulty of the exercises relates to constraints such as melody length, melody range, 
and intervals used.

### Human Vocal Ranges

To accomadate for multiple people with various vocal ranges, the app allows a user to 
sing any octave of a pitch and still have it marked correct.
