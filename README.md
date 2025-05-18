# NBody-2D

![License](https://img.shields.io/github/license/ColinGJohnson/nbody-2d)
![Java Version](https://img.shields.io/badge/Java-21-blue)

An [N-body simulation](https://en.wikipedia.org/wiki/N-body_simulation) which applies [universal gravitation](https://en.wikipedia.org/wiki/Newton%27s_law_of_universal_gravitation) to 2D particles. 

Includes an interactive viewer for rendering real time and saved simulations, a headless/CLI mode to pre-render and save simulations in protobuf format, and config files to define start state.

![image](https://github.com/user-attachments/assets/0fec02e0-5486-4ed8-b904-848d8c94ceca)

## Usage

To run the simulation, use the following command-line syntax:
``` bash
NBody2D [-hV] [--headless] [-c=<configurationPath>] [-i=<inputPath>] [-o=<outputPath>] [-s=<steps>]
```

### Configuration

The initial state and simulation behavior is determined by configuration file provided via the `--config` parameter.
Examples can be found in `examples/`

### Command-line Options:

| Option | Description |
| --- | --- |
| `-c`, `--config=<configurationPath>` | Path to the YAML configuration file. Defaults to `uniform.yml`. |
| `-h`, `--help` | Show help information and exit. |
| `--headless` | Run the simulation in headless mode (no GUI). |
| `-i`, `--input=<inputPath>` | Input path for a precalculated simulation. |
| `-o`, `--output=<outputPath>` | Path to save the simulation results. Defaults to `output.yml`. |
| `-s`, `--steps=<steps>` | Number of simulation steps to run in headless mode. Ignored if not running in headless mode. |
| `-V`, `--version` | Print version information and exit. |

## Viewer Controls

Enhance your simulation experience with intuitive controls:

| **Control** | **Action** |
| --- | --- |
| **Left Mouse Button** | Click and drag to pan. |
| **Arrow Keys** | Pan up, down, left, or right. |
| **Space Bar** | Start/stop the simulation. |
| **Mouse Scroll Wheel** | Zoom in and out at the mouse location. |
| **`r` Key** | Reset all particles to their initial positions. |
| **`t` Key** | Toggle rendering trails behind particles. |
| **`c` Key** | Toggle color modes for particle trails. |
| **`f` Key** | Toggle rendering of normalized force vectors. |
| **`Esc` Key** | Close the program. |

## Getting Started

Ensure you have **Java 21 or higher** installed.

``` bash
./gradlew build
java -jar build/libs/nbody-2d.jar [OPTIONS]
```
