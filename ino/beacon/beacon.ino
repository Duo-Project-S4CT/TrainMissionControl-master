const String START_DELIMITER = "<";
const String END_DELIMITER = ">";

/*
 * Counter used to setup sensors dynamically. Sets up INPUT/OUTPUT pins for every sensor and also performs a trigger check.
 *
 * Setup on Arduino:
 * - Sensors can be added starting from pin 2, 3.
 * - The first pin is the input (echo) and second the output (trig).
 * - Sensors have ids 1 through n amounts of sensors.
 *
 * Example:
 * - Single sensor:  pins 2 & 3 is sensor with id "1".
 * 
 * - Dual sensor:    pins 2 & 3 is sensor with id "1".
 *                   pins 4 & 5 is sensor with id "2".
 *
 * - Triple sensor:  pins 2 & 3 is sensor with id "1".
 *                   pins 4 & 5 is sensor with id "2".
 *                   pins 6 & 7 is sensor with id "3".
 */
const int SENSOR_COUNT = 5;

void setup() {
  Serial.begin(9600);

  pinMode(3, OUTPUT);
  pinMode(2, INPUT);

  for (int i = 0; i < SENSOR_COUNT; i++) {
    pinMode(5 + i * 2, OUTPUT);
    pinMode(4 + i * 2, INPUT);
  }
}

void loop() {
  int sensors[SENSOR_COUNT] = {};

  for (int i = 0; i < SENSOR_COUNT; i++) {
    sensors[i] = isTriggered(5 + i * 2, 4 + i * 2);
  }

  String data = "[";
  for (int i = 0; i < sizeof sensors / sizeof sensors[0]; i++) {
    if (sensors[i]) {
      data += String(i + 1) + ",";
    }
  }

  if (data.length() > 1) {  
    data.remove(data.length() - 1);
  }
  data += "]";

  send("{sensors:" + data + ",distance:" + String(distance(3, 2)) + "}");
}

bool isTriggered(int ping, int echo) {
  return distance(ping, echo) < 10;
}

long distance(int ping, int echo) {
  digitalWrite(ping, LOW);
  delayMicroseconds(2);
  digitalWrite(ping, HIGH);
  delayMicroseconds(10);
  digitalWrite(ping, LOW);

  return pulseIn(echo, HIGH) / 29 / 2;
}

void send(String raw) {
  Serial.println(START_DELIMITER + raw + END_DELIMITER);
}
