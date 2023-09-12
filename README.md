# engine

The MechMania engine runs the game by taking inputs from two clients (also called bots),
and outputs a gamelog (a large json specifying what happened each turn).
You can feed this gamelog to the visualizer to visualize what happened.

## Installation

First, make sure you have Java 17+ installed. If you have issues later, downgrading to Java 17 might fix it.

To install, extract the engine.jar from the engine.zip file
[from the latest release](https://github.com/MechMania-29/engine/releases).
Save this file somewhere you can use.

<details>
<summary>Manual build instructions</summary>

To manually build the jar, first download the repo.

cd into the repo and run gradle to build, like so:

On windows:
```sh
./gradlew.bat build
```

On linux/mac:
```sh
./gradlew build
```

</details>

## Usage

To invoke the engine, simply run engine.jar (note if the name of the jar is different you will have to modify this command):

```sh
java -jar engine.jar [port1] [port2]
```

`port1` and `port2` are the ports of the human bot and zombie bot respectively.

The engine will use this information to connect to the bots, which should be serving on these ports.

An example command might look like:

```sh
java -jar engine.jar 9001 9002
```

### Environment Variables

To modify behavior and help debug some things, we have a few env vars to specify certain settings.

`OUTPUT` - When set to a path, outputs the gamelog there. For example, `OUTPUT=gamelog.json` will output the gamelog at `gamelog.json`.
The default path is `gamelogs/game_yyyy_mm_dd_hh_mm_ss.json`.

`DEBUG` - When set to 1, enables debug mode which will output more detailed debug output and a debuglog for each turn to debuglogs/.