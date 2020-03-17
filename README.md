# P2P Chord Publish Subscribe System

## Before Compile and Run:
- The boot peer must be the first one to join the P2P Chord network, which means we must run the program on boot peer firstly.
	- Please set up your boot peer ip in Peer.java on line 21 before compiling. 
	- By default, the boot peer ip is set to be the localhost ip address (127.0.0.1) with port number of 8001.
- The boot peer must also be the last one to leave the P2P Chord network.

## Compile:
- Please locate to the P2PPubSub/src folder and input the command to compile:
	- javac Client.java

## Run: 
- If you run the program via the following command without arguments, the process will use your ip and a default port 8001 for communication.
	- java Client
- If you run the program with arguments as follows, the process will use your ip and your specific port number for communication.
	- java Client -port YOUR_PORT_NUMBER
- NOTE 1: The boot peer’s port number must be 8001. Or you can change it in the Peer.java on line 23.

- After running a process, a new peer or node will join into the P2P Chord network via the boot peer.
- NOTE 2: if you want to add a new node into the network, you need to open a new terminal to run the application with IP combined with its port number that wouldn't conflict with existing nodes.
	- If a new node generates a chord ID which already exists and conflicts with another node, please change the port number when restart the program by: 
	- java Client -port YOUR_PORT_NUMBER

## Commands used on running program:
- To display or stop displaying the finger table information:
	- v
- To add categories:
	- addcategory CATEGORY1 CATEGORY2 ...
- To subscribe/ unsubscribe categories:
	- subscribe -category CATEGORY1 CATEGORY2
- To publish a message:
	- publish -category CATEGORY -content CONTENT
- To voluntarily leave the network and exit the process:
	- q
- To terminate a node to simulate node failure scenario：
	- ctrl + c 
- To show current subscription list:
	- show -subscriptionlist
- To show current valid category set:
	- show -categoryset

