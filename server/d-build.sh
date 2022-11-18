#!/bin/sh

# Remember to gradle clean and bootJar before building the image
docker build -t taskquestimages.azurecr.io/taskquest-server .