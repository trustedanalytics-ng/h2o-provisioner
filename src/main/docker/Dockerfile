FROM openjdk:8u102-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
ENV SPRING_PROFILES_ACTIVE=cloud
ENV PATH=$PATH:/hadoop-2.6.0/bin/
ENV HADOOP_CONF_DIR=/yarn-conf

VOLUME /tmp

ADD krb5.conf /etc/krb5.conf

RUN apt-get update --fix-missing
RUN apt-get install -y npm nodejs python-pip r-base krb5-user
RUN ln -s `which nodejs` /usr/bin/node
RUN pip install tabulate

RUN git clone https://github.com/trustedanalytics/h2o-3 /h2o-3 \
  && cd /h2o-3 \
  && export BUILD_HADOOP=1; ./gradlew build -x test \
  && mv /h2o-3/h2o-hadoop/h2o-cdh5.4.2-assembly/build/libs/h2odriver.jar /h2odriver.jar \
  && cd .. \
  && rm /h2o-3 -r \
  && rm -rf /root/.gradle

RUN mkdir /yarn-conf \
  && cat /etc/krb5.conf \
  && wget http://archive.apache.org/dist/hadoop/core/hadoop-2.6.0/hadoop-2.6.0.tar.gz \
  && tar -xvf hadoop-2.6.0.tar.gz \
  && rm -f hadoop-2.6.0.tar.gz

ADD h2o-provisioner-*[0-9].jar /app.jar

RUN cd / \
  && rm -rf /h2o-3 \
  && rm -rf /h2o-3.0.1.7-cdh5.4.2* \
  && rm -rf /var/lib/apt/lists/*

EXPOSE 9090 30389-30398

RUN ln -s -f /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java /etc/alternatives/java
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
