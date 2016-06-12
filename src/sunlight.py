import time
import os
import RPi.GPIO as GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
DEBUG = 1

PERIOD = 60

def RCtime (RCpin):
    reading = 0
    GPIO.setup(RCpin, GPIO.OUT)
    GPIO.output(RCpin, GPIO.LOW)
    time.sleep(0.1)

    GPIO.setup(RCpin, GPIO.IN)
    while (GPIO.input(RCpin) == GPIO.LOW):
        reading += 1
    return reading

try:
    with open ("sunlightData.txt", "a") as file:
        dataPoints = 0
        while (dataPoints < 96):
            r18 = RCtime(18)
            r23 = RCtime(23)
            r25 = RCtime(25)
            s = str((r18 + r23 + r25) / 3)
            file.write(s + '\n')
            dataPoints = dataPoints + 1
            time.sleep(PERIOD)
        file.write("\n")
    file.closed
finally:
    GPIO.cleanup()
