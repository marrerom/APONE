<span class="c19"></span>

<span class="c19"></span>

<span class="c19"></span>

<span class="c19">Academic Platform for ONline Experiments (APONE)</span>

<span class="c23 c45 c61">USER GUIDE</span>

<span class="c45 c55">TU Delft, February 2018</span>

------------------------------------------------------------------------

<span class="c19"></span>

<span class="c23 c39">INDEX</span>
==================================

<span class="c7"><a href="#h.r3dsf1qbjzya" class="c0">INDEX</a></span><span class="c7">        </span><span class="c7"><a href="#h.r3dsf1qbjzya" class="c0">2</a></span>

<span class="c7"><a href="#h.3ay5k1lxo8u6" class="c0">1.INFORMATION RETRIEVAL AND A/B EXPERIMENTS</a></span><span class="c7">        </span><span class="c7"><a href="#h.3ay5k1lxo8u6" class="c0">3</a></span>

<span class="c7"><a href="#h.15tqltp61k1i" class="c0">2. APONE OVERVIEW</a></span><span class="c7">        </span><span class="c7"><a href="#h.15tqltp61k1i" class="c0">7</a></span>

<span class="c7"><a href="#h.nui5tusi70m2" class="c0">3. TECHNICAL DETAILS</a></span><span class="c7">        </span><span class="c7"><a href="#h.nui5tusi70m2" class="c0">8</a></span>

<span><a href="#h.1o1g18nxr6l8" class="c0">APONE 1.0.0</a></span><span>        </span><span><a href="#h.1o1g18nxr6l8" class="c0">8</a></span>

<span><a href="#h.rcf6m4eq4ib6" class="c0">Web GUI</a></span><span>        </span><span><a href="#h.rcf6m4eq4ib6" class="c0">9</a></span>

<span><a href="#h.hhxa4jaf9li7" class="c0">ClientE 1.0.0</a></span><span>        </span><span><a href="#h.hhxa4jaf9li7" class="c0">9</a></span>

<span class="c7"><a href="#h.otbwp6vxuezc" class="c0">4. HOW TO CREATE AN EXPERIMENT</a></span><span class="c7">        </span><span class="c7"><a href="#h.otbwp6vxuezc" class="c0">11</a></span>

<span><a href="#h.ld121gufe27t" class="c0">Step 1. Create and Start the Experiment in APONE</a></span><span>        </span><span><a href="#h.ld121gufe27t" class="c0">12</a></span>

<span><a href="#h.d8x5bkomugtj" class="c0">Step 2. Install and Deploy the Client</a></span><span>        </span><span><a href="#h.d8x5bkomugtj" class="c0">14</a></span>

<span><a href="#h.of4eauq5ytmm" class="c0">Step 3. Test your Client</a></span><span>        </span><span><a href="#h.of4eauq5ytmm" class="c0">15</a></span>

<span><a href="#h.mm87pof3whza" class="c0">Step 4. System in Production</a></span><span>        </span><span><a href="#h.mm87pof3whza" class="c0">16</a></span>

<span class="c7"><a href="#h.uoe0sw1gg8vf" class="c0">5. HOW TO REGISTER EVENTS</a></span><span class="c7">        </span><span class="c7"><a href="#h.uoe0sw1gg8vf" class="c0">18</a></span>

<span class="c7"><a href="#h.4c7j6h9jzgv8" class="c0">6. WEB INTERFACE USER REFERENCE</a></span><span class="c7">        </span><span class="c7"><a href="#h.4c7j6h9jzgv8" class="c0">21</a></span>

<span><a href="#h.tvccp3q33vaj" class="c0">6.1 Experiment::Manage</a></span><span>        </span><span><a href="#h.tvccp3q33vaj" class="c0">21</a></span>

<span><a href="#h.4yszkj5w6cza" class="c0">Start/Stop an experiment</a></span><span>        </span><span><a href="#h.4yszkj5w6cza" class="c0">21</a></span>

<span><a href="#h.vmt6xf16qafb" class="c0">6.2 Experiment::Create New</a></span><span>        </span><span><a href="#h.vmt6xf16qafb" class="c0">22</a></span>

<span><a href="#h.olmfp3hqi68v" class="c0">6.3 Experiment::Create New (Advanced)</a></span><span>        </span><span><a href="#h.olmfp3hqi68v" class="c0">23</a></span>

<span><a href="#h.ijbya3g9uojb" class="c0">6.4 Events::Manage</a></span><span>        </span><span><a href="#h.ijbya3g9uojb" class="c0">24</a></span>

<span><a href="#h.olnngs56antg" class="c0">6.5 Monitoring::Experiments</a></span><span>        </span><span><a href="#h.olnngs56antg" class="c0">25</a></span>

<span><a href="#h.p91t1dnzll2d" class="c0">6.6 Monitoring::Users</a></span><span>        </span><span><a href="#h.p91t1dnzll2d" class="c0">27</a></span>

<span class="c7"><a href="#h.ko9o28hs68du" class="c0">8. ADVANCED EXPERIMENTS</a></span><span class="c7">        </span><span class="c7"><a href="#h.ko9o28hs68du" class="c0">28</a></span>

<span><a href="#h.qqnxczautj51" class="c0">8.1 MORE CONTROL IN OUR EXPERIMENT</a></span><span>        </span><span><a href="#h.qqnxczautj51" class="c0">28</a></span>

<span><a href="#h.fc7qihyz0t71" class="c0">8.2 PLANOUT</a></span><span>        </span><span><a href="#h.fc7qihyz0t71" class="c0">29</a></span>

<span><a href="#h.po6ma0prh2y3" class="c0">PlanOut in APONE</a></span><span>        </span><span><a href="#h.po6ma0prh2y3" class="c0">31</a></span>

<span class="c7"><a href="#h.wz8hjou74xlm" class="c0">9. RELEVANT REFERENCES</a></span><span class="c7">        </span><span class="c7"><a href="#h.wz8hjou74xlm" class="c0">33</a></span>

<span><a href="#h.8n6iae29v1r7" class="c0">Surveys</a></span><span>        </span><span><a href="#h.8n6iae29v1r7" class="c0">33</a></span>

<span><a href="#h.pk3s0o30l1xw" class="c0">Tips</a></span><span>        </span><span><a href="#h.pk3s0o30l1xw" class="c0">33</a></span>

<span><a href="#h.sebg0wfqiwoa" class="c0">Experiment Life-Cicle</a></span><span>        </span><span><a href="#h.sebg0wfqiwoa" class="c0">33</a></span>

<span><a href="#h.v8jjtrhwndmh" class="c0">Interleaving</a></span><span>        </span><span><a href="#h.v8jjtrhwndmh" class="c0">33</a></span>

<span><a href="#h.ia08tl9thj6d" class="c0">Large-scale</a></span><span>        </span><span><a href="#h.ia08tl9thj6d" class="c0">34</a></span>

<span><a href="#h.3leyknim79lb" class="c0">PlanOut</a></span><span>        </span><span><a href="#h.3leyknim79lb" class="c0">34</a></span>

<span>1.</span><span>INFORMATION RETRIEVAL AND A/B EXPERIMENTS</span>
=====================================================================

<span class="c1">In this section, we will introduce very briefly how Information Retrieval systems are evaluated, and we will compare the process followed in traditional evaluation with the one followed in A/B experiments.</span>

<span class="c1"></span>

<span>Information Retrieval has been traditionally evaluated through </span><span class="c15">offline evaluation</span><span>, following the so called </span><span class="c15">Cranfield Paradigm</span><span class="c1">, because of the Cranfield projects initiated in 1957 by Cleverdon, which provided the foundations for the evaluation of IR systems. This paradigm consists on evaluating the results of the systems against a) a document collection, b) a predefined set of information needs or topics, c) the relevance judgements of the documents in the document collection in relation to the queries, and d) measures to compare the results (ranking and relevance of documents) obtained by the systems for the set of queries.</span>

<span class="c1"></span>

<span class="c1">This approach provides a way to evaluate with reliable judgments, collected from experts or using crowdsourcing techniques, but it has two major disadvantages: the cost to obtain such judgments, and the lack of a user model that may interfere in the results obtained (eg. interaction, personalized results, impact of interface changes).</span>

<span class="c1"></span>

<span>In order to include the user in the evaluation, two approaches have been used: </span><span class="c15">interactive evaluation</span><span> in controlled environments, and </span><span class="c15">online evaluation</span><span>. In the first case, a group of users interact with the systems in a controlled environment. In the second approach, the users are </span><span class="c15">exposed</span><span class="c1"> to the systems with no idea that an experiment is running.</span>

<span class="c1"></span>

<span>All of them are controlled experiments, and they aim to test one or more hypotheses. In offline experiments the aim is to test if the results obtained by the systems are good enough to decide if one system is better than another. In the case of interactive and online experiments,  the aim is to test if the changes in user behaviour have been caused by changes to the IR system (be them in the back-end or the front-end). In both cases though, we try to statistically prove that one system is better than other, and the dependent variable that measure the effect of the changes is known as </span><span class="c15">Overall Evaluation Criteria (OEC).</span>

<span class="c1"></span>

<span class="c15">A/B testing</span><span> is one specific type of online experiment where users are confronted with two approaches, usually a new approach (</span><span class="c15">treatment),</span><span> and an existing approach (</span><span class="c15">control)</span><span>. Each user is typically exposed to one of the </span><span class="c15">variants</span><span> (treatment or control), and his behaviour is recorded in order to decide which one has been more successful. The </span><span class="c15">experimenter</span><span class="c1"> has to define and implement the metrics to measure user behaviour (eg. clicks, dwell-time, time to click, number of queries), decide how to aggregate them in an OEC to test the hypothesis, and estimate the  required sample size, conditioned by the OCE. Therefore, the experimental design should be planned together with the methodology for data analysis. </span>

<span class="c1"></span>

<span>It is also relevant to note that the entity which is assigned to a treatment or a control is called the </span><span class="c15">experimental unit.</span><span> It determines t</span><span>he granularity at which the experimental conditions are compared (eg. the clicks, the number of q</span><span>ueries). Although the experimental unit is typically the user, depending on the experiment we could have smaller units (eg. queries, user-day) or larger (eg. network of users).</span><span class="c15"> </span><span class="c1">In general, we will assume that the experimental unit is the user in the examples contained in this document unless otherwise stated. </span>

<span class="c1"></span>

<span>It is also important to note that the assignment of variants to the experimental unit has to be </span><span>random</span><span> in order to guarantee the validity of the experiment, and the same experimental unit is always assigned to the same variant (usually it is desirable that the same user always interacts with the same variant during the experiment, specially if changes in the interface are involved).</span>

<span class="c1"></span>

<span class="c1">Let’s see an example. Suppose we are working on a new ranking algorithm B for our search engine, and we want to test if it is actually better than the state-of-the-art ranking algorithm A. We can follow different evaluation methods:</span>

<span class="c1"></span>

<span class="c23 c45 c7 c37">Traditional offline evaluation</span>

1.  <span>Get data: get a test collection, that is, a collection of documents, queries, and judgments of relevance over these documents for those queries. The latter information is typically a tuple of three values: </span><span class="c16">query\_id</span><span>,</span><span class="c16"> document\_id</span><span>, and</span><span class="c16"> relevance\_document\_query</span><span class="c35">.</span><span> The </span><span class="c20"><a href="https://www.google.com/url?q=http://ir.dcs.gla.ac.uk/resources/test_collections/cran/&amp;sa=D&amp;ust=1517523797492000&amp;usg=AFQjCNEcLaV2t4PuQa713A7Ju9WzO-Rs_Q" class="c0">Cranfield Collection</a></span><span> is an example of such a test collection. More examples can be found in </span><span class="c20"><a href="https://www.google.com/url?q=http://trec.nist.gov/data/test_coll.html&amp;sa=D&amp;ust=1517523797492000&amp;usg=AFQjCNFV5_-NVd06bVYnFJ1-quWqx7gmuw" class="c0">Text REtrieval Conference (TREC)</a></span><span class="c1">. </span>
2.  <span>Index documents and run queries: index the collection of documents in our system, launch the queries and save the ranking of documents obtained by systems A and B (these are called </span><span class="c15">runs</span><span>). Typically the results are saved as tuples of four values: </span><span class="c16">system\_id</span><span>,</span><span class="c16"> query\_id</span><span>,</span><span class="c16"> document\_id</span><span>, and</span><span class="c16"> relevance\_document\_query</span><span class="c23 c35 c37">.</span>
3.  <span class="c1">Select an Overall Evaluation Criteria: select a specific OEC to compare results, for example, mean reciprocal rank (mean of the inverse of the rank of the first relevant document obtained for each query). </span>
4.  <span>Analyse results: </span><span>calculate the results of both systems for the OEC selected in relation to the relevance judgments we have. For example, we could see that system A obtains a mean of 1/3 (on average, the first relevant document is in the third position), meanwhile system B obtains a mean of 1/2 (on average, the first relevant document is in the second position)</span><sup><a href="#ftnt1" id="ftnt_ref1">[1]</a></sup><span class="c1">. As a conclusion, and in relation to the test collection and metric used, system B improves system A.</span>

<span class="c1"></span>

<span class="c23 c7 c37 c45">A/B testing</span>

1.  <span class="c1">Get Data: get a collection of documents.</span>
2.  <span class="c1">Index documents: Index the collection of documents in the search engine.</span>
3.  <span class="c1">Develop a client: implement a client that let the users make queries. Identify the experimental unit and implement a random algorithm to assign algorithm A or B to each unit. In this case, the experimental unit would typically be the user, so the client must identify the user and assign algorithm A or B in her searches. Provide means to automatically register the behaviour of the user that can be used to calculate the Overall Evaluation Criteria selected.</span>
4.  <span>Select an Overall Evaluation Criteria: what are the interactions of the user that could indicate that one ranking algorithm is better than the other? Several metrics could be used, for example the click-rank (average positions of documents clicked on the search results page), where the most common variant is the mean reciprocal rank (average of the position of the first document </span><span class="c15">clicked</span><sup><a href="#ftnt2" id="ftnt_ref2">[2]</a></sup><span class="c1">). </span>
5.  <span class="c1">Analyse results:  after running the experiment and saving the results, we calculate the OEC for the group of users exposed to A, and for the group of users exposed to B, and compare the mean reciprocal rank obtained as we explained in offline evaluation. </span>

<span class="c1"></span>

<span class="c1"> </span>

<span class="c16">APONE</span><span> reduces the time needed to setup an A/B experiment by reducing the work to develop a client (step 3 above). For example, if the experimental unit is the user, the experimenter just has to determine what are the web pages displayed for control and treatment. In the previous example, we could develop a web page that will be displayed when the user is assigned to the control (ranking A), and a different one that will be displayed when he is assigned to the treatment (ranking B). The platform will redirect each user (according to his unique identifier) to the appropriate URL. The platform guarantees that the assignment of users to variants is random. It also offers the means to monitor and control the running experiments, and endpoints to save and retrieve the information we will have to analyze in step 5.</span>

<span class="c23 c35 c37"></span>

<span>Finally</span><span class="c1">, we should make sure that the results obtained are statistically significant. We could compare both algorithms with only three users in A/B testing, or only three queries in offline evaluation, but would those results be reliable? In the case of online experiments, the monitoring of the OEC during the experiment could help the experimenter decide when to stop.</span>

<span class="c1"></span>

<span>It is also recommended to take into consideration possible external elements that may condition the behaviour of the user. For example, we could pay attention to the date and/or to the user-agent information saved in the event. Sometimes the behaviour (or even the users themselves) are different during weekdays than in the weekends, the use of a mobile device or a different browser may also change completely their behaviour, and the first time a user access the experiment may have a behaviour completely different (novelty effect). For example, a bad search engine may receive more queries than a good one at first because the users do not find what they are looking for! </span>

<span class="c1"></span>

<span> </span>

------------------------------------------------------------------------

<span class="c23 c39">2. APONE OVERVIEW</span>
==============================================

<span class="c7">        </span>

<span>The </span><span class="c15">Academic Platform for ONline Experiments</span><span> </span><span class="c15">(</span><span class="c16">APONE, </span><span>or just </span><span class="c15">platform</span><span class="c15">)</span><span> aims at the evaluation of new approaches in interfaces and algorithms, by means of the interaction of users in online real environments. These new approaches (</span><span class="c15">treatments</span><span>) could affect the user interfaces as well as the back-end algorithms or a combination of both, and the user interactions could be compared with those obtained from existing approaches (</span><span class="c15">control</span><span class="c1">).</span>

<span class="c1"></span>

<span>The objective of the platform is to speed up the setup of controlled A/B experiments on the Web, as well as to keep a shared resource of methods and data that can help in research. APONE builds upon </span><span class="c20"><a href="https://www.google.com/url?q=https://facebook.github.io/planout/&amp;sa=D&amp;ust=1517523797496000&amp;usg=AFQjCNFuN-0pROkG7wRU4q70jXyUDzD95g" class="c0">PlanOut</a></span><span class="c1"> to define experiments, and offers a web GUI to easily create, manage and monitor them.</span>

<span class="c1"></span>

<span class="c1">A second component is the client, accessed by the users (directly or via redirection through the experimental platform) and developed by the experimenter, which will interact with the RESTful web services of the platform directly to i) get the variant (treatment or control) and parameters (optionally) corresponding to the user, and ii) register the events that occur during the user interaction. The analysis of this information will allow the experimenter decide which treatment is best.</span>

<span class="c1"></span>

<span>In order to show the capabilities of the platform as well as to ease the development of such a client, an example is also provided (</span><span>Client Example</span><span class="c16"> --ClientE--</span><span class="c1">).</span>

<span class="c1"></span>

<span class="c16">APONE</span><span> is not restricted to any specific domain of research. The domain of the experiments defined and run is actually given by the client linked to it, which determines the experience of the user. Nonetheless, the provided </span><span class="c16">ClientE</span><span class="c35"> </span><span class="c1">and most examples in this user guide are limited to the Information Retrieval domain.</span>

<span class="c1"></span>

<span>The purpose of this guide is to explain how to define, run and control an experiment. We assume that the experimenter has online access to </span><span class="c16">APONE</span><span class="c1">. It is also possible to download and install the platform following the installation instructions in github.</span>

<span class="c1"></span>

<span class="c1"></span>

<span>3. </span><span class="c23 c39">TECHNICAL DETAILS</span>
==============================================================

<span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 0.00px solid #000000; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 622.29px; height: 247.50px;">![](images/image2.jpg)</span>

<span class="c11">Figure 1. IREPlatform. Main Components and Technologies</span>

<span class="c1"></span>

<span>APONE</span><span class="c14"> 1.0.0</span>
-------------------------------------------------

<span class="c1"></span>

<span>RESTful Web Services developed in Java (</span><span class="c20"><a href="https://www.google.com/url?q=https://jersey.github.io/&amp;sa=D&amp;ust=1517523797498000&amp;usg=AFQjCNHhgUhnrBrgkUK_edRWccCaUI2X_w" class="c0">Jersey</a></span><span>) to be deployed in </span><span class="c20"><a href="https://www.google.com/url?q=http://tomcat.apache.org&amp;sa=D&amp;ust=1517523797498000&amp;usg=AFQjCNH2ceiqNQJK_bWkN5iUEXXUpavqXQ" class="c0">Tomcat</a></span><span> (at least 8.5). Requires connection to </span><span class="c20"><a href="https://www.google.com/url?q=https://www.rabbitmq.com/&amp;sa=D&amp;ust=1517523797499000&amp;usg=AFQjCNEF541FrloTBuKHQSilN5uqYF9BJA" class="c0">RabbitMQ</a></span><span> and </span><span class="c20"><a href="https://www.google.com/url?q=https://www.mongodb.com/&amp;sa=D&amp;ust=1517523797499000&amp;usg=AFQjCNGJmdGEjTJvWIoFvTCddI5c_DFtGA" class="c0">MongoDB</a></span><span> services. It uses </span><span class="c20"><a href="https://www.google.com/url?q=http://twitter4j.org/&amp;sa=D&amp;ust=1517523797499000&amp;usg=AFQjCNEc4tTeDsrJcOnGodWZzYmilQYtjw" class="c0">Twitter4j</a></span><span> as a library to delegate Twitter to grant access to the users via Oauth, and </span><span class="c20"><a href="https://www.google.com/url?q=https://github.com/Glassdoor/planout4j&amp;sa=D&amp;ust=1517523797499000&amp;usg=AFQjCNFwhTcxR8TviqobtaIbX9eGUx-rag" class="c0">PlanOut4j</a></span><span class="c1"> as a library to randomize the assignment of experimental units to variants. </span>

<span class="c1"></span>

<span>RabbitMQ is a message broker in charge of handling and saving in MongoDB the events sent by the client. This way, the client does not have to wait for </span><span>the platform</span><span class="c1"> to take care of the events. At the same time, the message broker keeps control of the number and type of events registered, which is useful for monitoring the experiments.</span>

<span class="c1"></span>

<span>MongoDB is a NoSQL database that stores the definition of the experiments, their configuration and status, and the related events sent by the client and handled by RabbitMQ. The events are saved as JSON objects, with predefined properties (see section 6.4). Depending on a property that identifies the data type of the information to be saved, it will be saved as a string, binary or JSON object. This is where the flexibility of MongoDB comes into play. Nonetheless, it is possible to change the database used by implementing the interface </span><span class="c16">tudelft.dds.irep.data.database.Database</span><span> in </span><span>the platform</span><span class="c1">.</span>

<span class="c1"></span>

<span>The </span><span class="c20"><a href="https://www.google.com/url?q=https://facebook.github.io/planout/&amp;sa=D&amp;ust=1517523797500000&amp;usg=AFQjCNGlY0faHm40bg0j5gXOGC6bLx-Mrw" class="c0">PlanOut library</a></span><span> (or its Java port </span><span class="c15">PlanOut4j</span><span>) offers an open source framework to define </span><span>complex</span><span> experiments, guaranteeing the randomization of the variant assignment that controls the user experience</span><span>. It uses a </span><span>domain-specific language</span><span> (see section 8.2) to define parameters whose values will change per variant. Its use is optional when we create an experiment in </span><span>the platform</span><span>. The library and the language are</span><span> well documented, with user guides and scientific papers (see section 9), and different tools that support it (eg. online editor </span><span class="c20"><a href="https://www.google.com/url?q=http://planout-editor.herokuapp.com/&amp;sa=D&amp;ust=1517523797501000&amp;usg=AFQjCNHi2r61AXQWraRvBkoOW1M_Xva8wg" class="c0">http://planout-editor.herokuapp.com/</a></span><span> </span><span class="c1">). Therefore, the use of this library makes it easier to reproduce the experiments.</span>

### <span class="c33">Web GUI</span>

<span>Web GUI included in </span><span class="c16">APONE</span><span> project</span><span>. It uses </span><span class="c20"><a href="https://www.google.com/url?q=https://docs.oracle.com/javaee/5/tutorial/doc/bnagy.html&amp;sa=D&amp;ust=1517523797502000&amp;usg=AFQjCNFCV_C-MGcSBk5J3UjLae6fq1nRyA" class="c0">JSP</a></span><span>, and requires </span><span class="c20"><a href="https://www.google.com/url?q=https://semantic-ui.com/&amp;sa=D&amp;ust=1517523797502000&amp;usg=AFQjCNHsvz3bqbPPZU4Of4Lj7n9oJWTanQ" class="c0">Semantic-UI</a></span><span> and </span><span class="c20"><a href="https://www.google.com/url?q=https://jquery.com/&amp;sa=D&amp;ust=1517523797502000&amp;usg=AFQjCNFtfyqqBBv-3rqkYxYP4W44U-z8Ug" class="c0">JQuery</a></span><span class="c1">. It supports the definition, visualization and monitoring of the experiments, as well as the visualization of the events and users of the platform.</span>

<span class="c1"></span>

<span class="c1">Semantic-UI is a framework to ease the development of web interfaces. For this project, only CSS and JavaScript libraries on the client-side have been used.</span>

<span>Client</span><span class="c14">E 1.0.0</span>
---------------------------------------------------

<span>The experimenter can follow two main approaches to implement a client. The simplest way would be to indicate a different URL for each variant when defining the experiment, so the user is redirected from the platform to the appropriate URL. In this scenario, the experimenter has to provide the client hosted at that URL </span><span>(see Figure 2)</span><span class="c1">.</span>

<span class="c1"></span>

<span style="overflow: hidden; display: inline-block; margin: -0.00px -0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 600.00px; height: 216.00px;">![](images/image6.png)</span>

<span class="c11">Figure 2: the users access the experimental platform from the browser, and it redirects them to the different variants (same user is always redirected to the same variant).</span>

<span class="c1"></span>

<span>Another approach would be to develop the client, which asks to the platform if a user should receive control or treatment. In this case, the user would access directly the website developed by the experimenter, which is responsible of loading or redirecting to the appropriate interface </span><span>(see figure 3)</span><span class="c1">.</span>

<span class="c1"></span>

<span style="overflow: hidden; display: inline-block; margin: -0.00px -0.00px; border: 1.33px solid #b7b7b7; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 527.88px; height: 130.50px;">![](images/image3.jpg)</span>

<span class="c17">Figure 3: the users access a specific web service developed by the experimenter, and this service is responsible of asking the platform what variant should be displayed to the user, and load/redirect it.</span><span class="c1"> </span>

<span class="c1"></span>

<span>In both cases, control and treatments may have associated pairs parameter-values by using PlanOut scripts (see section </span><span class="c20"><a href="#h.fc7qihyz0t71" class="c0">PlanOut</a></span><span class="c1">). You can read more about this second approach as well as about the use of PlanOut in section 8 (Advanced experiments).</span>

<span class="c1"></span>

<span>For demonstration purposes, in section 4 we will show how to implement two different experiments using the first approach with </span><span class="c16">ClientE </span><span>(figure 2). We will use </span><span class="c20"><a href="https://www.google.com/url?q=https://www.elastic.co/&amp;sa=D&amp;ust=1517523797504000&amp;usg=AFQjCNEFER-q8jACH9fuUljChApDhEM9dQ" class="c0">ElasticSearch</a></span><span> as search engine, and we will use the Cranfield documents to set-up those experiments: one which tests changes in the interface (different color in the links of the search results), and another one which tests changes in the ranking algorithm (default ElasticSearch ranking algorithm versus BM25). This client, in HTML and Javascript, will make use of </span><span>the platform</span><span class="c1"> endpoints to register the events, so the experimenter can analyze the results later and decide the best variant according to the behaviour of the users exposed to them.</span>

<span class="c1"></span>

<span>ElasticSearch is a distributed and open source Search Engine based on </span><span class="c20"><a href="https://www.google.com/url?q=https://lucene.apache.org/&amp;sa=D&amp;ust=1517523797505000&amp;usg=AFQjCNGsRdYdnCYEKVMjREexoFwxZTmtDg" class="c0">Apache Lucene</a></span><span>. It has been</span><span class="c1"> implemented in Java and provides a RESTful interface.</span>

<span class="c23 c39"></span>
=============================

------------------------------------------------------------------------

<span class="c23 c39"></span>
=============================

<span class="c23 c39">4. HOW TO CREATE AN EXPERIMENT</span>
===========================================================

<span>In this section, we will see how to create and run an experiment using </span><span class="c16">ClientE</span><span> as example, running locally in your computer. We will assume that APONE is running at </span><span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPlatform&amp;sa=D&amp;ust=1517523797506000&amp;usg=AFQjCNHSMCbvIa0YPgnjNeHtGgkBrqv1fQ" class="c0">http://ireplatform.ewi.tudelft.nl:8080/IREPlatform</a></span>

<span class="c1"></span>

<span class="c16">ClientE</span><span> serves as an example for two different experiments at the same time. One of them implies changes in the front-end (changes in the color of the links of the search results). We will call it </span><span class="c15">ColorExp</span><span>. The other one involves changes in the back-end (changes in the ranking algorithm used). We will call it </span><span class="c15">RankingExp</span><span class="c1">.</span>

<span class="c1"></span>

<span>In </span><span class="c15">ColorExp</span><span> we want to test if it would be better to change the color of the links of the search engine results from blue to green. In this case, we are going to assume that the </span><span class="c15">Click-through Rate (CTR)</span><span> over the first search results page is a good indicator of the success of the new approach (green color) with respect to the previous one (blue color)</span><sup><a href="#ftnt3" id="ftnt_ref3">[3]</a></sup><span class="c1">. We will consider as experimental unit a user, that is, each user will always receive the same variant (control:blue color, or treatment:green color). In order to evaluate the results of our experiment, we will work with one value per user: the mean CTR for all the queries made by that user during the experiment.</span>

<span class="c1"></span>

<span>In </span><span class="c15">RankingExp</span><span> we want to test if our new ranking algorithm (let’s suppose we are the proud inventors of the Okapi BM25 ranking algorithm, and we make it work with ElasticSearch) is better than the default algorithm implemented in ElasticSearch. In this case, we will assume that the </span><span class="c15">Reciprocal Rank (RR)</span><span> is a good indicator of the success of our BM25 with respect to the default ranking algorithm, and, as previously explained in </span><span class="c15">ColorExp</span><span>, our experimental units are the users. At the end, we will compute an average per user, obtaining the </span><span class="c15">Mean Reciprocal Rank (MRR)</span><span class="c1">: the mean of the RR over all the queries made by that user during the experiment.</span>

<span class="c1"></span>

<span class="c1">Note that, depending on the decisions you make at this point, the design will be affected: the Overall Evaluation Criteria you are going to use will determine the information (events) that need to be registered, as well as the number of different users we will need in order to get statistically significant results.</span>

<span class="c14">Step 1. Create and Start the Experiment in APONE</span>
-------------------------------------------------------------------------

<span>In order to use the services of </span><span>the platform, for example to</span><span> register events, the client needs to communicate with an experiment defined and running in </span><span class="c16">APONE</span><span>. We will see here how to create our own experiments </span><span class="c15">ColorExp</span><span> and </span><span class="c15">RankingExp</span><span class="c1">.</span>

<span class="c1"></span>

<span>First, you have to access to the platform (</span><span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPlatform&amp;sa=D&amp;ust=1517523797508000&amp;usg=AFQjCNG3HhshaHvv76uiiR623m27LopvUA" class="c0">http://ireplatform.ewi.tudelft.nl:8080/IREPlatform</a></span><span class="c1">) and log-in with a twitter account. Once you are authenticated by Twitter, you will be redirected to the platform (this may take a while).</span>

<span class="c1"></span>

<span>To create </span><span class="c15">ColorExp</span><span> we go to the interface </span><span class="c15">Experiment::Create New</span><span>, fill the information, and press the button </span><span class="c15">Add</span><span>. You will see that the </span><span class="c15">Experimenter</span><span class="c1"> field is already set with the user name in Twitter you used to access the platform. In figure 4 you can see an example of the information to fill in (go to section 4 to see an explanation of each of the fields of the form).</span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px -0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 663.73px; height: 270.50px;">![](images/image9.png)</span>

<span class="c11">Figure 4. Example of definition of ColorExp.</span>

<span class="c1"></span>

<span class="c1">The most important part here are the client URLs. We will add the following URLs:</span>

<span class="c1">Blue (control): </span>

<span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPClient-simple-/color.html?rankingAlg%3Ddefault%26linkColor%3Dblue&amp;sa=D&amp;ust=1517523797509000&amp;usg=AFQjCNHcs5AbuNmHCdpp_wiMJYe_NHTOSA" class="c0">http://localhost:8080/ClientE/WebContent/color.html?rankingAlg=default&amp;linkColor=blue</a></span>

<span class="c1"></span>

<span class="c1">Green (treatment):</span>

<span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPClient-simple-/color.html?rankingAlg%3Ddefault%26linkColor%3Dgreen&amp;sa=D&amp;ust=1517523797510000&amp;usg=AFQjCNF_uQdP3D_4y8PsVnpzndm6zmKdUA" class="c0">http://localhost:8080/ClientE/WebContent/color.html?rankingAlg=default&amp;linkColor=green</a></span>

<span class="c1"></span>

<span>Note that in both cases we are using the same URL, but with different values for the query parameters linkColor</span><sup><a href="#ftnt4" id="ftnt_ref4">[4]</a></sup><span class="c1">. This information will be used by the client to decide what to do (in this case, set the color of the search results to blue or green). </span>

<span class="c1"></span>

<span>To create </span><span class="c15">RankingExp</span><span class="c1"> we follow a similar  approach, but this time the client URLs are:</span>

<span class="c1">Default (control): </span>

<span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPClient-simple-/ranking.html?rankingAlg%3Ddefault%26linkColor%3Dblue&amp;sa=D&amp;ust=1517523797512000&amp;usg=AFQjCNFDkmw07tlbKDc20UZTkvq9ivFsgQ" class="c0">http://localhost:8080/ClientE/WebContent/ranking.html?rankingAlg=default&amp;linkColor=blue</a></span>

<span class="c1"></span>

<span class="c1">BM25 (treatment):</span>

<span class="c20"><a href="https://www.google.com/url?q=http://ireplatform.ewi.tudelft.nl:8080/IREPClient-simple-/ranking.html?rankingAlg%3Dbm25%26linkColor%3Dblue&amp;sa=D&amp;ust=1517523797513000&amp;usg=AFQjCNHll55WBX10wIwisv2wFzX9CXaYhg" class="c0">http://localhost:8080/ClientE/WebContent/ranking.html?rankingAlg=bm25&amp;linkColor=blue</a></span>

<span class="c1"></span>

<span>Note that this time the query parameter whose value changes is </span><span class="c15">rankingAlg,</span><span class="c1"> to indicate the client to use the default ranking algorithm in ElasticSearch, or to use BM25 instead. In figure 5, you can see an example of the information to fill in.</span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px -0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 663.24px; height: 275.50px;">![](images/image7.png)</span>

<span class="c17">Figure 5: Example of definition of </span><span class="c17 c15">RankingExp</span><span class="c17">.</span>

<span class="c1"></span>

<span>Once we have created both experiments, we have to start them. In order to do that, we select them from the right window in </span><span class="c15">Experiments::Manage</span><span>, and press the button Start, located at the bottom. The status should then change from </span><span class="c15">OFF</span><span> to </span><span class="c15">ON</span><span class="c1">. </span>

<span class="c14">Step 2. Install and Deploy the Client</span>
--------------------------------------------------------------

<span class="c1">At this point you will need to get the client ready in your local machine. In order to do that you will need to download and deploy it in a server. One of the lightest servers is SimpleHTTPServer, included in the python library, but you can use any other server. </span>

<span class="c1"></span>

<span>Download the client from </span><span class="c20"><a href="https://www.google.com/url?q=https://github.com/marrerom/IREPClient-simple-&amp;sa=D&amp;ust=1517523797515000&amp;usg=AFQjCNFJ--F-HfSh5I-uXyyZz_v0IMhR7Q" class="c0">https://github.com/marrerom/ClientE</a></span><span> and </span><span>update the two HTML files, </span><span class="c15">color.html</span><span> and </span><span class="c15">ranking.html</span><span>, located in the folder </span><span class="c15">WebContents</span><span>, </span><span>with the corresponding identifier of the experiment running in the platform. You can get that identifier from </span><span class="c15">Experiments::Manage</span><span>, it is the</span><span> number that appears right to each experiment in the list (you can also see the identifier at the top of the window displayed when clicking on the experiment). </span><span>Replace the parameter in the method </span><span class="c16">init</span><span> (triggered when loading the page) of the HTML files with the corresponding identifier of our experiments (the identifier of our ColorExp in the file </span><span class="c15">color.html</span><span>, and the identifier of our </span><span class="c15">RankingExp</span><span> in the file </span><span class="c15">ranking.html)</span><span class="c1">. </span>

<span class="c1"></span>

<span class="c1">Deploy the client. In case you use SimpleHTTPServer, go to the folder where the client is located and launch the following command from console:</span>

<span class="c8">python -m SimpleHTTPServer 8080</span>

<span>It deploys the contents of the folder where it is launched. Therefore, now you should see in </span><span class="c20"><a href="https://www.google.com/url?q=http://localhost:8080&amp;sa=D&amp;ust=1517523797516000&amp;usg=AFQjCNEubkHdjokk1i2NFvg9unz7_TkabQ" class="c0">http://localhost:8080</a></span><span> the project. </span><span>You can test it is working by accessing the web pages </span><span class="c15">color.html</span><span> or </span><span class="c15">ranking.html. </span><span>In both cases, you should see a search website (see figure 6). However the query functionality still does not work because we do not have yet the parameters needed (user, rankingAlg, and linkColor).</span>

<span> </span><span style="overflow: hidden; display: inline-block; margin: -0.00px -0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 600.94px; height: 188.00px;">![](images/image12.png)</span>

<span class="c11">Figure 6: main search web page. The user id is displayed at the top right corner just for demonstration purposes (the user is not supposed to see this!).</span>

<span class="c11"></span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 308.00px; height: 122.67px;">![](images/image1.png)</span><span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 292.83px; height: 123.00px;">![](images/image8.png)</span>

<span class="c17">Figure 7: search results interface (left), and contents of a specific document retrieved (right). The user can navigate the search results pages, click on the documents retrieved, and go back to the previous page with the button at the top left. </span>

<span class="c1"></span>

<span class="c14">Step 3. Test your Client</span>
-------------------------------------------------

<span>We can test if our client is working properly by simulating we are users of the experiment. That way, we can test if the platform is properly redirecting the users to the different variants, if the client receives the parameters and display the corresponding variant, and if the interactions are saved as events in the platform. In order to do so, go to </span><span class="c15">Experiments::Manage</span><span>, select your experiment (</span><span class="c15">RankingExp</span><span> or </span><span class="c15">ColorExp)</span><span>, and press the button </span><span class="c15">Test</span><span class="c1"> at the bottom. Every time you press that button, you will be automatically assigned a random identifier and redirected to one of the variants of the experiment, that is, one of the client URLs defined in the variants, which will appear as a pop-up window. </span>

<span class="c1"></span>

<span>In the URL received we will have not only the query parameters we defined (if any), but also two additional parameters: identifier of the user assigned randomly by the platform (</span><span class="c15">\_idunit</span><span>), and name of the variant assigned to that user (</span><span class="c15">\_variant).</span><span> All that information is properly encoded</span><sup><a href="#ftnt5" id="ftnt_ref5">[5]</a></sup><span>.</span><span class="c1"> </span>

<span class="c1"></span>

<span class="c1">Now you can interact with the client as a user, issuing queries and checking results (see figures 6 and 7). You can test that the search engine works (the ElasticSearch service is located in ireplatform.ewi.tudelft.nl:9200, and it is open to make queries over the cran collection).</span>

<span class="c1"></span>

<span>During this interaction, there should be events registered in the platform. In the case of </span><span class="c16">ClientE</span><span>, the events registered are </span><span class="c15">search, click </span><span>and </span><span class="c15">page</span><span class="c1">, corresponding to the user actions of searching, clicking on a document retrieved, or going to the next (or any other) page of results (see figure 8).</span>

### <span style="overflow: hidden; display: inline-block; margin: -0.00px 0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 314.00px; height: 249.17px;">![](images/image11.png)</span>

<span class="c17">Figure 8: Events registered by </span><span class="c16 c17">ClientE</span><span class="c17">. The content of these events is saved in JSON format.</span>

<span class="c1"></span>

<span>You should see those events in the right window in </span><span class="c15">Events::Manage</span><span>, together with the event </span><span class="c15">exposure</span><span class="c1">, which is registered every time the client page is loaded. Click on the events to check the information they contain, or filter, select and download those of interest for you in CSV or JSON format by using the buttons at the bottom.</span>

<span class="c1"> </span>

<span>You can also check the status of the experiment in </span><span class="c15">Monitoring::Experiments</span><span>. There you should see the experiments running, the number of exposures and users who have completed the experiments they have been exposed to (we will talk about this in the next section). Clicking on those numbers, you will see the information about the number of users per variant in each case</span><sup><a href="#ftnt6" id="ftnt_ref6">[6]</a></sup><span class="c1">.</span>

<span class="c14">Step 4. System in Production</span>
-----------------------------------------------------

<span class="c1">When you develop your own client, after making sure everything works as expected, you will need to host it in a public domain, so any user can access it. Create a new experiment in the platform with the proper URLs, and remember to update the new experiment identifier in your client.</span>

<span class="c1"></span>

<span>Once the client is up and running in the new host, you can turn on the experiment in the platform. The users of the platform will be able to participate in the running experiments created by other users by pressing the button </span><span class="c15">Participate in random experiment</span><sup><a href="#cmnt1" id="cmnt_ref1">[a]</a></sup><span class="c15"> </span><span>from the menu </span><span class="c15">Monitoring::Users</span><span class="c1">. There you can also see how many experiments you have created, in how many experiments you have participated (and completed the experiment) and the remaining number of experiments you can participate in.</span>

<span class="c1"></span>

<span>Maybe it is a good idea in general to test first the experiment with real users for a short period of time. To this end, you can create the experiment (maybe with more users assigned to control if it is safer), and check the events are properly registered. Afterwards, selecting the experiment from </span><span class="c15">Experiment::Manage, </span><span>you can create a new experiment with the button </span><span class="c15">New conf.</span><span> which will create an experiment with exactly the same metadata (except for the identifier) and variants, but you can assign a different distribution of users among variants. Now you can stop or remove the previous one, and start the new one. Again, remember to update the experiment identifier in your client.</span>

------------------------------------------------------------------------

<span class="c23 c39">5. HOW TO REGISTER EVENTS</span>
======================================================

<span class="c1">The client is responsible of sending to the platform the information related to the user interaction, so the experimenter can analyze what variant is better after the experiment has finished. In order to do that, it has to call to the following endpoint:</span>

<span class="c1"></span>

<span class="c16">POST /IREPlatform/service/event/register</span><sup><a href="#ftnt7" id="ftnt_ref7">[7]</a></sup>

<span class="c8"></span>

<span>which consumes a JSON with the information of the event we want to save (see Table 1).</span>

<span class="c1"></span>

<a href="" id="t.f4384f47af2b59f6a5c664fcf930d37adc4ec76c"></a><a href="" id="t.0"></a>

<table>
<colgroup>
<col width="33%" />
<col width="33%" />
<col width="33%" />
</colgroup>
<tbody>
<tr class="odd">
<td align="left"><p><span class="c23 c17 c45 c7">name</span></p></td>
<td align="left"><p><span class="c23 c17 c45 c7">type</span></p></td>
<td align="left"><p><span class="c23 c17 c45 c7">comment</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">idconfig</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Id of the experiment</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11">idunit</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Id of the experimental unit (eg. user)</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">ename</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Name of the event (e.g. ‘click’). Reserved names are ‘exposures’ and ‘completed’.</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11">evalue</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Information we want to save</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">etype </span></p></td>
<td align="left"><p><span class="c11">Enum: [“BINARY”, “STRING”, “JSON”]</span></p></td>
<td align="left"><p><span class="c17">Data type of the information contained in evalue. It will determine the type of data used to save the contents in the database. If the contents are binary, they should be previously encoded in Base64</span><sup><a href="#ftnt8" id="ftnt_ref8">[8]</a></sup><span class="c11">.</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11">paramvalues</span></p></td>
<td align="left"><p><span class="c11">object</span></p></td>
<td align="left"><p><span class="c11">If you use PlanOut (see section 8.2), parameter values received (the platform can not calculate them as some may have been overwritten). If not, empty object: {}</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">timestamp</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Timestamp of the event (ISO 8601 format). This is optional, if it does not exist, then the platform will assign the current timestamp when it starts processing the petition.</span></p></td>
</tr>
</tbody>
</table>

<span class="c11">Table 1. JSON consumed by the endpoint to register an event.</span>

<span class="c11"></span>

<span class="c1">When we register an event, the variant name assigned to the user will also be saved automatically, together with the information about the user agent, the owner of the experiment, and a unique identifier of the event.</span>

<span class="c1"></span>

<span>In the specific case of </span><span class="c16">ClientE</span><span>, it includes a general Javascript function called </span><span class="c16">registerEvent</span><span>, which makes an AJAX call to the servlet Register. That servlet then calls </span><span class="c16">the platform </span><span>and send the information of the event to be registered</span><span class="c1">. In this case we will register three types of events:</span>

-   <span>Search: we save the query made by the user, the time it took and the total documents retrieved in JSON. Function </span><span class="c16">registerSearch</span><span>.</span>
-   <span>Page View: we save the page viewed by the user (only the first 10 pages, each one with 10 results, are retrieved), the query made, and the total number of documents retrieved in JSON. Function </span><span class="c16">registerPageView</span><span class="c1">.</span>
-   <span>Document View: we save the query made, the document the user clicked on, and the rank of that document with respect to the total number of documents retrieved in JSON. Function </span><span class="c16">registerDocView</span><span class="c1">.</span>

<span>There are two special type of events you can send and check, called </span><span class="c15">exposure </span><span>and </span><span class="c15">completed. </span><span>The first indicates that the user has been exposed to the experiment, while the second indicates that the user has already finished that experiment. It is up to the client to send or not these events, and the moment they should be sent</span><sup><a href="#ftnt9" id="ftnt_ref9">[9]</a></sup><span>. In </span><span class="c16">ClientE</span><span>, only the event </span><span class="c15">exposure </span><span>is sent when loading the page (function </span><span class="c16">init</span><span>). The function to register and check the event </span><span class="c15">complete</span><span> are implemented but not used (functions </span><span class="c16">registerCompleted</span><span>, </span><span class="c16">checkIfCompleted</span><span>). The platform will use these events to monitor the experiment, and in the case of </span><span class="c15">complete</span><span> events, to stop sending that user to the same experiment (from </span><span class="c15">Monitoring::Users </span><span>-&gt;</span><span class="c15"> Participate in random experiment).</span><span> The number of completed experiments, together with the number of exposures can be monitored in </span><span class="c15">Monitoring::Experiments</span><span class="c1">.</span>

<span>To check if a user has already completed the experiment from the client (i.e. exists at least one event </span><span class="c15">completed </span><span class="c1">for that user/experiment), we can make a call to the following endpoint:</span>

<span class="c16">GET /IREPlatform/service/user/checkCompletedExp/{id experiment}/{id user}</span>

<span>the response will be a string ‘true’ or ‘false’ that can be used by the client to decide what to do (for example, avoid registering new events from a user who has already completed the experiment).</span>

------------------------------------------------------------------------

<span class="c23 c39"></span>
=============================

<span class="c23 c39">6. WEB INTERFACE USER REFERENCE</span>
============================================================

<span class="c14">6.1 Experiment::Manage</span>
-----------------------------------------------

<span>The manage experiments</span><span> interface shows the existing experiments in the database,</span><span class="c1"> be them running or not. For each experiment the following information is displayed:</span>

<span class="c23 c16 c17">\[experiment name\]:\[configuration name\]@\[experimenter\] - Status: \[ON/OFF\] - \[Identifier\]</span>

<span class="c23 c16 c17">\[Description of the experiment\]</span>

<span>If we click on it, a window with the full information about the experiment will pop-up. The unique identification of the experiment will be at the top. This identifier will be used when calling the </span><span>platform’s</span><span class="c16"> </span><span>endpoints to ask for services like getting parameters or registering events while that experiment is running</span><span>.</span><span class="c1"> </span>

<span class="c1"></span>

<span>We can control the experiments that are displayed by applying filters. In order to do so, we fill partially or completely the form at left and then click on the ‘Filter’ button. The experiments that match ALL the stated conditions in the filter will be displayed in the right panel. In the case of the </span><span class="c15">Experiment.Description, Variants.ClientURL, Variants.Description, Variants.Definition and Configuration.Client</span><span>, the search is made by using the input as a regular expression (Perl Compatible Regular Expression). This is noted with the word </span><span class="c15">regex</span><span class="c1"> in the alt-text that appears when we place the mouse on them. For the other fields, the match is exact, even for the dates (date started, ended, and date to end), which match the exact day (independently of the time).</span>

<span class="c1"></span>

<span>Several of the experiments displayed can be selected to be tested, removed, started, or stopped (see subsection </span><span class="c15">Start/Stop an experiment</span><span>) with the corresponding buttons below. We can also show the events saved for a specific experiment with the button </span><span class="c15">Show Events</span><span>, or create a new experiment by replicating the metadata and variants of the selected experiment with the button </span><span class="c15">New Configuration</span><span>. In the first case, the interface </span><span class="c15">Events::Manage</span><span> will be displayed. In the second case, the interface </span><span class="c15">Experiment</span><span class="c15">s::Create New </span><span>will be displayed, with the metadata and variants of the experiment already filled and disabled (to enable the form, click button </span><span class="c15">Clear</span><span class="c1">). </span>

### <span class="c33">Start/Stop an experiment </span>

<span>An experiment can be started or stopped by selecting it in the interface </span><span class="c15">Experiments::Manage</span><span> and clicking the button </span><span class="c15">Start</span><span> or </span><span class="c15">Stop</span><span>. If the experiment is already running or has been previously run, we can also start or stop it in the </span><span class="c15">Monitoring::Experiments </span><span>interface by sliding the button in the </span><span class="c15">Status</span><span class="c1">.</span>

<span class="c1"></span>

<span>Note that the same experiment can be started/stopped as many times as we want. Only while the experiment is running, it is possible for </span><span>the platform</span><span> to redirect to the predefined client URLs, get parameter values, or register events. Every time we start or stop an experiment, the date and time is saved. This information is displayed in the fields </span><span class="c15">Started</span><span> and </span><span class="c15">Ended</span><span> when we click on an experiment to display its information in the interface </span><span class="c15">Experiment::Manage</span><span class="c1">. Those dates should be taken into account when analyzing the data.</span>

<span>6.2 </span><span class="c14">Experiment::Create New</span>
----------------------------------------------------------------

<span class="c1">A new experiment has three areas of information to be filled: metadata about the experiment, definition of the variants that will be displayed to the user, and configuration to determine how it will be run (percentage of users per variant, and conditions to finish).</span>

<span class="c1"></span>

<span class="c7">Metadata</span><span class="c1">:</span>

-   <span class="c1">Experimenter: name or any other identification of the person in charge of the experiment. This information will appear already pre-filled with the name of the Twitter user authenticated to access to the platform.</span>
-   <span>Name: name or any other identification of the experiment. Useful to look it up or identify it quickly when searching or monitoring our experiments.</span><span> This is not a unique identifier of an experiment, it is just to make it easier the search and visualiz</span><span>ation of</span><span class="c1"> a specific experiment.</span>
-   <span class="c1">Description: a description of the experiment. It should be a brief summary of objectives and methodology, that is, what we want to prove with the experiment, and how we are going to do it.</span>

<span class="c1"></span>

<span class="c7">Variants</span><span>: </span>

-   <span>Name: name to identify the specific variant of the experiment (eg. in </span><span class="c15">ColorExp</span><span>, </span><span class="c15">Blue</span><span> and </span><span class="c15">Green</span><span class="c1">).</span>
-   <span class="c1">Description: brief description of the approach the variant represents. </span>
-   <span>Client URL: URL of the specific variant. It is used to redirect the user to the assigned variant. You can use query parameters, as is the case of </span><span class="c16">ClientE</span><span class="c1">. </span>
-   <span>Is Control: checked if the variant is the control. We can only have one control per experiment, but it is possible to define multiple treatments. </span><span>The idea behind is that we can compare the results obtained from the treatments with something that we </span><span class="c15">already </span><span>know. For example, if we test directly two new approaches for our ranking algorithm, we may see which one is better, but they both might actually be bad approaches.</span><span class="c1"> In offline evaluation, we assume that the relevance judgements are the truth. In online evaluation we need to know the impact of one of the variants in order to give some validity to the results obtained from the others.</span>

<span class="c1"></span>

<span class="c7">Configuration</span><span class="c1">:</span>

-   <span>Name: used to identify the specific configuration. Note that we can create a new experiment with the same metadata and variants, but different configuration. For example, we can run the same experiment at first with just 10% of the users accessing the treatments instead of the control for testing purposes. Then, we can create a new experiment with the same metadata and variants but with a different distribution, this time 50% for treatment and 50% for control. See in section </span><span class="c15">Experiment::</span><span class="c15">Manage </span><span class="c1">how to create easily a new experiment from an existing one, replicating metadata and variants.</span>
-   <span class="c1">Date to end: condition to finish the experiment. Once reached, the experiment will stop automatically, that is, the client we are using will no longer be redirected from the platform, able to get the parameters, or register events for that experiment.</span>
-   <span>Maximum completed units</span><sup><a href="#ftnt10" id="ftnt_ref10">[10]</a></sup><span>: maximum number of different experimental units (eg. users) to complete the experiment (note that in order to consider an experiment </span><span class="c15">completed, </span><span>the client must send at least one event of this type (see section 5)</span><span class="c1">. It is a condition to finish the experiment. Once reached, the experiment will stop automatically. </span>
-   <span>Percentage of units per variant: percentage of units accessing each one of the variants defined in the experiment. At this moment, the percentage </span><span class="c7">has to be an integer</span><span> between 0 and 100. If the sum of the percentages are less than 100, the rest </span><span>will be automatically assigned to the control variant</span><span class="c1">.</span>

<span class="c1"></span>

<span>6.3 Experiment::Create New</span><span class="c14"> (Advanced)</span>
---------------------------------------------------------------------------

<span class="c1">We can define an experiment in advanced mode, which additionally will let us include PlanOut scripts in the variants, and exclude the definition of client URLs (see section 8). Each variant may have the following additional information: </span>

-   <span>Unit: variable we are going to use to identify the experimental unit in the PlanOut script. </span><span class="c1">It is necessary when we use random operators in the script (see section 8.2). For example, in the following definition we use the variable ‘userid’ to identify each user: </span>

<span class="c16 c17">rankingAlgorithm = uniformChoice(choices=rankings, unit=</span><span class="c17 c7 c65">userid</span><span class="c23 c16 c17">);</span>

<span class="c1">Note that this is just the variable used in the script, not its actual value (which we don’t know and will be different for each user).</span>

-   <span>Definition: definition of the parameter-values in </span><span>PlanOut language</span><span>. There is a link to an external application (</span><span class="c20"><a href="https://www.google.com/url?q=https://planout-editor.herokuapp.com/&amp;sa=D&amp;ust=1517523797538000&amp;usg=AFQjCNG7qBD45xAbBtrXFBh2K1yFQFbqug" class="c0">PlanOut Experimental Editor</a></span><span class="c1">) which can be used to validate and test the script before creating the experiment, or to check why there was an error when we try to create it. </span>

<span class="c1"></span>

<span class="c1">In this mode, we may also include information about the client in the Configuration section:</span>

-   <span>Client</span><span>: information about the specific client used. Could be the URL and version to identify the code in a software repository, could also include the URL where it is running, etc. From a research perspective, it is important to identify uniquely the specific version of the client used in an experiment.</span><span> For example, if we wanted to run an experiment with </span><span class="c16">ClientE</span><span class="c1">, we should note that here (link to repository in github and version).</span>

<span class="c1"></span>

<span>6.4 </span><span>Events</span><span class="c14">::Manage</span>
---------------------------------------------------------------------

<span>When an experiment is running, we can register events from our client through calls to </span><span>the platform</span><span class="c1">. Each event contains information about the experiment, and about the specific information we want to save about the interaction of the user.</span>

<span class="c1"></span>

<span class="c23 c45 c7 c37">Experiment:</span>

-   <span class="c1">Experiment ID: unique identifier of the experiment</span>
-   <span class="c1">Experimenter: owner of the experiment</span>
-   <span class="c1">Variant: name of the variant the user is being exposed to.</span>
-   <span class="c1">Parameters and values: name and values of the PlanOut parameters for that specific user (if any).</span>

<span class="c1"></span>

<span class="c7">Interaction:</span><span class="c1"> </span>

-   <span class="c1">Unit id: identification of the experimental unit (typically a user).</span>

<!-- -->

-   <span class="c1">Timestamp: timestamp of the event.</span>
-   <span>Name: user-defined name assigned to the event we want to register (eg. click, page-view, etc.). The names </span><span class="c15">exposure </span><span>and </span><span class="c15">completed </span><span>can be used, but only to indicate that there is a new exposure, or to register that the user has completed the experiment</span><span class="c1">. </span>
-   <span class="c1">Value: the specific information we want to save about the interaction with the user (eg. number and positions of clicks, issued queries, search results obtained, etc.).</span>
-   <span>Data type: data type of the specific information (value) we want to save: binary, JSON or string format. Note that the </span><span class="c15">exposure </span><span>and </span><span class="c15">completed </span><span class="c1">events do not have any value, but still the data type, when they are automatically registered, is string.</span>
-   <span class="c1">User-agent: user agent information used by the user in the interaction with the client.</span>

<span class="c1"></span>

<span class="c1">The interface shows the existing events in the database. For each event the following information is displayed:</span>

<span class="c23 c16 c17"> \[experiment\]:\[event name\]@\[experimenter\] - \[binary|string|JSON\]</span>

<span class="c16 c17 c23">\[Value -first 100 characters, and only if not binary-\]</span>

<span class="c1">If we click on it, a window with the full information about the event will pop-up. The unique identification of the event will be displayed at the top. </span>

<span class="c1"></span>

<span>We can control the events that are displayed by applying filters. In order to do so, we have to fill partially or completely the form at left and then click on the </span><span class="c15">Filter</span><span> button. The events that match ALL the stated conditions in the filter will be displayed in the right panel. In the case of user-agent, </span><span>the search is made by using the input as a regular expression </span><span>(Perl Compatible Regular Expression). In the case of the field value, it depends. If the type of event selected is string or any, then the input is used as a regular expression (but it won’t work for events with type JSON). However,</span><span> if the type selected is JSON, then the input will be considered as a JSON document in the search</span><span class="c7"> (note that the input in this case has to be a valid JSON document!)</span><span class="c1">. For the other fields, the match is exact, even for the timestamp, which match the exact day (independently of the time).</span>

<span class="c1"></span>

<span class="c1">Finally, the events displayed can be selected to be removed or downloaded as CSV or JSON with the buttons at the bottom of the right panel. </span>

<span>6.5 </span><span class="c14">Monitoring::Experiments</span>
-----------------------------------------------------------------

<span>There are two special types of events called </span><span class="c15">exposure </span><span>and </span><span class="c15">completed</span><span>. They are optionally sent by the client, however</span><span> both events are vital to monitor the experiment, so we can see the number of </span><span class="c15">different </span><span>users exposed to the experiment so far, and how many of them completed the experiment</span><span>.</span>

<span class="c1"></span>

<span>We can see the experiments running in the top panel, while the experiments that already finished are displayed in the bottom panel. Each row shows information about an experiment. The </span><span class="c15">unit</span><span> column shows the number of different users exposed to the experiment, and also the number of users who have completed the experiment. If we click on these numbers, a new window will pop-up displaying a more detailed information about the exposures or the completion of the experiments: you will see the total number per variant, and also the total number per parameter-values assigned in case we used PlanOut scripts</span><sup><a href="#ftnt11" id="ftnt_ref11">[11]</a></sup><span> (</span><span>see Figures 10 and 11</span><span class="c1">).</span>

<span class="c1"></span>

<span style="overflow: hidden; display: inline-block; margin: -0.00px 0.00px; border: 1.33px solid #999999; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 472.50px; height: 211.64px;">![](images/image5.png)</span>

<span class="c17">Figure 10. Detailed information about exposures in our ranking experiment with no PlanOut script associated (no pairs of parameter-values). In Monitoring::Experiments</span>

<span class="c1"></span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px 0.00px; border: 1.33px solid #666666; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 463.50px; height: 257.65px;">![](images/image4.png)</span>

<span class="c17">Figure 11. Detailed information about exposures in an experiment with a PlanOut script associated. Note that, depending on the definition of the script, we may have different values for the same parameter in the same variant. In Monitoring::Experiments</span>

<span class="c1">  </span>

<span>From the dashboard it is also possible to start/stop the experiments by sliding the button in </span><span class="c15">Status</span><span class="c1">.</span>

<span class="c14">6.6 Monitoring::Users</span>
----------------------------------------------

<span>In this interface we can see information about the current user (all the users of the platform if we are administrators). For each user, we will see the experiments owned by the user, the experiments the user has completed</span><sup><a href="#ftnt12" id="ftnt_ref12">[12]</a></sup><span class="c1">, and the current number of remaining running experiments the user can still participate in. </span>

<span class="c1"></span>

<span>At the bottom of the window we can see the button </span><span class="c15">Participate in random experiment.</span><span> If we press that button, we will be redirected to one of the experiments to participate as a user.</span>

------------------------------------------------------------------------

<span class="c14"></span>
-------------------------

<span class="c23 c39">8. ADVANCED EXPERIMENTS</span>
====================================================

<span>8.1</span><span class="c14"> MORE CONTROL IN OUR EXPERIMENT</span>
------------------------------------------------------------------------

<span>The use of the platform to redirect the users automatically to the experiments currently running from </span><span class="c15">Monitoring::Users </span><span class="c1">is useful, but imposes some limits:</span>

<span class="c1"></span>

-   <span class="c1">The users has to authenticate themselves first in the platform in order to participate in the experiments, and, at the moment at least, they can not decide what experiment they want to participate in. On the other hand, the experimenter does not have any control in the users who can or can not participate in the experiment.</span>

<span class="c1"></span>

-   <span class="c1">The experimental unit has to be the user: the assignment of the variants is done per user identifier, that is, the user name authenticated in the platform.</span>

<span class="c1"></span>

-   <span class="c1">When using the platform to redirect users, the information about the variant assigned to that specific user (as well as the user identification) is sent in the URL. Although it is encoded, it is not encrypted: what happens if the user modify this information? What happens if the user access again this URL directly from the browser with new parameters?</span>

<span class="c1"></span>

<span>In order to avoid all these situations, we provide a specific endpoint (see below) to get the information related to the assigned variant, given an experimental unit identifier. This way, in the definition of the experiment we can leave empty the fields </span><span class="c15">client URL</span><span> (although we should fill this information in the field </span><span class="c15">Controller</span><span class="c1"> at least for information purposes). Then, from our client, we assign a user identifier and then we call the endpoint to get the variant (and parameters) assigned. This way, the users can access directly this URL to participate in the experiment.</span>

<span class="c1"></span>

<span class="c1">The endpoints to get directly the information related to the variant assigned to a experimental unit are:</span>

<span class="c1"></span>

<span class="c16">GET /IREPlatform/service/experiment/getparams/{id experiment}</span>

<span class="c8"></span>

<span class="c8">GET /IREPlatform/service/experiment/getparams/{id experiment}/{id unit}</span>

<span class="c8"></span>

<span class="c1">As a result of these calls, we will receive a JSON object with the parameter-value pairs from the PlanOut script, if any (params), as well as three additional pieces of information: the identification of the user (\_idunit), the variant assigned (\_variant), and the URL assigned to that variant in the definition of the experiment, if any (\_url).</span>

<span class="c1"></span>

<span class="c1">We have an additional endpoint which consumes a JSON:</span>

<span class="c1"></span>

<span class="c8">POST /IREPlatform/service/experiment/getparams/</span>

<span class="c8"></span>

<span class="c1">With this endpoint it is possible to set values of existing parameters in the defined PlanOut scripts (see section 8.2), so they may condition the value of the rest of the parameters when running the script (see Table 2). </span>

<span class="c1"></span>

<a href="" id="t.7ecf52ee33dbf14a50751162c93b37e2730da86b"></a><a href="" id="t.1"></a>

<table>
<colgroup>
<col width="33%" />
<col width="33%" />
<col width="33%" />
</colgroup>
<tbody>
<tr class="odd">
<td align="left"><p><span class="c23 c17 c45 c7">name</span></p></td>
<td align="left"><p><span class="c23 c17 c45 c7">type</span></p></td>
<td align="left"><p><span class="c23 c17 c45 c7">comment</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">idconfig</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Id of the experiment</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11">idunit</span></p></td>
<td align="left"><p><span class="c11">string</span></p></td>
<td align="left"><p><span class="c11">Id of the experimental unit (eg. user)</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11">overrides</span></p></td>
<td align="left"><p><span class="c11">object</span></p></td>
<td align="left"><p><span class="c11">Parameter values we want to set before running the corresponding PlanOut script.</span></p></td>
</tr>
</tbody>
</table>

<span class="c17">Table</span><span class="c11"> 2. JSON consumed by the endpoint to get parameters.</span>

<span>For example, we can use a parameter named </span><span class="c15">browser</span><span> in the script, as explained in section 8.2. Depending on the browser the user is actually using, desktop or mobile, we can overwrite the value of this parameter </span><span class="c15">before </span><span>PlanOut calculates and sends back the values of the other parameters for that user.</span>

<span class="c8"></span>

<span>Finally, note that there are two similar endpoints, depending on the previous knowledge of the identifier of the unit or not. If we don’t know it, then the platform will assign one randomly (UUID version 1) </span><span class="c7">every time we call that endpoint</span><span class="c1">: the client has to make sure that the same physical unit (be it the user, a query, etc.) corresponds always to the same identifier.</span>

<span>8.2</span><span class="c14"> PLANOUT</span>
-------------------------------------------------

<span class="c1">In this section we will explain briefly what PlanOut is and how we can use it to define experiments. It is used by the platform to define experiments, however, the experimenter does not need to use it directly, as it is possible to define experiments just by indicating the URL’s of the variants instead of using PlanOut scripts.</span>

<span class="c1"></span>

<span class="c20"><a href="https://www.google.com/url?q=http://facebook.github.io/planout/docs/planout-language.html&amp;sa=D&amp;ust=1517523797551000&amp;usg=AFQjCNGhT3J2sq0tv1z4ztOqv1BDoEDm2A" class="c0">PlanOut domain-specific-language</a></span><span> lets us define univariate or multivariate experiments, by defining parameters</span><sup><a href="#ftnt13" id="ftnt_ref13">[13]</a></sup><span> and their possible values. The experiment is defined with a script that determines the parameters, values and operators used to assign values to parameters. For example, for our </span><span class="c15">ColorExp </span><span>we can define a script where our parameter is ‘linkColor’ and the assignment is made over two predefined values: ‘blue’ and ‘green’ with a probability of 0.5. This selection will depend on the </span><span class="c7">experimental unit</span><sup><a href="#ftnt14" id="ftnt_ref14">[14]</a></sup><span>, for example the user id (represented in the example below with the variable </span><span class="c15">userid</span><span>)</span><span class="c1">, so for the same user id, the PlanOut library will always assign the same color:</span>

<span class="c1"></span>

<span class="c8">possibleColors = \['blue', 'green'\];</span>

<span class="c16">linkColor = uniformChoice(choices=possibleColors, unit=</span><span class="c7 c65">userid</span><sup><a href="#ftnt15" id="ftnt_ref15">[15]</a></sup><span class="c16">);</span>

<span class="c1"></span>

<span>When we ask from our client to the platform for the value of these parameters, we send the unique identifier of the experimental unit, and then we will get the parameter values according to that identifier. </span><span>The client developed by the experimenter transforms the values of the parameters received in changes in the interaction with the user.</span><span> In </span><span>this</span><span class="c1"> example, it is expected that for some users, when they issue queries, the links of the results will be blue, while other users will see them green.</span>

<span class="c1"></span>

<span class="c1">This way, we are separating experimental design from application logic, with a twofold objective: </span>

1.  <span>It is easier for the experimenter to set-up an experiment because the PlanOut library takes care of the randomization in the assignment of units to parameter values. The experimenter just has to ask from her code what is the </span><span class="c15">linkColor </span><span class="c1">assigned to a specific user id, and implement the corresponding changes in the interface (or back-end) that the user is going to interact with. That way we could have a general client which, according to the values of the parameters defined in the experiment, makes the corresponding changes instead of having multiple codes, one for each variant we want to test.</span>
2.  <span class="c1">PlanOut scripts may be used as a concise summary of an experiment’s design and manipulations, which makes it easier to communicate about, and replicate experiments. </span>

<span class="c1"></span>

<span>An important additional benefit of using PlanOut is the possibility of running full factorial experiments easily. For example, instead of using just the link color in the previous example, we may want to change also the ranking algorithm as in </span><span class="c15">RankingExp</span><span class="c1">:</span>

<span class="c1"> </span>

<span class="c8">possibleRankings = \[‘default’,‘BM25’\];</span>

<span class="c8">possibleColors = \['blue’, 'green'\];</span>

<span class="c8">rankingAlgorithm = uniformChoice(choices=possibleRankings, unit=userid);</span>

<span class="c16">linkColor = uniformChoice(choices=possibleColors, unit=userid);</span>

<span class="c1"></span>

<span>As a result, we would get all the different combinations of parameters </span><span class="c15">rankingAlgorithm</span><span> and </span><span class="c15">linkColor, </span><span>that is,</span><span class="c15"> </span><span>we will have users exposed to 2</span><span class="c63">2</span><span class="c1"> different variants (two factors or independent variables, two levels each).</span>

<span class="c1"></span>

<span>It is also possible to use conditional statements, as well as </span><span class="c20"><a href="https://www.google.com/url?q=https://facebook.github.io/planout/docs/random-operators.html&amp;sa=D&amp;ust=1517523797554000&amp;usg=AFQjCNH0VYf4cHJ4aGkt42dNYcdsLRsWrA" class="c0">other random assignment operators</a></span><span class="c1"> that let us define complex experiments. The use of conditional statements is useful to assign parameters that depend on the assignment of previous parameters, but it is also possible to overwrite others that depend on the user, so we could end up writing something like this:</span>

<span class="c1"></span>

<span class="c8">colorsMobile = \['blue’, 'green'\];</span>

<span class="c8">if (browser == ‘mobile’) { </span>

<span class="c8">linkColor = uniformChoice(choices=colorsMobile,unit=userid);</span>

<span class="c8">} else {</span>

<span class="c8">linkColor = ‘blue’;  \#no treatment</span>

<span class="c8">}</span>

<span class="c1"></span>

<span>Here, the variable </span><span class="c15">browser</span><span> can be overwritten with the actual browser used by a user every time we ask for the value of the parameters for him. However, bear in mind that in this case the assignment of the variables is not random (</span><span class="c15">browser </span><span>is a </span><span class="c15">quasi-independent</span><span class="c1"> variable), and the conclusions of the experiment may be compromised. In any case, if we still want to filter by mobile browsers, desktop users shouldn’t be part of the experiment, even if they receive only the control (they are not really part of the control population!). The client should previously filter those users and avoid them to be part of the experiment.</span>

### <span class="c33">PlanOut in APONE</span>

<span>To define an experiment making use of PlanOut you have to go to </span><span class="c15">Experiment::Create new (Adv.)</span><span>, and fill in the box called </span><span class="c15">Definition</span><span> with the PlanOut script associated to each variant. You can make use of the PlanOut editor located in the link </span><span class="c15">Planout DSL</span><span class="c1"> to make sure the script is correct before creating the experiment.</span>

<span>The values of the parameters defined will be sent in the client URL (if defined) as query parameters when a user participates in an experiment pressing the button </span><span class="c15">Participate in random experiment</span><span>, or they will be included in the JSON returned when we call the endpoint </span><span class="c16">getparams</span><span> (see section 8.1)</span><span class="c16">.</span><span class="c1"> In the latter case, we could overwrite one or more params, so the values of the remaining parameters may change.</span>

<span class="c1"></span>

<span>The downside of using PlanOut is that you may not be able to define what is the control and what the treatments, specially if you want to run a full factorial experiment. In that case, you should use only one script, which will be part of one variant (which will be </span><span class="c15">control </span><span class="c1">for the platform). All the users will go to that variant, but depending on the identifier of the user, they will get different parameters. Therefore, in order to monitor the experiment and analyze the results, you must pay attention to the values of the parameters, instead of just the name of the variant, which will be the same in all cases.</span>

<span class="c1"></span>

<span class="c1">In general, the use of PlanOut will be conditioned by three main factors:</span>

-   <span class="c1">Reuse of the client for different experiments</span>
-   <span class="c1">Number of variables to test</span>
-   <span class="c1">Full factorial experiment</span>

<span class="c1"></span>

<span class="c1">If you are going to reuse the client for multiple experiments, you will probably have several parameters that condition the behaviour or interface showed to the user. Keeping them in a script that can be reused is a cleaner solution: you can comment there the parameters, add new parameters or modify existing ones, keeping in those scripts the logic of the each experiment.</span>

<span class="c1"></span>

<span class="c1">If you have a big number of variables to test, then maybe it is not a good idea to add all of them to each one of the client URL defined in each variant (probably a lot of them too). If you have a big number of variables to test, and you want a full factorial experiment, then it is clear you need PlanOut. Otherwise you will have to define all the possible variants manually. See the flowchart in figure 9.</span>

<span style="overflow: hidden; display: inline-block; margin: 0.00px -0.00px; border: 1.33px solid #b7b7b7; transform: rotate(0.00rad) translateZ(0px); -webkit-transform: rotate(0.00rad) translateZ(0px); width: 624.00px; height: 350.67px;">![](images/image10.jpg)</span>
==============================================================================================================================================================================================================================================================================

<span class="c17">Figure 9: Flowchart to decide if it is worth it to use PlanOut in </span><span class="c16 c17">APONE</span><span class="c11"> as part of the definition of an experiment.</span>

<span class="c23 c39">9. RELEVANT REFERENCES</span>
===================================================

### <span class="c33">Surveys</span>

<span>Hofmann, Katja, Lihong Li, and Filip Radlinski. "Online evaluation for information retrieval." </span><span class="c15">Foundations and Trends in Information Retrieval,</span><span class="c1"> 10(1), pp. 1-117, 2016</span>

<span>Kohavi, Ron, et al. "Controlled experiments on the web: survey and practical guide." </span><span class="c15">Data mining and knowledge discovery</span><span class="c1"> 18(1), pp. 140-181, 2009</span>

<span class="c1"></span>

### <span class="c33">Tips</span>

<span>Kohavi, Ron, et al. "Trustworthy online controlled experiments: Five puzzling outcomes explained." </span><span class="c15">Proceedings of the 18th ACM SIGKDD international conference on Knowledge discovery and data mining, pp.</span><span> </span><span class="c1">786-794, 2012</span>

<span>Kohavi, Ron, et al. "Seven rules of thumb for web site experimenters." </span><span class="c15">Proceedings of the 20th ACM SIGKDD international conference on Knowledge discovery and data mining, </span><span>pp.</span><span class="c1"> 1857-1866, 2014</span>

<span class="c1"></span>

### <span class="c33">Experiment Life-Cicle</span>

<span>Kevic, Katja, et al. "Characterizing experimentation in continuous deployment: a case study on bing." </span><span class="c15">Proceedings of the 39th International Conference on Software Engineering: Software Engineering in Practice Track</span><span>,</span><span class="c1"> pp. 123-132, 2017.</span>

<span class="c1"></span>

### <span class="c33">Interleaving</span>

<span>Joachims, Thorsten. "Optimizing search engines using clickthrough data." </span><span class="c15">Proceedings of the eighth ACM SIGKDD international conference on Knowledge discovery and data mining</span><span>, pp.</span><span class="c1"> 133-142, 2002</span>

<span>Chapelle, Olivier, et al. "Large-scale validation and analysis of interleaved search evaluation." </span><span class="c15">ACM Transactions on Information Systems, </span><span class="c1">30(1), pp. 6:1–6:41, 2012</span>

<span class="c1"></span>

### <span class="c33">Large-scale</span>

<span>Kohavi, Ron, et al. "Online controlled experiments at large scale." </span><span class="c15">Proceedings of the 19th ACM SIGKDD international conference on Knowledge discovery and data mining</span><span>, pp. 1168-1176,</span><span class="c1"> 2013</span>

<span class="c1"></span>

### <span class="c33">PlanOut         </span>

<span>Bakshy, Eytan, Dean Eckles, and Michael S. Bernstein. "Designing and deploying online field experiments." </span><span class="c15">Proceedings of the 23rd international conference on World wide web</span><span>, 283-292,</span><span>  2014</span>

<span class="c1"></span>

<span class="c1"></span>

<span class="c1"></span>

<span class="c1"></span>

------------------------------------------------------------------------

<a href="#ftnt_ref1" id="ftnt1">[1]</a><span class="c17"> These results can be calculated automatically for several standard metrics using the trec\_eval tool located at </span><span class="c20 c17"><a href="https://www.google.com/url?q=http://trec.nist.gov/trec_eval/index.html&amp;sa=D&amp;ust=1517523797561000&amp;usg=AFQjCNGJXTxO-IdSRk4ArAvJh8MricIqiQ" class="c0">http://trec.nist.gov/trec_eval/index.html</a></span>

<a href="#ftnt_ref2" id="ftnt2">[2]</a><span class="c11"> Note that we are assuming that if the user clicks on a document, then that document is relevant. Usually, several metrics are combined in an OEC, for example, the click-rate plus the dwell-time, which measures how much time a user spends in a document. Those two metrics have to be then aggregated in some way, and this is one of the research problems in online evaluation in general.</span>

<a href="#ftnt_ref3" id="ftnt3">[3]</a><span class="c17"> This is a simplification. Usually different metrics are combined to get a more reliable </span><span class="c17 c15">Overall Evaluation Criteria</span><span class="c17">. In Hofmann et al. (see </span><span class="c20 c17"><a href="#h.wz8hjou74xlm" class="c0">references</a></span><span class="c11">) there is a extensive list of metrics that could be used in this kind of IR experiments.</span>

<a href="#ftnt_ref4" id="ftnt4">[4]</a><span class="c17"> </span><span>The platform does not have any restrictions regarding the URLs that can be used. You could use, for example, different URLs for each variant in other experiments, with or without query parameters.</span>

<a href="#ftnt_ref5" id="ftnt5">[5]</a><span class="c11">  The value of each query parameter in the URL will be then URIencoded. The whole query string after the symbol ‘?’ will be then Base64 encoded and then URIEncoded. For example:</span>

<span class="c11">http://myclient?URIencode(Base64encode(\_variant=URIencode(“Blue”)&\_idunit=URIencode(“1234”)))</span>

<span class="c11"></span>

<a href="#ftnt_ref6" id="ftnt6">[6]</a><span class="c17"> Note that the events may not be immediately saved in the database, as they are sent to a message broker platform (RabbitMQ). A possible delay here will also affect the monitoring system, as it depends on the events </span><span class="c17 c15">exposure</span><span class="c17"> and </span><span class="c17 c15">completed</span><span class="c11"> already registered by the message broker.</span>

<a href="#ftnt_ref7" id="ftnt7">[7]</a><span class="c11"> Two different content-types are supported for this endpoint, APPLICATION/JSON and TEXT/PLAIN. The latter should be used in order to easy cross-domain communications, where the calls are made directly from the client-side to a server located in a different domain (Cross-Origin Resource Sharing).</span>

<a href="#ftnt_ref8" id="ftnt8">[8]</a><span class="c17"> For long binary information, </span><span class="c17">the platform</span><span class="c17"> offers an additional endpoint where the information is consumed as Multipart-form-data, and all the parameters are string except for </span><span class="c17 c15">evalue</span><span class="c11">, which is an InputStream. In this case, the contents are sent without encoding. </span>

<a href="#ftnt_ref9" id="ftnt9">[9]</a><span class="c17"> You can send more than one </span><span class="c17 c15">completed</span><span class="c11"> event for the same user, same experiment, although they are counted as one in terms of monitoring.</span>

<a href="#ftnt_ref10" id="ftnt10">[10]</a><span class="c17"> Note that there may be a delay to stop the experiment when the stopping conditions match (</span><span class="c17 c15">date to end </span><span class="c17">or</span><span class="c17 c15"> max. completed units</span><span class="c11">) as these conditions are checked every five seconds.</span>

<a href="#ftnt_ref11" id="ftnt11">[11]</a><span class="c17"> We could have different parameter values in the same variant if, for example, in the script definition of the variant we use a random assignment operator like </span><span class="c17 c15">uniformChoice</span><span class="c17"> (c</span><span class="c17">heck </span><span class="c17">section 8.2).</span>

<a href="#ftnt_ref12" id="ftnt12">[12]</a><span class="c17"> Experiments where the user identifier in the event </span><span class="c17 c15">completed</span><span class="c11"> is the name of the user in the platform.</span>

<a href="#ftnt_ref13" id="ftnt13">[13]</a><span class="c11"> Predefined parameters used by the platform start with “\_”, so it is better to avoid names starting by that symbol.</span>

<a href="#ftnt_ref14" id="ftnt14">[14]</a><span class="c11"> In PlanOut scripts it is possible to include more than one unit at the same time, for example, the user id and the query id. However, APONE is limited to one unit.</span>

<a href="#ftnt_ref15" id="ftnt15">[15]</a><span class="c17"> The name of this variable has to be specified in the field </span><span class="c17 c15">Unit</span><span class="c11"> when creating a new experiment in advanced mode.</span>

<a href="#cmnt_ref1" id="cmnt1">[a]</a><span class="c1">I tried to assign first those experiments with fewer participants, but it turned out that this may be a problem if one experiment does not have any participant because it is not working properly (the user would be redirected there all the time). Therefore, I changed the algorithm to assign experiments randomly. I will solve it in the next version.</span>
