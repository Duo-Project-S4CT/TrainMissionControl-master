#include <AFMotor.h>
#include <SoftwareSerial.h>

SoftwareSerial BTS(9, 10); // RX, TX

bool moving = false;

AF_DCMotor motor1(1, MOTOR12_1KHZ);
AF_DCMotor motor2(2, MOTOR12_1KHZ);

void setup()
{
    BTS.begin(9600);
    Serial.begin(9600);
}

void loop()
{
    if (BTS.available())
    {
        moving = !moving;
        Serial.println(moving);
        BTS.read(); // dump data

        if (moving)
        {
            stop();
        }
        else
        {
            move();
        }
    }
}

void move()
{
    motor1.setSpeed(255); // Define maximum velocity
    motor1.run(FORWARD);  // rotate the motor clockwise
    motor2.setSpeed(255); // Define maximum velocity
    motor2.run(FORWARD);  // rotate the motor clockwise
}

void stop()
{
    motor1.setSpeed(0);  // Define minimum velocity
    motor1.run(RELEASE); // stop the motor when release the button
    motor2.setSpeed(0);  // Define minimum velocity
    motor2.run(RELEASE); // rotate the motor clockwise
}
