# Gesture Recogniser

Uses Infrared Sensor to make calls.

Stage 1:
  Calibration - the baseline for infrared level is recorded
Stage 2:
  Input is done moving hand over the sensor, encoded in binary - short cover for "1" and long cover for "0".
Stage 3:
  Able to call by covering the sensor for over 3 seconds.
  
Features:
  Only digits from 0-9 are recognised, invalid binary inputs are ignored
  Able to delete digits using "1111".
  Able to clear the input using "11111".
  
