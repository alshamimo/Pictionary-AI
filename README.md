# 🎨 Pictionary AI — Sketch Classifier

A Java-based neural network that classifies hand-drawn sketches from the 
Google QuickDraw dataset. Built from scratch as a university team project 
at Leibniz University Hannover.

---

## 🧠 How It Works

The model takes a sketch as input and classifies it into one of 5 categories.
The entire neural network — including forward propagation, backpropagation, 
and gradient descent — was implemented manually in Java without any ML framework.

---

## ✨ Features

- Full forward & backpropagation implemented from scratch
- Minibatch training for faster convergence
- Early stopping to prevent overfitting
- Modular software pipeline (MVC architecture)
- Team collaboration via GitLab CI (branches, merge requests)

---

## 🛠 Tech Stack

| | |
|---|---|
| **Language** | Java |
| **Build Tool** | Gradle |
| **Testing** | JUnit |
| **Version Control** | GitLab CI |
| **IDE** | IntelliJ IDEA |

---

## 📁 Project Structure
```
app/
├── model/        # Neural network logic (layers, weights, activation)
├── training/     # Training loop, minibatch, early stopping
├── data/         # Data loading and preprocessing
└── view/         # UI / visualization
```

---

## 🚀 Getting Started
```bash
# Clone the repo
git clone https://github.com/alshamimo/Pictionary-AI.git

# Build with Gradle
./gradlew build

# Run
./gradlew run
```

---

## 👥 Team

University team project — Leibniz University Hannover, 2025

---

## 📫 Contact

**Mohammed Al-shami** · [LinkedIn](https://linkedin.com/in/alshami-dev) · alshamim846@gmail.com
