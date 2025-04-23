# java-udp-socket-comm
##  Java UDP Socket Communication

A simple yet powerful Java program that demonstrates communication between processes over UDP sockets. The application operates in two modes — **master** and **slave** — based on port availability. Master nodes receive and store numbers from slaves, calculate their average, and return results on request.

---

## ⚙ How it Works

- If the program **can bind** to the specified port — it enters **Master Mode**:
  - Receives integers from slaves.
  - Stores incoming numbers.
  - On receiving `0` → calculates and returns the average.
  - On receiving `-1` → broadcasts a shutdown signal and terminates.

- If the port is **already taken** — it enters **Slave Mode**:
  - Sends its number to the master.
  - If number is `0`, it waits for the master's response (average).
  - Ends after communication.

---

##  Features

-  **UDP-based number transmission**
-  **Average calculation** (rounded down)
-  **Master-slave role auto-detection**
-  **Broadcast termination command (-1)**

---

##  Project Structure

```plaintext
java-udp-socket-comm/
├── src/
│   └── Main.java         # All logic (Master/Slave, UDP communication)
├── .gitignore
└── README.md             # You're here!
```

---

##  How to Run

### 1. Clone the repository
```bash
git clone https://github.com/Desstori15/java-udp-socket-comm.git
cd java-udp-socket-comm
```

### 2. Compile
```bash
javac src/Main.java
```

### 3. Run the master (binds to port 8888 and waits)
```bash
java -cp src Main 8888 -1
```

### 4. Run slaves in separate terminals (send a number)
```bash
java -cp src Main 8888 42
java -cp src Main 8888 17
```

### 5. Request average
```bash
java -cp src Main 8888 0
```

### 6. Shutdown the master (broadcast -1)
```bash
java -cp src Main 8888 -1
```

---

 ## Notes
Port 8888 is just an example — use any free port.
All messages are sent as raw strings over UDP.
The average returned is an integer (rounded down using Math.floor()).
Ensure your system/firewall allows UDP on chosen port.


## Author
Vladislav Dobriyan
GitHub: @Desstori15
