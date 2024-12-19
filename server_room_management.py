from machine import Pin, I2C
import network
import time
from bmp280 import BMP280
from umqtt.simple import MQTTClient
from Project import config
import ssl
import random
import ubinascii
import ntptime


    
def get_timestamp():
    # Get the current timestamp in seconds since the Unix epoch (UTC)
    current_time_ms = time.time()
    return current_time_ms


# setup wifi
ssid = config.ssid
password = config.pwd

# connect to wifi
wlan = network.WLAN(network.STA_IF)
wlan.active(True)
wlan.config(pm=0xa11140)  # Disable powersave mode
wlan.connect(ssid, password)

connection_timeout = 10
while connection_timeout > 0:
    if wlan.status() == 3: # connected
        break
    connection_timeout -= 1
    print('Waiting for Wi-Fi connection...')
    time.sleep(1)

# Handle connection error
if wlan.status() != 3:
    raise RuntimeError('wifi connection failed')
else:
    print('connected')
    status = wlan.ifconfig()
    print('ip = ' + status[0])
    
try:
    ntptime.settime()  # Fetches time from NTP server
    print("Time synchronized with NTP")
except Exception as e:
    print("NTP sync failed:", e)
    
time.sleep_ms(config.RATE)

# config ssl connection w Transport Layer Security encryption (no cert)
context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT) # TLS_CLIENT = connect as client not server/broker
    
# define I2C connection and BMP and LED
i2c = machine.I2C(id=0, sda=Pin(20), scl=Pin(21)) # id=channel
bmp = BMP280(i2c)
led_pin = Pin('LED', Pin.OUT)

def connectMQTT():
    client = MQTTClient(client_id=b'server_room_picow',
                        server=config.MQTT_BROKER,
                        port=config.MQTT_PORT,
                        user=config.MQTT_USER,
                        password=config.MQTT_PWD,
                        ssl=context)
    client.connect()
    return client

client = connectMQTT()

def publish(topic, value):
    print(topic)
    print(value)
    client.publish(topic, value)
    print("publish Done")
    
def on_message(topic, msg):
    print(f"Received message: {msg} on topic: {topic}")
    if msg == b"ON":
        led_pin.on()  # Turn on the LED
        print("LED ON")
    elif msg == b"OFF":
        led_pin.off()  # Turn off the LED
        print("LED OFF")
        
client.set_callback(on_message)
client.subscribe(b"picow/control")



def publish_with_timestamp(topic, value):
    timestamp = get_timestamp()
    #load = "yqCpOtA1F8BRfDo9LjpH9eZ7vs1gK43n2ibNYcWw2v6uXkHksh3bxA4Gv2z5qJZRraCgK9tWmksl6Z4MlFWfx70xUk7ZTJePGjRFG5Odpt4LrHtUlTCOmVqP6qxS9aQ8v7OumXqITt58PahCjdiQ7a3PnvM8o9w9S3M1OSW1sZ6pQbiJjnp6gtx2OK53lc7NVKdOw9FzvZmG1jP5Uo3m9oxCG23jLPyEkWTg4bu2G4KMRKFC5sbvhRaJpc9j9YWa8d1S5v6C2kq7dPL2zB8B2HRXUcnVrxmEdgHpRwp5QomXoN7FFj3NY5hws4D2p4gHtDL5w5vKwqfGb35iVRT73k3pnCzLaaR92gD9Mr6Mm0Fwcf6rtdWcZGvqFeGrTrVnT4gW2zAeLRuBx8Z1ll5yVYfb10XGgdSyTxdHHHGNS0t9ttllhfPec2a9W6ib6xM9c0lJm3smhI0d7zPdcHISzd45DRdAOA3yVt1A5QOehgaRf3v1buVnl6lWiZHRgYfgNjd2qA3wrduVffrO5Ln0YngPaKdzpPE8pl6EhmMb9pr0SzRgXzydXZINjueqzVfsQyUk6ShdHl1l76R06f0k1aWEyU4Ls45RZIRNjLlx6WbGd4xpCwPqevs5yFGJP69lR6h0OSzV3D1yfcLVb0JlBwwb9paYPjCKgPpR8wPRDC3pEGBJ6NJrntHfvnIWm1No9HPO6RjK5BhNr6z4OBg8iNrJzOpR7jOBzz5zDOeHKY0vhHl8z2i2ix5OXM8wmF8fbANkLBsbTo5yYcUqTYHMrhOBTOmsY4y3N9L0jgn4pz8w3dFodxCihD7D75rBrp0RJEr4k5hH3Be2hpLfO7bHpeIhKqfHOP4Wb6fVfVLVoy1BB6ZdrnlpVmTiyBwrHqL5zK6OTXh7YXn69zHg1fg6fGGN7i9CGG25D1JThmfHPrqSxlr3EIZp0oQOGcDvdgxTL5pNRV40gQGmtBchFqsIZq8UvHo4ABpnvTrD6Xxh1FhsvS6AsOVGi0J9k6kjwX9F8vnAAGZcqTnaxD7fo7gse91hU4Fe7HrE8skSh5iVmndHpC8hZrVAnldgpnkpz9k2yHm1w2ML3t9DwA8HfYtiHNjFYnoFgrPl6qU7P1z7shY9F2r0p8Hndt8Vbp0ymD4c4q1VO0FGf6g"
    message = f"{timestamp} - {value} - {load}"
    print(f"Publishing to {topic}: {message}")
    client.publish(topic, message)
    print("Publish Done")
    


while True:
    
    temperature = bmp.temperature
    pressure = bmp.pressure

    print(str(temperature))
    print(str(pressure))

    # Publish as MQTT payload
    publish_with_timestamp('picow/temperature', str(temperature))
    publish_with_timestamp('picow/pressure', str(pressure))
    client.check_msg()


    # Delay 5 seconds
    time.sleep_ms(config.RATE)



