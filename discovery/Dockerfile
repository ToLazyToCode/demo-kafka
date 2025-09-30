# Dockerfile for custom ZooKeeper
FROM openjdk:17-jdk-slim

# Set environment variables
ENV ZOOKEEPER_HOME=/opt/zookeeper \
    ZOO_CONF_DIR=/opt/zookeeper/conf \
    ZOO_DATA_DIR=/var/lib/zookeeper/data \
    ZOO_DATA_LOG_DIR=/var/lib/zookeeper/log \
    ZOOKEEPER_VERSION=3.9.2

# Install wget + unzip
RUN apt-get update && apt-get install -y wget tar bash && rm -rf /var/lib/apt/lists/*
ARG ZOOKEEPER_VERSION=3.9.4
# Download and install ZooKeeper
RUN wget https://downloads.apache.org/zookeeper/zookeeper-$ZOOKEEPER_VERSION/apache-zookeeper-$ZOOKEEPER_VERSION-bin.tar.gz \
    && tar -xzf apache-zookeeper-$ZOOKEEPER_VERSION-bin.tar.gz \
    && mv apache-zookeeper-$ZOOKEEPER_VERSION-bin $ZOOKEEPER_HOME \
    && rm apache-zookeeper-$ZOOKEEPER_VERSION-bin.tar.gz

WORKDIR $ZOOKEEPER_HOME

# Copy config (you’ll create zoo.cfg in same dir as Dockerfile)
COPY zoo.cfg $ZOO_CONF_DIR/zoo.cfg

# Expose client port
EXPOSE 2181

# Start ZooKeeper
ENTRYPOINT ["bin/zkServer.sh", "start-foreground"]