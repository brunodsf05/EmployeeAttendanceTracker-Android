# EmployeeAttendanceTracker-Android

This project is a service for managing employee attendance.

You are currently looking at the **android** client.

[EmployeeAttendanceTracker-Backend](https://github.com/brunodsf05/EmployeeAttendanceTracker-Backend)



## ✨ Features
-   🔐 **User Authentication**: Secure login for administrators and employees.
-   🕒 **Clock In/Out**: Employees can register entry and exit times only if they are physically at work.
-   📊 **Attendance Records**: Store and query presence and absence logs.



## ⚙️ Prerequisites

### ✅ Required
-   🤖 **Android studio** IDE

### 🧩 Optional
-   📱 **Android phone** to test this in real hardware
-   🔐 **Keystore** to compile an signed APK



## 📥 Installation and Setup

### 🌀 Clone the repository
```sh
git clone https://github.com/brunodsf05/EmployeeAttendanceTracker-Android.git
cd EmployeeAttendanceTracker-Backend
```



## 🛠️ Maintenance

### 🧠 Useful knowledge
If you encounter incompatible AGP error...
1.  Open [/gradle/libs.version.toml](/gradle/libs.versions.toml).
2.  Find the ``agp = "X.X.X"`` line thats below ``[versions]``.
3.  Change the version to the one the console is telling you to use. Example: ``agp = 8.6.0``.
4.  Open the ``File`` tab and click in ``Sync Project with Gradle Files (Ctrl+Mayús+O)``.



## 📂 Project Structure
TODO