FROM openjdk:8-jre

EXPOSE 8014

ADD ./target/esafebox-1.0.jar /esafebox-docker.jar

ENTRYPOINT ["java", "-jar", "esafebox-docker.jar"]
