# Overview
The gaml.extension.unitu GAMA plugin  provides a number of tools and GAML extensions (built-in species and new experiment type) to transform a GAMA model into a Unity 3D universe.
It works with the [simple UNITY template](https://github.com/project-SIMPLE/simple.template.unity) and the [simple server middleware](https://github.com/project-SIMPLE/GamaServerMiddleware). 

The plugin integrates 3 new element types for GAMA:
* An abstract species, abstract_unity_linker, which links a GAMA simulation to a Unity game. 
* An abstract species, abstract_unity_player, which represents a Unity player in a GAMA simulation.
* A new type of experiment, unity, which creates a unity_linker at initialization.

In addition, the plugin integrates a tool accessible from the UnityVR menu that allows you to generate a new model from a GAMA model, extending it and linking it to Unity. 

Finally, the plugin includes several templates illustrating how to use the plugin (Demo) or how to send or receive geometries from GAMA to Unity or from Unity to GAMA. 

A description of the use of the plugin with a tutorial can be found [here](https://github.com/project-SIMPLE/documentation/wiki).

# Installation

## From GAMA
To use the plugin from a release version of GAMA (version 1.9.3), it can be installed directly from the plugin installer.
Specifically, select "Support/Install new plugins..." from the menu, 
![qs1](./images/InstallPlugin.png)

then "Work with", copy the address [https://project-simple.github.io/gaml.extension.unity/](https://project-simple.github.io/gaml.extension.unity/) and select the plugin afterwards.
![qs1](./images/Plugin.png)

## Developer mode 
If you have a developer version of GAMA (branch 1.9.3), you can clone the project directly to add the plugin to GAMA. 


 
