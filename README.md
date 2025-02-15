# Server Room Temperature & Air Pressure Management System

## 📌 Introduction
The **Server Room Temperature & Air Pressure Management System** is an **IoT-based solution** designed to monitor and regulate critical environmental conditions in server rooms. It uses a **Raspberry Pi Pico W** and a **BMP280 sensor** to collect temperature and air pressure data, which is transmitted to the cloud via **MQTT** for real-time monitoring, historical analysis, and automated control actions.

![grafan1](https://github.com/user-attachments/assets/e51ef11d-7e4b-46aa-a5d8-e3337971a1c6)
![grafan2](https://github.com/user-attachments/assets/94802c8a-0aa4-4a14-a898-b5c687685d43)
![grafan3](https://github.com/user-attachments/assets/8a59851b-fdd0-41d1-ac16-7ef194be6843)

## 🔧 Features
✅ **Sensor Data Collection** – Uses BMP280 to collect temperature & air pressure readings  
✅ **Real-Time Monitoring** – Sends data to the cloud and mobile application using MQTT  
✅ **Automated Control** – Simulated fan operation via LED based on sensor thresholds  
✅ **Cloud Integration** – Stores data in **InfluxDB** and visualizes it with **Grafana**  
✅ **Mobile App** – Built with **Kotlin**, enables remote monitoring and control  

## 🏗️ System Architecture
The project consists of **four layers**:

1. **Sensor Layer** – Raspberry Pi Pico W with BMP280 gathers data  
2. **Networking Layer** – Uses MQTT via **HiveMQ Cloud** for communication  
3. **Data Management Layer** – Stores data in **InfluxDB** and processes it via **Node-RED**  
4. **Application Layer** – **Grafana & Kotlin-based mobile app** for real-time visualization  

![arch](https://github.com/user-attachments/assets/19dd7b56-b0ef-44bd-b789-e3e10c4651fb)
![layer](https://github.com/user-attachments/assets/15ad15ab-2e84-4110-a64b-302e5574347e)

### 🔌 Hardware Components
- 🖥 **Raspberry Pi Pico W** (Microcontroller)
- 🌡 **BMP280 Sensor** (Measures temperature & air pressure)
- 💡 **LED** (Simulates fan operation)

### 🛠 Software & Tools
- **MicroPython** – Programming for Raspberry Pi Pico W
- **MQTT (HiveMQ Broker)** – Data communication protocol
- **InfluxDB** – Time-series database for sensor data
- **Node-RED** – Data processing & control logic
- **Grafana** – Real-time visualization
- **Kotlin (Android App)** – Mobile application for monitoring  

## 📊 Evaluation & Performance
The system was tested under various conditions:
- **Latency Tests** – Measuring system delays based on different sending rates  
- **Data Accuracy** – Verifying real-time synchronization of sensor readings  
- **Network Performance** – Impact of connection stability on real-time monitoring  

📌 **Key Findings:**
- **Real-time updates work efficiently** under normal conditions  
- **Time synchronization issues** were identified between the Raspberry Pi and Android app  
- **Higher data transmission rates impact performance**, requiring optimization  

## 🚀 Future Improvements
🔹 **Fix time synchronization** using Network Time Protocol (NTP)  
🔹 **Enable fan control via the mobile app**  
🔹 **Add alert notifications** for critical environmental changes  
🔹 **Improve security measures** for data protection  
🔹 **Use Docker & Docker Compose** for scalable deployment  

## 📂 Project Repository
🔗 GitHub Repository: [IOT Project](https://github.com/alireza-dehghan-nayeri/IOT)

## 🤝 Contributors
- **Alireza Dehghan Nayeri**  
- **Fatemeh Soufian Khakestar**  
- **Chamudi Vidanagama**  
- **Bharathi Sekar**  

## 📜 License
This project is licensed under the **MIT License** – feel free to modify and use it!  

---

📩 *For any questions or contributions, feel free to open an issue or pull request!* 🚀
