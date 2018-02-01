
# Academic Platform for ONline Experiments (APONE)

The Academic Platform for ONline Experiments (APONE) aims at the evaluation of new approaches in interfaces and algorithms, by means of the interaction of users in online real environments. These new approaches (treatments) could affect the user interfaces as well as the back-end algorithms or a combination of both, and the user interactions could be compared with those obtained from existing approaches (control).

The objective  of the platform is to speed up the setup of **controlled A/B experiments on the Web**, as well as to keep a shared resource of methods and data that can help in research. APONE builds upon [PlanOut](https://facebook.github.io/planout/) to define experiments, and offers a web GUI to easily create, manage and monitor them.

A second component is the client, accessed by the users (directly or via redirection through the experimental platform) and developed by the experimenter, which will interact with the RESTful web services of the platform directly to i) get the variant and parameters corresponding to the experimental unit identifier (usually the user identifier), and ii) register the events that occur during the user interaction. This information can be monitored and downloaded. Its analysis will allow the experimenter decide which treatment is best.

In order to show the capabilities of the platform as well as to ease the development of such a client, an example is also provided ([Client Example, or ClientE](https://github.com/marrerom/ClientE)).

APONE is not restricted to any specific domain of research. The domain of the experiments defined and run is actually given by the client linked to it, which determines the experience of the user. Nonetheless, the provided ClientE and most examples in this user guide are limited to the Information Retrieval domain.

A [user guide](https://github.com/marrerom/APONE/userguide.pdf) explains how to easily define, run and control an experiment. We assume that the experimenter has [online access to APONE](http://ireplatform.ewi.tudelft.nl:8080/APONE). It is also possible to download and install the platform following the [installation instructions](https://github.com/marrerom/APONE/install.pdf).