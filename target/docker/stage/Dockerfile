FROM openjdk:8-jre-alpine as stage0
WORKDIR /opt/docker
COPY opt /opt
USER root
RUN ["chmod", "-R", "u=rX,g=rX", "/opt/docker"]
RUN ["chmod", "u+x,g+x", "/opt/docker/bin/template"]

FROM openjdk:8-jre-alpine
WORKDIR /opt/docker
COPY --from=stage0 --chown=daemon:root /opt/docker /opt/docker
EXPOSE 80
USER daemon
ENTRYPOINT ["/opt/docker/bin/template"]
CMD []
