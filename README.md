# ProductReviewsWebApp

[![Java CI with Maven](https://github.com/NathanMacDiarmid/ProductReviewsWebApp/actions/workflows/maven.yml/badge.svg)](https://github.com/NathanMacDiarmid/ProductReviewsWebApp/actions/workflows/maven.yml)
[![Build and deploy JAR app to Azure Web App](https://github.com/NathanMacDiarmid/ProductReviewsWebApp/actions/workflows/master_productreviewswebapp.yml/badge.svg)](https://github.com/NathanMacDiarmid/ProductReviewsWebApp/actions/workflows/master_productreviewswebapp.yml)

Web app link - http://productreviewswebapp.azurewebsites.net

This Product Reviews Web App is a SpringBoot web application that is deployed on Azure Web Services. It uses RESTful commands and Thymeleaf calls to organize and display information that is persisted and stored in a database.

## This project contains three entities:

### Product



### Review



### Client



## Run locally

- Clone the repository to your machine and run the ProductReviewsWebAppApplication.java file
- On any web browser, navigate to localhost:8080, allowing an offline developer view of the project


## Run with Prometheus and Grafana monitoring

Prometheus and Grafana can be used to query and visualize a variety of app metrics. In order to get them working complete the following steps:

- Install docker on your machine (on debian you can use "apt install docker.io")
- Give execution permission to setup.sh ("chmod +x setup.sh")
- Run setup.sh ("./setup.sh")
- Navigate to localhost:3000 for Grafana, localhost:9090 for Prometheus and localhost:8080 for the spring boot application!
- To tear down after run teardown.sh ("./teardown.sh")

## Contribute

- Once changes are made, re-run ProductReviewsWebAppApplication.java and refresh localhost:8080 on your web browser, the changes should appear
- Create a new branch and commit to the repository
- Open a pull request on that branch, once approved, congratulations, you've successfully contributed to Product Reviews Web App!
