# COSC345 Android App

## Voice Control

Say 'menu' to activate voice control. At present you can say 'test' to start the fft test activity, or any of 'intervals', 'rhythms', or 'melodies' to open the respective menu. Additionally you can say 'help', which will tell you how use voice control and the commands available. Voice control is not active in the fft test activity, the reason for this is that both of these need access to the audio recording resource, which does not allow concurrent use as far as I can tell.

## FFT Test

A basic activity has been set up which uses a FFT algorithm to convert mic input to a frequency. This frequency is shown in a text view in the UI activity. The fft algorithm runs on a non-UI thread. This activity can be accesed from the main menu under the menu entry 'FFT TEST'.
