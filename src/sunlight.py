import time
import os
import RPi.GPIO as GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
DEBUG = 1

PERIOD = 1

def RCtime (RCpin):
    reading = 0
    GPIO.setup(RCpin, GPIO.OUT)
    GPIO.output(RCpin, GPIO.LOW)
    time.sleep(0.01)

    GPIO.setup(RCpin, GPIO.IN)
    while (GPIO.input(RCpin) == GPIO.LOW):
        reading += 1
    return reading

try:
    with open ("../data/data.txt", "a") as file:
        dataPoints = 0
        while (dataPoints < 96):
            value = RCtime(18)
            if value <= 100000:
                value = 100000 - value
            elif value > 100000:
                value = 0
            print 'Value at time period ', dataPoints, ' is ', value
            file.write(str(value) + '\n')
            dataPoints = dataPoints + 1
            time.sleep(PERIOD)
        file.write("\n")
    file.closed
finally:
    GPIO.cleanup()
