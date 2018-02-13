
# Academic Platform for ONline Experiments (APONE)

The Academic Platform for ONline Experiments (APONE) is an open source platform to set up **controlled A/B experiments on the Web**, as well as to keep a shared resource of methods and data that can help in research. APONE builds upon [PlanOut](https://facebook.github.io/planout/) to define experiments, and offers a web GUI to easily create, manage and monitor them.

The platform supports the definition of complex experiments as well as the recording of complex information from the interactions with users. It is not restricted to any specific domain of research and supports any technology used in the developing of the clients users are exposed to. Additionally, the users accesing the platform are able to participate in running experiments, which makes the platform useful for educational purposes as well. 

A [user guide](docs/APONEUserGuide.md) explains how to easily define, run and control an experiment using a demo Client ([Client Example, or ClientE](https://github.com/marrerom/ClientE)) and the [running instace of APONE we provide](http://ireplatform.ewi.tudelft.nl:8080/APONE), where you can also find three running experiments with ClientE for demonstration purposes. ClientE and most examples in this user guide are focused on the Information Retrieval domain. 

It is also possible to download and install the platform following the [installation instructions](docs/installation.md).

### Current Features

- Unlimited concurrent experiments
- Start/stop/restart experiments 
- Unlimited number of variants
- Percentage of experimental units per variant
- Definition of complex experiments by using [PlanOut definition language](https://facebook.github.io/planout/docs/planout-language.html)
- Possibility to override variable-values during the experiment
- Automatic stopping conditions
- Multivariate experiments
- Automatic set-up of full-factorial experiments
- Dashboard to monitor experiments in real-time
- Participation of users of the platform in the running experiments (crowd-workers)
- Leaderboard of crowd-workers who have completed more experiments
- Different formats to register interaction information (events): String, JSON, Binary
- Download events in CSV and JSON
- Message broker (RabbitMQ) to deal with events
- Assignment methods: client-side, server-side, traffic-splitting
- RESTful web services
- Web GUI to manage experiments and events

### Future Features
- (Statistical) analysis of events from within the platform
