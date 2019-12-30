# SolaceAPI

## Synopsis
The overall purpose of my program is to have the ability to select a specific Solace broker from a Solace Cloud Account through the use of an API Token. A publisher will publish a message to a specific topic through the chosen Solace broker. The broker will then route the message to a subscriber who is subscribed to the same topic.


An brief overview of my program is depicted by the following diagram and steps detailed below:
![Dianna Img](https://github.com/diannamcallister/SolaceAPI/blob/master/diagrams/readme_pic.png)

1. The user creates an API Token on their Solace Cloud Account that allows all services and details about independent services to be queried.
2. The user enters this API Token into the program in the SolaceAPI class in the function http_get and runs the program from the GetServiceAndPublish class.
3. An HTTP GET request is performed to Solace Cloud in which all the services (and their service IDs) visible under the API Token are returned and displayed for the user.
4. The user is then prompted to choose a service to use.
5. Another HTTP GET request is performed to Solace Cloud in which further details about the selected service are retrieved and returned.
6. A subscriber connects to the selected broker using the credentials from step 5 and subscribes to a topic entered by the user.
7. A publisher connects to the selected broker using teh credentials from step 5  and publishes a message entered by the user to the topic entered by the user.
8. The subscriber consumes the message from the broker for the topic it is subscribed to.

## Usage
1. In SolaceAPI class, change the API Token to the one for your Solace Cloud Account in the line under the comment: 
```// INSERT YOUR API TOKEN BELOW```
2. Run the program from the GetServiceAndPublish class.
