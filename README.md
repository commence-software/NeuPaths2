# Version 2.0.2

![NeuPaths_Logo_GitHub](https://github.com/user-attachments/assets/1b151bab-5797-4fb2-a23f-fc9a85545498)

# A Nervous System for Distributed Software: Introducing NeuPaths

NeuPaths is a framework for event-driven, distributed, and parallel dataflow programming, written in pure Java SE.

Most distributed systems are built around a thing in the middle: A broker, a queue, a message bus, a coordinator — some central authority that everything else talks *through*.  NeuPaths, however, is completely de-centralized using a design that is a metaphore for the human nervous system.

In a nervous system there is no central switchboard that every signal must pass through. There are neurons, and there are the connections between them, and signals propagate across that mesh. Processing happens *at the neurons*, in parallel, triggered by the arrival of stimuli. The system is astonishingly resilient — you can lose cells, you can lose pathways, and the whole keeps functioning. That resilience is not bolted on. It's a direct consequence of the architecture having no single point that everything depends on.

NeuPaths borrows that shape directly. Here are the moving parts:

- A **stimulus** is a unit of data — a typed message or event that flows through the system. You define your own.
- A **cell** is an autonomous processing node. It listens, it processes, it routes. Cells are the neurons.
- A **synapse** is a connection between cells — a data conduit. It's an abstraction over whatever transport you're actually using, whether that's a socket across a data center or shared memory on the same machine.
- An **activator** is your logic. It's the part you write. Each activator has one or more **receptors** (typed inputs) and zero or more **transmitters** (typed outputs).
- A **subscription** is a wiring rule. It says, in effect, "when this cell's transmitter fires, deliver it to that receptor." Formally it's a contract: (producing cell, producing transmitter) → consuming receptor.
- A **domain** is a namespace that scopes where stimuli are allowed to travel, so you can compartmentalize a large system.

Put those together and you get a **cellular system**: a mesh of cells bound by synapses, with no broker, no server, and no controller anywhere in it. Each cell is autonomous. Data flows across the mesh from producers to the cells that subscribed to it, and the framework quietly filters out duplicates when the same stimulus arrives by more than one path.

## Why remove the broker?

Because the broker is where a lot of your hardest problems concentrate. It's the thing you have to keep alive, keep scaled, keep from becoming the bottleneck, keep from becoming the single point of failure. An enormous amount of operational effort in distributed systems goes into defending the center.

When there is no center, several things change at once. There's no single point of failure to design around. There's no broker cluster to provision, tune, and babysit. Location stops mattering to your code — a neighboring cell might be in the same JVM or on a machine three racks over, and your activator neither knows nor cares. And resilience becomes something you get from the *shape* of the system rather than something you buy from a component: run the same activator on two nodes, let stimuli travel over more than one network path, and the framework's duplicate-filtering makes the redundancy safe.

There are trade-offs, of course. A mature broker brings tooling, ordering guarantees, and a large ecosystem, and a decentralized mesh asks you to think differently about all of them. This is a genuine trade. But for a whole class of systems — resilient, on-premise, edge, or simply systems where the operational weight of a broker isn't worth it — I think it's a trade worth making, and almost nobody in the Java world is offering it.

## The dataflow part

There's a second idea in NeuPaths that's just as important as the missing broker, and it also comes straight from the biology: an activator only fires when *all* of its receptors hold data.

This is dataflow execution. Your logic doesn't run on a schedule or in response to a single message. It runs when its inputs are complete. A neuron that needs three signals waits, quietly, until all three have arrived, and only then does it act — possibly producing new stimuli that ripple onward to other cells. This means the timing and priority of processing are dictated entirely by the arrival of data. The system is event-driven all the way down, and it's parallel by nature, because independent cells process independent stimuli at the same time without coordinating.

It also means cells spend most of their lives asleep. Between stimuli, a cell sits on the operating system's wait queue doing nothing — no polling, no spinning, no wasted cycles. Work happens exactly when there is work to do.

## Write once, run anywhere

One deliberate choice: NeuPaths is pure Java SE with no native code and no external dependencies. This means the framework runs anywhere a JVM runs, deploys as a plain artifact with nothing to install alongside it, and adds no operational surface area of its own. In a world where a lot of "simple" infrastructure quietly drags in a half-dozen services, being genuinely self-contained is a feature.

## The big picture

I'd like to leave you with this mental shift: Stop picturing a hub with spokes. Picture a mesh with no middle. Picture logic that wakes when its inputs are ready and sleeps the rest of the time. Picture a system you can grow, shrink, and partially break while it keeps running. That's the nervous system NeuPaths is trying to give your software — and once you've seen distributed systems that way, the thing in the middle starts to look less like a necessity and more like a habit.

## Where do I start?

The `documents` directory contains many resources for learning NeuPaths programming.  The [Overview](documents/NeuPaths_Overview.pdf) and
[SDK User Guide](documents/NeuPaths_SDK_User_Guide.pdf) documents introduce the NeuPaths concepts.  The `javadoc` subdirectory contains the complete API documentation.  After you are familiar with the concepts, look at the `examples`.  They demonstrate many of the key features and techniques you will use in your NeuPaths programs.

## Where can I find support?

Contact aaron.caraveo@commence-software.com with questions, support requests and issues.  The NeuPaths Discord server is also available for support and community discussion.  Send an email to request an invitation.
