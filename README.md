# nbody-2d
A simple [N-body simulation](https://en.wikipedia.org/wiki/N-body_simulation) implementing Newtonian gravity on a 2D field of dynamically colored particles.

![image](https://github.com/user-attachments/assets/0fec02e0-5486-4ed8-b904-848d8c94ceca)

## Usage
```
NBody2D [-hV] [--headless] [-c=<configurationPath>] [-i=<inputPath>]
               [-o=<outputPath>] [-s=<steps>]
  -c, --config=<configurationPath>
                            Path to the YAML configuration file. Defaults to
                              'uniform.yml'.
  -h, --help                Show this help message and exit.
      --headless            Whether to run the simulation in headless mode (no
                              GUI)
  -i, --input=<inputPath>   Input path for a precalculated simulation.
  -o, --output=<outputPath> Path where the simulation results will be written.
                              Defaults to 'output.yml'.
  -s, --steps=<steps>       Number of simulation steps to run in headless mode.
                              Ignored if not in headless mode.
  -V, --version             Print version information and exit.
```

## Viewer Controls
- **Left Mouse Button** - click and drag to pan
- **Arrow Keys** - pan up/down/left/right
- **Space Bar** - start/stop the simulation
- **Scroll** - Zoom in and out on the mouse location
- **r** - reset all particles to their initial positions 
- **t** - toggle rendering trails behind particles
- **c** - toggle color for particles trails
- **f** - toggle rendering normalized force vectors
- **Esc** - closes the program
