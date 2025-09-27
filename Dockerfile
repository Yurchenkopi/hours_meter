FROM maven:3.9.9-amazoncorretto-21

RUN mkdir hours_meter

WORKDIR hours_meter

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["java", "-jar", "target/hours_meter-v2.0.jar", "-Pdocker"]